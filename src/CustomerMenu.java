import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Customer main menu. */
public class CustomerMenu {
    private final User user;
    private final ProductCatalog catalog;
    private final RestockMonitor monitor;
    private final Cart cart = new Cart();
    private final OrderHistory history = new OrderHistory();
    private final Scanner in;
    private final CsvStorage storage;

    public CustomerMenu(User user, ProductCatalog catalog, RestockMonitor monitor, Scanner in, CsvStorage storage) {
        this.user = user;
        this.catalog = catalog;
        this.monitor = monitor;
        this.in = in;
        this.storage = storage;
        for (Order order : storage.loadOrders(catalog.getAllProducts())) {
            if (order.getUsername().equals(user.getUsername())) {
                history.addOrder(order);
            }
        }
    }

    public void run() {
        while (true) {
            ConsoleUtil.clear();
            System.out.println("=== Customer Menu (" + user.getUsername() + ") ===");
            System.out.println("1. Browse by category");
            System.out.println("2. Search products");
            System.out.println("3. My cart");
            System.out.println("4. Checkout");
            System.out.println("5. Order history");
            System.out.println("6. Reorder");
            System.out.println("0. Logout");
            String choice = ask("Choose an option: ");
            if (choice == null || choice.equals("0")) return;
            if (choice.equals("1")) {
                browseByCategory();
                ConsoleUtil.pause(in);
            }
            else if (choice.equals("2")) {
                searchProducts();
            }
            else if (choice.equals("3")) {
                manageCart();
                ConsoleUtil.pause(in);
            }
            else if (choice.equals("4")) {
                checkout();
                ConsoleUtil.pause(in);
            }
            else if (choice.equals("5")) {
                viewOrderHistory();
                ConsoleUtil.pause(in);
            }
            else if (choice.equals("6")) {
                reorder();
                ConsoleUtil.pause(in);
            }

            else {
                System.out.println("Invalid option. Please enter 1 or 0.");
                ConsoleUtil.pause(in);
            }
        }
    }

    private void browseByCategory() {
        String selectedCategory = chooseCategory();
        if (selectedCategory == null) return;

        showCategoryProducts(selectedCategory, catalog.getByCategory(selectedCategory));
    }

    /** Lists the categories and returns the one the customer picks, or null if none was chosen. */
    private String chooseCategory() {
        List<Product> products = catalog.getAllProducts();
        List<String> categories = new ArrayList<>();

        for (Product product : products) {
            String category = product.getCategory();

            if (!categories.contains(category)) {
                categories.add(category);
            }
        }

        if (categories.isEmpty()) {
            System.out.println("No products available.");
            return null;
        }

        System.out.println("\n=== PRODUCT CATEGORIES ===");

        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        String choice = ask("\nChoose a category: ");
        if (choice == null) return null;

        int categoryNumber;

        try {
            categoryNumber = Integer.parseInt(choice);
        } catch (NumberFormatException e) {
            System.out.println("Invalid category selection.");
            return null;
        }

        if (categoryNumber < 1 || categoryNumber > categories.size()) {
            System.out.println("Invalid category selection.");
            return null;
        }

        return categories.get(categoryNumber - 1);
    }

    /** Prints the category heading and its product table. Returns false if there is nothing to show. */
    private boolean showCategoryProducts(String category, List<Product> products) {
        System.out.println("\n=== " + category.toUpperCase() + " ===");

        if (products.isEmpty()) {
            System.out.println("No products in this category.");
            return false;
        }

        printProductTable(products);
        return true;
    }

    private void printProductTable(List<Product> products) {
        List<Product> sortedProducts = Sorter.mergeSort(
                products, Comparator.comparing(Product::getProductId));

        System.out.printf("%-10s %-24s %12s %8s  %s%n",
                "Product ID", "Product", "Price", "Stock", "Status");
        System.out.println("-".repeat(70));

        for (Product product : sortedProducts) {
            String status;
            if (product.isOutOfStock()) {
                status = "Out of Stock";
            } else if (monitor.isLowStock(product)) {
                status = "Running Low";
            } else {
                status = "Available";
            }

            System.out.printf("%-10s %-24s %12s %8d  %s%n",
                    product.getProductId(), product.getName(),
                    String.format("PHP %.2f", product.getFinalPrice()),
                    product.getStock(), status);
        }
    }

    // ---------- Quick Search ----------

