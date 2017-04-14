package clustering;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class Points {
	
	final static public Random RANDOM = new Random(System.currentTimeMillis());
	
	// Prevent instantiation
	private Points() {
		
	}
	
	public static Coordinate[] getPoints(int size, boolean biased) {
		Coordinate[] points = new Coordinate[size];

		if(biased) {
			double latSkew = ThreadLocalRandom.current().nextDouble(0.75, 1.0);
			double longSkew = ThreadLocalRandom.current().nextDouble(0.75, 1.0);
			double latBiased = ThreadLocalRandom.current().nextDouble();
			double longBiased = ThreadLocalRandom.current().nextDouble();
			
			for (int i = 0; i < points.length; i++) {
				double latitude = nextSkewedBoundedDouble(-90.0, 90.0, latSkew, latBiased);
				double longitude = nextSkewedBoundedDouble(-180.0, 180.0, longSkew, longBiased);
				points[i] = new Coordinate(latitude, longitude);
			}
		} else {
			for (int i = 0; i < points.length; i++) {
				double latitude = ThreadLocalRandom.current().nextDouble(-90.0, 90.0);
				double longitude = ThreadLocalRandom.current().nextDouble(-180.0, 180.0);
				points[i] = new Coordinate(latitude, longitude);
			}
		}

		return points;
	}

	private static double nextSkewedBoundedDouble(double min, double max, double skew, double bias) {
        double range = max - min;
        double mid = min + range / 2.0;
        double unitGaussian = RANDOM.nextGaussian();
        double biasFactor = Math.exp(bias);
        double retval = mid+(range*(biasFactor/(biasFactor+Math.exp(-unitGaussian/skew))-0.5));
        return retval;
    }
}
