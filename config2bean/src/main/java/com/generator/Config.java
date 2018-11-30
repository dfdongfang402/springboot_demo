package com.generator;

import com.generator.utils.BeanInfo;

import java.util.ArrayList;
import java.util.List;

public class Config {
	private static List<BeanInfo> beans = new ArrayList<>();
	private static String srcDir = null;
	private static String exclDir = null;
	private static String xmlDir = null;
	private static String binDir = null;
	private static String gbeansDir = null;
	private static String luaDir = null;

	public static String getSrcDir() {
		return srcDir;
	}

	public static void setSrcDir(String srcDir) {
		Config.srcDir = srcDir;
	}

	public static String getGbeansDir() {
		return gbeansDir;
	}

	public static void setGbeansDir(String gbeansDir) {
		Config.gbeansDir = gbeansDir;
	}

	public static List<BeanInfo> getBeans() {
		return beans;
	}

	public static Boolean addBean(BeanInfo name) {
		if (beans.contains(name)) {
			return false;
		}
		beans.add(name);
		return true;
	}

	public static String getExclDir() {
		return exclDir;
	}

	public static void setExclDir(String exclDir) {
		Config.exclDir = exclDir;
	}

	public static String getXmlDir() {
		return xmlDir;
	}

	public static void setXmlDir(String xmlDir) {
		Config.xmlDir = xmlDir;
	}

	public static String getBinDir() {
		return binDir;
	}

	public static void setBinDir(String binDir) {
		Config.binDir = binDir;
	}
	
	public static String getLuaDir() {
		return luaDir;
	}

	public static void setLuaDir(String luaDir) {
		Config.luaDir = luaDir;
	}
}