    /** Keeps prompting for keywords until the customer presses Enter. */
    private void searchProducts() {
        while (true) {
            ConsoleUtil.clear();
            System.out.println("=== SEARCH PRODUCTS ===");
            System.out.println("Type one or more keywords (e.g. \"coffee\", \"canned tuna\", \"per kg\").");
            String query = ask("Search (or press Enter to go back): ");
            if (query == null || query.isEmpty()) return;

            ConsoleUtil.clear();
            System.out.println("=== SEARCH RESULTS FOR \"" + query + "\" ===");

            List<Product> matches = Sorter.mergeSort(
                    Searcher.linearSearchByKeywords(catalog.getAllProducts(), query),
                    Comparator.comparing(Product::getProductId));
            if (matches.isEmpty()) {
                System.out.println("\nNo products matched your search.");
            } else {
                printSearchResults(matches);
                addToCartFrom(matches, "\nProduct ID to add to cart (or press Enter to skip): ");
            }
            System.out.println();
            ConsoleUtil.pause(in);
        }
    }

    private void printSearchResults(List<Product> products) {
        System.out.println(products.size() + " product(s) found.\n");
        System.out.printf("%-10s %-26s %-18s %12s %8s  %s%n",
                "Product ID", "Product", "Category", "Price", "Stock", "Status");
        System.out.println("-".repeat(88));

        for (Product p : products) {
            String status;
            if (p.isOutOfStock()) {
                status = "Out of Stock";
            } else if (monitor.isLowStock(p)) {
                status = "Running Low";
            } else {
                status = "Available";
            }

            System.out.printf("%-10s %-26s %-18s %12s %8d  %s%n",
                    p.getProductId(), p.getName(), p.getCategory(),
                    String.format("PHP %.2f", p.getFinalPrice()),
                    p.getStock(), status);
        }
    }

    private String ask(String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : null;
    }

    private void manageCart() {
        while (true) {
            ConsoleUtil.clear();
            System.out.println("\n=== MY CART ===");
            printCart();

            System.out.println("\n1. Add item");
            System.out.println("2. Edit quantity");
            System.out.println("3. Remove item");
            System.out.println("4. Search products");
            System.out.println("0. Back");

            String choice = ask("Choose an option: ");
            if (choice == null || choice.equals("0")) return;

            if (choice.equals("1")) {
                addItemToCart();
                ConsoleUtil.pause(in);
            } else if (choice.equals("2")) {
                editCartQuantity();
                ConsoleUtil.pause(in);
            } else if (choice.equals("3")) {
                removeCartItem();
                ConsoleUtil.pause(in);
            } else if (choice.equals("4")) {
                searchProducts();
            } else {
                System.out.println("Invalid option.");
                ConsoleUtil.pause(in);
            }
        }
    }

    private void printCart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        System.out.printf("%-10s %-24s %8s %14s%n", "Product ID", "Product", "Quantity", "Subtotal");
        System.out.println("-".repeat(59));

        int totalQuantity = 0;
        for (CartItem item : cartItemsSortedById()) {
            Product product = item.getProduct();
            System.out.printf("%-10s %-24s %8d %14s%n",
                    product.getProductId(), product.getName(), item.getQuantity(),
                    String.format("PHP %.2f", item.getSubtotal()));
            totalQuantity += item.getQuantity();
        }

