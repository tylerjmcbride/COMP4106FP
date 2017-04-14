package clustering;

import java.util.concurrent.ThreadLocalRandom;

public final class KMeansPlusPlus {
	
	// Prevent instantiation
	private KMeansPlusPlus() {
		
	}
	
	/**
	 * Runs the k-means++ algorithm to determine the initial centroids.
	 * @param numClusters The desired number of clusters.
	 * @param points The array of coordinates.
	 * @return The initial centroids.
	 */
	public static Coordinate[] performKMeansPlusPlus(int numClusters, Coordinate[] points) {
		Coordinate[] centroids = new Coordinate[numClusters];
		Coordinate randomPoint = points[ThreadLocalRandom.current().nextInt(points.length)];
		centroids[0] = new Coordinate(randomPoint.latitude, randomPoint.longitude);
		
		for(int i = 1; i < centroids.length; i++) {
			double[] weightedProbabilityDistribution = new double[points.length];
			double sum = 0.0;
			
			for(int j = 0; j < points.length; j++) {
				sum += Math.pow(HaversineDistance.calculate(getNearestCentroid(centroids, points[j]), points[j]), 2);
				weightedProbabilityDistribution[j] = sum;
			}

			// Choose a random position in the weighted probability distribution
			double randomPosition = ThreadLocalRandom.current().nextDouble() * sum;
			
			// Search for the range which the random position belongs in and assign the respective point as the new centroid
            for (int j = 0 ; j < weightedProbabilityDistribution.length; j++) {
                if (weightedProbabilityDistribution[j] >= randomPosition) {
                    centroids[i] = new Coordinate(points[j].latitude, points[j].longitude);
                    break;
                }
            }
		}
		
		return centroids;
	}
	
	/**
	 * Gets the closest centroid to the given point.
	 * @param centroids The array of centroids.
	 * @param point The point.
	 * @return The cloest centroid to the given point.
	 */
	private static Coordinate getNearestCentroid(Coordinate[] centroids, Coordinate point) {
		Coordinate nearestCentroid = null;

		double minimumDistance = Double.POSITIVE_INFINITY;
		for (int c = 0; c < centroids.length; c++) {
			Coordinate centroid = centroids[c];
			if(centroid != null) {
				double distance = HaversineDistance.calculate(centroid, point);
				if (distance < minimumDistance) {
					minimumDistance = distance;
					nearestCentroid = centroid;
				}
			}
		}

		return nearestCentroid;
	}
}
