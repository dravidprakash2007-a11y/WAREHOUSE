import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Perishable goods: expiry checks, cold-chain packing and storage temperature. */
public class PerishableItem extends Item {
    private LocalDate expiryDate;
    private double storageTempC;
    private static final double COLD_CHAIN_FLAT_FEE = 3.00;

    public PerishableItem(String sku, String name, double unitPrice, int qtyOnHand,
                          double weightKg, LocalDate expiryDate, double storageTempC) {
        super(sku, name, unitPrice, qtyOnHand, weightKg);
        this.expiryDate = expiryDate;
        this.storageTempC = storageTempC;
    }

    public LocalDate getExpiryDate()  { return expiryDate; }
    public double getStorageTempC()   { return storageTempC; }
    public void setExpiryDate(LocalDate d) { this.expiryDate = d; }
    public void setStorageTempC(double t)  { this.storageTempC = t; }

    public boolean isExpired() { return expiryDate.isBefore(LocalDate.now()); }

    public long daysToExpiry() { return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate); }

    @Override
    public boolean canStore() { return !isExpired(); }   // storage rule

    @Override
    public String pickPack(int qty) {                    // overriding
        if (isExpired()) throw new IllegalStateException(getSku() + " is expired and cannot be shipped");
        super.pickPack(qty);
        String warn = daysToExpiry() <= 3 ? " (WARNING: expires in " + daysToExpiry() + " days)" : "";
        return "PERISHABLE: insulated box + ice pack @ " + storageTempC + "C" + warn;
    }

    @Override
    public double handlingFee(int qty) { return 1.50 * qty + COLD_CHAIN_FLAT_FEE; }

    @Override
    public String storageRule() { return "Cold store " + storageTempC + "C, exp " + expiryDate; }
}
