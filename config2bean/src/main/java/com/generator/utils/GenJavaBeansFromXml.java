package com.generator.utils;

import com.generator.DiffWithSvn;
import com.generator.type.Bean;
import com.generator.type.BeanNameSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wdf on 2018/12/5.
 */
public class GenJavaBeansFromXml {
    public static Logger logger = LoggerFactory.getLogger(GenJavaBeansFromXml.class);
    private static Set<String> ignorefilenames = new HashSet<String>();


    private Args mainArgs;

    public GenJavaBeansFromXml(Args args) {
        this.mainArgs = args;
        FreemarkerUtil.initCfg(mainArgs.templatedir);
    }


    public void generate() throws Exception {
        GenUtil.parseXml(mainArgs.genCodeXmlPath);

        ignorefilenames = GenUtil.initIngoreFiles(mainArgs.ignorefile);

        if (mainArgs.partMode) {
            filterNode();
        }
        BeanNameSpace.getRoot().writeJavafile();
        java.util.ArrayList<String> serverClasslist = new java.util.ArrayList<>();
        java.util.ArrayList<String> clientClasslist = new java.util.ArrayList<>();
        GenUtil.getGenClassList(BeanNameSpace.getRoot(), serverClasslist, clientClasslist, ignorefilenames);
        if (serverClasslist.isEmpty() && clientClasslist.isEmpty()) {
            logger.error("没有找到修改的beans");
            return;
        }
        final freemarker.template.Template temp = FreemarkerUtil.getTemplate("java_main.ftl");
        String filename = mainArgs.javaDir + "/confbeans/GenedMain.java";
        new File(filename).getParentFile().mkdirs();
        final java.io.Writer out = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(filename), mainArgs.encode);
        java.util.Map<String, Object> root = new java.util.HashMap<>();
        root.put("serverClassList", serverClasslist);
        root.put("clientClassList", clientClasslist);
        root.put("defineOnly", mainArgs.defineOnly);
        root.put("csvmode", Boolean.toString(mainArgs.csvMode));

        temp.process(root, out);
        out.close();

        logger.info("java文件生成成功");
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



    private void filterNode() throws Exception {
        //根据SVN记录，找出修改过的xls文件
        Set<String> diffxlses = DiffWithSvn.getDiffXls(mainArgs.xlspath);

        doFilter(BeanNameSpace.getRoot(), diffxlses, DiffWithSvn.getDiffGenCodeXmls(mainArgs.genCodeXmlPath));
        GenUtil.addReferenceBean(BeanNameSpace.getRoot());
    }
}
