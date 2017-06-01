package utills.GPS;

/**
 * Created by User on 01.06.2017.
 */
public class GPSUtills {
    public static long EATH_RADIUS = 6372795;

    public static double gpsConvertToRad(double coord){
       return coord* Math.PI/180;
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2){
        double lat1_cos, lat2_cos, lat1_sin, lat2_sin;
        double sin_delta_long, cos_delta_long;
        double y, x;

        lat1 = gpsConvertToRad(lat1);
        lon1 = gpsConvertToRad(lon1);
        lat2 = gpsConvertToRad(lat2);
        lon2 = gpsConvertToRad(lon2);

        lat1_cos = Math.cos(lat1);
        lat2_cos = Math.cos(lat2);
        lat1_sin = Math.sin(lat1);
        lat2_sin = Math.sin(lat2);

        sin_delta_long = Math.sin(lon2-lon1);
        cos_delta_long = Math.cos(lon2-lon1);

        y = Math.sqrt(Math.pow(lat2_cos*sin_delta_long, 2)+Math.pow(lat1_cos*lat2_sin-lat1_sin*lat2_cos*cos_delta_long,2));
        x=lat1_sin*lat2_sin+lat1_cos*lat2_cos*cos_delta_long;

        return (Math.atan2(y,x) * EATH_RADIUS)/1000;
    }

    public static void main(String[] args) {
        System.out.println(distance(59.60934652152653,  31.146411895751957, 59.607001697649764, 31.166496276855472));
    }
}
