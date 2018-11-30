package com.generator.type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.generator.Main;
import com.generator.utils.CachedFileOutputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Bean extends Type {
	private static freemarker.template.Template temp;
	private static freemarker.template.Template getTemplate() throws IOException {
		if (temp == null)
			temp = Main.cfg.getTemplate("bean.ftl");
		return temp;
	}
	public List<Variable> variables = new ArrayList<Variable>();
	public String name;
	private String[] fromXls;
//	private boolean genserverxml = false;
//	private boolean genclientxml = false;
	private String genxml;
	private String fromxml;
	private boolean genfile = true;//
	private String baseclass;
	private boolean isDefineOnly = false;// only for gen data source, for example, dod battle xml 
	public BeanNameSpace ns;
	public Bean(String name) {
		this.name = name;
	}
	public String getBaseclass() {
		return baseclass;
	}
	public void parse(Element e) {
		this.fromXls = e.getAttribute("from").split(",");
		this.genxml = e.getAttribute("genxml").trim().toLowerCase();
		String defineOnlyAttr = e.getAttribute("isdefineonly");
		if (defineOnlyAttr != null && !defineOnlyAttr.isEmpty()) {
            this.isDefineOnly = defineOnlyAttr.toLowerCase().equals("true");
        }
		this.baseclass = e.getAttribute("baseclass");
		this.fromxml = e.getBaseURI().substring(e.getBaseURI().lastIndexOf('/')+1);
		NodeList nodes = e.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element child = (Element)node;
			String nodename = child.getNodeName();
			if (nodename.equals("variable")) {
				this.variables.add(new Variable(child));
			}
		}
	}
	
	public void setGenfile(boolean genfile) {
		this.genfile = genfile;
	}
	
	public boolean getGenfile() {
		return genfile;
	}
	
	public String[] getFromXls() {
		return fromXls;
	}
	
	public String getFromXml() {
		return fromxml;
	}
	
	public String getGenxml() {
		return genxml;
	}
	
	public boolean isDefineOnly()
	{
		return isDefineOnly;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFullName() {
		return ns.getFullName() + '.' + name;
	}
	
//	public List<Bean> getReferenceBeans() {
//		List<Bean> references = new ArrayList<Bean>();
//		for (Variable variable : variables) {
//			variable.getReferenceBean(dir, references);
//		}
//		if (!baseclass.equals("")) {
//			Bean basebean = BeanNameSpace.getBean(dir + '.' + baseclass);
//			references.add(basebean);
//			references.addAll(basebean.getReferenceBeans());
//		}
//		Main.logger.debug("baseclass=" + baseclass);
//		return references;
//	}
	
	public void writeJavaFile() {
		String filename = Main.dstdir + File.separator + 
				getFullName().replace(".", File.separator) + ".java";
		final java.util.Map<String, Object> root = new java.util.HashMap<>();
		root.put("namespace", ns.getFullName());
		root.put("classname", name);
		root.put("defineOnly", Main.defineOnly);
		// 究竟是一个顶级的bean,还是另一个collection的一部分
		final boolean isCollectionValue = fromXls[0].isEmpty();
		root.put("xlsfiles", fromXls);
		root.put("baseclass", baseclass);
		// 以什么样的模式生xml
		if (!(genxml.isEmpty() || genxml.equals("server") || genxml
				.equals("client")))
			throw new RuntimeException("错误的值,genxml=" + genxml);
		if (isCollectionValue != genxml.isEmpty())
			throw new RuntimeException("错误的值,genxml=" + genxml);
		root.put("genxml", genxml);
//		if (genxml.equals("client")) {
//			filename = filename.toLowerCase();
//		}
		List<TemplateVariable> tVariables = new ArrayList<>();
		for (Variable v : variables) {
			tVariables.add(v.toJavaVariable());
		}
		root.put("varList", tVariables);
		try {
			File file = new File(filename);
			file.getParentFile().mkdirs();
			final java.io.Writer out = new java.io.OutputStreamWriter(
					new CachedFileOutputStream(file), Main.encode);
			getTemplate().process(root, out);
			out.close();
		} catch (final Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 1)
			return;
		String clientxmlpath = args[0];
		File path = new File(clientxmlpath);
		if (!path.isDirectory()) {
			return;
		}
		for (File file : path.listFiles()) {
			String newname = file.getName().toLowerCase();
			if (newname.equals(file.getName())) {
				continue;
			}
			file.renameTo(new File(clientxmlpath + File.separator + newname));
		}
	}
}
