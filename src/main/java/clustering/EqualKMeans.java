package clustering;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EqualKMeans {

	private final Coordinate[] centroids;
	private final Coordinate[] points;

	private final Color[] colors;
	private final double[][] distances;
	private final int[] assignments;
	private final int[] sizes;
	private final int maxSize;

	private final boolean graphEachIteration;

	public EqualKMeans(Coordinate[] centroids, Coordinate[] points, boolean graphEachIteration) {
		this.centroids = centroids;
		this.points = points;
		this.graphEachIteration = graphEachIteration;

		colors = Graphs.randomizeColors(centroids.length);
		
		distances = new double[centroids.length][points.length];
		sizes = new int[centroids.length];

		assignments = new int[points.length];
		Arrays.fill(assignments, -1);

		if (centroids.length > 0) {
			maxSize = (int) Math.ceil(points.length / centroids.length);
		} else {
			throw new IllegalArgumentException(
					"There must be at least one centroid.");
		}
	}

	/**
	 * For each individual point predetermine the distance between said point
	 * and each cluster centroid.
	 */
	private void calculateDistances() {
		for (int c = 0; c < centroids.length; c++) {
			for (int p = 0; p < points.length; p++) {
				distances[c][p] = HaversineDistance.calculate(centroids[c],
						points[p]);
			}
		}
	}

	/**
	 * Will assign each point to its nearest centroid; however, if the cluster
	 * around the centroid is larger than the ideal count that point will be
	 * reassigned to another cluster.
	 * 
	 * @return The amount of points moved.
	 */
	private void assign() {
		Arrays.fill(sizes, 0);
		List<PointMeta> pointMeta = new ArrayList<>();

		for (int point = 0; point < points.length; point++) {
			double summedDistance = 0.0;
			for (int centroid = 0; centroid < centroids.length; centroid++) {
				summedDistance += Math.pow(distances[centroid][point], 3);
			}
			pointMeta.add(new PointMeta(point, summedDistance));
		}

		Collections.sort(pointMeta);
		Collections.reverse(pointMeta);

		for (PointMeta meta : pointMeta) {
			int point = meta.point;
			Integer minimumCentroid = null;
			double minimumDistance = Double.POSITIVE_INFINITY;

			for (int centroid = 0; centroid < centroids.length; centroid++) {
				double distance = distances[centroid][point];
				if (distance < minimumDistance && sizes[centroid] < maxSize) {
					minimumCentroid = centroid;
					minimumDistance = distance;
				}
			}

			if (minimumCentroid == null) {
				for (int centroid = 0; centroid < centroids.length; centroid++) {
					double distance = distances[centroid][point];
					if (distance < minimumDistance && sizes[centroid] == maxSize) {
						minimumCentroid = centroid;
						minimumDistance = distance;
					}
				}
			}

			sizes[minimumCentroid]++; // Update cluster count

			if (assignments[point] != minimumCentroid) {
				assignments[point] = minimumCentroid;
			}
		}

		for (int point = 0; point < points.length; point++) {
			swap(point, new ArrayList<>());
		}
	}
	
	private void swap(int point, List<Integer> exemptCentroids) {
		Integer assignedCentroid = assignments[point];
		Integer nearestCentroid = getNearestCentroid(point, exemptCentroids);
		double minimumDistance = distances[assignedCentroid][point]; // We don't want to end up with a worse result
		Integer minimumPoint = null;
		
		// If the nearest centroid not equal to the centroid that is assigned
		if(nearestCentroid != null && nearestCentroid != assignedCentroid) {
			// Find the a point in the cluster of the nearest centroid which has a minimal distance to the assigned cluster
			for (int swappablePoint = 0; swappablePoint < points.length; swappablePoint++) {
				if(assignments[swappablePoint] != nearestCentroid) {
					continue;
				}
				
				// If the new point has a shorter distance to the assigned centroid
				double distance = distances[assignedCentroid][swappablePoint];
				if(distance < minimumDistance) {
					minimumPoint = swappablePoint;
					minimumDistance = distance;
				}
			}
			
			// Perform swap
			if(minimumPoint != null) {
				assignments[point] = assignments[minimumPoint];
				assignments[minimumPoint] = assignedCentroid;
				
				// Continue to swap this point until the distance to its respective centroid is minimal
				swap(minimumPoint, new ArrayList<>());
			} else {
				// There might be another centroid with a promising swap
				exemptCentroids.add(nearestCentroid);
				swap(point, exemptCentroids);
			}
		}
	}
	
	/**
	 * Gets the nearest centroid respective to the given point.
	 * 
	 * @param point
	 *            The position of the point in the {@link EqualKMeans#distances}
	 *            array.
	 * @return The position of the nearest centroid.
	 */
	private Integer getNearestCentroid(int point, List<Integer> exemptCentroids) {
		Integer nearestCentroid = null;

		double minimumDistance = Double.POSITIVE_INFINITY;
		for (int centroid = 0; centroid < centroids.length; centroid++) {
			double distance = distances[centroid][point];

			if (distance < minimumDistance && !exemptCentroids.contains(centroid)) {
				minimumDistance = distance;
				nearestCentroid = centroid;
			}
		}

		return nearestCentroid;
	}	

	/**
	 * Will recalculate the centroid for each cluster.
	 */
	private void adjustCentroids() {
		for (int c = 0; c < centroids.length; c++) {
			int numPoints = 0;
			double x = 0;
		    double y = 0;
		    double z = 0;

			// Effectively loop over all points that belong to the cluster
			for (int p = 0; p < points.length; p++) {
				if (assignments[p] != c) {
					continue;
				}

				double latitude = points[p].latitude * Math.PI / 180;
		        double longitude = points[p].longitude * Math.PI / 180;

		        x += Math.cos(latitude) * Math.cos(longitude);
		        y += Math.cos(latitude) * Math.sin(longitude);
		        z += Math.sin(latitude);
				numPoints++;
			}
			if (numPoints > 0) {
				x = x / numPoints;
			    y = y / numPoints;
			    z = z / numPoints;
	
			    double centralLongitude = Math.atan2(y, x);
			    double centralSquareRoot = Math.sqrt(x * x + y * y);
			    double centralLatitude = Math.atan2(z, centralSquareRoot);
			
				centroids[c].latitude = centralLatitude * 180 / Math.PI;
				centroids[c].longitude = centralLongitude * 180 / Math.PI;
			}
		}
	}

	public int[] performEqualKMeans(int numIterations) {
		calculateDistances();
		assign();

		for (int iteration = 0; iteration <= numIterations; iteration++) {
			if(graphEachIteration) {
				try {
					Graphs.displayGraph(centroids, points, colors, assignments);
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			adjustCentroids();
			calculateDistances();
			assign();
		}

		return assignments;
	}

	class PointMeta implements Comparable<PointMeta> {
		public int point;
		public double summedDistance;

		public PointMeta(int point, double summedDistance) {
			this.point = point;
			this.summedDistance = summedDistance;
		}

		@Override
		public int compareTo(PointMeta o) {
			return Double.compare(this.summedDistance, o.summedDistance);
		}
	}
}