package model;

public class User {
    private int id;
    private String name;
    private String email;
    private String location;
    private String role; // 👑 NEW FIELD

    // ✅ Full constructor (used when reading from DB)
    public User(int id, String name, String email, String location, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.location = location;
        this.role = role;
    }

    // ✅ Constructor for new users
    public User(String name, String email, String location, String role) {
        this.name = name;
        this.email = email;
        this.location = location;
        this.role = role;
    }

    // ⚙️ Compatibility constructor (still supports old calls)
    public User(int id, String name, String email, String location) {
        this(id, name, email, location, "user");
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getLocation() { return location; }
    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return String.format("User [id=%d, name=%s, email=%s, location=%s, role=%s]",
                id, name, email, location, role);
    }
}
