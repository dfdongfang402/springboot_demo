package com.generator.utils;

import com.generator.type.Bean;
import com.generator.type.Variable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wang dongfang
 * @ClassName GenXmlBeansFromExcel.java
 * @Description TODO
 * @createTime 2018年12月05日 16:15:00
 */
public class GenXmlBeansFromExcel {

    public static Logger logger = LoggerFactory.getLogger(GenXmlBeansFromExcel.class);
    private static Set<String> ignorefilenames = new HashSet<>();


    private ArrayListMultimap<String, Bean> beanMap = ArrayListMultimap.create();
    private Set<String> nameSet = new HashSet<>();

    private Args mainArgs;

    public GenXmlBeansFromExcel(Args args) {
        this.mainArgs = args;
        FreemarkerUtil.initCfg(mainArgs.templatedir);
    }

    public void generate() {
        parseExcel(mainArgs.xlspath);
        writeToFile();
    }


    public void parseExcel(String path) {
        File xlsPath = new File(path);
        if(!xlsPath.isDirectory()) {
            logger.error("{} is not dir", path);
            throw new RuntimeException("xlspath is not dir");
        }
        File[] fs = xlsPath.listFiles();
        for(File f : fs){
            org.apache.poi.xssf.usermodel.XSSFWorkbook wb = null;
            InputStream input = null;
            try {
                if(f.isDirectory()) {
                    parseExcel(f.getPath());
                } else {
                    if(!f.getName().endsWith(".xlsm") && !f.getName().endsWith("xlsx")) {
                        continue;
                    }
                    if(f.getName().startsWith("~$")) {
                        continue;
                    }
                    input = new FileInputStream(f);
                    wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook(input);
                    org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheetAt(0);

                    Row fileNameRow = sheet.getRow(0);
                    Row serverRow = sheet.getRow(1);
                    Row clientRow = sheet.getRow(2);
                    Row colNameRow = sheet.getRow(3);
                    String fname = fileNameRow.getCell(0).getStringCellValue();
                    String serverClassName = fileNameRow.getCell(1).getStringCellValue();
                    String clientClassName = fileNameRow.getCell(2).getStringCellValue();
                    if(StringUtils.isBlank(serverClassName) && StringUtils.isBlank(clientClassName)) {
                        continue;
                    }
                    Bean bean = null;
                    if(!StringUtils.isBlank(serverClassName)) {
                        bean = createBean(serverClassName, serverRow, colNameRow);
                        if(bean != null) {
                            bean.setGenxml("server");
                            bean.setFromXls(new String[] {f.getPath().replaceAll("\\\\","/")});
                            beanMap.put(fname, bean);
                        }
                    }
                    if(!StringUtils.isBlank(clientClassName)) {
                        bean = createBean(clientClassName, clientRow, colNameRow);
                        if(bean != null) {
                            bean.setGenxml("client");
                            bean.setFromXls(new String[] {f.getPath().replaceAll("\\\\","/")});
                            beanMap.put(fname, bean);
                        }
                    }
                    if(bean == null) {
                        throw new Exception("init bean is null");
                    }
                    if(nameSet.contains(bean.getName())) {
                        throw new Exception("重复 bean name=" + bean.getName());
                    }
                    nameSet.add(bean.getName());

                }
            } catch (Exception e) {
                logger.error(String.format("exception: %s, file=%s",e.getMessage(), f.getName()), e);
                throw new RuntimeException();
            } finally {
                if(input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(wb != null) {
                    try {
                        wb.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void writeToFile() {
        //先写入main.xml
        String filename = mainArgs.genCodeXmlDir + File.separator + "main.xml";
        Map<String, Object> root = Maps.newHashMap();
        root.put("files", beanMap.keySet());
        try {
            File file = new File(filename);
            file.getParentFile().mkdirs();
            final java.io.Writer out = new java.io.OutputStreamWriter(
                    new CachedFileOutputStream(file), mainArgs.encode);
            FreemarkerUtil.getTemplate("gen_beans_main.ftl").process(root, out);
            out.close();
        } catch (final Exception ex) {
            ex.printStackTrace();
            return;
        }

        //写入其他xml
        Set<String> keySet = beanMap.keySet();
        for(String fname : keySet) {
            List<Bean> beans = beanMap.get(fname);
            if(beans == null || beans.size() == 0) {
                continue;
            }
            root.clear();

            root.put("fname", fname);
            root.put("beans", beans);
            try {
                File file = new File(mainArgs.genCodeXmlDir + File.separator + fname + ".xml");
                file.getParentFile().mkdirs();
                final java.io.Writer out = new java.io.OutputStreamWriter(
                        new CachedFileOutputStream(file), mainArgs.encode);
                FreemarkerUtil.getTemplate("gen_beans_xml.ftl").process(root, out);
                out.close();
            } catch (final Exception ex) {
                logger.error(String.format("write xml error fname=%s", fname), ex);
                return;
            }
        }

    }

    private Bean createBean(String className, Row varRow, Row colNameRow) {
        int index = 0;
        if(StringUtils.isBlank(className)) {
            return null;
        }

        List<Variable> vl = new ArrayList<>();
        int length = varRow.getLastCellNum();
        while (index < length) {
            Cell c = varRow.getCell(index);
            if(c == null || StringUtils.isBlank(c.getStringCellValue())) {
                index++;
                continue;
            }
            Variable v = new Variable(c);
            if(StringUtils.isBlank(v.valueType)) {
                Cell colnameCell = colNameRow.getCell(index);
                v.fromCol = colnameCell.getStringCellValue();
                index++;
            } else {
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < v.vectorLength; i++) {
                    Cell colnameCell = colNameRow.getCell(index);
                    sb.append(",").append(colnameCell.getStringCellValue());
                    index++;
                }
                v.fromCol = sb.deleteCharAt(0).toString();
            }
            vl.add(v);
        }
        if(vl.size() == 0) {
            return null;
        }
        String[] scn = className.split(":");
        Bean bean;
        if(scn.length == 1) {
            bean = new Bean(className);
        } else {
            bean = new Bean(scn[0]);
            bean.setBaseclass(scn[1]);
        }
        bean.variables.addAll(vl);

        return bean;
    }

}
