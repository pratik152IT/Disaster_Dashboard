package database;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:disaster_dashboard.db";

    public DatabaseManager() {
        initializeDatabase();
    }

    // ✅ Add this static connection method for DAO
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Initialize the database
    public void initializeDatabase() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT NOT NULL, " +
                    "location TEXT NOT NULL);";
            stmt.execute(createUserTable);

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    // Add user directly (used by older version)
    public void addUser(String name, String email, String location) {
        String sql = "INSERT INTO users (name, email, location) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, location);
            pstmt.executeUpdate();

            System.out.println("User added successfully: " + name);
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    // Fetch all users directly
    public void fetchUsers() {
        String sql = "SELECT * FROM users";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== Registered Users ===");
            while (rs.next()) {
                System.out.println("User [id=" + rs.getInt("id") +
                        ", name=" + rs.getString("name") +
                        ", email=" + rs.getString("email") +
                        ", location=" + rs.getString("location") + "]");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
    }
}
