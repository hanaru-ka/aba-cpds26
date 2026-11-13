/** Customer account. */
public class Customer extends User {
    public Customer(String username, String salt, String passwordHash) {
        super(username, salt, passwordHash, "CUSTOMER");
    }
}
