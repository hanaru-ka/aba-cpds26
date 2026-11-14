import java.util.Scanner;

/** Admin main menu. */
public class AdminMenu {
    private final ProductCatalog catalog;
    private final RestockMonitor monitor;

    public AdminMenu(ProductCatalog catalog, RestockMonitor monitor) {
        this.catalog = catalog;
        this.monitor = monitor;
    }

    public void run() {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View Restock Alerts");
            System.out.println("2. Check Stock by Section");
            System.out.println("3. Add Product");
            System.out.println("4. Edit Product");
            System.out.println("5. Remove Product");
            System.out.println("6. Logout");

            System.out.print("Choose an option: ");
            String choice = in.nextLine().trim();

            if (choice.equals("1")) {
                showRestockAlerts();
            } else if (choice.equals("2")) {
                checkStockBySection();
            } else if (choice.equals("3")) {
                addProduct();
            } else if (choice.equals("4")) {
                editProduct();
            } else if (choice.equals("5")) {
                removeProduct();
            } else if (choice.equals("6")) {
                System.out.println("Logging out...");
                break;
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void showRestockAlerts() {
        System.out.println("\nView Restock Alerts is currently in progress.");
    }

    private void checkStockBySection() {
        System.out.println("\nCheck Stock by Section is currently in progress.");
    }

    private void addProduct() {
        System.out.println("\nAdd Product is currently in progress.");
    }

    private void editProduct() {
        System.out.println("\nEdit Product is currently in progress.");
    }

    private void removeProduct() {
        System.out.println("\nRemove Product is currently in progress.");
    }
}
