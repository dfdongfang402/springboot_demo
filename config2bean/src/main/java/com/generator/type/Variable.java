package com.generator.type;

import org.w3c.dom.Element;

public class Variable extends Type {
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

	public final String getType() {
		return type;
	}

	public String[] getTablelist() {
		return tablelist;
	}

	/** 是不是Collection */
	public boolean isCollectionValue() {
		return !valueType.isEmpty();
	}

	public java.util.Map<String, String[]> getPrefixMapping() {
		return prefixMapping;
	}

	/** 变量名 */
	public String name;

	/** 变量的类型 */
	public String type;
	/** 初始值 */
	public String initValue;
	/** 注释 */
	public String comment;
	/** 从哪些表读？ */
	public String[] tablelist;

	public java.util.Map<String, String[]> prefixMapping = new java.util.HashMap<String, String[]>();
	/** 如果是Collection，容器内的值类型 */
	public String valueType;

	public String getMinValue() {
		return minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public String getRef() {
		return ref;
	}

	public String minValue;
	public String maxValue;
	public String ref;
	public String pattern;
	
	private void putPrefixMap(Element e) {
		String fromCol = e.getAttribute("fromCol");
		if (!fromCol.isEmpty()) {
			String[] p = fromCol.split("\\|");
			for (String s : p) {
				String[] s1 = s.split(":");
				if (s1.length == 2) {
					// prefix:var1,var2,var3这样的形式
					String[] strs = s1[1].split(",");
					for(int i = 0; i < strs.length;i++)
						strs[i] = strs[i].trim();
					prefixMapping.put(s1[0], strs);
				} else if (s1.length == 1) {
					// var1,var2,var3这样的形式
					String[] strs = s1[0].split(",");
					for(int i = 0; i < strs.length;i++)
						strs[i] = strs[i].trim();
					prefixMapping.put("", strs);
				} else {
					throw new RuntimeException("错误的值,fromcol=" + fromCol);
				}
			}
		} else {
			String prefix = e.getAttribute("prefix");	
			prefixMapping.put("", prefix.split(","));
		}
	}
	
	public TemplateVariable toJavaVariable() {
		return new TemplateVariable(this);
	}
	
	public static boolean isBaseType(String type) {
		if (type.equalsIgnoreCase("string")) {
			return true;
		} else if (type.equalsIgnoreCase("int")) {
			return true;
		} else if (type.equalsIgnoreCase("long")) {
			return true;
		} else if (type.equalsIgnoreCase("double")) {
			return true;
		} else if (type.equalsIgnoreCase("float")) {
			return true;
		} else if (type.equalsIgnoreCase("bool")) {
			return true;
		}
		return false;
	}
	
	public boolean isBaseType() {
		return isBaseType(type);
	}
	
	public Variable(Element e) {
		name = e.getAttribute("name");
		minValue = e.getAttribute("min");
		maxValue = e.getAttribute("max");
		ref = e.getAttribute("ref");
		initValue = e.getAttribute("default");
		type = e.getAttribute("type");
		pattern = e.getAttribute("pattern");
		if (pattern == null)
			pattern = "";
		putPrefixMap(e);
		if (type.equalsIgnoreCase("vector")
				|| type.equalsIgnoreCase("set")) {
			// 这是一个容器
			valueType = e.getAttribute("value").trim();
		}
		{
			String comment = "";
			org.w3c.dom.Node nextnode = e.getNextSibling();
			if (nextnode != null
					&& org.w3c.dom.Node.TEXT_NODE == nextnode.getNodeType()) {
				comment = nextnode.getTextContent().trim()
						.replaceAll("[\r\n]", "");
			}
			this.comment = comment;
		}
		
	}

//	void getReferenceBean(String dir, List<Bean> references) {
//		if (!type.equals("String") && !type.equals("int") &&
//				!type.equals("long") && !type.equals("double") && 
//				!type.equals("float") && !type.equals("boolean")) {
//			if (type.startsWith("java.util.ArrayList") || type.startsWith("java.util.TreeSet")) {
//				if (!valueType.equalsIgnoreCase("Integer")
//						&& !valueType.equalsIgnoreCase("int")
//						&& !valueType.equalsIgnoreCase("String")
//						&& !valueType.equalsIgnoreCase("Long")
//						&& !valueType.equalsIgnoreCase("Double")
//						&& !valueType.equalsIgnoreCase("float")
//						&& !valueType.equalsIgnoreCase("boolean")) {
//					Bean bean = BeanNameSpace.getBean(valueType);
//					if (bean == null) {
//						bean = BeanNameSpace.getBean(dir + '.' + valueType);
//					}
//					references.add(bean);
//					references.addAll(bean.getReferenceBeans());
//				}
//			} else {
//				Bean bean = BeanNameSpace.getBean(type);
//				if (bean == null)
//					Main.logger.debug("error find type=" + type);
//				references.add(bean);
//			}
//		}
//	}
}
