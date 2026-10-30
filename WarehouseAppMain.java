import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WarehouseAppMain {

    public static void main(String[] args) {
        LogisticsService svc = new LogisticsService("SwiftLog");
        svc.addWarehouse(new Warehouse("WH-01", "Chicago", 500));
        svc.addWarehouse(new Warehouse("WH-02", "Dallas", 60));

        banner("1. RECEIVE INVENTORY");
        svc.inbound("WH-01", new Item("LAP-100", "Laptop", 899.99, 0, 2.2), 40);
        svc.inbound("WH-01", new FragileItem("VAS-200", "Crystal Vase", 120.00, 0, 1.5, 4, 2), 25);
        svc.inbound("WH-01", new PerishableItem("MLK-300", "Organic Milk", 3.49, 0, 1.0,
                LocalDate.now().plusDays(10), 4.0), 100);
        svc.inbound("WH-01", new Item("LAP-100", "Laptop", 899.99, 0, 2.2), 10);   // merges into existing SKU

        tryIt("Expired perishable", () ->
                svc.inbound("WH-01", new PerishableItem("YOG-400", "Yogurt", 1.99, 0, 0.3,
                        LocalDate.now().minusDays(2), 4.0), 30));
        tryIt("Over-capacity receipt", () ->
                svc.inbound("WH-02", new Item("BOX-500", "Cardboard Pack", 0.80, 0, 0.5), 200));

        banner("2. STOCK SNAPSHOT (before shipping)");
        svc.printStockSnapshots();

        banner("3. CREATE SHIPMENTS");
        Map<String, Integer> order1 = new LinkedHashMap<>();
        order1.put("LAP-100", 5);
        order1.put("VAS-200", 10);
        order1.put("MLK-300", 20);
        Shipment s1 = svc.createShipment("WH-01", "Seattle", "FastFreight", 2800, Priority.EXPRESS, order1);

        Map<String, Integer> order2 = new LinkedHashMap<>();
        order2.put("LAP-100", 2);
        Shipment s2 = svc.createShipment("WH-01", "Denver", "RoadRunner", 1500, Priority.STANDARD, order2);

        Map<String, Integer> bad = new LinkedHashMap<>();
        bad.put("LAP-100", 1);
        bad.put("VAS-200", 999);   // not enough stock -> whole order rolls back
        tryIt("Oversized order", () ->
                svc.createShipment("WH-01", "Miami", "FastFreight", 2000, Priority.OVERNIGHT, bad));

        banner("4. DISPATCH & TRACK");
        tryIt("Skip IN_TRANSIT", () -> svc.updateStatus(s1.getShipId(), ShipmentStatus.DELIVERED));
        svc.dispatch(s1.getShipId());
        svc.dispatch(s2.getShipId());
        svc.updateStatus(s1.getShipId(), ShipmentStatus.DELIVERED);
        tryIt("Edit after dispatch", () -> s2.setDestination("Boulder"));
        System.out.println("Track: " + svc.track(s1.getShipId()));
        System.out.println("Track: " + svc.track(s2.getShipId()));

        banner("5. STOCK SNAPSHOT (after shipping)");
        svc.printStockSnapshots();

        banner("6. SHIPPING COSTS");
        System.out.printf("Quote, 10 kg weight-only:          $%.2f%n", svc.shippingCost(10));
        System.out.printf("Quote, 10 kg / 1000 km / STANDARD: $%.2f%n", svc.shippingCost(10, 1000, Priority.STANDARD));
        System.out.printf("Quote, 10 kg / 1000 km / OVERNIGHT:$%.2f%n", svc.shippingCost(10, 1000, Priority.OVERNIGHT));
        List<Item> stock = svc.getWarehouse("WH-01").getInventory();
        System.out.printf("Handling to process all remaining WH-01 stock (polymorphic): $%.2f%n", svc.handlingCost(stock));
        System.out.println();
        svc.printCostSummary();
    }

    private static void banner(String t) { System.out.println("\n===== " + t + " ====="); }

    private static void tryIt(String label, Runnable r) {
        try { r.run(); System.out.println("[!] " + label + ": unexpectedly succeeded"); }
        catch (RuntimeException e) { System.out.println("[BLOCKED ] " + label + " -> " + e.getMessage()); }
    }
}
