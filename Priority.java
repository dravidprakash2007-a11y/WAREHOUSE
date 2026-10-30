/** Delivery priority with a price multiplier. */
public enum Priority {
    STANDARD(1.0), EXPRESS(1.6), OVERNIGHT(2.5);

    private final double multiplier;

    Priority(double multiplier) { this.multiplier = multiplier; }

    public double getMultiplier() { return multiplier; }
}
