package model;

public class User {
    private int id;
    private String name;
    private String email;
    private String location;

    // Constructors
    public User(int id, String name, String email, String location) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.location = location;
    }

    public User(String name, String email, String location) {
        this.name = name;
        this.email = email;
        this.location = location;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getLocation() { return location; }

    // To string
    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", email=" + email + ", location=" + location + "]";
    }
}
