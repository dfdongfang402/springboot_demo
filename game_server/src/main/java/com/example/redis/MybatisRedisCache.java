package com.example.redis;

import com.example.game.utils.SerializeUtil;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author wang dongfang
 * @ClassName MybatisRedisCache.java
 * @Description TODO
 * @createTime 2018年12月21日 11:02:00
 */
public class MybatisRedisCache implements Cache {

    private static Logger logger = LoggerFactory.getLogger(MybatisRedisCache.class);
    /** The ReadWriteLock. */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private String id;
    private byte[] idByte;

    public MybatisRedisCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>MybatisRedisCache:id="+id);
        this.id = id;
        this.idByte = SerializeUtil.serialize(id);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
        logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>putObject:"+key+"="+value);
        if(value == null){
            return;
        }
        DataWraper data = new DataWraper();
        data.setData(value);
        R.Local().hSet(idByte,SerializeUtil.serialize(key.toString()), SerializeUtil.serialize(value));
    }

    @Override
    public Object getObject(Object key) {
        byte[] bytes = R.Local().hGet(idByte, SerializeUtil.serialize(key.toString()));
        if(bytes == null || bytes.length == 0){
            return null;
        }
        DataWraper dataWraper = SerializeUtil.deserialize(bytes, DataWraper.class);
        logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>getObject:"+key+"="+dataWraper);
        return dataWraper.getData();
    }

    @Override
    public Object removeObject(Object key) {
        return R.Local().hDel(idByte, SerializeUtil.serialize(key.toString()));
    }

    @Override
    public void clear() {
        R.Local().del(idByte);
    }

    @Override
    public int getSize() {
        return (int) R.Local().hLen(id.getBytes());
    }

    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }
}

