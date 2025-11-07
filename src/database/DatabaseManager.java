package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.User;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:disaster_dashboard.db";

    public DatabaseManager() {
        initializeDatabase();
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // ✅ Initialize database and ensure schema is up to date
    public void initializeDatabase() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            // Step 1: Create table if not exists
            String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT NOT NULL, " +
                    "location TEXT NOT NULL, " +
                    "role TEXT DEFAULT 'user');";
            stmt.execute(createUserTable);

            // Step 2: Try to add missing column "role" if table already existed
            try (Statement stmt2 = conn.createStatement()) {
                stmt2.execute("ALTER TABLE users ADD COLUMN role TEXT DEFAULT 'user';");
                System.out.println("✅ Added missing column 'role' to users table.");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    System.err.println("Error updating table schema: " + e.getMessage());
                }
            }

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    // ✅ Add user (with role)
    public void addUser(String name, String email, String location, String role) {
        String sql = "INSERT INTO users (name, email, location, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, location);
            pstmt.setString(4, role);
            pstmt.executeUpdate();
            System.out.println("User added successfully: " + name + " (" + role + ")");
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    // ✅ Fetch all users into List<User>
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("location"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }

    // ✅ Print users (used in console app)
    public void fetchUsers() {
        List<User> users = getAllUsers();
        System.out.println("\n=== Registered Users ===");
        for (User u : users) {
            System.out.println(u);
        }
    }

    // ✅ Generate HTML table of users (used in web dashboard)
    public String fetchUsersAsHTML() {
        StringBuilder html = new StringBuilder();
        html.append("""
            <h2>👥 Registered Users</h2>
            <table border='1' cellspacing='0' cellpadding='6'>
              <tr style='background-color:#f2f2f2;'>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Location</th>
                <th>Role</th>
              </tr>
        """);

        for (User u : getAllUsers()) {
            html.append("<tr>")
                .append("<td>").append(u.getId()).append("</td>")
                .append("<td>").append(u.getName()).append("</td>")
                .append("<td>").append(u.getEmail()).append("</td>")
                .append("<td>").append(u.getLocation()).append("</td>")
                .append("<td>").append(u.getRole()).append("</td>")
                .append("</tr>");
        }

        html.append("</table>");
        return html.toString();
    }
}
