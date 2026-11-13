import java.util.HashMap;
import java.util.Map;

/** Registration, login, masked password input, and password hashing. */
public class AuthManager {
    private final Map<String, User> accounts = new HashMap<>(); // username -> account

    public AuthManager(Map<String, User> loaded) {
        accounts.putAll(loaded);
    }

    public boolean usernameExists(String username) {
        // TODO: accounts.containsKey(username)
        return false;
    }

    /** Returns true if registered; false if username exists or passwords do not match. */
    public boolean register(String username, String password, String confirmPassword) {
        // TODO: validate, generate salt, hash password, add to accounts
        return false;
    }

    /** Returns the User if credentials match, otherwise null. */
    public User login(String username, String password) {
        // TODO: look up user, hash typed password with stored salt, compare
        return null;
    }

    /** Reads a password and shows "*" for each keystroke. */
    public String readMaskedPassword(String prompt) {
        // TODO: mask input
        return "";
    }

    public String generateSalt() {
        // TODO: random salt (SecureRandom)
        return "";
    }

    public String hashPassword(String password, String salt) {
        // TODO: SHA-256 (or PBKDF2WithHmacSHA256) of salt + password
        return "";
    }

    public Map<String, User> getAccounts() { return accounts; }
}
