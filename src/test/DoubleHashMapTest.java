package test;

import betterMinMax.DoubleHashMap;

public class DoubleHashMapTest extends Test {
    		
	public void executeTests() {
        testSimpleOneValue();
        testMultipleOneValue();
	}

    public void testSimpleOneValue() {
        DoubleHashMap<Long, Integer> doubleHashMap = new DoubleHashMap<>();

        equals(doubleHashMap.contains(1L, 2L), false);

        doubleHashMap.put(1L, 2L, 10);

        equals(doubleHashMap.get(1L, 2L), 10);
        equals(doubleHashMap.contains(2L, 1L), false);
    }

    public void testMultipleOneValue() {
        DoubleHashMap<Long, Integer> doubleHashMap = new DoubleHashMap<>();

        doubleHashMap.put(5L, 3L, 15);
        doubleHashMap.put(10L, 10L, 100);
        doubleHashMap.put(2L, 2L, 4);
        doubleHashMap.put(20L, 2L, 40);

        equals(doubleHashMap.contains(5L, 3L), true);
        equals(doubleHashMap.contains(10L, 10L), true);
        equals(doubleHashMap.contains(2L, 2L), true);
        equals(doubleHashMap.contains(20L, 2L), true);

        equals(doubleHashMap.get(5L, 3L), 15);
        equals(doubleHashMap.get(10L, 10L), 100);
        equals(doubleHashMap.get(2L, 2L), 4);
        equals(doubleHashMap.get(20L, 2L), 40);

        doubleHashMap.clear();

        equals(doubleHashMap.contains(5L, 3L), false);
        equals(doubleHashMap.contains(10L, 10L), false);
        equals(doubleHashMap.contains(2L, 2L), false);
        equals(doubleHashMap.contains(20L, 2L), false);
    }
}
