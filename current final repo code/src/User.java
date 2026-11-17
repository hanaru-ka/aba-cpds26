/** Base account: username, salt, password hash, and role. */
public class User {
    private final String username;
    private final String salt;
    private final String passwordHash;
    private final String role; // "CUSTOMER" or "ADMIN"

    public User(String username, String salt, String passwordHash, String role) {
        this.username = username;
        this.salt = salt;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getSalt() { return salt; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
}
