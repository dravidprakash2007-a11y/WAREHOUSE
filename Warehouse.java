import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A warehouse with a unit-capacity limit and an inventory list (kept private). */
public class Warehouse {
    private final String whId;
    private String location;
    private int capacity;
    private final List<Item> inventory = new ArrayList<>();

    public Warehouse(String whId, String location, int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.whId = whId;
        this.location = location;
        this.capacity = capacity;
    }

    public String getWhId()     { return whId; }
    public String getLocation() { return location; }
    public int getCapacity()    { return capacity; }
    public void setLocation(String location) { this.location = location; }

    public void setCapacity(int capacity) {
        if (capacity < usedCapacity()) throw new IllegalArgumentException("Capacity below current stock");
        this.capacity = capacity;
    }

    /** Read-only view: callers cannot add or remove items behind our back. */
    public List<Item> getInventory() { return Collections.unmodifiableList(inventory); }

    public int usedCapacity() {
        int total = 0;
        for (Item i : inventory) total += i.getQtyOnHand();
        return total;
    }

    public int freeCapacity() { return capacity - usedCapacity(); }

    public Item findItem(String sku) {
        for (Item i : inventory) if (i.getSku().equalsIgnoreCase(sku)) return i;
        return null;
    }

    /** Receive stock; merges into an existing SKU if present. */
    public void receive(Item item, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Receive quantity must be positive");
        if (!item.canStore())
            throw new IllegalStateException("Rejected " + item.getSku() + ": fails storage rule (" + item.storageRule() + ")");
        if (qty > freeCapacity())
            throw new IllegalStateException("Warehouse " + whId + " over capacity: free=" + freeCapacity() + ", incoming=" + qty);
        Item existing = findItem(item.getSku());
        if (existing != null) {
            existing.receive(qty);
        } else {
            item.receive(qty);
            inventory.add(item);
        }
    }

    /** Picks and packs stock; the item's own (overridden) pickPack decides the handling. */
    public Shipment.Line pickPack(String sku, int qty) {
        Item item = findItem(sku);
        if (item == null) throw new IllegalArgumentException("Unknown SKU " + sku + " in " + whId);
        String note = item.pickPack(qty);                    // polymorphic call
        return new Shipment.Line(item, qty, note);
    }

    public double inventoryValue() {
        double v = 0;
        for (Item i : inventory) v += i.stockValue();
        return v;
    }

    public void printSnapshot() {
        System.out.printf("--- Stock snapshot: %s (%s) used %d/%d ---%n", whId, location, usedCapacity(), capacity);
        if (inventory.isEmpty()) System.out.println("  (empty)");
        for (Item i : inventory) System.out.println("  " + i);
        System.out.printf("  Inventory value: $%.2f%n", inventoryValue());
    }
}
