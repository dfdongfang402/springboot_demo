package confbeans;

<#if !defineOnly>
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
</#if>

public class ConfCheck {

public interface Checkable{
public void checkValid(java.util.Map
<String,java.util.Map
<Integer,? extends Object> > objs);
}
final static private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfCheck.class);
<#if !defineOnly>
public static String getCellValue(Row w,String name,java.util.ArrayList
<String> collnames){
    int index=collnames.indexOf(name);
    if(index<0) throw new IllegalArgumentException("错误的参数,name="+name);
    Cell c=w.getCell(index);
    if(c==null){
    return null;
    }
    switch(c.getCellType()){
    case BLANK:
    return null;
    case NUMERIC:
    double d = c.getNumericCellValue();
    if((int)d == d)
    return String.valueOf((int)d);
    else
    return String.valueOf(d);
    case STRING:
    return c.getStringCellValue();
    case BOOLEAN:
    return String.valueOf(c.getBooleanCellValue());
    default:
    throw new RuntimeException("在第"+w.getRowNum()+"行的\""+name+"\"字段中发现未知的cell类型"+c.getCellType());
    }
    }

    public static Integer getIntegerCellValue(Row w,String name,java.util.ArrayList
<String> collnames){
    int index=collnames.indexOf(name);
    if(index<0) throw new IllegalArgumentException("错误的参数,name="+name);
    Cell c=w.getCell(index);
    if(c==null){
    return null;
    }
    switch(c.getCellType()){
    case BLANK:
    return null;
    case NUMERIC:
    return (int)c.getNumericCellValue();
    case STRING:
    return Integer.valueOf(c.getStringCellValue());
    default:
    throw new RuntimeException("未知的cell类型");
    }
    }

    public static Long getLongCellValue(Row w,String name,java.util.ArrayList
<String> collnames){
    int index=collnames.indexOf(name);
    if(index<0) throw new IllegalArgumentException("错误的参数,name="+name);
    Cell c=w.getCell(index);
    if(c==null){
    return null;
    }
    switch(c.getCellType()){
    case BLANK:
    return null;
    case NUMERIC:
    return (long)c.getNumericCellValue();
    case STRING:
    return Long.valueOf(c.getStringCellValue());
    default:
    throw new RuntimeException("未知的cell类型");
    }
    }

    public static Boolean getBooleanCellValue(Row w,String name,java.util.ArrayList
<String> collnames){
    int index=collnames.indexOf(name);
    if(index<0) throw new IllegalArgumentException("错误的参数,name="+name);
    Cell c=w.getCell(index);
    if(c==null){
    return null;
    }
    switch(c.getCellType()){
    case BLANK:
    return null;
    case NUMERIC:
    return c.getNumericCellValue()!=0;
    case STRING:
    {
    String t=c.getStringCellValue();
    if(t.equals("是") || t.equals("true"))
    return true;
    else if(t.equals("否") || t.equals("false"))
    return false;
    else
    throw new RuntimeException("在第"+w.getRowNum()+"行的\""+name+"\"字段中发现错误的值"+t);
    }
    default:
    throw new RuntimeException("未知的cell类型");
    }
    }

    public static Double getDoubleCellValue(Row w,String name,java.util.ArrayList
<String> collnames){
    int index=collnames.indexOf(name);
    if(index<0) throw new IllegalArgumentException("错误的参数,name="+name);
    Cell c=w.getCell(index);
    if(c==null){
    return null;
    }
    switch(c.getCellType()){
    case BLANK:
    return null;
    case NUMERIC:
    return c.getNumericCellValue();
    case STRING:
    return Double.valueOf(c.getStringCellValue());
    default:
    throw new RuntimeException("未知的cell类型");
    }
    }
</#if>
    private static void usage(){
    System.out.println("usage: java -jar xxx.jar inputdir server_outputdir client_outputdir");
    }

    private static final String[] serverClassList={
<#list serverClassList as classname>
    "${classname}",
</#list>
    };

    private static final String[] clientClassList={
<#list clientClassList as classname>
    "${classname}",
</#list>
    };

    @SuppressWarnings("unchecked")
    public static boolean doCheck(java.util.Map objs){
    boolean ret=true;
    for(Object obj:objs.values()){
    if(!(obj instanceof java.util.Map<?,?>)) continue;
    java.util.Map
    <Integer,? extends Object> m =(java.util.Map
    <Integer,? extends Object>)obj;
    for(java.util.Map.Entry
    <Integer,? extends Object> o:m.entrySet())
    if(o.getValue() instanceof confbeans.ConfCheck.Checkable){
    try{
    ((confbeans.ConfCheck.Checkable)o.getValue()).checkValid((java.util.Map
    <String,java.util.Map
    <Integer,? extends Object>>)objs);
    }catch(Exception ex){
    logger.error("key="+o.getKey()+"的记录校验失败,原因是"+ex.getMessage());
    ret=false;
    }
    }
    }
    return ret;
    }
<#if !defineOnly>

    @SuppressWarnings("unchecked")
    public static java.util.Map
    <String,java.util.Map
    <Integer,? extends Object> > doConv(String inputdir,String outputdir,String outputdir2){
    java.util.Map
    <String,java.util.Map
    <Integer,? extends Object> > objs= new java.util.TreeMap
    <String,java.util.Map
    <Integer,? extends Object> >();
    com.thoughtworks.xstream.XStream xstream=new com.thoughtworks.xstream.XStream();
    try{
    <#list serverClassList as classname>
        objs.put("${classname}",${classname}.toXML(inputdir, outputdir, xstream));
    </#list>
    <#list clientClassList as classname>
        objs.put("${classname}",${classname}.toXML(inputdir, outputdir2, xstream));
    </#list>
    }catch(java.io.IOException ex){
    logger.error("io错误",ex);
    }
    return objs;
    }

    /**
    * @param args
    */
    public static void main(String[] args) throws Exception {
    if(args.length!=3) {
    usage();
    return ;
    }
    String inputdir=args[0];
    String outputdir=args[1];
    String outputdir2=args[2];

    if(!new java.io.File(inputdir).exists())
    throw new RuntimeException("目录错误，"+inputdir+"不存在");
    if(!new java.io.File(outputdir).exists())
    throw new RuntimeException("目录错误，"+outputdir+"不存在");
    if(!new java.io.File(outputdir2).exists())
    throw new RuntimeException("目录错误，"+outputdir2+"不存在");
    doCheck(doConv(inputdir,outputdir,outputdir2));
    }
</#if>
    }
