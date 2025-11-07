package service;

import api.EONETAPI;
import api.GeoUtils;
import api.USGSEarthquakeAPI;
import database.DatabaseManager;
import model.Disaster;
import model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DisasterService {

    /**
     * Fetch all disasters (Earthquakes + EONET Events) and send alerts
     * to users whose locations match affected areas.
     */
    public static void getAllDisastersAndSendAlerts() throws Exception {
        DatabaseManager db = new DatabaseManager();
        List<User> users = db.getAllUsers();

        // 🌋 Fetch Earthquakes
        JSONArray quakes = USGSEarthquakeAPI.fetchEarthquakes();
        for (int i = 0; i < Math.min(5, quakes.length()); i++) {
            JSONObject quake = quakes.getJSONObject(i);
            JSONObject props = quake.getJSONObject("properties");
            JSONArray coords = quake.getJSONObject("geometry").getJSONArray("coordinates");
            double lon = coords.getDouble(0);
            double lat = coords.getDouble(1);

            String place = GeoUtils.getCityFromCoordinates(lat, lon);
            String title = props.optString("title", "Earthquake");
            String time = new java.util.Date(props.optLong("time")).toString();

            System.out.println("\n🌍 Earthquake detected near: " + place);

            boolean alertSent = false;
            for (User u : users) {
                if (place.equalsIgnoreCase("Unknown")) continue;
                if (u.getLocation().toLowerCase().contains(place.toLowerCase()) ||
                    place.toLowerCase().contains(u.getLocation().toLowerCase())) {
                    EmailService.sendAlertEmail(u.getEmail(), place, "Earthquake", title + " at " + time);
                    alertSent = true;
                }
            }
            if (!alertSent) System.out.println("No registered users near " + place);
        }

        // 🔥 Fetch Natural Events
        JSONArray events = EONETAPI.fetchEvents();
        for (int i = 0; i < Math.min(5, events.length()); i++) {
            JSONObject ev = events.getJSONObject(i);
            JSONArray geo = ev.getJSONArray("geometry");
            JSONObject latestGeo = geo.getJSONObject(geo.length() - 1);
            JSONArray coords = latestGeo.getJSONArray("coordinates");

            double lon = coords.getDouble(0);
            double lat = coords.getDouble(1);

            String place = GeoUtils.getCityFromCoordinates(lat, lon);
            String title = ev.optString("title", "Natural Event");
            String date = latestGeo.optString("date");

            System.out.println("\n🌍 Natural Event detected near: " + place);

            boolean alertSent = false;
            for (User u : users) {
                if (place.equalsIgnoreCase("Unknown")) continue;
                if (u.getLocation().toLowerCase().contains(place.toLowerCase()) ||
                    place.toLowerCase().contains(u.getLocation().toLowerCase())) {
                    EmailService.sendAlertEmail(u.getEmail(), place, "Natural Event", title + " at " + date);
                    alertSent = true;
                }
            }
            if (!alertSent) System.out.println("No registered users near " + place);
        }
    }

    /**
     * 🌍 Fetch all current disasters (without sending alerts)
     * Returns data in a format ready for JSON → Web map display.
     */
    public static JSONArray getAllDisastersAsJSON() throws Exception {
        JSONArray disasterArray = new JSONArray();

        // --- Earthquakes ---
        JSONArray quakes = USGSEarthquakeAPI.fetchEarthquakes();
        for (int i = 0; i < Math.min(10, quakes.length()); i++) {
            JSONObject quake = quakes.getJSONObject(i);
            JSONObject props = quake.getJSONObject("properties");
            JSONArray coords = quake.getJSONObject("geometry").getJSONArray("coordinates");
            double lon = coords.getDouble(0);
            double lat = coords.getDouble(1);

            JSONObject obj = new JSONObject();
            obj.put("type", "Earthquake");
            obj.put("title", props.optString("title", "Unknown"));
            obj.put("location", GeoUtils.getCityFromCoordinates(lat, lon));
            obj.put("date", new java.util.Date(props.optLong("time")).toString());
            obj.put("lat", lat);
            obj.put("lon", lon);

            disasterArray.put(obj);
        }

        // --- Natural Events (EONET) ---
        JSONArray events = EONETAPI.fetchEvents();
        for (int i = 0; i < Math.min(10, events.length()); i++) {
            JSONObject ev = events.getJSONObject(i);
            JSONArray geo = ev.getJSONArray("geometry");
            JSONObject latestGeo = geo.getJSONObject(geo.length() - 1);
            JSONArray coords = latestGeo.getJSONArray("coordinates");

            double lon = coords.getDouble(0);
            double lat = coords.getDouble(1);

            JSONObject obj = new JSONObject();
            obj.put("type", "Natural Event");
            obj.put("title", ev.optString("title", "Unknown"));
            obj.put("location", GeoUtils.getCityFromCoordinates(lat, lon));
            obj.put("date", latestGeo.optString("date"));
            obj.put("lat", lat);
            obj.put("lon", lon);

            disasterArray.put(obj);
        }

        return disasterArray;
    }
}
