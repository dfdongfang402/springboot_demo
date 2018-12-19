package com.generator.utils;

import java.lang.reflect.Field;
import java.util.Collection;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FromXml {
	public static void fromXml(Object obj, Node node) {
		Field[] fields = obj.getClass().getDeclaredFields();
		try {
			for (Field field : fields) {

				if (field.isAnnotationPresent(IsSaveToFile.class)) {
					field.setAccessible(true);
					if (isComplexType(field.getType())) {
						pushComplexField(field, obj, node);
						continue;
					}
					pushSimpleField(field, obj, node);
				}
			}
		} catch (SecurityException e) {
			System.out.println("toBIN :::::::::::::::: SecurityException");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("toBIN :::::::::::::::: IllegalArgumentException");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println("toBIN :::::::::::::::: IllegalAccessException");
			e.printStackTrace();
		}
	}

	/**
	 * 鍒ゆ柇鏄惁涓篖ist鎴栬�匰et绫诲瀷
	 */
	private static boolean isComplexType(Class<? extends Object> obj) {
		if (Collection.class.isAssignableFrom(obj))
			return true;
		return false;
	}

	private static void pushSimpleField(Field field, Object obj, Node node) throws IllegalArgumentException, IllegalAccessException {
		Class<?> type = field.getType();
		String name = field.getName();
		Node item = node.getAttributes().getNamedItem(name);
		if (item == null) {
			return;
		}
		String nodeValue = item.getNodeValue();

		if (type == Integer.class || type == int.class) {
			field.set(obj, Integer.valueOf(nodeValue));
			return;
		}
		if (type == Long.class || type == long.class) {
			field.set(obj, Long.valueOf(nodeValue));
			return;
		}
		if (type == Boolean.class || type == boolean.class) {
			field.set(obj, Boolean.valueOf(nodeValue));
			return;
		}
		if (type == Float.class || type == float.class) {
			field.set(obj, Float.valueOf(nodeValue));
			return;
		}
		if (type == Double.class || type == double.class) {
			field.set(obj, Double.valueOf(nodeValue));
			return;
		}
		if (type == String.class) {
			field.set(obj, String.valueOf(nodeValue));
			return;
		}
		System.out.println("Error锛氭棤娉曚粠xml鏂囦欢涓鍙栫被鍨嬩负" + type.toString() + " 鐨�  " + name);
	}

	private static void pushComplexField(Field field, Object obj, Node node) throws IllegalArgumentException, IllegalAccessException {
		Class<?> type = field.getType();
		String name = field.getName();
		NodeList childNodes = node.getChildNodes();
		Node childNode = null;
		if (childNodes != null) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node tnode = childNodes.item(i);
				if (tnode.getNodeType() == Node.ELEMENT_NODE) {
					if (tnode.getNodeName().equals(name)) {
						childNode = tnode;
						break;
					}
				}
			}
		}
		if (childNode == null) {
			return;
		}
		if (Collection.class.isAssignableFrom(type)) {
			Collection<Object> collection = (Collection<Object>) field.get(obj);
			NodeList nodes = childNode.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node tnode = nodes.item(i);
				if (tnode.getNodeType() == Node.ELEMENT_NODE) {
					collection.add(getNodeValue(tnode));
				}
			}
			return;
		}
		// if (Set.class.isAssignableFrom(id)) {
		// Set<Object> set = (Set<Object>) field.get(obj);
		// NodeList nodes = childNode.getChildNodes();
		// for (int i = 0; i < nodes.getLength(); i++) {
		// Node tnode = nodes.item(i);
		// if (tnode.getNodeType() != Node.ELEMENT_NODE) {
		// set.add(getNodeValue(tnode));
		// }
		// }
		// return;
		// }
	}

	private static Object getNodeValue(Node node) {
		String type = node.getNodeName().toLowerCase();
		String value = node.getTextContent();
		if (type.equals("string")) {
			return value;
		} else if (type.equals("int")) {
			return Integer.valueOf(value);
		} else if (type.equals("long")) {
			return Long.valueOf(value);
		} else if (type.equals("float")) {
			return Float.valueOf(value);
		} else if (type.equals("double")) {
			return Double.valueOf(value);
		} else if (type.equals("bool")) {
			return Boolean.valueOf(value);
		}
		System.err.println("瀹瑰櫒鍐呯被鍨嬫棤娉曡瘑鍒紒");
		System.exit(0);
		return null;
	}
}
