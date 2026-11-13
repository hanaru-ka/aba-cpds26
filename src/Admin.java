/** Admin account (pre-created in accounts.csv). */
public class Admin extends User {
    public Admin(String username, String salt, String passwordHash) {
        super(username, salt, passwordHash, "ADMIN");
    }
}
