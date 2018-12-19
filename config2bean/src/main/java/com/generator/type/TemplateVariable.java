package com.generator.type;

public class TemplateVariable {
	public String name;
	public String minValue;
	public String maxValue;
	public String ref;
	public String initValue;
	public String valueType;
	public String type;
	public String comment;
	public String pattern;
	public java.util.Map<String, String[]> prefixMapping;
	public String[] tablelist;
	private void cast(String type) {
		if (type.equalsIgnoreCase("string")) {
			if (initValue.equals(""))
				initValue = "null";
			this.type = "String";
		} else if (type.equalsIgnoreCase("int")) {
			if (initValue.equals(""))
				initValue = "0";
			this.type = "int";
		} else if (type.equalsIgnoreCase("long")) {
			if (initValue.equals(""))
				initValue = "0L";
			else
				initValue += 'L';
			this.type = "long";
		} else if (type.equalsIgnoreCase("double")) {
			if (initValue.equals(""))
				initValue = "0.0";
			this.type = "double";
		} else if (type.equalsIgnoreCase("float")) {
			if (initValue.equals(""))
				initValue = "0.0f";
			else
				initValue += 'f';
			this.type = "float";
		} else if (type.equalsIgnoreCase("bool")) {
			if (initValue.equals(""))
				initValue = "false";
			this.type = "boolean";
		} else if (type.equalsIgnoreCase("vector")
				|| type.equalsIgnoreCase("set")) {
			// 这是一个容器
			if (valueType.equalsIgnoreCase("Integer")
					|| valueType.equalsIgnoreCase("int")) {
				this.valueType = "Integer";
			} else if (valueType.equalsIgnoreCase("String")) {
				this.valueType = "String";
			} else if (valueType.equalsIgnoreCase("Long")) {
				this.valueType = "Long";
			} else if (valueType.equalsIgnoreCase("double")) {
				this.valueType = "Double";
			} else if (valueType.equalsIgnoreCase("float")) {
				this.valueType = "Float";
			} else if (valueType.equalsIgnoreCase("bool")) {
				this.valueType = "Boolean";
			}
			if (type.equalsIgnoreCase("vector")) {
				this.type = "java.util.ArrayList<" + this.valueType + ">";
			} else if (type.equalsIgnoreCase("set")) {
				this.type = "java.util.TreeSet<" + this.valueType + ">";
			}
			initValue = "new " + this.type + "()";
		} else {
			throw new RuntimeException("不认识的类型,id=" + type);
		}
	}
	
	public TemplateVariable(Variable v) {
		name = v.name;
		minValue = v.minValue;
		maxValue = v.maxValue;
		ref = v.ref;
		initValue = v.initValue;
		valueType = v.valueType;
		comment = v.comment;
		prefixMapping = v.prefixMapping;
		pattern = v.pattern;
		cast(v.type);
	}
	
	public String getValueType() {
		return valueType;
	}

	public final String getComment() {
		return comment;
	}

	public final String getInitValue() {
		return initValue;
	}

	public final String getName() {
		return name;
	}
	
	public String getMinValue() {
		return minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public String getRef() {
		return ref;
	}

	public final String getType() {
		return type;
	}
	
	public String getPattern() {
		return pattern;
	}

	/** 是不是Collection */
	public boolean isCollectionValue() {
		return !valueType.isEmpty();
	}

	public java.util.Map<String, String[]> getPrefixMapping() {
		return prefixMapping;
	}
}
