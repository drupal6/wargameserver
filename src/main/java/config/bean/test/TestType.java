package config.bean.test;

import java.util.HashMap;
import java.util.Map;

public enum TestType {
	TEST1,
	TEST2,
	TEST3,
	TEST4,
	;
	
	private static Map<Integer, TestType> ordinalMap = new HashMap<Integer, TestType>();
	
	static {
		for(TestType type : values()) {
			ordinalMap.put(type.ordinal(), type);
		}
	}
	
	public static TestType ordinalValue(int ordinal) {
		return ordinalMap.get(ordinal);
	}
}
