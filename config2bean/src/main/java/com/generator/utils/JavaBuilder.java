package com.generator.utils;

import com.generator.Config;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class JavaBuilder {
	private String tablePath = null;
	private String beanPath = null;

	public JavaBuilder(String tablePath, String beanPath) {
		this.tablePath = tablePath;
		this.beanPath = beanPath;
	}

	public void generateFile(BeanInfo bean) {
		// System.out.println("path: " + this.path);
		File dirs = new File(Config.getSrcDir() + "/" + this.beanPath + "/" + bean.getPath());
		dirs.mkdirs();
		// dirs = new File(this.tablePath + "/" + bean.getPath());
		// dirs.mkdirs();
		try {
			File javafile = new File(Config.getSrcDir() + "/" + this.beanPath + "/" + bean.getPath() + "/" + bean.getClassName() + ".java");
			javafile.createNewFile();
			PrintWriter pw = new PrintWriter(javafile);
			beanContent(pw, bean);
			pw.close();

			// javafile = new File(this.beanPath + "/" + bean.getPath() + "/" +
			// bean.getClassName() + ".java");
			// javafile.createNewFile();
			// pw = new PrintWriter(javafile);
			// tableContent(pw, bean);
			// pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void beanContent(PrintWriter pw, BeanInfo bean) {
		pw.println("package " + (this.beanPath + "." + bean.getPath()).replace('/', '.') + ";");
		pw.println();
		pw.println("public class " + bean.getClassName() + " {");
		for (BeanField field : bean.getFields()) {
			pw.println();
			pw.println("\t@beantools.tools.IsSaveToFile");
			pw.println("\tpublic " + fieldHelper(field));
		}
		pw.println();
		pw.println("\tpublic static final String file_ = \"" + (bean.getPath() + "." + bean.getClassName()).replaceAll("/", ".").toLowerCase() + "\";");
		pw.println("}");
	}

	private String fieldHelper(BeanField field) {
		if (field.fieldtype.equals("vector")) {
			return "java.util.Vector<" + fieldHelper2(field.fieldtypeE) + "> " + field.fieldname + " = new java.util.Vector<" + fieldHelper2(field.fieldtypeE) + ">();";
		}
		return fieldHelper2(field.fieldtype) + " " + field.fieldname + " = null;";
	}

	private String fieldHelper2(String type) {
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

	private void tableContent(PrintWriter pw, BeanInfo bean) {
		pw.println("package " + (this.tablePath + "." + bean.getPath()).replace('/', '.') + ";");
		pw.println();
		pw.println("import java.util.Vector;");
		pw.println("import java.util.TreeMap;");
		pw.println();
		pw.println("import " + (this.beanPath + "." + bean.getPath()).replace('/', '.') + "." + bean.getClassName() + ";");
		pw.println("public class " + bean.getClassName() + " {");
		pw.println("\tpublic static final String file = \"" + (bean.getPath() + "." + bean.getClassName()).toLowerCase() + "\";");
		pw.println("\tTreeMap<Integer, " + bean.getClassName() + "> data = new TreeMap<Integer, " + bean.getClassName() + ">();");
		pw.println("}");
	}

	public static final String file = "";
}
