package com.generator.utils;

import java.util.ArrayList;
import java.util.List;

import com.generator.Config;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BeanInfo {
	public static void mkClientBean(Node node, String path) {
		Node genxml = node.getAttributes().getNamedItem("genxml");
		if (genxml == null || !genxml.getNodeValue().equals("client")) {
			return;
		}
		String classname = node.getAttributes().getNamedItem("name").getNodeValue();
		// 添加这个bean到全局Environment
		// Config.addBean(path.replaceAll("/", ".") + "." + classname);
		BeanInfo bean = new BeanInfo(classname, path);
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node tnode = childNodes.item(i);
			if (tnode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (tnode.getNodeName().equals("variable")) {
				BeanField field = new BeanField();
				NamedNodeMap attributes = tnode.getAttributes();
				field.fieldtype = attributes.getNamedItem("id").getNodeValue().toLowerCase();
				field.fieldname = attributes.getNamedItem("name").getNodeValue();
				if (attributes.getNamedItem("value") != null) {
					field.fieldtypeE = attributes.getNamedItem("value").getNodeValue().toLowerCase();
				}
				bean.addField(field);
			}
		}
		Config.addBean(bean);
	}

	public BeanInfo(String name, String packageName) {
		this.className = name;
		this.path = packageName;
	}

	public boolean addField(BeanField field) {
		return this.fields.add(field);
	}

	public String getPath() {
		return path;
	}

	public List<BeanField> getFields() {
		return fields;
	}

	public String getClassName() {
		return className;
	}

	private String path;
	private String className;
	private List<BeanField> fields = new ArrayList<BeanField>();
}