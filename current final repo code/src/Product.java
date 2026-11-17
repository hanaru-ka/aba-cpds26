/** A grocery product. */
public class Product {
    private final String productId;
    private String name;
    private String category;
    private double price;
    private int stock;
    private double discount;      // e.g. 0.10 for 10%
    private String specialOffer;

    public Product(String productId, String name, String category,
                   double price, int stock, double discount, String specialOffer) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.discount = discount;
        this.specialOffer = specialOffer;
    }

    public boolean isOutOfStock() {
        return stock <= 0;
    }

    public boolean isLowStock(int threshold) {
        return stock > 0 && stock <= threshold;
    }

    public double getFinalPrice() {
        return price * (1 - discount);
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public double getDiscount() { return discount; }
    public String getSpecialOffer() { return specialOffer; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setDiscount(double discount) { this.discount = discount; }
    public void setSpecialOffer(String specialOffer) { this.specialOffer = specialOffer; }
}
