package main;

import static spark.Spark.*;

import service.DisasterService;
import database.DatabaseManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class WebServer {
    public static void main(String[] args) {
        // Set the web server port
        port(8080);

        // Initialize the database
        DatabaseManager db = new DatabaseManager();

        // Optional: serve static files (for frontend assets later)
        staticFiles.externalLocation("public");

        // 🏠 Home Page
        get("/", (req, res) -> """
            <h1>🌍 Disaster Dashboard</h1>
            <p><a href='/map'>🗺️ View Global Disaster Map</a></p>
            <p><a href='/users'>👥 View Registered Users</a></p>
            <p><a href='/api/disasters' target='_blank'>📡 View Disaster Data (JSON API)</a></p>
            <p><a href='/api/users' target='_blank'>📡 View User Data (JSON API)</a></p>
            <hr>
            <p style='color:gray;'>Backend running on port 8080</p>
            """);

        // 🌍 Map Page (for demo)
        get("/map", (req, res) -> {
            res.type("text/html");
            JSONArray disasters = DisasterService.getAllDisastersAsJSON();
            return generateMapHTML(disasters.toString());
        });

        // 👥 User List (HTML)
        get("/users", (req, res) -> {
            res.type("text/html");
            return db.fetchUsersAsHTML();
        });

        // 📡 REST API: Get all disasters (JSON)
        get("/api/disasters", (req, res) -> {
            res.type("application/json");
            JSONArray disasters = DisasterService.getAllDisastersAsJSON();
            return disasters.toString(2);
        });

        // 📡 REST API: Get all users (JSON)
        get("/api/users", (req, res) -> {
            res.type("application/json");
            JSONArray users = new JSONArray();
            db.getAllUsers().forEach(u -> {
                JSONObject obj = new JSONObject();
                obj.put("id", u.getId());
                obj.put("name", u.getName());
                obj.put("email", u.getEmail());
                obj.put("location", u.getLocation());
                obj.put("role", u.getRole());
                users.put(obj);
            });
            return users.toString(2);
        });

        System.out.println("✅ Disaster Dashboard Web Server running at: http://localhost:8080/");
    }

    // 🧩 Helper function to generate interactive map HTML
    private static String generateMapHTML(String disasterData) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
          <title>Global Disaster Map</title>
          <meta charset="utf-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css" />
          <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
          <style>
            body { font-family: Arial, sans-serif; margin: 0; padding: 0; text-align: center; }
            h2 { background-color: #007bff; color: white; padding: 10px; margin: 0; }
            #map { height: 600px; width: 100%%; }
          </style>
        </head>
        <body>
          <h2>🌋 Current Global Disaster Events</h2>
          <div id="map"></div>

          <script>
            var map = L.map('map').setView([20, 0], 2);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
              maxZoom: 10,
              attribution: '© OpenStreetMap contributors'
            }).addTo(map);

            var disasters = JSON.parse('%s');
            disasters.forEach(d => {
              var marker = L.marker([d.lat, d.lon]).addTo(map);
              marker.bindPopup(
                `<b>${d.type}</b><br>${d.title}<br>${d.location}<br>${d.date}`
              );
            });
          </script>
        </body>
        </html>
        """.formatted(disasterData.replace("'", "\\'"));
    }
}
