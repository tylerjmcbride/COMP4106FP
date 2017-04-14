package clustering;

public class HaversineDistance {
	
	public static final double R = 6372.8; // In kilometers
	
	// Prevent instantiation
	private HaversineDistance() {
		
	}
	
	public static double calculate(Coordinate x, Coordinate y) {
		double dLat = Math.toRadians(y.latitude - x.latitude);
        double dLon = Math.toRadians(y.longitude - x.longitude);
        double lat1 = Math.toRadians(y.latitude);
        double lat2 = Math.toRadians(x.latitude);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
	}
}
