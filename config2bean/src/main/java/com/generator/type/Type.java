package com.generator.type;

public abstract class Type {
	public static boolean isBaseType(String type) {
		return type.equalsIgnoreCase("string") 
				|| type.equalsIgnoreCase("int")
				|| type.equalsIgnoreCase("long")
				|| type.equalsIgnoreCase("double")
				|| type.equalsIgnoreCase("bool")
				|| type.equalsIgnoreCase("float");
	}
	
	
}
