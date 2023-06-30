package Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomHashMap<KeyType, ValueType> {
	
	HashMap<KeyType, ValueType> thisMap;
	HashMap<KeyType, CustomHashMap<KeyType, ValueType>> childsHash;
	private int NumberOfIndexes;
	public int valuesSet = 0;
	
	public CustomHashMap(int NumberOfIndexes) {
		
		this.NumberOfIndexes = NumberOfIndexes;
		if(NumberOfIndexes == 1) {
			thisMap = new HashMap<KeyType, ValueType>();
		} else {
			childsHash = new HashMap<KeyType, CustomHashMap<KeyType, ValueType>>();
		}
	}
	
	public void setValue(ValueType value, KeyType... keys) {
		List<KeyType> keysList= new ArrayList<KeyType>();
  		Collections.addAll(keysList, keys);
  		
		this.setValue(value, keysList);
	}
	
	public void setValue(ValueType value, List<KeyType> keys) {
		if(keys.size() != NumberOfIndexes) {
			throw new IllegalArgumentException();
		}
		
		valuesSet++;
		if(NumberOfIndexes == 1) {
			this.thisMap.put(keys.get(0), value);
			
		} else {
			KeyType thisKey = keys.remove(0);
			
			if(!this.childsHash.containsKey(thisKey)) {
				createNewChild(thisKey);
			}
			
			this.childsHash.get(thisKey).setValue(value, keys);
			
		}
	}
	
	private CustomHashMap<KeyType, ValueType> createNewChild(KeyType key) {
		CustomHashMap<KeyType, ValueType> newMap = new CustomHashMap<KeyType, ValueType>(NumberOfIndexes - 1);
		this.childsHash.put(key, newMap);
		
		return newMap;
	}
	
	public ValueType getValue(KeyType... keys) {
		List<KeyType> keysList= new ArrayList<KeyType>();
  		Collections.addAll(keysList, keys);
  		
		return this.getValue(keysList);
	}
	
	public ValueType getValue(List<KeyType> keys) {
		if(keys.size() != NumberOfIndexes) {
			throw new IllegalArgumentException();
		}
		
		if(NumberOfIndexes == 1) {
			return this.thisMap.get(keys.get(0));
			
		} else {
			KeyType thisKey = keys.remove(0);
			
			CustomHashMap<KeyType, ValueType> childHashMap = this.childsHash.get(thisKey);
			
			if(childHashMap == null)
				return null;
			
			return childHashMap.getValue(keys);
		}
	}
	
	public boolean contains(KeyType... keys) {
		List<KeyType> keysList= new ArrayList<KeyType>();
  		Collections.addAll(keysList, keys);
  		
		return this.contains(keysList);
	}
	
	public boolean contains(List<KeyType> keys) {
		if(keys.size() != NumberOfIndexes) {
			throw new IllegalArgumentException();
		}
		
		if(NumberOfIndexes == 1) {
			return this.thisMap.containsKey(keys.get(0));
			
		} else {
			KeyType thisKey = keys.remove(0);
			
			if(!this.childsHash.containsKey(thisKey))
				return false;
			
			CustomHashMap<KeyType, ValueType> childHashMap = this.childsHash.get(thisKey);
			
			
			return childHashMap.contains(keys);
		}
	}

	public void clear() {
		if(NumberOfIndexes == 1) {
			thisMap = new HashMap<KeyType, ValueType>();
		} else {
			childsHash = new HashMap<KeyType, CustomHashMap<KeyType, ValueType>>();
		}
	}
	

}
