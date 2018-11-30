package com.generator.utils;

public class BeanField {
	public String fieldtype;
	public String fieldtypeE;
	public String fieldname;

	@Override
	public String toString() {
		if (fieldtype.equals("vector")) {
			return "Vector<" + getType(fieldtypeE) + "> " + fieldname + " = new Vector<" + getType(fieldtypeE) + ">();";
		}
		return getType(fieldtype) + " " + fieldname + " = null;";
	}

	private String getType(String type) {
		String ret = "";
		if (type.equals("string")) {
			ret = "String";
		} else if (type.equals("int")) {
			ret = "Integer";
		} else if (type.equals("long")) {
			ret = "Long";
		} else if (type.equals("float")) {
			ret = "Float";
		} else if (type.equals("double")) {
			ret = "Double";
		} else if (type.equals("bool")) {
			ret = "Boolean";
		}
		return ret;
	}
}