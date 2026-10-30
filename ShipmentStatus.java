/** Shipment lifecycle. Order of constants defines the only legal progression. */
public enum ShipmentStatus {
    CREATED, IN_TRANSIT, DELIVERED;

    /** A status may only move to the one directly after it. */
    public boolean canMoveTo(ShipmentStatus next) {
        return next.ordinal() == this.ordinal() + 1;
    }
}
