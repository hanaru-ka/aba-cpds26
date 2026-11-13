/** Simulated payments: card, e-wallet (GCash/PayMaya), and cash. */
public class PaymentProcessor {

    public boolean processCard(String cardNumber, String expiry, String cvv) {
        // TODO: validate format only (no real gateway)
        return false;
    }

    public boolean processEWallet(String provider, String phoneNumber) {
        // TODO: validate phone number format
        return false;
    }

    public boolean processCash() {
        // TODO: confirm cash payment
        return true;
    }
}
