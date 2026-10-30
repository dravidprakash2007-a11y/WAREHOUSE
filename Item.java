/**
 * Base stock item. qtyOnHand is private and only changed through guarded methods.
 * Subclasses override pickPack(), handlingFee() and canStore()/storageRule().
 */
public class Item {
    private static int itemsCreated = 0;          // static variable

    private final String sku;
    private String name;
    private double unitPrice;
    private int qtyOnHand;
    private double weightKg;

    public Item(String sku, String name, double unitPrice, int qtyOnHand, double weightKg) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU required");
        if (unitPrice < 0 || qtyOnHand < 0 || weightKg <= 0)
            throw new IllegalArgumentException("Invalid price, quantity or weight for " + sku);
        this.sku = sku;
        this.name = name;
        this.unitPrice = unitPrice;
        this.qtyOnHand = qtyOnHand;
        this.weightKg = weightKg;
        itemsCreated++;
    }

    // ---------- getters / setters ----------
    public String getSku()        { return sku; }
    public String getName()       { return name; }
    public double getUnitPrice()  { return unitPrice; }
    public int getQtyOnHand()     { return qtyOnHand; }
    public double getWeightKg()   { return weightKg; }
    public static int getItemsCreated() { return itemsCreated; }

    public void setName(String name) { this.name = name; }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.unitPrice = unitPrice;
    }

    /** Guarded setter: stock can never go negative. */
    public void setQtyOnHand(int qty) {
        if (qty < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        this.qtyOnHand = qty;
    }

    // ---------- stock operations ----------
    /** Adds stock (used by receiving). */
    public void receive(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Receive quantity must be positive");
        qtyOnHand += qty;
    }

    /**
     * Removes stock for a shipment and returns a handling note.
     * Overridden by FragileItem and PerishableItem.
     */
    public String pickPack(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Pick quantity must be positive");
        if (qty > qtyOnHand)
            throw new IllegalStateException("Insufficient stock for " + sku + ": need " + qty + ", have " + qtyOnHand);
        qtyOnHand -= qty;
        return "Standard box pack";
    }

    // ---------- polymorphic behaviour ----------
    /** Handling fee for qty units; overridden by subclasses. */
    public double handlingFee(int qty) { return 0.50 * qty; }

    /** Whether the item may be accepted into storage right now. */
    public boolean canStore() { return true; }

    public String storageRule() { return "Ambient shelf"; }

    public double stockValue() { return unitPrice * qtyOnHand; }

    @Override
    public String toString() {
        return String.format("%-8s %-18s qty=%-4d $%8.2f  [%s]", sku, name, qtyOnHand, unitPrice, storageRule());
    }
}
