package com.generator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import freemarker.template.Configuration;

public class Main {

	public static class VarInfo {
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
	}

	private static javax.xml.xpath.XPathFactory xpathFactory = javax.xml.xpath.XPathFactory
			.newInstance();
	static private Logger logger = LoggerFactory.getLogger(Main.class);

	private static freemarker.template.Configuration initFreeMarker(
			String templatedir) {
		final freemarker.template.Configuration cfg;
		try {
			cfg = new freemarker.template.Configuration();
			cfg.setDirectoryForTemplateLoading(new java.io.File(templatedir));
		} catch (final Exception e) {
			logger.error("err", e);
			return null;
		}
		cfg.setObjectWrapper(new freemarker.template.DefaultObjectWrapper());
		return cfg;
	}

	static public void main(String[] args) throws Exception {
		/*if (args.length < 3)
			return;*/
		// bean定义文件
		//gbeans/main.xml nsrc templates false ${files}
		final String xmlpath = "gbeans/main.xml";//args[0];
		// 生好的java文件放哪里
		final String dstdir = "nsrc";//args[1];
		final String templatedir = "templates";//args[2];
		final boolean defineOnly = false;
		/*if (args.length == 4)
			defineOnly = true;
		else
			defineOnly = false;*/

		final freemarker.template.Configuration cfg = initFreeMarker(templatedir);

		javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory
				.newInstance();
		factory.setXIncludeAware(true);
		factory.setNamespaceAware(true);
		org.w3c.dom.Document doc;
		try {
			javax.xml.parsers.DocumentBuilder db = factory.newDocumentBuilder();
			doc = db.parse(xmlpath);
		} catch (final Exception ex) {
			logger.error("parse doc fail", ex);
			return;
		}
		if (doc == null)
			return;

		Object xpathresult;

		xpathresult = xpathFactory.newXPath().evaluate("//bean", doc,
				javax.xml.xpath.XPathConstants.NODESET);

		if (xpathresult == null) {
			logger.error(xmlpath + "文件的格式不正确");
			return;
		}
		
		String[] genxlsfiles = null; 
		if (args.length == 5 && !args[4].equalsIgnoreCase("all"))
		{
			logger.info("只打指定XLS文件 ：" + args[4]);
			genxlsfiles = args[4].split(",");
			for(int i = 0; i < genxlsfiles.length ; i++)
				genxlsfiles[i] = genxlsfiles[i].trim();
		}
		else
		{
			logger.info("打所有的XLS文件 ");
		}
		
		org.w3c.dom.NodeList nl = (org.w3c.dom.NodeList) xpathresult;
		// 需要生哪些文件？
		ArrayList<String> serverClasslist = new ArrayList<String>();
		ArrayList<String> clientClasslist = new ArrayList<String>();
		for (int i = 0; i != nl.getLength(); ++i) {
			Element e = (Element) nl.item(i);
			final String targetClassname = ClassNameUtil.xmlpathToNS(e);
			logger.info("writing " + targetClassname);
			writeJavaFile(cfg, dstdir, targetClassname, e, defineOnly);
			
			if(!checkGenXlsFile(e, genxlsfiles))
				continue;
			
			if(checkNoxmlGbeans(e.getAttribute("name"))){
				continue;
			}
			
			if (e.getAttribute("genxml").equals("server")){
				serverClasslist.add(targetClassname);
			}
			else if (e.getAttribute("genxml").equals("client"))
				clientClasslist.add(targetClassname);
		}

		
		final freemarker.template.Template temp = cfg.getTemplate("main.ftl");
		String filename = dstdir + "/mytools/ConvMain.java";
		new java.io.File(filename).getParentFile().mkdirs();
		final java.io.Writer out = new java.io.OutputStreamWriter(
				new java.io.FileOutputStream(filename), "utf-8");
		java.util.Map<String, Object> root = new java.util.HashMap<String, Object>();
		root.put("serverClassList", serverClasslist);
		root.put("clientClassList", clientClasslist);
		root.put("defineOnly", defineOnly);
		temp.process(root, out);
		out.close();

		logger.error("java文件生成成功");
	}
	
	static List<String> noXmlList = new ArrayList<String>();
	static{
		noXmlList.add("SBattleInfo");
		noXmlList.add("SBattleNode");
		noXmlList.add("Sflag");
		noXmlList.add("SBattleMonster");
		noXmlList.add("SRegion");
		noXmlList.add("SFubenjiguan");
		
		noXmlList.add("CBattleNode");
		noXmlList.add("Cflag");
		noXmlList.add("CFubenjiguan");
		noXmlList.add("CSBattleInfo");
		noXmlList.add("CSBattleNode");
		noXmlList.add("CSBattleMonster");
		
		noXmlList.add("CBattleInfo");
		noXmlList.add("CBattleMonster");
		noXmlList.add("CRegion");
		
	}
	private static boolean checkNoxmlGbeans(String attribute) {
		if(noXmlList.contains(attribute))
			return true;
		return false;
	}