        System.out.println("-".repeat(59));
        System.out.printf("%-10s %-24s %8d %14s%n", "", "", totalQuantity,
                String.format("PHP %.2f", cart.getTotal()));

    }

    private void addItemToCart() {
        String selectedCategory = chooseCategory();
        if (selectedCategory == null) return;

        List<Product> categoryProducts = catalog.getByCategory(selectedCategory);
        if (!showCategoryProducts(selectedCategory, categoryProducts)) return;

        addToCartFrom(categoryProducts, "\nProduct ID to add (or press Enter to cancel): ");
    }

    /** Asks for a Product ID from the list just shown, then a quantity, and adds it to the cart. */
    private void addToCartFrom(List<Product> shown, String prompt) {
        String id = ask(prompt);
        if (id == null || id.isEmpty()) return;

        Product product = null;
        for (Product candidate : shown) {
            if (candidate.getProductId().equalsIgnoreCase(id.trim())) {
                product = catalog.getProduct(candidate.getProductId());
                break;
            }
        }
        if (product == null) {
            System.out.println("Product not found in the list above.");
            return;
        }
        if (product.isOutOfStock()) {
            System.out.println(product.getName() + " is out of stock.");
            return;
        }

        String qtyText = ask("Quantity: ");
        int quantity = parsePositiveInt(qtyText);
        if (quantity <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }

        int alreadyInCart = 0;
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getProductId().equals(product.getProductId())) {
                alreadyInCart = item.getQuantity();
                break;
            }
        }
        if (alreadyInCart + quantity > product.getStock()) {
            System.out.println("Not enough stock. Available: " + product.getStock()
                    + ", already in cart: " + alreadyInCart + ".");
            return;
        }

        cart.addItem(product, quantity);
        System.out.println("Added " + quantity + " x " + product.getName() + " to your cart.");
    }

    private void editCartQuantity() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        String id = ask("Product ID to edit: ");
        if (id == null || id.isEmpty()) return;

        String qtyText = ask("New quantity: ");
        int quantity = parsePositiveInt(qtyText);
        if (quantity <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }

        Product product = catalog.getProduct(id.trim().toUpperCase());
        if (product != null && quantity > product.getStock()) {
            System.out.println("Not enough stock. Available: " + product.getStock() + ".");
            return;
        }

        if (cart.updateQuantity(id.trim().toUpperCase(), quantity)) {
            System.out.println("Quantity updated.");
        } else {
            System.out.println("That product is not in your cart.");
        }
    }

    private void removeCartItem() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        String id = ask("Product ID to remove: ");
        if (id == null || id.isEmpty()) return;

        if (cart.removeItem(id.trim().toUpperCase())) {
            System.out.println("Removed from your cart.");
        } else {
            System.out.println("That product is not in your cart.");
        }
    }

    private int parsePositiveInt(String text) {
        if (text == null) return 0;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private List<CartItem> cartItemsSortedById() {
        List<Product> products = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            products.add(item.getProduct());
        }
        products = Sorter.mergeSort(products, Comparator.comparing(Product::getProductId));

        List<CartItem> ordered = new ArrayList<>();
        for (Product product : products) {
            for (CartItem item : cart.getItems()) {
                if (item.getProduct().getProductId().equals(product.getProductId())) {
                    ordered.add(item);
                    break;
                }
            }
        }
        return ordered;
    }

    private void checkout() {
        ConsoleUtil.clear();
        System.out.println("=== CHECKOUT ===");
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty. Add items before checking out.");
            return;
        }
        printCart();

        System.out.println("\n1. Card");
        System.out.println("2. E-wallet");
        System.out.println("3. Cash");
        System.out.println("0. Cancel");
        String choice = ask("Payment method: ");
        if (choice == null || choice.equals("0")) return;

        PaymentProcessor payment = new PaymentProcessor();
        String paymentMode;
        boolean approved;

        if (choice.equals("1")) {
            String cardNumber = ask("Card number: ");
            String expiry = ask("Expiry (MM/YY): ");
            String cvv = ask("CVV: ");
            approved = payment.processCard(cardNumber, expiry, cvv);
            paymentMode = "CARD";
        } else if (choice.equals("2")) {
            String provider = ask("Provider (GCash/PayMaya): ");
            String phone = ask("Phone number (09XXXXXXXXX): ");
            approved = payment.processEWallet(provider, phone);
            paymentMode = "EWALLET";
        } else if (choice.equals("3")) {
            approved = payment.processCash();
            paymentMode = "CASH";
        } else {
            System.out.println("Invalid option.");
            return;
        }

        if (!approved) {
            System.out.println("Payment was not approved. Please check your details and try again.");
            return;
        }

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
        }

        String orderId = nextOrderId();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Order order = new Order(orderId, user.getUsername(), date, paymentMode,
                new ArrayList<>(cart.getItems()), cart.getTotal());

        storage.saveOrder(order);
        storage.saveReceipt(order);
        history.addOrder(order);
        monitor.rebuild(catalog.getAllProducts());
        cart.clear();

        System.out.println("\nPayment approved. Order " + orderId + " placed.\n");
        System.out.println(order.generateReceipt());
    }


    private String nextOrderId() {
        int max = 0;
        for (Order order : storage.loadOrders(catalog.getAllProducts())) {
            String id = order.getOrderId();
            if (id != null && id.length() > 1 && id.charAt(0) == 'O') {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(1)));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("O%04d", max + 1);
    }

    private void viewOrderHistory() {
        System.out.println("=== ORDER HISTORY ===");
        List<Order> orders = history.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("You have no past orders.");
            return;
        }

        System.out.printf("%-8s %-18s %-10s %14s%n", "Order ID", "Date", "Payment", "Total");
        System.out.println("-".repeat(54));
        for (Order order : orders) {
            System.out.printf("%-8s %-18s %-10s %14s%n",
                    order.getOrderId(), order.getDate(), order.getPaymentMode(),
                    String.format("PHP %.2f", order.getTotal()));
        }
    }

    private void reorder() {
        List<Order> orders = history.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("You have no past orders to reorder from.");
            return;
        }

        viewOrderHistory();
        String orderId = ask("\nOrder ID to reorder: ");
        if (orderId == null || orderId.isEmpty()) return;

        Order order = history.findOrder(orderId.trim().toUpperCase());
        if (order == null) {
            System.out.println("No order with that ID.");
            return;
        }

        int added = 0;
        int skipped = 0;
        for (CartItem item : order.getItems()) {
            Product product = catalog.getProduct(item.getProduct().getProductId());
            if (product == null || product.isOutOfStock()) {
                skipped++;
                continue;
            }
            int quantity = Math.min(item.getQuantity(), product.getStock());
            cart.addItem(product, quantity);
            added++;
        }

        System.out.println("Added " + added + " item(s) to your cart"
                + (skipped > 0 ? "; skipped " + skipped + " item(s) no longer available." : "."));
    }

}
