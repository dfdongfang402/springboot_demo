package confbeans;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfCheck {

	public interface Checkable{
		public void checkValid(java.util.Map<String, java.util.Map<Integer, ? extends Object>> objs);
	}
	final static private Logger logger = LoggerFactory.getLogger(ConfCheck.class);
	@SuppressWarnings("unchecked")
	public static boolean doCheck(java.util.Map<?, ?> objs){
		boolean ret=true;
		for(Object obj:objs.values()){
			if(!(obj instanceof java.util.Map<?,?>)) continue;
			java.util.Map<Integer,? extends Object> m =(java.util.Map<Integer,? extends Object>)obj;
			for(java.util.Map.Entry<Integer,? extends Object> o:m.entrySet())
			if(o.getValue() instanceof ConfCheck.Checkable){
				try{
					((Checkable)o.getValue()).checkValid((java.util.Map<String,java.util.Map<Integer,? extends Object>>)objs);
				}catch(Exception ex){
					logger.error("key="+o.getKey()+"的记录校验失败,原因是"+ex.getMessage());
					ret=false;
				}
			} 
		}
		return ret;
	}
}
