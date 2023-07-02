package versions.base.betterMinMax;

import java.util.HashMap;

public class DoubleHashMap<KeyType, ValueType> {
    private HashMap<KeyType, HashMap<KeyType, ValueType>> topMap;

    public DoubleHashMap() {
        clear();
    }

    public void clear() {
        topMap = new HashMap<>();
    }

    public ValueType get(KeyType index1, KeyType index2) {
        HashMap<KeyType, ValueType> childMap = topMap.get(index1);
        if(childMap == null) {
            return null;
        }

        return childMap.get(index2);
    }

    public void put(KeyType i, KeyType j, ValueType value) {

        if(!topMap.containsKey(i)) {
            HashMap<KeyType, ValueType> childMap = new HashMap<KeyType, ValueType>();
            childMap.put(j, value);
            topMap.put(i, childMap);
            return;
        }

        topMap.get(i).put(j, value);
    }

    public boolean contains(KeyType index1, KeyType index2) {
        boolean containsTop = topMap.containsKey(index1);
        if(!containsTop) {
            return false;
        }

        return topMap.get(index1).containsKey(index2);
    }
}
