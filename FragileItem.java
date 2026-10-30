/** Fragile goods: padded packing, higher handling fee, bounded stacking. */
public class FragileItem extends Item {
    private int fragilityLevel;      // 1 (delicate) .. 5 (extremely fragile)
    private int maxStackHeight;

    public FragileItem(String sku, String name, double unitPrice, int qtyOnHand,
                       double weightKg, int fragilityLevel, int maxStackHeight) {
        super(sku, name, unitPrice, qtyOnHand, weightKg);
        setFragilityLevel(fragilityLevel);
        this.maxStackHeight = maxStackHeight;
    }

    public int getFragilityLevel() { return fragilityLevel; }
    public int getMaxStackHeight() { return maxStackHeight; }

    public void setFragilityLevel(int level) {
        if (level < 1 || level > 5) throw new IllegalArgumentException("Fragility must be 1-5");
        this.fragilityLevel = level;
    }

    @Override
    public String pickPack(int qty) {                    // overriding
        super.pickPack(qty);
        return "FRAGILE: bubble wrap + foam corners (level " + fragilityLevel + "), max stack " + maxStackHeight;
    }

    @Override
    public double handlingFee(int qty) { return (1.00 + 0.75 * fragilityLevel) * qty; }

    @Override
    public String storageRule() { return "Fragile - max stack " + maxStackHeight; }
}
