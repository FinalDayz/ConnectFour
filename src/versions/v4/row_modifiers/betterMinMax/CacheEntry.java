package versions.v4.row_modifiers.betterMinMax;

public class CacheEntry {

    public static byte TYPE_UPPER_BOUND = 1;
    public static byte TYPE_LOWER_BOUND = 2;
    public static byte TYPE_EXACT = 3;

    public byte type;
    public float value;
}
