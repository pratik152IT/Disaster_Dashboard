package main;

import database.DatabaseManager;
import api.EONETAPI;
import api.USGSEarthquakeAPI;
import weather.WeatherAPI;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class DisasterDashboardApp {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            DatabaseManager db = new DatabaseManager();
            db.initializeDatabase();

            System.out.print("Enter your name: ");
            String name = sc.nextLine();

            System.out.print("Enter your email: ");
            String email = sc.nextLine();

            System.out.print("Enter your location: ");
            String location = sc.nextLine();

            db.addUser(name, email, location);
            db.fetchUsers();

            System.out.println("\nFetching weather for your location...");
            System.out.println(WeatherAPI.getWeather(location));

            System.out.println("\nFetching latest disaster data...");

            // Fetch EONET Events
            JSONArray eonetEvents = EONETAPI.fetchEvents();
            System.out.println("\n=== NASA EONET Events ===");
            for (int i = 0; i < Math.min(5, eonetEvents.length()); i++) {
                JSONObject event = eonetEvents.getJSONObject(i);
                System.out.println("[EONET] " + event.getString("title"));
            }

            // Fetch USGS Earthquake Data
            JSONArray earthquakes = USGSEarthquakeAPI.fetchEarthquakes();
            System.out.println("\n=== USGS Earthquakes ===");
            for (int i = 0; i < Math.min(5, earthquakes.length()); i++) {
                JSONObject quake = earthquakes.getJSONObject(i);
                JSONObject props = quake.getJSONObject("properties");
                System.out.println("[USGS] M " + props.getDouble("mag") + " - " + props.getString("place"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
