package com.generator.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Collection;

public class ToLua {

	/*
	 * 判断对象中成员的类型 本来想通过模板特化这样做，但java传过来的都是Object类型的变量
	 * 
	 * 先凑合用这些if来判断了
	 * 
	 * 万物皆对象
	 */
	void writeFile(Field field, Object value, OutputStreamWriter out) throws IOException {
		if(value == null)
			return;
		
		Class<?> cls = field.getType();
		if (cls == Integer.class || cls == int.class) {
			out.write(field.getName() + "=" + (Integer) value );			
			return;
		}
		if (cls == Long.class || cls == long.class) {
			out.write(field.getName() + "=" + (Long) value );
			return;
		}
		if (cls == Boolean.class || cls == boolean.class) {
			out.write(field.getName() + "=" + (Boolean) value );
			return;
		}
		if (cls == Float.class || cls == float.class) {
			out.write(field.getName() + "=" + (Float) value );
			return;
		}
		if (cls == Double.class || cls == double.class) {
			out.write(field.getName() + "=" + (Double) value );
			return;
		}
		if (cls == String.class) {
			String tmpStr = (String)value;
			tmpStr = tmpStr.replace("\"", "\\\"");
			out.write(field.getName() + "=\"" + tmpStr + "\"" );
			return;
		}
		if (Collection.class.isAssignableFrom(cls)) {
			writeFileList((Collection<?>) value, field, out);
			return;
		}
		System.out.println("Error：不能将类型为" + cls.getName() + " --> " + value.getClass() + "的变量写入文件中");
	}
	
	void writeValue(Object value, OutputStreamWriter out) throws IOException {
		if(value == null)
			return;
		
		Class<?> cls = value.getClass();
		if (cls == Integer.class || cls == int.class) {
			out.write("" + (Integer) value);			
			return;
		}
		if (cls == Long.class || cls == long.class) {
			out.write("" + (Long) value );
			return;
		}
		if (cls == Boolean.class || cls == boolean.class) {
			out.write("" + (Boolean) value );
			return;
		}
		if (cls == Float.class || cls == float.class) {
			out.write("" + (Float) value );
			return;
		}
		if (cls == Double.class || cls == double.class) {
			out.write("" + (Double) value );
			return;
		}
		if (cls == String.class) {
			String tmpStr = (String)value;
			tmpStr = tmpStr.replace("\"", "\\\"");
			out.write("\"" + tmpStr + "\"" );
			return;
		}

		System.out.println("Error：不能将类型为" + cls.getName() + " --> " + value.getClass() + "的变量写入文件中");
	}

	void writeFileList(Collection<?> value, Field field, OutputStreamWriter out) throws IOException {
		if (value == null) {
			return;
		}
		out.write(field.getName() + "={ ");
		Boolean needWriteDot = false;
		for (Object v : value) {
			if(needWriteDot)
				out.write(", ");
			needWriteDot = true;
			writeValue(v,out);
		}
		out.write(" }");
	}

	/*
	 * 通过java的反射机制获取对象中定义的所有成员变量
	 * 
	 * 只获取传入对象定义的成员变量，并没有获取父类的成员变量 将对象序列化
	 */
	public static String toLuaString(Object obj, OutputStreamWriter writer) throws IOException {		
		ToLua sx = new ToLua();
		Field[] fields = obj.getClass().getDeclaredFields();
		String idStr = "";
		try {
			Boolean needWriteDot = false;
			for (Field field : fields) {
				// 要序列化的变量必须有IsSaveToFile元注解
				if (field.isAnnotationPresent(IsSaveToFile.class)) {
					field.setAccessible(true);
					try {
						if("id".equals(field.getName()))
						{
							int id = (Integer)field.get(obj);
							idStr += id;
							writer.write("\t["+id+"] = { ");
						}
						
						if(needWriteDot && (field.get(obj) != null))
							writer.write(", ");
						
						if(field.get(obj) != null)
							sx.writeFile(field, field.get(obj), writer);
						
						needWriteDot = true;
					} catch (NullPointerException e) {
						System.out.println("toLua :::::::::::::::: Exception");
						e.printStackTrace();
					}
				}
			}
			writer.write(" }");
			
		} catch (SecurityException e) {
			System.out.println("toLua :::::::::::::::: SecurityException");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("toLua :::::::::::::::: IllegalArgumentException");
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) {
			System.out.println("toLua :::::::::::::::: IllegalAccessException");
			e.printStackTrace();
		}
		
		return idStr;
	}
}
