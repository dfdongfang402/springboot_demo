package com.generator.utils;

/**
 * Created by wdf on 2018/12/5.
 */
public class Args {
    public  String javaDir = "../game_server/src/main/java";                //生成的代码存放目录
    public  String dstDir = "cfg_xml";                //结果存放目录
    public  String dataXmldir = "../game_server/src/main/java";                //生成的数据xml存放目录
    public  String templatedir = "src/main/resources/templates/";
    public  boolean defineOnly = false;
    public  boolean partMode = false;
    public  boolean csvMode = false;
    public  String xlspath = "xlsdir" ;              //excel 文件路径
    public  String csvpath;               //csv格式文件路径
    public  String genCodeXmlPath = "gen_code_xml/main.xml";        //xml生成beans的主xml文件
    public  String genCodeXmlDir = "gbeans/main.xml";        //根据excel或者csv生成的用于生成代码的xml文件路径
    public  String encode = "utf-8";
    public  String csvEncode = "gbk";
    public  String ignorefile = null;
    public  boolean luaOnly = false;
    public  GEN_MODE genMode = GEN_MODE.XML_2_CPP;


    //生成模式
    public enum GEN_MODE {
        XML_2_JAVA(1),                  // 根据xml定义的bean结构生成java
        XML_2_CPP(2),                   // 根据xml定义的bean结构生成cpp
        EXCEL_2_XML_BEAN(3),            // 根据excel生成描述bean结构xml

        ;

        private final int index;

        GEN_MODE(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public static GEN_MODE valueOf(int index) {
            GEN_MODE[] types = GEN_MODE.values();
            for(GEN_MODE ct : types) {
                if(ct.getIndex() == index) {
                    return ct;
                }
            }
            return null;
        }
    }
}
