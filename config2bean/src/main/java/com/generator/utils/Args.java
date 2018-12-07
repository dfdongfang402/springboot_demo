package com.generator.utils;

/**
 * Created by wdf on 2018/12/5.
 */
public class Args {
    public  String dstdir = "../game_server/src/main/java";                //生成的代码存放目录
    public  String templatedir = "src/main/resources/templates/";
    public  boolean defineOnly = true;
    public  boolean partMode = false;
    public  boolean csvMode = false;
    public  String xlspath = "excel" ;              //excel 文件路径
    public  String csvpath;               //csv格式文件路径
    public  String genCodeXmlPath = "genxml/main.xml";        //根据excel或者csv生成的用于生成代码的xml文件路径
    public  String encode = "utf-8";
    public  String csvEncode = "gbk";
    public  String ignorefile = null;
    public  boolean luaOnly = false;
    public  GEN_MODE genMode = GEN_MODE.XML_2_JAVA;


    //生成模式
    public enum GEN_MODE {
        XML_2_JAVA(1),                  // 根据xml定义的bean结构生成java
        XML_2_CPP(2),                   // 根据xml定义的bean结构生成cpp
        EXCEL_2_XML_BEAN(3),            // 根据excel生成描述bean结构xml
        EXCEL_AND_XMLBEAN_2_XMLDATA(3), // 根据excel和bean结构xml生成数据配置的xml文件

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
