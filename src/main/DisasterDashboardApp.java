package main;

import database.DatabaseManager;
import api.WeatherAPI;
import service.DisasterService;
import service.EmailService;
import model.User;

import java.util.*;
import java.util.concurrent.*;

public class DisasterDashboardApp {
    private static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            DatabaseManager db = new DatabaseManager();

            System.out.print("Enter your name: ");
            String name = sc.nextLine();

            System.out.print("Enter your email: ");
            String email = sc.nextLine();

            System.out.print("Enter your location: ");
            String location = sc.nextLine();

            System.out.print("Do you want to register as an admin? (yes/no): ");
            String wantsAdmin = sc.nextLine().trim().toLowerCase();

            String role = "user";
            if (wantsAdmin.equals("yes")) {
                System.out.print("Enter admin password: ");
                String enteredPassword = sc.nextLine().trim();
                if (enteredPassword.equals(ADMIN_PASSWORD)) {
                    role = "admin";
                    System.out.println("✅ Admin verified successfully!");
                } else {
                    System.out.println("❌ Incorrect password. Registered as normal user.");
                }
            }

            db.addUser(name, email, location, role);

            if (role.equals("admin")) {
                System.out.println("\n👑 Welcome, Admin " + name + "!");
                System.out.println("You can view all users and trigger alerts manually.");
                db.fetchUsers();
            } else {
                System.out.println("\n👋 Welcome, " + name + "!");
                System.out.println("You will receive disaster alerts for your area via email.");
            }

            // --- Weather check for current user ---
            System.out.println("\nFetching weather for your location...");
            try {
                System.out.println(WeatherAPI.getWeather(location));
            } catch (Exception e) {
                System.out.println("⚠️ Weather data unavailable for " + location);
            }

            // --- Admin-only command section ---
            if (role.equals("admin")) {
                System.out.println("\nType 'run test alert' to trigger a weather alert demo, or press Enter to skip:");
                String command = sc.nextLine().trim().toLowerCase();

                if (command.equals("run test alert")) {
                    runWeatherAlertDemo(db);
                } else {
                    System.out.println("⏸️ Skipped test alert demo.");
                }

                System.out.println("\nFetching latest disaster data and sending alerts...");
                DisasterService.getAllDisastersAndSendAlerts();
            } else {
                System.out.println("\nUser mode active — alerts will be sent automatically if your area is affected.");
            }

            // --- Periodic background checks ---
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    System.out.println("\n🔄 Periodic check: fetching latest disaster data...");
                    DisasterService.getAllDisastersAndSendAlerts();
                } catch (Exception e) {
                    System.err.println("Error in periodic check: " + e.getMessage());
                }
            }, 1, 1, TimeUnit.HOURS);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // 🧩 DEMO FUNCTION: Admin-only simulated weather alert broadcast
    private static void runWeatherAlertDemo(DatabaseManager db) {
        System.out.println("\n🚨 Starting Weather Alert Demo...");
        List<User> users = db.getAllUsers();

        for (User u : users) {
            try {
                String weather = WeatherAPI.getWeather(u.getLocation());
                // Very simple condition simulation:
                boolean hot = weather.contains("Temperature") && weather.matches(".*(3[0-9]|4[0-9])\\.?\\d*°C.*");

                if (hot) {
                    System.out.println("🌡️ High temperature detected at " + u.getLocation() + " → Sending alert to " + u.getEmail());
                    EmailService.sendEmail(u.getEmail(),
                            "🔥 Heat Alert for " + u.getLocation(),
                            "Dear " + u.getName() + ",\n\n" +
                            "High temperature conditions detected at your location (" + u.getLocation() + "). " +
                            "Please take precautions and stay hydrated.\n\n" +
                            "— DisasterDashboard");
                } else {
                    System.out.println("✅ " + u.getLocation() + " is safe. No alert sent.");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Could not fetch weather for " + u.getLocation() + ": " + e.getMessage());
            }
        }
        System.out.println("✅ Demo weather alert operation complete.");
    }
}
