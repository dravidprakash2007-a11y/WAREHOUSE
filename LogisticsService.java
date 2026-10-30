import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Coordinates inbound receiving, outbound shipments, tracking and billing. */
public class LogisticsService {
    // pricing constants (static variables)
    private static final double BASE_FEE = 5.00;
    private static final double RATE_PER_KG = 1.20;
    private static final double RATE_PER_KM = 0.05;
    private static final double MIN_CHARGE = 8.00;
    private static int nextShipNo = 1001;

    private final String serviceName;
    private final List<Warehouse> warehouses = new ArrayList<>();
    private final Map<String, Shipment> shipments = new LinkedHashMap<>();

    public LogisticsService(String serviceName) { this.serviceName = serviceName; }

    public String getServiceName() { return serviceName; }
    public void addWarehouse(Warehouse w) { warehouses.add(w); }

    public Warehouse getWarehouse(String whId) {
        for (Warehouse w : warehouses) if (w.getWhId().equalsIgnoreCase(whId)) return w;
        throw new IllegalArgumentException("Unknown warehouse " + whId);
    }

    public Shipment getShipment(String shipId) {
        Shipment s = shipments.get(shipId);
        if (s == null) throw new IllegalArgumentException("Unknown shipment " + shipId);
        return s;
    }

    // ---------------- inbound ----------------
    public void inbound(String whId, Item item, int qty) {
        getWarehouse(whId).receive(item, qty);
        System.out.printf("[INBOUND ] %d x %s -> %s%n", qty, item.getName(), whId);
    }

    // ---------------- outbound ----------------
    /** Picks every requested SKU (sku -> qty). All-or-nothing: stock is restored if any line fails. */
    public Shipment createShipment(String whId, String destination, String carrier,
                                   double distanceKm, Priority priority, Map<String, Integer> order) {
        Warehouse wh = getWarehouse(whId);
        Shipment s = new Shipment("SH-" + nextShipNo, destination, carrier, distanceKm, priority);
        List<Shipment.Line> picked = new ArrayList<>();
        try {
            for (Map.Entry<String, Integer> e : order.entrySet()) {
                Shipment.Line line = wh.pickPack(e.getKey(), e.getValue());
                picked.add(line);
                s.addLine(line);
            }
        } catch (RuntimeException ex) {
            for (Shipment.Line l : picked) l.getItem().receive(l.getQty());   // rollback
            throw ex;
        }
        nextShipNo++;
        shipments.put(s.getShipId(), s);
        System.out.printf("[OUTBOUND] Created %s to %s (%d lines, %.1f kg)%n",
                s.getShipId(), destination, s.getItems().size(), s.totalWeightKg());
        for (Shipment.Line l : s.getItems())
            System.out.printf("           - %d x %s: %s%n", l.getQty(), l.getItem().getName(), l.getHandlingNote());
        return s;
    }

    public void dispatch(String shipId) {
        Shipment s = getShipment(shipId);
        s.dispatch();
        System.out.println("[DISPATCH] " + s.trackingLine());
    }

    public void updateStatus(String shipId, ShipmentStatus next) {
        Shipment s = getShipment(shipId);
        s.updateStatus(next);
        System.out.println("[STATUS  ] " + s.trackingLine());
    }

    public String track(String shipId) { return getShipment(shipId).trackingLine(); }

    // ---------------- shipping cost (overloaded) ----------------
    /** Weight-only quote. */
    public double shippingCost(double weightKg) {
        return Math.max(MIN_CHARGE, weightKg * RATE_PER_KG);
    }

    /** Full quote: weight + distance + priority. */
    public double shippingCost(double weightKg, double distanceKm, Priority priority) {
        double base = BASE_FEE + weightKg * RATE_PER_KG + distanceKm * RATE_PER_KM;
        return Math.max(MIN_CHARGE, base) * priority.getMultiplier();
    }

    /** Polymorphism: each Item subtype supplies its own handling fee. */
    public double handlingCost(List<Item> items) {
        double total = 0;
        for (Item i : items) total += i.handlingFee(i.getQtyOnHand());
        return total;
    }

    // ---------------- billing ----------------
    public double billing(String shipId) {
        Shipment s = getShipment(shipId);
        double freight = shippingCost(s.totalWeightKg(), s.getDistanceKm(), s.getPriority());
        double handling = s.handlingCost();
        double goods = s.goodsValue();
        double total = freight + handling + goods;
        System.out.printf("%-8s freight $%8.2f | handling $%7.2f | goods $%9.2f | TOTAL $%9.2f%n",
                shipId, freight, handling, goods, total);
        return total;
    }

    public void printCostSummary() {
        System.out.println("=== Shipping cost summary ===");
        double grand = 0;
        for (String id : shipments.keySet()) {
            Shipment s = shipments.get(id);
            System.out.printf("%s [%s] %.1f kg, %.0f km, %s%n", id, s.getStatus(),
                    s.totalWeightKg(), s.getDistanceKm(), s.getPriority());
            System.out.print("   ");
            grand += billing(id);
        }
        System.out.printf("Grand total billed: $%.2f%n", grand);
    }

    public void printStockSnapshots() {
        for (Warehouse w : warehouses) w.printSnapshot();
    }
}
