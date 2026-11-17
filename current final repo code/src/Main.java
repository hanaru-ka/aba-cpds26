import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/** Entry point: loads CSV data, shows the login screen, and opens the menu for each role.
 * There are two preloaded accounts; one for the admin, and one for the customer
 * Admin: admin / admin123
 * Customer: customer1 / password123
 * */
public class Main {
    public static void main(String[] args) {
        if (relaunchInTerminalWindow(args)) {
            System.out.println("ABA-Mart opened in a new terminal window. Continue there.");
            return;
        }
        boolean inOwnWindow = args.length > 0 && args[0].equals("--child");
        Scanner in = new Scanner(System.in);
        CsvStorage storage = new CsvStorage();
        AuthManager auth = new AuthManager(storage.loadAccounts());
        auth.setInput(in);
        if (auth.ensureDefaultAdmin()) {
            storage.saveAccounts(auth.getAccounts());
            System.out.println("Default admin account created (username: admin, password: admin123).");
        }
        ProductCatalog catalog = new ProductCatalog(storage.loadProducts());
        RestockMonitor monitor = new RestockMonitor(catalog.getAllProducts());

        while (true) {
            ConsoleUtil.clear();
            System.out.println("=== ABA-Mart Online Grocery ===");
            System.out.println("1. Login");
            System.out.println("2. Register (new customer)");
            System.out.println("3. Exit");
            String choice = ask(in, "Choose an option: ");
            if (choice == null || choice.equals("3")) break;

            if (choice.equals("1")) {
                User user = loginScreen(in, auth);
                if (user != null) {
                    ConsoleUtil.clear();
                    System.out.println("Welcome, " + user.getUsername() + "!");
                    startSession(user, catalog, monitor, in);
                    monitor.rebuild(catalog.getAllProducts());
                    storage.saveProducts(catalog.getAllProducts());
                    storage.saveAccounts(auth.getAccounts());
                    System.out.println("Logged out.");
                }
                ConsoleUtil.pause(in);
            } else if (choice.equals("2")) {
                if (registerScreen(in, auth)) {
                    storage.saveAccounts(auth.getAccounts());
                }
                ConsoleUtil.pause(in);
            } else {
                System.out.println("Invalid option. Please enter 1, 2, or 3.");
                ConsoleUtil.pause(in);
            }
        }

        storage.saveAccounts(auth.getAccounts());
        storage.saveProducts(catalog.getAllProducts());
        System.out.println("Goodbye!");
        if (inOwnWindow) {
            System.out.print("Press Enter to close this window...");
            if (in.hasNextLine()) in.nextLine();
        }
        in.close();
    }

    private static boolean relaunchInTerminalWindow(String[] args) {
        if (args.length > 0 && args[0].equals("--child")) return false;
        if (!System.getProperty("os.name").toLowerCase().contains("win")) return false;
        if (AuthManager.isTerminal(System.console())) return false;
        try {
            String java = Paths.get(System.getProperty("java.home"), "bin", "java.exe").toString();
            new ProcessBuilder("cmd", "/c", "start", "ABA Mart", java,
                    "-cp", System.getProperty("java.class.path"), "Main", "--child")
                    .directory(new File(System.getProperty("user.dir")))
                    .start();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static User loginScreen(Scanner in, AuthManager auth) {
        String username = ask(in, "Username: ");
        if (username == null) return null;
        String password = auth.readMaskedPassword("Password: ");
        User user = auth.login(username, password);
        if (user == null) {
            System.out.println(auth.getLastError());
        }
        return user;
    }

    private static boolean registerScreen(Scanner in, AuthManager auth) {
        String username = ask(in, "Choose a username: ");
        if (username == null) return false;
        String password = auth.readMaskedPassword("Choose a password: ");
        String confirm = auth.readMaskedPassword("Confirm password: ");
        if (auth.register(username, password, confirm)) {
            System.out.println("Registration successful. You can now log in.");
            return true;
        }
        System.out.println(auth.getLastError());
        return false;
    }

    private static void startSession(User user, ProductCatalog catalog, RestockMonitor monitor, Scanner in) {
        if (user instanceof Admin) {
            new AdminMenu(catalog, monitor).run();
        } else {
            new CustomerMenu(user, catalog, monitor, in).run();
        }
    }

    /** Prints a prompt and reads one trimmed line; returns null if input has ended. */
    private static String ask(Scanner in, String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : null;
    }
}
