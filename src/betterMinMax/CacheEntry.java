package betterMinMax;

import java.io.Serializable;

public class CacheEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    public static byte TYPE_UPPER_BOUND = 1;
    public static byte TYPE_LOWER_BOUND = 2;
    public static byte TYPE_EXACT = 3;

    public byte type;
    public float value;
}
