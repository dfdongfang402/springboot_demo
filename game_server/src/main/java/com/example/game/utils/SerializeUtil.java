package com.example.game.utils;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum SerializeUtil {

    INSTANCE;

	private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

	private static Objenesis objenesis = new ObjenesisStd(true);

	@SuppressWarnings("unchecked")
	private static <T> Schema<T> getSchema(Class<T> cls) {
		Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
		if (schema == null) {
			schema = RuntimeSchema.createFrom(cls);
			if (schema != null) {
				cachedSchema.put(cls, schema);
			}
		}
		return schema;
	}

	/**
	 * 序列化（对象 -> 字节数组）
	 */
	@SuppressWarnings("unchecked")
	public static <T> byte[] serialize(T obj) {
		Class<T> cls = (Class<T>) obj.getClass();
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try {
			Schema<T> schema = getSchema(cls);
			return ProtobufIOUtil.toByteArray(obj, schema, buffer);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			buffer.clear();
		}
	}

	public static <T> T deserialize(ByteBuffer data, Class<T> cls) {
		return deserialize(data.array(), data.position(), data.remaining(), cls);
	}

	public static <T> T deserialize(byte[] data, Class<T> cls) {
		return deserialize(data, 0, data.length, cls);
	}

	/**
	 * 反序列化（字节数组 -> 对象）
	 */
	public static <T> T deserialize(byte[] data, int offset, int size, Class<T> cls) {
		try {
			T message = objenesis.newInstance(cls);
			Schema<T> schema = getSchema(cls);
            ProtobufIOUtil.mergeFrom(data, offset, size, message, schema);
			return message;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
