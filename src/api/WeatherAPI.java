package weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class WeatherAPI {

    // Fetch weather by city using free APIs (no key needed)
    public static String getWeather(String city) {
        try {
            // Step 1: Convert city to coordinates using Open-Meteo Geocoding API
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" 
                            + city.replace(" ", "%20") + "&count=1";

            HttpURLConnection geoConn = (HttpURLConnection) new URL(geoUrl).openConnection();
            geoConn.setRequestMethod("GET");

            if (geoConn.getResponseCode() != 200) {
                return "❌ Error fetching location data. Code: " + geoConn.getResponseCode();
            }

            BufferedReader geoReader = new BufferedReader(new InputStreamReader(geoConn.getInputStream()));
            StringBuilder geoResponse = new StringBuilder();
            String geoLine;

            while ((geoLine = geoReader.readLine()) != null) {
                geoResponse.append(geoLine);
            }

            geoReader.close();
            geoConn.disconnect();

            JSONObject geoJson = new JSONObject(geoResponse.toString());
            if (!geoJson.has("results")) {
                return "❌ Could not find location for '" + city + "'";
            }

            JSONObject location = geoJson.getJSONArray("results").getJSONObject(0);
            double lat = location.getDouble("latitude");
            double lon = location.getDouble("longitude");
            String country = location.optString("country", "Unknown");

            // Step 2: Get weather data from Open-Meteo API
            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat
                                + "&longitude=" + lon + "&current_weather=true";

            HttpURLConnection weatherConn = (HttpURLConnection) new URL(weatherUrl).openConnection();
            weatherConn.setRequestMethod("GET");

            if (weatherConn.getResponseCode() != 200) {
                return "❌ Error fetching weather data. Code: " + weatherConn.getResponseCode();
            }

            BufferedReader weatherReader = new BufferedReader(new InputStreamReader(weatherConn.getInputStream()));
            StringBuilder weatherResponse = new StringBuilder();
            String line;

            while ((line = weatherReader.readLine()) != null) {
                weatherResponse.append(line);
            }

            weatherReader.close();
            weatherConn.disconnect();

            JSONObject weatherJson = new JSONObject(weatherResponse.toString());
            JSONObject current = weatherJson.getJSONObject("current_weather");

            double temp = current.getDouble("temperature");
            double wind = current.getDouble("windspeed");
            String time = current.getString("time");

            return " Location: " + city + ", " + country + "\n"
                    + " Temperature: " + temp + "°C\n"
                    + " Wind Speed: " + wind + " km/h\n"
                    + " Time: " + time;

        } catch (Exception e) {
            return " Error: " + e.getMessage();
        }
    }
}
