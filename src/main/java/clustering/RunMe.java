package clustering;

import java.util.Scanner;
import java.util.StringJoiner;

public class RunMe {
	
	private static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		RunMe.run();
	}
	
	public static void run() {
		boolean skewedPoints = true;
		boolean printEachIteration = false;
		int numPoints = 1000;
		int numClusters = 25;
	
		int choice = -1;
		do {
			StringJoiner joiner = new StringJoiner(System.lineSeparator());
			joiner.add("Please choose one of the follow options:");
			joiner.add(String.format("\t1 - Toggle skewed points %s", (skewedPoints) ? "off" : "on"));
			joiner.add(String.format("\t2 - Toggle print each iteration %s", (printEachIteration) ? "off" : "on"));
			joiner.add(String.format("\t3 - Change number of points (Currently %d)", numPoints));
			joiner.add(String.format("\t4 - Change number of clusters (Currently %d)", numClusters));
			joiner.add("\t5 - Run equal k-means with k-means++ initialization");
			joiner.add("\t0 - Exit");
			joiner.add("Enter the number corresponding to the menu item: ");
			System.out.print(joiner.toString());

			if (scanner.hasNextInt())
				choice = scanner.nextInt();
	        else {
	            scanner.next();
	            continue;
	        }

			if(choice == 1) {
				skewedPoints = !skewedPoints;
			} else if(choice == 2) {
				printEachIteration = !printEachIteration;
			} else if(choice == 3) {
				System.out.print(System.lineSeparator() + "Enter the number of points desired: ");
				
				int input = -1;
				do {
					if (scanner.hasNextInt()) {
						input = scanner.nextInt();
						if(input > 0 && input >= numClusters) {
							numPoints = input;
						}
					} else {
			            scanner.next();
			            continue;
			        }
				} while(input < 0 || input < numClusters);
			} else if(choice == 4) {
				System.out.print(System.lineSeparator() + "Enter the number of clusters desired: ");
				
				int input = -1;
				do {
					if (scanner.hasNextInt()) {
						input = scanner.nextInt();
						if(input > 0 && input <= numPoints) {
							numClusters = input;
						}
					} else {
			            scanner.next();
			            continue;
			        }
				} while(input < 0 || numPoints < input);
			} else if(choice == 5) {
				Coordinate[] points = Points.getPoints(numPoints, skewedPoints);
				Coordinate[] centroids = KMeansPlusPlus.performKMeansPlusPlus(numClusters, points);
				
				EqualKMeans means = new EqualKMeans(centroids, points, printEachIteration);
				int[] assignments = means.performEqualKMeans(200);
				
				Graphs.displayGraph(centroids, points, Graphs.randomizeColors(centroids.length), assignments);
			}
		} while(choice != 0);
	}
}