	/**
	 * 
	 * @param e
	 * @param singlexlsfile
	 * @return true 表示文件可以加入classlist，false不可以
	 */
	private static boolean checkGenXlsFile(Element e, String[] genxlsfiles)
	{
		if(genxlsfiles == null)
			return true;
		String from = e.getAttribute("from");
		if(from == null)
			return true;
		//如果有from属性，则要匹配是否在打表的文件之列
		String[] xlsfiles = from.split(",");
		for(String xlsfile : xlsfiles)
		{
			for(String singlexlsfile : genxlsfiles)
				if(xlsfile.equalsIgnoreCase(singlexlsfile))
					return true;
		}
		return false;
	}

	private static boolean isInt(String v) {
		try {
			Integer.parseInt(v);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	private static boolean isFloat(String v) {
		try {
			Float.parseFloat(v);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	private static boolean isDouble(String v) {
		try {
			Double.parseDouble(v);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	private static boolean isLong(String v) {
		try {
			Long.parseLong(v);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	private static boolean writeJavaFile(Configuration cfg, String dstdir,
			String targetClassname, Element element, boolean defineOnly) {

		final ClassNameUtil cname = new ClassNameUtil(targetClassname);
		final String filename = dstdir + "/" + cname.getDir() + ".java";
		final java.util.Map<String, Object> root = new java.util.HashMap<String, Object>();
		root.put("namespace", cname.getNamespace());
		root.put("classname", cname.getShortName());
		root.put("defineOnly", defineOnly);
		String[] xlsfiles = element.getAttribute("from").split(",");
		// 究竟是一个顶级的bean,还是另一个collection的一部分
		final boolean isCollectionValue = xlsfiles[0].isEmpty();
		root.put("xlsfiles", xlsfiles);
		root.put("baseclass", element.getAttribute("baseclass"));
		// 以什么样的模式生xml
		String genxml = element.getAttribute("genxml");
		if (!(genxml.isEmpty() || genxml.equals("server") || genxml
				.equals("client")))
			throw new RuntimeException("错误的值,genxml=" + genxml);
		if (isCollectionValue != genxml.isEmpty())
			throw new RuntimeException("错误的值,genxml=" + genxml);
		root.put("genxml", genxml);

		final org.w3c.dom.NodeList list = element
				.getElementsByTagName("variable");
		ArrayList<VarInfo> varlist = new ArrayList<VarInfo>(
				list.getLength());
		for (int i = 0; i != list.getLength(); ++i) {
			final Element varE = (Element) list.item(i);
			varlist.add(fillBeanInfo(targetClassname, isCollectionValue, varE));
		}
		root.put("varList", varlist);
		try {
			final freemarker.template.Template temp = cfg
					.getTemplate("bean.ftl");
			new java.io.File(filename).getParentFile().mkdirs();
			final java.io.Writer out = new java.io.OutputStreamWriter(
					new java.io.FileOutputStream(filename), "utf-8");
			temp.process(root, out);
			out.close();
		} catch (final Exception ex) {
			logger.error("err", ex);
			return isCollectionValue;
		}
		return isCollectionValue;
	}

	/**
	 * 
	 * @param targetClassname
	 * @param isCollectionValue
	 *            究竟是一个顶级的bean,还是另一个collection的一部分
	 * @param varE
	 *            &lt;variable> 节点
	 * @param bi
	 */
	private static VarInfo fillBeanInfo(String targetClassname,
			final boolean isCollectionValue, final Element varE) {
		VarInfo bi = new VarInfo();
		bi.name = varE.getAttribute("name");

		String type = varE.getAttribute("type");
		String min = varE.getAttribute("min");
		String max = varE.getAttribute("max");
		String ref = varE.getAttribute("ref");
		String fromCol = varE.getAttribute("fromCol");
		if (!fromCol.isEmpty()) {
			String[] p = fromCol.split("\\|");
			for (String s : p) {
				String[] s1 = s.split(":");
				if (s1.length == 2) {
					// prefix:var1,var2,var3这样的形式
					String[] strs = s1[1].split(",");
					for(int i = 0; i < strs.length;i++)
						strs[i] = strs[i].trim();
					bi.prefixMapping.put(s1[0], strs);
				} else if (s1.length == 1) {
					// var1,var2,var3这样的形式
					String[] strs = s1[0].split(",");
					for(int i = 0; i < strs.length;i++)
						strs[i] = strs[i].trim();
					bi.prefixMapping.put("", strs);
				} else {
					throw new RuntimeException("错误的值,fromcol=" + fromCol);
				}
			}
		} else if (isCollectionValue) {
			throw new RuntimeException("未指定fromcol属性");
		} else {
			String prefix = varE.getAttribute("prefix");	
			bi.prefixMapping.put("", prefix.split(","));
		}

		if (type.equalsIgnoreCase("string") || type.equalsIgnoreCase("int")
				|| type.equalsIgnoreCase("long")
				|| type.equalsIgnoreCase("double")
				|| type.equalsIgnoreCase("bool")
				|| type.equalsIgnoreCase("float")) {
			String defaultValue = "";
			if (type.equalsIgnoreCase("string")) {
				defaultValue = "null";
				bi.type = "String";
				if (!min.isEmpty() && !isInt(min))
					throw new RuntimeException("min属性错误");
				if (!max.isEmpty() && !isInt(max))
					throw new RuntimeException("max属性错误");
				bi.minValue = min;
				bi.maxValue = max;
			} else if (type.equalsIgnoreCase("int")) {
				defaultValue = "0";
				bi.type = "int";
				if (!min.isEmpty() && !isInt(min))
					throw new RuntimeException("min属性错误");
				if (!max.isEmpty() && !isInt(max))
					throw new RuntimeException("max属性错误");
				bi.minValue = min;
				bi.maxValue = max;
			} else if (type.equalsIgnoreCase("long")) {
				defaultValue = "0L";
				bi.type = "long";
				if (!min.isEmpty())
					if (isLong(min))
						bi.minValue = min + "L";
					else
						throw new RuntimeException("min属性错误");
				if (!max.isEmpty())
					if (isLong(max))
						bi.maxValue = max + "L";
					else
						throw new RuntimeException("max属性错误");
			} else if (type.equalsIgnoreCase("double")) {
				defaultValue = "0.0";
				bi.type = "double";
				if (!min.isEmpty())
					if (isDouble(min))
						bi.minValue = min + "d";
					else
						throw new RuntimeException("min属性错误");
				if (!max.isEmpty())
					if (isDouble(max))
						bi.maxValue = max + "d";
					else
						throw new RuntimeException("max属性错误");
			} else if (type.equalsIgnoreCase("float")) {
				defaultValue = "0.0f";
				bi.type = "float";
				if (!min.isEmpty())
					if (isFloat(min))
						bi.minValue = min + "f";
					else
						throw new RuntimeException("min属性错误");
				if (!max.isEmpty())
					if (isFloat(max))
						bi.maxValue = max + "f";
					else
						throw new RuntimeException("max属性错误");
			} else if (type.equalsIgnoreCase("bool")) {
				defaultValue = "false";
				bi.type = "boolean";
			}
			bi.initValue = defaultValue;
		} else if (type.equalsIgnoreCase("vector")
				|| type.equalsIgnoreCase("set")) {
			// 这是一个容器
			String valueType = varE.getAttribute("value").trim();

			if (valueType.equalsIgnoreCase("Integer")
					|| valueType.equalsIgnoreCase("int")) {
				bi.valueType = "Integer";
				valueType = "Integer";
			} else if (valueType.equalsIgnoreCase("String")) {
				bi.valueType = "String";
				valueType = "String";
			} else if (valueType.equalsIgnoreCase("Long")) {
				bi.valueType = "Long";
				valueType = "Long";
			} else if (valueType.equalsIgnoreCase("double")) {
				bi.valueType = "Double";
				valueType = "Double";
			} else if (valueType.equalsIgnoreCase("float")) {
				bi.valueType = "Float";
				valueType = "Float";
			} else if (valueType.equalsIgnoreCase("bool")) {
				bi.valueType = "Boolean";
				valueType = "Boolean";
			} else {
				bi.valueType = valueType;
			}
			if (type.equalsIgnoreCase("vector")) {
				bi.type = "java.util.ArrayList<" + valueType + ">";
			} else if (type.equalsIgnoreCase("set")) {
				bi.type = "java.util.TreeSet<" + valueType + ">";
			}
			bi.initValue = "new " + bi.type + "()";
		} else {
			throw new RuntimeException("不认识的类型,type=" + type);
		}

		{
			String comment = "";
			org.w3c.dom.Node nextnode = varE.getNextSibling();
			if (nextnode != null
					&& org.w3c.dom.Node.TEXT_NODE == nextnode.getNodeType()) {
				comment = nextnode.getTextContent().trim()
						.replaceAll("[\r\n]", "");
			}
			bi.comment = comment;
		}
		bi.ref = ref;
		return bi;
	}
}
