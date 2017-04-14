package clustering;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

public final class Graphs {

	// Prevent instantiation
	private Graphs() {
		
	}
	
	public static Color[] randomizeColors(int numCentroids) {
		Color[] colors = new Color[numCentroids];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = new Color(ThreadLocalRandom.current().nextInt(100, 200), ThreadLocalRandom.current().nextInt(100, 200), ThreadLocalRandom.current().nextInt(100, 200));
		}
		return colors;
	}
	
	public static void displayGraph(Coordinate[] centroids, Coordinate[] points, Color[] colors, int[] assignments) {
		int[] sizes = new int[centroids.length];
		Arrays.fill(sizes, 0);
		
		XYChart chart = new XYChartBuilder().width(1000).height(650).title("Equal K-Means with K-Means++ Initialization").yAxisTitle("latitude").xAxisTitle("longitude").build();
		// Customize Chart
		chart.getStyler().setDefaultSeriesRenderStyle(
				XYSeriesRenderStyle.Scatter);
		chart.getStyler().setChartTitleVisible(true);
		chart.getStyler().setLegendVisible(true);
		chart.getStyler().setAxisTitlesVisible(true);
		chart.getStyler().setXAxisDecimalPattern("0");
		chart.getStyler().setYAxisDecimalPattern("0");

		Map<Integer, List<Coordinate>> clusters = new HashMap<>();
		for (int c = 0; c < centroids.length; c++) {
			clusters.put(c, new ArrayList<>());
		}

		for (int p = 0; p < points.length; p++) {
			clusters.get(assignments[p]).add(points[p]);
		}

		for (Entry<Integer, List<Coordinate>> entry : clusters.entrySet()) {
			List<Coordinate> clusterPoints = entry.getValue();
			int centroid = entry.getKey();
			
			sizes[centroid] = clusterPoints.size();
			double[] xData = new double[clusterPoints.size()];
			double[] yData = new double[clusterPoints.size()];

			for (int i = 0; i < clusterPoints.size(); i++) {
				Coordinate coordinate = clusterPoints.get(i);
				yData[i] = coordinate.latitude;
				xData[i] = coordinate.longitude;
			}

			XYSeries series = chart.addSeries(String.format("Cluster %d (%d)", centroid + 1, clusterPoints.size()), xData, yData);
			series.setMarkerColor(colors[centroid]);
			series.setMarker(SeriesMarkers.CIRCLE);
			
			XYSeries centroidSeries = chart.addSeries(String.format("Centroid %d", centroid), new double[] { centroids[centroid].longitude }, new double[] { centroids[centroid].latitude });
			centroidSeries.setMarkerColor(Color.BLACK);
			centroidSeries.setShowInLegend(false);
			centroidSeries.setMarker(SeriesMarkers.DIAMOND);
		}

		// Show it
		new SwingWrapper(chart).displayChart();
	}
}
