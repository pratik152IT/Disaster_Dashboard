package service;

import database.DatabaseManager;
import java.sql.*;
import java.util.List;

public class AlertService {

    public static void sendAlerts(List<String> affectedLocations, String disasterType, String description) {
        String sql = "SELECT email, location FROM users";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String email = rs.getString("email");
                String location = rs.getString("location");

                if (affectedLocations.contains(location)) {
                    String subject = "ALERT: " + disasterType + " in your area!";
                    String body = "Dear user,\n\n" + description + "\n\nStay Safe!";
                    EmailService.sendEmail(email, subject, body);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
