/** Simulated payments: card, e-wallet (GCash/PayMaya), and cash. */
public class PaymentProcessor {

    public boolean processCard(String cardNumber, String expiry, String cvv) {
        if (cardNumber == null || expiry == null || cvv == null) return false;

        String digitsOnly = cardNumber.replaceAll("[\\s-]", "");
        if (!digitsOnly.matches("\\d{13,19}")) return false;
        if (!expiry.trim().matches("(0[1-9]|1[0-2])/\\d{2}")) return false;
        if (!cvv.trim().matches("\\d{3,4}")) return false;
        return true;
    }

    public boolean processEWallet(String provider, String phoneNumber) {
        if (provider == null || provider.trim().isEmpty()) return false;
        if (phoneNumber == null) return false;

        return phoneNumber.trim().matches("09\\d{9}");
    }

    public boolean processCash() {
        return true;
    }
}
