package com.generator.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import beantools.Env;

public class DataHelper {
	public static void GenerateBeans(String tablePath, String beanPath) {
		JavaBuilder jb = new JavaBuilder(tablePath, beanPath);
		for (BeanInfo bean : Env.getBeans()) {
			jb.generateFile(bean);
		}
	}

	public static void parseclient() throws Exception {
		DocumentBuilderFactory docfac = DocumentBuilderFactory.newInstance();
		docfac.setXIncludeAware(true);
		docfac.setNamespaceAware(true);
		DocumentBuilder docbuilder = docfac.newDocumentBuilder();
		Document document = docbuilder.parse(Env.getGbeansDir() + "/main.xml");
		Element element = document.getDocumentElement();
		xmlNodeClient(element, null);
	}

	public static void xmlNodeClient(Node node, String path) {
		// 遇到namespace
		if (node.getNodeName().equals("namespace")) {
			if (path == null) {
				path = node.getAttributes().getNamedItem("name").getNodeValue();
			} else {
				path = path + "/" + node.getAttributes().getNamedItem("name").getNodeValue();
			}
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node tnode = nodeList.item(i);
				if (tnode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				xmlNodeClient(tnode, path);
			}
		} else {
			BeanInfo.mkClientBean(node, path);
		}
	}

	public static List<Object> loadFromXml(Class<? extends Object> cls) throws Exception {
		List<Object> list = new Vector<Object>();
		Field field = cls.getDeclaredField("file_");
		String file = (String) field.get(cls);
		if (Env.getXmlDir() == null || Env.getXmlDir().equals("")) {
			file = file + ".xml";
		} else {
			file = Env.getXmlDir() + "/" + file + ".xml";
		}

		DocumentBuilderFactory docfac = DocumentBuilderFactory.newInstance();
		docfac.setXIncludeAware(true);
		docfac.setNamespaceAware(true);
		DocumentBuilder docbuilder = docfac.newDocumentBuilder();
		Document document = docbuilder.parse(file);
		NodeList childNodes = document.getDocumentElement().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node tnode = childNodes.item(i);
			if (tnode.getNodeType() == Node.ELEMENT_NODE) {
				Object instance = cls.newInstance();
				FromXml.fromXml(instance, tnode);
				list.add(instance);
			}
		}
		System.out.println(file + " : " + list.size());
		return list;
	}

	public static void saveToBinary(List<Object> list, String fileName) throws Exception {
		File dirs = new File(Env.getBinDir());
		dirs.mkdirs();
		File file = new File(Env.getBinDir() + "/" + fileName + ".bin");
		file.createNewFile();
		FileOutputStream fileops = new FileOutputStream(file);
		DataOutputStream dataops = new DataOutputStream(fileops);
		for (Object obj : list) {
			ToBinary.toBinary(obj, dataops);
		}
		dataops.close();
		fileops.close();
	}
	
	public static void saveToLua(List<Object> list, String fileName) throws Exception {
		if (Env.getLuaDir() == null)
			return;
		fileName = fileName.replace(".", "/");
		int idx = fileName.lastIndexOf("/");
		String className = fileName.substring(idx+1);
		String classPath = fileName.substring(0, idx);
		
		File dirs = new File(Env.getLuaDir() + "/" + classPath);
		dirs.mkdirs();
		
		final FileOutputStream luaops=new FileOutputStream(Env.getLuaDir()+"/"+classPath+"/"+className+".lua");
		final java.io.OutputStreamWriter luawriter = new java.io.OutputStreamWriter(luaops, "UTF-8");
		try{
			luawriter.write("local " + className + " = {}\r\n\r\n");
			luawriter.write(className + ".Data = {\r\n");
			String allIds = "";
			Boolean needWriteDot = false;
			for (Object obj : list) {
				if(needWriteDot)
					luawriter.write(",\r\n");
				needWriteDot = true;
				
				String idStr = ToLua.toLuaString(obj, luawriter);
				
				if(!"".equals(allIds))
					allIds += ",";
				allIds += idStr;
			}
			luawriter.write("\r\n}\r\n\r\n");
			luawriter.write(className + ".AllIds = { " + allIds + " } \r\n\r\n" );
			
			luawriter.write("function " + className + ":GetRecorder(id)\r\n");
			luawriter.write("\treturn "+className+".Data[id]\r\n");
			luawriter.write("end\r\n\r\n");
			
			luawriter.write("function " + className + ":GetAllID()\r\n");
			luawriter.write("\treturn "+className+".AllIds\r\n");
			luawriter.write("end\r\n\r\n");
			
			luawriter.write("return " + className + "\r\n\r\n");
		}finally{
			luawriter.close();
			luaops.close();
		}
	}
	
	public static void convXml(List<Object> list, String fileName) throws Exception {
		saveToBinary(list, fileName);
		if (Env.getLuaDir() != null)
			saveToLua(list, fileName);
	}
}
