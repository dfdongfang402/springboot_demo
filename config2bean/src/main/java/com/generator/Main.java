package com.generator;

import com.generator.utils.Args;
import com.generator.utils.Args.GEN_MODE;
import com.generator.utils.GenJavaBeansFromXml;
import com.generator.utils.GenXmlBeansFromExcel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

    public static Logger logger = LoggerFactory.getLogger(Main.class);
    public static Args mainArgs = new Args();

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().equals("-gencodexmlpath")) {
                mainArgs.genCodeXmlPath = args[++i];
            } else if (args[i].toLowerCase().equals("-dstdir")) {
                mainArgs.dstdir = args[++i];
            } else if (args[i].toLowerCase().equals("-templatedir")) {
                mainArgs.templatedir = args[++i];
            } else if (args[i].toLowerCase().equals("-define")) {
                mainArgs.defineOnly = true;
            } else if (args[i].toLowerCase().equals("-partmode")) {
                mainArgs.partMode = true;
            } else if (args[i].toLowerCase().equals("-csvmode")) {
                mainArgs.csvMode = Boolean.valueOf(args[++i]);
            } else if (args[i].toLowerCase().equals("-xlspath")) {
                mainArgs.xlspath = args[++i];
            } else if (args[i].toLowerCase().equals("-csvpath")) {
                mainArgs.csvpath = args[++i];
            } else if (args[i].toLowerCase().equals("-ignorefile")) {
                mainArgs.ignorefile = args[++i];
            } else if (args[i].toLowerCase().equals("-csvencode")) {
                mainArgs.csvEncode = args[++i];
            } else if (args[i].toLowerCase().equals("-mode")) {
                mainArgs.genMode = GEN_MODE.valueOf(Integer.parseInt(args[++i]));
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

    static public void main(String[] args) throws Exception {
        parseArgs(args);
        if(mainArgs.genMode == null) {
            usage();
            throw new IllegalArgumentException("no mode");
        }
        switch (mainArgs.genMode) {
            case XML_2_JAVA:
                if (mainArgs.genCodeXmlPath == null || mainArgs.dstdir == null || mainArgs.templatedir == null) {
                    usage();
                    throw new IllegalArgumentException("param not enough");
                }
                new GenJavaBeansFromXml(mainArgs).generate();
                break;
            case EXCEL_2_XML_BEAN:
                if (mainArgs.xlspath == null || mainArgs.dstdir == null || mainArgs.templatedir == null) {
                    usage();
                    throw new IllegalArgumentException("param not enough");
                }
                new GenXmlBeansFromExcel(mainArgs).generate();
                break;
        }

    }

}
