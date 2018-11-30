package com.generator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.xpath.XPathExpressionException;


public class ClassNameUtil {

	static private Logger logger = LoggerFactory.getLogger(ClassNameUtil.class);
	public static String xmlpathToNS(org.w3c.dom.Element e) {
		String ns = null;
		
		//从下往上，根据xml路径，找到对应的namespace
		while (e != null) {
			if (e.getNodeName().equals("bean")) {
				ns = e.getAttribute("name");
				if(ns.isEmpty()) 
					throw new RuntimeException("name==null");
			}
			else if (e.getNodeName().equals("namespace")) {
				if(ns==null) throw new RuntimeException("name==null");
				String namespace_name=e.getAttribute("name");
				if(namespace_name.isEmpty()) throw new RuntimeException("name==null,ns="+ns);
				ns = namespace_name.concat(".").concat(ns);
			}
			
			//找到根节点，就停止
			short parentType=e.getParentNode().getNodeType();
			if(parentType==org.w3c.dom.Node.DOCUMENT_NODE) break;
			else if(parentType==org.w3c.dom.Node.ELEMENT_NODE)
				e = (org.w3c.dom.Element)e.getParentNode();
			else return null;
		}
		return ns;
	}

	private final String classname;
	private final String[] tmp;

	public ClassNameUtil(String classname) {
		super();
		this.classname = classname;
		this.tmp = classname.split("\\.");
	}

	public String getDir() {
		final StringBuilder sb = new StringBuilder(tmp[0]);
		for (int i = 1; i != tmp.length; ++i)
			sb.append("/").append(tmp[i]);
		return sb.toString();
	}

	public String getShortName() {
		return tmp[tmp.length - 1];
	}
	public String getClassnameString() {
		return classname;
	}

	public String getNamespace() {
		return classname.substring(0, classname.lastIndexOf("."));
	}
	public String[] getNamespaceArray() {
		return getNamespace().split("\\.");
	}

	public static org.w3c.dom.Element getElementByClassName(String classname,
			org.w3c.dom.Element varElement) {
		javax.xml.xpath.XPathFactory xpathFactory = javax.xml.xpath.XPathFactory.newInstance();
		final String[] tmp = classname.split("\\.");
		if (tmp.length < 3) return null;
		final StringBuilder sb = new StringBuilder("/");
		for (int i = 0; i != tmp.length - 1; ++i)
			sb.append("/namespace[@name='").append(tmp[i]).append("']");
		sb.append("/bean[@name='").append(tmp[tmp.length - 1]).append("']");
		try {
			return (org.w3c.dom.Element) xpathFactory.newXPath().evaluate(sb.toString(),
					varElement.getOwnerDocument(), javax.xml.xpath.XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			logger.error("err", e);
			return null;
		}
	}
	
	static String capFirst(String s){
		if(s.isEmpty()) return s;
		if(s.length()>1) return s.toUpperCase().charAt(0)+s.substring(1);
		else return s.toUpperCase();
	}
}
