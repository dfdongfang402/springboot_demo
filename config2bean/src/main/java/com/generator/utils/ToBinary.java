package com.generator.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

public class ToBinary {

	/*
	 * 判断对象中成员的类型 本来想通过模板特化这样做，但java传过来的都是Object类型的变量
	 * 
	 * 先凑合用这些if来判断了
	 * 
	 * 万物皆对象
	 */
	void writeFile(Class<?> cls, Object value, DataOutputStream out) throws IOException {
		if (cls == Integer.class || cls == int.class) {
			writeFileInt((Integer) value, out);
			return;
		}
		if (cls == Long.class || cls == long.class) {
			writeFileLong((Long) value, out);
			return;
		}
		if (cls == Boolean.class || cls == boolean.class) {
			writeFileBoolean((Boolean) value, out);
			return;
		}
		if (cls == Float.class || cls == float.class) {
			writeFileFloat((Float) value, out);
			return;
		}
		if (cls == Double.class || cls == double.class) {
			writeFileDouble((Double) value, out);
			return;
		}
		if (cls == String.class) {
			writeFileString((String) value, out);
			return;
		}
		if (Collection.class.isAssignableFrom(cls)) {
			writeFileList((Collection<?>) value, out);
			return;
		}
		System.out.println("Error：不能将类型为" + cls.getName() + " --> " + value.getClass() + "的变量写入文件中");
	}

	void writeFileSize(Integer value, DataOutputStream out) throws IOException {
		writeFileInt(value, out);
	}

	void writeFileInt(Integer value, DataOutputStream out) throws IOException {
		if (value == null) {
			out.writeInt(0);
			return;
		}
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ((value) & 0x0ff);
		bytes[1] = (byte) ((value >>> 8) & 0x0ff);
		bytes[2] = (byte) ((value >>> 16) & 0x0ff);
		bytes[3] = (byte) ((value >>> 24) & 0x0ff);
		out.write(bytes);
	}

	void writeFileLong(Long value, DataOutputStream out) throws IOException {
		if (value == null) {
			out.writeLong(0L);
			return;
		}
		byte[] bytes = new byte[8];
		bytes[0] = (byte) ((value) & 0x0ff);
		bytes[1] = (byte) ((value >>> 8) & 0x0ff);
		bytes[2] = (byte) ((value >>> 16) & 0x0ff);
		bytes[3] = (byte) ((value >>> 24) & 0x0ff);
		bytes[4] = (byte) ((value >>> 32) & 0x0ff);
		bytes[5] = (byte) ((value >>> 40) & 0x0ff);
		bytes[6] = (byte) ((value >>> 48) & 0x0ff);
		bytes[7] = (byte) ((value >>> 56) & 0x0ff);
		out.write(bytes);
	}

	void writeFileBoolean(Boolean value, DataOutputStream out) throws IOException {
		if (value == null) {
			out.writeBoolean(false);
			return;
		}
		out.writeBoolean(value);
	}

	void writeFileString(String value, DataOutputStream out) throws IOException {
		if (value == null) {
			writeFileSize(0, out);
			return;
		}
		byte[] bytes = value.getBytes("utf-16le");
		writeFileSize(bytes.length, out);
		out.write(bytes);
	}

	void writeFileFloat(Float value, DataOutputStream out) throws IOException {
		if (value == null) {
			writeFileFloat((float) 0, out);
			return;
		}
		// 打表潜规则，浮点数以long存储，数值 = value * 10000
		Double dvalue = (double) (value * 10000);
		writeFileLong(dvalue.longValue(), out);
		out.writeByte(4); // 代表10^4
	}

	void writeFileDouble(Double value, DataOutputStream out) throws IOException {
		if (value == null) {
			writeFileDouble((double) 0, out);
			return;
		}
		// 打表潜规则，浮点数以long存储，数值 = value * 10000
		Double dvalue = (double) (value * 10000);
		writeFileLong(dvalue.longValue(), out);
		out.writeByte(4); // 代表10^4
	}

	void writeFileList(Collection<?> value, DataOutputStream out) throws IOException {
		if (value == null) {
			out.writeInt(0);
			return;
		}

		// xmlconfig中的坑
		writeFileInt(value.size(), out);
		for (Object v : value) {
			writeFile(v.getClass(), v, out);
		}
	}

	/*
	 * 通过java的反射机制获取对象中定义的所有成员变量
	 * 
	 * 只获取传入对象定义的成员变量，并没有获取父类的成员变量 将对象序列化
	 */
	public static void toBinary(Object obj, DataOutputStream out) throws IOException {
		ToBinary sx = new ToBinary();
		Field[] fields = obj.getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				// 要序列化的变量必须有IsSaveToFile元注解
				if (field.isAnnotationPresent(IsSaveToFile.class)) {
					field.setAccessible(true);
					try {
						sx.writeFile(field.getType(), field.get(obj), out);
					} catch (NullPointerException e) {
						sx.writeFile(field.getType(), null, out);
					}
				}
			}
		} catch (SecurityException e) {
			System.out.println("toBIN :::::::::::::::: SecurityException");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("toBIN :::::::::::::::: IllegalArgumentException");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println("toBIN :::::::::::::::: IllegalAccessException");
			e.printStackTrace();
		}
	}
}
