package com.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.generator.type.Bean;
import com.generator.type.BeanNameSpace;
import com.generator.type.Variable;
import freemarker.template.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Main {

    public static Logger logger = LoggerFactory.getLogger(Main.class);

    private static freemarker.template.Configuration initFreeMarker(
            String templatedir) {
        final freemarker.template.Configuration cfg;
        try {
            cfg = new freemarker.template.Configuration(Configuration.VERSION_2_3_28);
            cfg.setDirectoryForTemplateLoading(new File(templatedir));
            cfg.setDefaultEncoding("UTF-8");
            // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
            cfg.setLogTemplateExceptions(false);
        } catch (final Exception e) {
            logger.error("err", e);
            return null;
        }
        cfg.setObjectWrapper(new freemarker.template.DefaultObjectWrapper(Configuration.VERSION_2_3_28));
        return cfg;
    }

    public static String xmlpath;
    public static String dstdir;                //生成的代码存放目录
    public static String templatedir;
    public static boolean defineOnly = false;
    public static boolean partMode = false;
    public static boolean csvMode = true;
    public static String xlspath ;              //excel 文件路径
    public static String csvpath;               //csv格式文件路径
    public static String genCodeXmlPath;        //根据excel或者csv生成的用于生成代码的xml文件路径
    public static String encode = "utf-8";
    public static String csvEncode = "gbk";
    public static String ignorefile = null;
    public static boolean luaOnly = false;
    private static Set<String> ignorefilenames = new HashSet<String>();
    public static freemarker.template.Configuration cfg;

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().equals("-xmlpath")) {
                xmlpath = args[++i];
            } else if (args[i].toLowerCase().equals("-dstdir")) {
                dstdir = args[++i];
            } else if (args[i].toLowerCase().equals("-templatedir")) {
                templatedir = args[++i];
            } else if (args[i].toLowerCase().equals("-define")) {
                defineOnly = true;
            } else if (args[i].toLowerCase().equals("-partmode")) {
                partMode = true;
            } else if (args[i].toLowerCase().equals("-csvmode")) {
                csvMode = Boolean.valueOf(args[++i]);
            } else if (args[i].toLowerCase().equals("-xlspath")) {
                xlspath = args[++i];
            } else if (args[i].toLowerCase().equals("-csvpath")) {
                csvpath = args[++i];
            } else if (args[i].toLowerCase().equals("-genbeans")) {
                genCodeXmlPath = args[++i];
            } else if (args[i].toLowerCase().equals("-ignorefile")) {
                ignorefile = args[++i];
            } else if (args[i].toLowerCase().equals("-csvencode")) {
                csvEncode = args[++i];
            }
        }
    }

    private static void usage() {
        System.out.println("Usage: java -jar gen.jar [options] ...");
        System.out.println("	-xmlpath        xml file path");
        System.out.println("	-dstdir         gen file output dir");
        System.out.println("	-templatedir    tempalte file path");
        System.out.println("	-define         declare only");
        Runtime.getRuntime().exit(1);
    }

    private static void parse(Element e, BeanNameSpace curns) {
        final String nodeName = e.getNodeName().toLowerCase();
        if (nodeName.equals("namespace")) {
            String elementName = e.getAttribute("name");
            assert (elementName != null && !elementName.isEmpty());
            BeanNameSpace ns = BeanNameSpace.findNameSpace(elementName);
            if (ns == null)
                ns = new BeanNameSpace(elementName);
            if (curns != null)
                curns.addChildNamespace(ns);
            ns.parent = curns;
            NodeList nodes = e.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                Element childelement = (Element) node;
                parse(childelement, ns);
            }
        } else if (nodeName.equals("bean")) {
            String elementName = e.getAttribute("name");
            assert (elementName != null && !elementName.isEmpty());
            Bean bean = new Bean(elementName);
            bean.parse(e);
            bean.ns = curns;
            assert (curns != null);
            curns.addChildBean(bean);
        }
    }

    private static Set<String> initIngoreFiles() {
        Set<String> ignoreFiles = new HashSet<>();
        if (ignorefile == null || ignorefile.trim().equals(""))
            return ignoreFiles;

        try {
            FileInputStream input = new FileInputStream(new File(ignorefile));
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String line = null;
            while ((line = br.readLine()) != null) {
                String ifile = line.trim().toLowerCase();
                if (ifile.endsWith(".xml"))
                    ifile = ifile.substring(0, ifile.length() - ".xml".length());
                ignoreFiles.add(ifile);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ignoreFiles;
    }

    private static void parseXml() throws Exception {
        javax.xml.parsers.DocumentBuilderFactory factory =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
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
        parse(doc.getDocumentElement(), null);

        ignorefilenames = initIngoreFiles();
    }

    private static void makeCsvPath(String path) {
        int idx = path.indexOf('/');
        while (idx != -1) {
            File file = new File(csvpath + "/" + path.substring(0, idx));
            if (!file.exists())
                file.mkdir();
            path = path.substring(idx + 1);
            idx = path.indexOf('/');
        }
    }

    private static void convertXls2Csv(Set<String> diffxlses) {
        if (csvpath == null || csvpath.trim().equals("")) {
            return;
        }
        for (String diffxls : diffxlses) {
            makeCsvPath(diffxls);
            int idx = diffxls.lastIndexOf('.');
            String diffcsv = diffxls.substring(0, idx) + ".csv";
            com.generator.excel.XlstoCSV.xls2csv(new File(xlspath + "/" + diffxls), new File(csvpath + "/" + diffcsv), csvEncode);
        }
    }

    private static void filterNode() throws Exception {
        //根据SVN记录，找出修改过的xls文件
        Set<String> diffxlses = DiffWithSvn.getDiffXls(Main.xlspath);
        if (csvMode)
            convertXls2Csv(diffxlses);
        doFilter(BeanNameSpace.getRoot(), diffxlses, DiffWithSvn.getDiffGenCodeXmls(Main.genCodeXmlPath));
        addReferenceBean(BeanNameSpace.getRoot());
    }


    private static void doFilter(BeanNameSpace curns, Set<String> xlsfilter, Set<String> genBeansFilter) {
        //	logger.debug("namespace=" + curns.getName() + ", beans size=" + curns.getBeans().size());
        for (Bean bean : curns.beans.values()) {
            if (!genBeansFilter.contains(bean.getFromXml())) {
                boolean genfile = false;
                for (String fromxls : bean.getFromXls()) {
                    if (xlsfilter.contains(fromxls)) {
                        genfile = true;
                        break;
                    }
                }
                bean.setGenfile(genfile);
            }
        }
        for (BeanNameSpace ns : curns.getChildren().values()) {
            doFilter(ns, xlsfilter, genBeansFilter);
        }
    }

    private static Bean getBeanByName(String name) {
        String[] namestr = name.split("\\.");
        if (namestr.length == 0) {

            return null;
        }
        BeanNameSpace ns = BeanNameSpace.getRoot();
        int i = 0;
        if (!ns.getName().equals(namestr[i++]))
            return null;
        for (; i < namestr.length - 1; i++) {
            ns = ns.children.get(namestr[i]);
            if (ns == null)
                return null;
        }
        return ns.beans.get(namestr[i]);
    }

    private static Bean addBaseclass(Bean bean) {
        String baseclass = bean.getBaseclass();
        Bean referBean = null;
        if (baseclass != null && !baseclass.equals("")) {
            referBean = !baseclass.contains(".") ? bean.ns.beans.get(baseclass) : getBeanByName(baseclass);
            if (referBean == null) {
                Main.logger.info("error reference " + bean.getFullName() + ", " + baseclass);
            } else {
                referBean.setGenfile(true);
            }
        }
        return referBean;
    }

    private static void addReferenceBean(BeanNameSpace curns) {
        for (Bean bean : curns.beans.values()) {
            if (bean.getGenfile()) {
                Bean basebean = bean;
                do {
                    basebean = addBaseclass(basebean);
                } while (basebean != null);
                for (Variable v : bean.variables) {
                    if (v.isBaseType() || Variable.isBaseType(v.valueType))
                        continue;
                    //		String fullname = curns.getFullName();
                    //		Bean xx =curns.beans.get(v.valueType);
                    Bean referBean = !v.valueType.contains(".") ? curns.beans.get(v.valueType) : getBeanByName(v.valueType);
                    if (referBean == null) {
                        Main.logger.info("error reference " + bean.getFullName() +
                                ", " + v.valueType);
                        continue;
                    }
                    referBean.setGenfile(true);
                }
            }
        }
        for (BeanNameSpace ns : curns.getChildren().values()) {
            addReferenceBean(ns);
        }
    }

    private static void configure(String[] args) {
        parseArgs(args);
        cfg = initFreeMarker(templatedir);
        if (xmlpath == null || dstdir == null || templatedir == null) {
            usage();
            throw new IllegalArgumentException("没有定义xmlpath, dstdir, templatedir");
        }
    }


    private static void make() throws Exception {
        if (partMode) {
            filterNode();
        }
        BeanNameSpace.getRoot().writeJavafile();
        java.util.ArrayList<String> serverClasslist = new java.util.ArrayList<String>();
        java.util.ArrayList<String> clientClasslist = new java.util.ArrayList<String>();
        getConvClassList(BeanNameSpace.getRoot(), serverClasslist, clientClasslist);
        if (serverClasslist.isEmpty() && clientClasslist.isEmpty()) {
            logger.error("没有找到修改的beans");
            return;
        }
        final freemarker.template.Template temp = cfg.getTemplate("main.ftl");
        String filename = dstdir + "/mytools/ConvMain.java";
        new File(filename).getParentFile().mkdirs();
        final java.io.Writer out = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(filename), Main.encode);
        java.util.Map<String, Object> root = new java.util.HashMap<String, Object>();
        root.put("serverClassList", serverClasslist);
        root.put("clientClassList", clientClasslist);
        root.put("defineOnly", defineOnly);
        root.put("csvmode", Boolean.toString(csvMode));

        temp.process(root, out);
        out.close();

        logger.error("java文件生成成功");
    }

    static public void main(String[] args) throws Exception {
        configure(args);
        parseXml();
        make();
    }

    private static void getConvClassList(BeanNameSpace ns, List<String> serverClasslist, List<String> clientClasslist) {
        for (Bean bean : ns.beans.values()) {
            if (bean.getGenxml().equals("server") && bean.getGenfile()) {
                if (!ignorefilenames.contains(bean.getFullName().toLowerCase())) {
                    if (!bean.isDefineOnly())
                        serverClasslist.add(bean.getFullName());
                }
            } else if (bean.getGenxml().equals("client") && bean.getGenfile()) {
                if (!ignorefilenames.contains(bean.getFullName().toLowerCase())) {
                    if (!bean.isDefineOnly())
                        clientClasslist.add(bean.getFullName());
                }
            }
        }
        for (BeanNameSpace childns : ns.getChildren().values()) {
            getConvClassList(childns, serverClasslist, clientClasslist);
        }
    }

//	private static List<String> extAdded(Collection<String> srcbeans, Map<String,String> basebeans, Map<String, Set<String>> importbeans) {
//		List<String> ret = new ArrayList<String>();
//		for (String targetClassname : srcbeans) {
//			String curBaseClassname = basebeans.get(targetClassname);
//			if (curBaseClassname != null && !curBaseClassname.equals("")) {
//				ret.add(curBaseClassname);
//			}
//			
//			Set<String> importClassnames = importbeans.get(targetClassname);
//			if (importClassnames != null) {
//				ret.addAll(importClassnames);
//			}
//		}
//		return ret;
//	}
//
//	private static boolean isInt(String v) {
//		try {
//			Integer.parseInt(v);
//		} catch (NumberFormatException ex) {
//			return false;
//		}
//		return true;
//	}
//
//	private static boolean isFloat(String v) {
//		try {
//			Float.parseFloat(v);
//		} catch (NumberFormatException ex) {
//			return false;
//		}
//		return true;
//	}
//
//	private static boolean isDouble(String v) {
//		try {
//			Double.parseDouble(v);
//		} catch (NumberFormatException ex) {
//			return false;
//		}
//		return true;
//	}
//
//	private static boolean isLong(String v) {
//		try {
//			Long.parseLong(v);
//		} catch (NumberFormatException ex) {
//			return false;
//		}
//		return true;
//	}
    /**
     *
     * @param cfg
     * @param dstdir
     * @param targetClassname
     * @param element
     * @param defineOnly
     * @param xlsfilter
     * @return
     */
//	private static int writeJavaFile(Configuration cfg, String dstdir,
//			String targetClassname, Element element, boolean defineOnly) {
//		String[] xlsfiles = element.getAttribute("from").split(",");
//		final ClassNameUtil cname = new ClassNameUtil(targetClassname);
////		if (!importbeannames.contains(targetClassname) && !basebeannames.contains(targetClassname)) {
////			if (check) {
////				boolean needcov = false;
////				for (String xlsfile : xlsfiles) {
////					if (xlsfilter.contains(xlsfile)) {
////						needcov = true;
////						break;
////					}
////				}
////				if (!needcov) {
////					return -1;
////				}
////			}
////		}
//		
//		
//		final String filename = dstdir + "/" + cname.getDir() + ".java";
//		final java.util.Map<String, Object> root = new java.util.HashMap<String, Object>();
//		root.put("namespace", cname.getNamespace());
//		root.put("classname", cname.getShortName());
//		root.put("defineOnly", defineOnly);
//		
//		// 究竟是一个顶级的bean,还是另一个collection的一部分
//		final boolean isCollectionValue = xlsfiles[0].isEmpty();
//		root.put("xlsfiles", xlsfiles);
//		root.put("baseclass", element.getAttribute("baseclass"));
//		// 以什么样的模式生xml
//		String genxml = element.getAttribute("genxml");
//		if (!(genxml.isEmpty() || genxml.equals("server") || genxml
//				.equals("client")))
//			throw new RuntimeException("错误的值,genxml=" + genxml);
//		if (isCollectionValue != genxml.isEmpty())
//			throw new RuntimeException("错误的值,genxml=" + genxml);
//		root.put("genxml", genxml);
//
//		final org.w3c.dom.NodeList list = element.getElementsByTagName("variable");
//		java.util.ArrayList<VarInfo> varlist = new java.util.ArrayList<VarInfo>(list.getLength());
//		for (int i = 0; i != list.getLength(); ++i) {
//			final Element varE = (Element)list.item(i);
//			varlist.add(fillBeanInfo(targetClassname, isCollectionValue, varE));
//		}
//		root.put("varList", varlist);
//		try {
//			final freemarker.template.Template temp = cfg
//					.getTemplate("bean.ftl");
//			new java.io.File(filename).getParentFile().mkdirs();
//			final java.io.Writer out = new java.io.OutputStreamWriter(
//					new java.io.FileOutputStream(filename), "utf-8");
//			temp.process(root, out);
//			out.close();
//		} catch (final Exception ex) {
//			logger.error("err", ex);
//			return isCollectionValue ? 1:0;
//		}
//		return isCollectionValue ? 1:0;
//	}

//	private static boolean checkImportAndBaseBean(Element element, String targetClassname,
//			Map<String, Set<String>> importbeanmap) {
//		final org.w3c.dom.NodeList list = element.getElementsByTagName("variable");
//		boolean ret = false;
//		for (int i = 0; i != list.getLength(); ++i) {
//			final Element varE = (Element)list.item(i);
//			String type = varE.getAttribute("type");
//			if (type.equalsIgnoreCase("vector")
//					|| type.equalsIgnoreCase("set")) {
//				String valueType = varE.getAttribute("value").trim();
//				if (!valueType.equalsIgnoreCase("Integer")&& 
//					!valueType.equalsIgnoreCase("int") &&
//					!valueType.equalsIgnoreCase("String") && 
//					!valueType.equalsIgnoreCase("Long") && 
//					!valueType.equalsIgnoreCase("double")&&
//					!valueType.equalsIgnoreCase("float")&&
//					!valueType.equalsIgnoreCase("bool")) {
//					Set<String> imports = importbeanmap.get(targetClassname);
//					if (imports == null) {
//						imports = new HashSet<String>();
//						importbeanmap.put(targetClassname, imports);
//					}
//					imports.add(valueType);
//					ret = true;
//				}
//			}
//			String ref = varE.getAttribute("ref");
//			if (ref != null && !ref.equals("")) {
//				Set<String> imports = importbeanmap.get(targetClassname);
//				if (imports == null) {
//					imports = new HashSet<String>();
//					importbeanmap.put(targetClassname, imports);
//				}
//				imports.add(ref);
//			}
//		}
//		
//		return ret;
//	}

    /**
     *
     * @param targetClassname
     * @param isCollectionValue
     *            究竟是一个顶级的bean,还是另一个collection的一部分
     * @param varE
     *            &lt;variable> 节点
     * @param bi
     */
//	private static VarInfo fillBeanInfo(String targetClassname,
//			final boolean isCollectionValue, final Element varE) {
//		VarInfo bi = new VarInfo();
//		bi.name = varE.getAttribute("name");
//
//		String type = varE.getAttribute("type");
//		String min = varE.getAttribute("min");
//		String max = varE.getAttribute("max");
//		String ref = varE.getAttribute("ref");
//		String fromCol = varE.getAttribute("fromCol");
//		if (!fromCol.isEmpty()) {
//			String[] p = fromCol.split("\\|");
//			for (String s : p) {
//				String[] s1 = s.split(":");
//				if (s1.length == 2) {
//					// prefix:var1,var2,var3这样的形式
//					String[] strs = s1[1].split(",");
//					for(int i = 0; i < strs.length;i++)
//						strs[i] = strs[i].trim();
//					bi.prefixMapping.put(s1[0], strs);
//				} else if (s1.length == 1) {
//					// var1,var2,var3这样的形式
//					String[] strs = s1[0].split(",");
//					for(int i = 0; i < strs.length;i++)
//						strs[i] = strs[i].trim();
//					bi.prefixMapping.put("", strs);
//				} else {
//					throw new RuntimeException("错误的值,fromcol=" + fromCol);
//				}
//			}
//		} else if (isCollectionValue) {
//			throw new RuntimeException("未指定fromcol属性");
//		} else {
//			String prefix = varE.getAttribute("prefix");	
//			bi.prefixMapping.put("", prefix.split(","));
//		}
//
//		if (type.equalsIgnoreCase("string") || type.equalsIgnoreCase("int")
//				|| type.equalsIgnoreCase("long")
//				|| type.equalsIgnoreCase("double")
//				|| type.equalsIgnoreCase("bool")
//				|| type.equalsIgnoreCase("float")) {
//			String defaultValue = "";
//			if (type.equalsIgnoreCase("string")) {
//				defaultValue = "null";
//				bi.type = "String";
//				if (!min.isEmpty() && !isInt(min))
//					throw new RuntimeException("min属性错误");
//				if (!max.isEmpty() && !isInt(max))
//					throw new RuntimeException("max属性错误");
//				bi.minValue = min;
//				bi.maxValue = max;
//			} else if (type.equalsIgnoreCase("int")) {
//				defaultValue = "0";
//				bi.type = "int";
//				if (!min.isEmpty() && !isInt(min))
//					throw new RuntimeException("min属性错误");
//				if (!max.isEmpty() && !isInt(max))
//					throw new RuntimeException("max属性错误");
//				bi.minValue = min;
//				bi.maxValue = max;
//			} else if (type.equalsIgnoreCase("long")) {
//				defaultValue = "0L";
//				bi.type = "long";
//				if (!min.isEmpty())
//					if (isLong(min))
//						bi.minValue = min + "L";
//					else
//						throw new RuntimeException("min属性错误");
//				if (!max.isEmpty())
//					if (isLong(max))
//						bi.maxValue = max + "L";
//					else
//						throw new RuntimeException("max属性错误");
//			} else if (type.equalsIgnoreCase("double")) {
//				defaultValue = "0.0";
//				bi.type = "double";
//				if (!min.isEmpty())
//					if (isDouble(min))
//						bi.minValue = min + "d";
//					else
//						throw new RuntimeException("min属性错误");
//				if (!max.isEmpty())
//					if (isDouble(max))
//						bi.maxValue = max + "d";
//					else
//						throw new RuntimeException("max属性错误");
//			} else if (type.equalsIgnoreCase("float")) {
//				defaultValue = "0.0f";
//				bi.type = "float";
//				if (!min.isEmpty())
//					if (isFloat(min))
//						bi.minValue = min + "f";
//					else
//						throw new RuntimeException("min属性错误");
//				if (!max.isEmpty())
//					if (isFloat(max))
//						bi.maxValue = max + "f";
//					else
//						throw new RuntimeException("max属性错误");
//			} else if (type.equalsIgnoreCase("bool")) {
//				defaultValue = "false";
//				bi.type = "boolean";
//			}
//			bi.initValue = defaultValue;
//		} else if (type.equalsIgnoreCase("vector")
//				|| type.equalsIgnoreCase("set")) {
//			// 这是一个容器
//			String valueType = varE.getAttribute("value").trim();
//
//			if (valueType.equalsIgnoreCase("Integer")
//					|| valueType.equalsIgnoreCase("int")) {
//				bi.valueType = "Integer";
//				valueType = "Integer";
//			} else if (valueType.equalsIgnoreCase("String")) {
//				bi.valueType = "String";
//				valueType = "String";
//			} else if (valueType.equalsIgnoreCase("Long")) {
//				bi.valueType = "Long";
//				valueType = "Long";
//			} else if (valueType.equalsIgnoreCase("double")) {
//				bi.valueType = "Double";
//				valueType = "Double";
//			} else if (valueType.equalsIgnoreCase("float")) {
//				bi.valueType = "Float";
//				valueType = "Float";
//			} else if (valueType.equalsIgnoreCase("bool")) {
//				bi.valueType = "Boolean";
//				valueType = "Boolean";
//			} else {
//				bi.valueType = valueType;
//			}
//			if (type.equalsIgnoreCase("vector")) {
//				bi.type = "java.util.ArrayList<" + valueType + ">";
//			} else if (type.equalsIgnoreCase("set")) {
//				bi.type = "java.util.TreeSet<" + valueType + ">";
//			}
//			bi.initValue = "new " + bi.type + "()";
//		} else {
//			throw new RuntimeException("不认识的类型,type=" + type);
//		}
//
//		{
//			String comment = "";
//			org.w3c.dom.Node nextnode = varE.getNextSibling();
//			if (nextnode != null
//					&& org.w3c.dom.Node.TEXT_NODE == nextnode.getNodeType()) {
//				comment = nextnode.getTextContent().trim()
//						.replaceAll("[\r\n]", "");
//			}
//			bi.comment = comment;
//		}
//		bi.ref = ref;
//		return bi;
//	}
}
