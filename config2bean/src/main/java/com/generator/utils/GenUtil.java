package com.generator.utils;

import com.generator.Main;
import com.generator.type.Bean;
import com.generator.type.BeanNameSpace;
import com.generator.type.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wang dongfang
 * @ClassName GenUtil.java
 * @Description TODO
 * @createTime 2018年12月05日 15:53:00
 */
public class GenUtil {

    public static Logger logger = LoggerFactory.getLogger(GenUtil.class);

    public static Set<String> initIngoreFiles(String ignorefile) {
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

    public static void parseXml(String genCodeXmlPath) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory factory =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setXIncludeAware(true);
        factory.setNamespaceAware(true);
        org.w3c.dom.Document doc;
        try {
            javax.xml.parsers.DocumentBuilder db = factory.newDocumentBuilder();
            doc = db.parse(genCodeXmlPath);
        } catch (final Exception ex) {
            logger.error("parse doc fail", ex);
            throw new RuntimeException(ex);
        }
        if (doc == null)
            return;
        parse(doc.getDocumentElement(), null);
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

    public static void addReferenceBean(BeanNameSpace curns) {
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


    public static void getGenClassList(BeanNameSpace ns, List<String> serverClasslist, List<String> clientClasslist, Set<String> ignorefilenames) {
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
            getGenClassList(childns, serverClasslist, clientClasslist, ignorefilenames);
        }
    }

}
