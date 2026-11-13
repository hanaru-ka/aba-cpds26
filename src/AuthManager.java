import java.io.Console;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/** Registration, login, masked password input, and password hashing. */
public class AuthManager {
    private final Map<String, User> accounts = new HashMap<>(); // username -> account
    private final SecureRandom random = new SecureRandom();
    private Scanner input;      // only used as a last-resort fallback for password input
    private String lastError = "";
    private boolean warned = false;

    public AuthManager(Map<String, User> loaded) {
        accounts.putAll(loaded);
    }

    /** Lets Main share its Scanner so input is not read from two different Scanners. */
    public void setInput(Scanner input) { this.input = input; }

    /** Reason for the most recent failed register or login, for showing to the user. */
    public String getLastError() { return lastError; }

    public boolean usernameExists(String username) {
        return username != null && accounts.containsKey(username.trim());
    }

    /** Returns true if registered; false if username exists, is invalid, or passwords do not match. */
    public boolean register(String username, String password, String confirmPassword) {
        lastError = "";
        if (username == null || username.trim().isEmpty()) {
            lastError = "Username cannot be empty.";
            return false;
        }
        username = username.trim();
        if (username.contains(",") || username.contains(" ")) {
            lastError = "Username cannot contain spaces or commas.";
            return false;
        }
        if (usernameExists(username)) {
            lastError = "That username is already taken.";
            return false;
        }
        if (password == null || password.isEmpty()) {
            lastError = "Password cannot be empty.";
            return false;
        }
        if (!password.equals(confirmPassword)) {
            lastError = "Passwords do not match.";
            return false;
        }
        String salt = generateSalt();
        accounts.put(username, new Customer(username, salt, hashPassword(password, salt)));
        return true;
    }

