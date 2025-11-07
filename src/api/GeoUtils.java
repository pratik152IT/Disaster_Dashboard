package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeoUtils {
    private static final String API_KEY = "8cfac295b94a46ca9baa3366979663f2"; // 🔑 Replace with your real key

    public static String getCityFromCoordinates(double lat, double lon) {
        try {
            String urlStr = "https://api.opencagedata.com/geocode/v1/json?q=" + lat + "+" + lon + "&key=" + API_KEY;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                response.append(line);
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray results = json.getJSONArray("results");

            if (results.length() > 0) {
                JSONObject components = results.getJSONObject(0).getJSONObject("components");
                if (components.has("city")) return components.getString("city");
                if (components.has("town")) return components.getString("town");
                if (components.has("village")) return components.getString("village");
                if (components.has("state")) return components.getString("state");
            }
        } catch (Exception e) {
            System.err.println("Error in reverse geocoding: " + e.getMessage());
        }
        return "Unknown";
    }
}
