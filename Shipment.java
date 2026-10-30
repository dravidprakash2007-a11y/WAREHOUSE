import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** An outbound shipment with a guarded, forward-only status lifecycle. */
public class Shipment {

    /** One picked line: item, quantity and the handling note produced by pickPack(). */
    public static class Line {
        private final Item item;
        private final int qty;
        private final String handlingNote;

        public Line(Item item, int qty, String handlingNote) {
            this.item = item; this.qty = qty; this.handlingNote = handlingNote;
        }
        public Item getItem()          { return item; }
        public int getQty()            { return qty; }
        public String getHandlingNote(){ return handlingNote; }
        public double weightKg()       { return item.getWeightKg() * qty; }
        public double value()          { return item.getUnitPrice() * qty; }
        public double handlingFee()    { return item.handlingFee(qty); }   // polymorphic
    }

    private final String shipId;
    private String destination;
    private final List<Line> items = new ArrayList<>();
    private ShipmentStatus status = ShipmentStatus.CREATED;
    private String carrier;
    private double distanceKm;
    private Priority priority;
    private final List<String> history = new ArrayList<>();

    public Shipment(String shipId, String destination, String carrier, double distanceKm, Priority priority) {
        this.shipId = shipId;
        this.destination = destination;
        this.carrier = carrier;
        this.distanceKm = distanceKm;
        this.priority = priority;
        history.add("CREATED");
    }

    public String getShipId()           { return shipId; }
    public String getDestination()      { return destination; }
    public String getCarrier()          { return carrier; }
    public double getDistanceKm()       { return distanceKm; }
    public Priority getPriority()       { return priority; }
    public ShipmentStatus getStatus()   { return status; }
    public List<Line> getItems()        { return Collections.unmodifiableList(items); }
    public List<String> getHistory()    { return Collections.unmodifiableList(history); }

    public void setDestination(String d) { requireCreated(); this.destination = d; }
    public void setCarrier(String c)     { requireCreated(); this.carrier = c; }

    private void requireCreated() {
        if (status != ShipmentStatus.CREATED)
            throw new IllegalStateException("Shipment " + shipId + " can no longer be edited (" + status + ")");
    }

    public void addLine(Line line) { requireCreated(); items.add(line); }

    public double totalWeightKg() {
        double w = 0; for (Line l : items) w += l.weightKg(); return w;
    }
    public double goodsValue() {
        double v = 0; for (Line l : items) v += l.value(); return v;
    }
    /** Sum of each item's own handling rule. */
    public double handlingCost() {
        double c = 0; for (Line l : items) c += l.handlingFee(); return c;
    }

    /** Only legal forward transitions are accepted. */
    public void updateStatus(ShipmentStatus next) {
        if (!status.canMoveTo(next))
            throw new IllegalStateException("Illegal status change " + status + " -> " + next + " for " + shipId);
        status = next;
        history.add(next.name());
    }

    /** Hands the shipment to the carrier. */
    public void dispatch() {
        if (items.isEmpty()) throw new IllegalStateException("Cannot dispatch empty shipment " + shipId);
        if (carrier == null || carrier.isBlank()) throw new IllegalStateException("No carrier assigned to " + shipId);
        updateStatus(ShipmentStatus.IN_TRANSIT);
    }

    public String trackingLine() {
        return String.format("%s -> %s via %s [%s] trail: %s", shipId, destination, carrier, status, String.join(" > ", history));
    }
}