    /** Creates an admin account (admins cannot self-register). Returns false if the username exists. */
    public boolean createAdmin(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()
                || usernameExists(username)) {
            return false;
        }
        username = username.trim();
        String salt = generateSalt();
        accounts.put(username, new Admin(username, salt, hashPassword(password, salt)));
        return true;
    }

    /** Makes sure at least one admin exists. Returns true if a default admin was created. */
    public boolean ensureDefaultAdmin() {
        for (User u : accounts.values()) {
            if (u instanceof Admin) return false;
        }
        return createAdmin("admin", "admin123");
    }

    /** Returns the User if credentials match, otherwise null. */
    public User login(String username, String password) {
        lastError = "";
        User user = (username == null) ? null : accounts.get(username.trim());
        if (user == null || password == null) {
            lastError = "Invalid username or password.";
            return null;
        }
        String attempt = hashPassword(password, user.getSalt());
        boolean match = MessageDigest.isEqual(
                attempt.getBytes(StandardCharsets.UTF_8),
                user.getPasswordHash().getBytes(StandardCharsets.UTF_8));
        if (!match) {
            lastError = "Invalid username or password.";
            return null;
        }
        return user;
    }

    /**
     * Reads a password without opening any window.
     * - In a real terminal (a Command Prompt window or IntelliJ's Terminal tab) on Windows,
     *   each typed character is shown as "*" (through PowerShell); with the optional JLine
     *   library it also works on other systems.
     * - In a real terminal without JLine, the typed characters are hidden (Java's Console
     *   cannot print "*" per key).
     * - In IntelliJ's normal Run console, which echoes everything you type, the password
     *   is read like any other line and stays visible.
     */
    public String readMaskedPassword(String prompt) {
        Console console = System.console();
        if (isTerminal(console)) {
            String masked = readWithPowerShell(prompt);     // Windows: shows "*" using PowerShell
            if (masked == null) masked = readWithJLine(prompt); // shows "*" if the JLine library is added
            if (masked != null) return masked;
            char[] pw = console.readPassword("%s", prompt); // otherwise hidden input
            return pw == null ? "" : new String(pw);
        }
        if (!warned) {
            System.out.println("(Note: this console shows typed passwords. In IntelliJ, turn on "
                    + "\"Emulate terminal in output console\" in the Run configuration to hide them.)");
            warned = true;
        }
        if (input == null) input = new Scanner(System.in);
        System.out.print(prompt);
        return input.hasNextLine() ? input.nextLine() : "";
    }

    /**
     * Windows only: asks PowerShell for the password with Read-Host -AsSecureString, which
     * shows "*" for each key. PowerShell hands the text back through a temporary file that
     * is deleted right away. Returns null if this is not Windows or PowerShell fails.
     */
    private String readWithPowerShell(String prompt) {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) return null;
        Path tmp = null;
        try {
            tmp = Files.createTempFile("abamart", ".tmp");
            String label = prompt.replaceAll("[:\\s]+$", "").replace("'", "''");
            String file = tmp.toAbsolutePath().toString().replace("'", "''");
            String script = "$s = Read-Host -Prompt '" + label + "' -AsSecureString; "
                    + "$b = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($s); "
                    + "[IO.File]::WriteAllText('" + file + "', "
                    + "[Runtime.InteropServices.Marshal]::PtrToStringBSTR($b)); "
                    + "[Runtime.InteropServices.Marshal]::ZeroFreeBSTR($b)";
            Process p = new ProcessBuilder("powershell", "-NoProfile", "-Command", script)
                    .inheritIO().start();
            if (p.waitFor() != 0) return null;
            return new String(Files.readAllBytes(tmp), StandardCharsets.UTF_8);
        } catch (IOException | InterruptedException e) {
            return null;
        } finally {
            if (tmp != null) {
                try { Files.deleteIfExists(tmp); } catch (IOException ignored) { }
            }
        }
    }

    private Object jlineReader;      // org.jline.reader.LineReader, created once (via reflection)
    private boolean jlineFailed = false;

    /**
     * Reads a password showing "*" for each key, using the optional JLine library.
     * JLine is called through reflection so the project still compiles and runs without it.
     * Returns null if JLine is not on the classpath or cannot create a real terminal.
     */
    private String readWithJLine(String prompt) {
        if (jlineFailed) return null;
        try {
            if (jlineReader == null) {
                Class<?> tb = Class.forName("org.jline.terminal.TerminalBuilder");
                Object builder = tb.getMethod("builder").invoke(null);
                builder = tb.getMethod("system", boolean.class).invoke(builder, true);
                Object terminal = tb.getMethod("build").invoke(builder);
                String type = (String) Class.forName("org.jline.terminal.Terminal")
                        .getMethod("getType").invoke(terminal);
                if (type == null || type.toLowerCase().contains("dumb")) {
                    jlineFailed = true;
                    return null;
                }
                Class<?> lrb = Class.forName("org.jline.reader.LineReaderBuilder");
                Object b = lrb.getMethod("builder").invoke(null);
                b = lrb.getMethod("terminal", Class.forName("org.jline.terminal.Terminal")).invoke(b, terminal);
                jlineReader = lrb.getMethod("build").invoke(b);
            }
            return (String) Class.forName("org.jline.reader.LineReader")
                    .getMethod("readLine", String.class, Character.class)
                    .invoke(jlineReader, prompt, '*');
        } catch (Throwable t) {
            jlineFailed = true;
            return null;
        }
    }

    /** True if the console is a real terminal (checked with reflection because older Java lacks isTerminal). */
    static boolean isTerminal(Console console) {
        if (console == null) return false;
        try {
            return (Boolean) Console.class.getMethod("isTerminal").invoke(console);
        } catch (ReflectiveOperationException e) {
            return true; // Java 21 and older: a non-null console is always a terminal
        }
    }

    /** Random 16-byte salt, Base64-encoded. */
    public String generateSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /** Hashes salt + password with SHA-256 and returns the hash as Base64. */
    public String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    public Map<String, User> getAccounts() { return accounts; }
}