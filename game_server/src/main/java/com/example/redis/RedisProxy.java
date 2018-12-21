package com.example.redis;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Tuple;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisProxy {

    private IRedisPrivider redisPrivider;

    RedisProxy(IRedisPrivider privider) {
        this.redisPrivider = privider;
    }

    private Jedis getJedis() {
        return redisPrivider.getJedisClient();
    }

    public String set(String key, String field) {
        Jedis jedis = getJedis();
        try {
            return jedis.set(key, field);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public String set(byte[] key, byte[] field) {
        Jedis jedis = getJedis();
        try {
            return jedis.set(key, field);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public int hsetnx(String key, String field, String value) {
        Jedis jedis = getJedis();
        try {
            long result = jedis.hsetnx(key, field, value);
            return (int) result;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public long setnx(String key, String value) {
        Jedis jedis = getJedis();
        try {
            long ret = jedis.setnx(key, value);
            return ret;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public Set<String> sMember(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.smembers(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public long sRem(String key, String... value) {
        Jedis jedis = getJedis();
        try {
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public boolean sIsMember(String key, String member) {
        Jedis jedis = getJedis();
        try {
            return jedis.sismember(key, member);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return false;
    }

    public long sAdd(String key, String... value) {
        Jedis jedis = getJedis();
        try {
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public void del(byte[] key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
    }

    public void del(String key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
    }

    public void rename(String key, String newKey) {
        Jedis jedis = getJedis();
        try {
            jedis.rename(key, newKey);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
    }

    public long lPush(byte[] key, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public long lPush(String key, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public List<String> lRange(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public Long llen(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.llen(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public String rPop(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.rpop(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public String lPop(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.lpop(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public String rPoplPush(String skey, String dkey) {
        Jedis jedis = getJedis();
        try {
            return jedis.rpoplpush(skey, dkey);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public void lRem(byte[] key, int count, byte[] field) {
        Jedis jedis = getJedis();
        try {
            jedis.lrem(key, count, field);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
    }

    @Deprecated
    public Set<String> keys(String pattern) {
        Jedis jedis = getJedis();
        try {
            return jedis.keys(pattern);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public String lIndex(String key, long count) {
        Jedis jedis = getJedis();
        try {
            return jedis.lindex(key, count);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public long rPush(String key, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.rpush(key, value);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public String get(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public Map<String, String> mget(Collection<String> keys) {
        return mget(keys, true, "", false);
    }

    public Map<String, String> mget(Collection<String> keys, String keyPrefix) {
        return mget(keys, true, keyPrefix, false);
    }

    /**
     * @param keys
     * @param skipNull
     * @param keyPrefix
     * @param keepPrefix
     * @return
     */
    public Map<String, String> mget(Collection<String> keys, boolean skipNull, String keyPrefix, boolean keepPrefix) {
        if (keys == null || keys.size() == 0) {
            return Collections.EMPTY_MAP;
        }
        String[] keyArr = new String[keys.size()];
        keys.toArray(keyArr);
        return mget(keyArr, skipNull, keyPrefix, keepPrefix);
    }

    public Map<String, String> mget(String... keys) {
        return mget(keys, true, "", true);
    }

    /**
     * 批量获取keys的值，返回一个map
     *
     * @param keys       需要获取的key数组
     * @param skipNull   是否忽略掉null值的key
     * @param keyPrefix  所有key需要增加的prefix
     * @param keepPrefix 返回的map中，key是否包含prefix的值
     * @return
     */
    public Map<String, String> mget(String[] keys, boolean skipNull, String keyPrefix, boolean keepPrefix) {
        if (keys == null || keys.length == 0) {
            return Collections.EMPTY_MAP;
        }
        String[] keysWithPrefix = getKeysWithPrefix(keys, keyPrefix);
        return multiGet(keys, skipNull, keysWithPrefix, keepPrefix);
    }

    private Map<String, String> multiGet(String[] keys, boolean skipNull, String[] keysWithPrefix, boolean keepPrefix) {
        Jedis jedis = getJedis();
        try {
            Map<String, String> ret;
            List<String> value = jedis.mget(keysWithPrefix);
            ret = new HashMap<>(value.size());
            String val;
            for (int i = 0; i < keys.length; ++i) {
                val = value.get(i);
                if (skipNull && val == null) {
                    continue;
                }
                if (keepPrefix) {
                    ret.put(keysWithPrefix[i], val);
                } else {
                    ret.put(keys[i], val);
                }
            }
            return ret;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    private String[] getKeysWithPrefix(String[] keys, String keyPrefix) {
        String[] prefKeys;
        // 这里用blank判断，是要求prefix不能是空格
        if (StringUtils.isNotBlank(keyPrefix)) {
            prefKeys = new String[keys.length];
            for (int i = 0; i < keys.length; ++i) {
                prefKeys[i] = keyPrefix + keys[i];
            }
        } else {
            prefKeys = keys;
        }
        return prefKeys;
    }

    public byte[] get(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public long incrBy(String key, long incValue) {
        Jedis jedis = getJedis();
        try {
            return jedis.incrBy(key, incValue);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public long incr(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.incr(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public long decr(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.decr(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public byte[] hGet(byte[] key, byte[] field) {
        Jedis jedis = getJedis();
        try {
            return jedis.hget(key, field);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public String hGet(String key, String field) {
        Jedis jedis = getJedis();
        try {
            return jedis.hget(key, field);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public List<String> hMGet(String key, String... field) {
        Jedis jedis = getJedis();
        try {
            return jedis.hmget(key, field);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public List<String> hMGet(String key, List<String> fields) {
        Jedis jedis = getJedis();
        try {
            String[] arr = new String[fields.size()];
            int i = 0;
            for (String item : fields) {
                arr[i++] = item;
            }
            return jedis.hmget(key, arr);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public Map<String, String> hMGetAll(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hgetAll(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public long hLen(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hlen(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }


    public void setExpireTime(String key, int seconds) {
        Jedis jedis = getJedis();
        try {
            jedis.expire(key, seconds);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
    }
    public boolean exists(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.exists(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return false;
    }

    //TODO::::not perfect
    public String hMSet(String key, String nestedKey, String value) {
        Jedis jedis = getJedis();
        try {
            Map<String, String> map = new HashMap<>();
            map.put(nestedKey, value);
            return jedis.hmset(key, map);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public String hMSet(String key, Map<String, String> map) {
        Jedis jedis = getJedis();
        try {
            return jedis.hmset(key, map);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public void mset(Map<String, String> map) {
        if (map == null || map.size() == 0) {
            return;
        }
        String keyValues[] = new String[map.size() * 2];
        int index = 0;
        for (Map.Entry<String, String> mapEntry : map.entrySet()) {
            keyValues[index++] = mapEntry.getKey();
            keyValues[index++] = mapEntry.getValue();
        }
        Jedis jedis = getJedis();
        try {
            jedis.mset(keyValues);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
    }

    public List<String> hVal(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hvals(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public long hDel(String key, List<String> fields) {
        return hDel(key, fields.toArray(new String[fields.size()]));
    }

    public long hDel(String key, String... fields) {
        Jedis jedis = getJedis();
        try {
            return jedis.hdel(key, fields);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public long hDel(byte[] key, byte[] field) {
        Jedis jedis = getJedis();
        try {
            return jedis.hdel(key, field);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public long hSet(byte[] key, byte[] field, byte[] value) {
        Jedis jedis = getJedis();
        try {
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }


    public long hSet(String key, String field, String value) {
        Jedis jedis = getJedis();
        try {
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public Set<byte[]> hKeys(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hkeys(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public Set<String> hKeys(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hkeys(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public long hLen(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.hlen(key);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public boolean hExists(String key, String field) {
        Jedis jedis = getJedis();
        try {
            boolean ret = jedis.hexists(key, field);
            return ret;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return false;
    }

    public long pushListFromLeft(String key, String... members) {
        Jedis jedis = getJedis();
        try {
            long ret = jedis.lpush(key, members);
            return ret;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public String lSet(String key, long index, String members) {
        Jedis jedis = getJedis();
        try {
            String ret = jedis.lset(key, index, members);
            return ret;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public List<String> getRangeList(String key, int start, int end) {
        Jedis jedis = getJedis();
        try {
            List<String> retList = jedis.lrange(key, start, end);
            return retList;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public Set<Tuple> getZrevrangeWithScores(String key, int start, int end) {
        Jedis jedis = getJedis();
        try {
            Set<Tuple> retList = jedis.zrevrangeWithScores(key, start, end);
            return retList;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public long getLength(String key) {
        Jedis jedis = getJedis();
        try {
            long length = jedis.llen(key);
            return length;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public List<byte[]> getRangeList(byte[] key, int start, int end) {
        Jedis jedis = getJedis();
        try {
            List<byte[]> retList = jedis.lrange(key, start, end);
            return retList;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public long lRem(String key, int count, String value) {
        Jedis jedis = getJedis();
        long lremNum = 0;
        try {
            lremNum = jedis.lrem(key, count, value);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return lremNum;
    }

    public void lTrim(String key, int start, int end) {
        Jedis jedis = getJedis();
        try {
            jedis.ltrim(key, start, end);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
    }

    public String deleteListFromRight(String key) {
        Jedis jedis = getJedis();
        try {
            String retList = jedis.rpop(key);
            return retList;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public long hincrBy(String key, String field, long num) {
        Jedis jedis = getJedis();
        try {
            return jedis.hincrBy(key, field, num);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public double zincrby(String key, String member, double score) {
        Jedis jedis = getJedis();
        try {
            double res = jedis.zincrby(key, score, member);
            return res;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public long zAdd(String key, String member, long score) {
        Jedis jedis = getJedis();
        try {
            return jedis.zadd(key, (double) score, member);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public long zRem(String key, String member) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrem(key, member);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public long zAdd(String key, Map<String, Double> memberScores) {
        Jedis jedis = getJedis();
        try {
            return jedis.zadd(key, memberScores);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public double zincrBy(String key, String member, long score) {
        Jedis jedis = getJedis();
        try {
            return jedis.zincrby(key, (double) score, member);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public double zDel(String key, String... member) {
        Jedis jedis = getJedis();
        try {
            return jedis.zrem(key, member);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public com.google.common.base.Optional<Double> zScore(String key, String member) {
        Jedis jedis = getJedis();
        try {
            return com.google.common.base.Optional.fromNullable(jedis.zscore(key, member));
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public com.google.common.base.Optional<Long> zGetRankByAsc(String key, String member) {
        Jedis jedis = getJedis();
        try {
            return com.google.common.base.Optional.fromNullable(jedis.zrank(key, member));
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public com.google.common.base.Optional<Long> zGetRankByDesc(String key, String member) {
        Jedis jedis = getJedis();
        try {
            return com.google.common.base.Optional.fromNullable(jedis.zrevrank(key, member));
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public HashMap<String, Double> zRangByAsc(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            HashMap<String, Double> ret = new HashMap<>();
            Set<Tuple> result = jedis.zrangeWithScores(key, start, end);
            if (result != null && result.size() > 0) {
                for (Tuple member : result) {
                    ret.put(member.getElement(), member.getScore());
                }
            }
            return ret;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }


    public Map<String, Double> zrevrangeWithScores(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            Map<String, Double> ret = new HashMap<>();
            Set<Tuple> result = jedis.zrevrangeWithScores(key, start, end);
            if (result != null && result.size() > 0) {
                for (Tuple member : result) {
                    ret.put(member.getElement(), member.getScore());
                }
            }
            return ret;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public int zrevrank(String key, String member) {
        Jedis jedis = getJedis();
        try {
            Long rank = jedis.zrevrank(key, member);
            return rank == null ? 0 : rank.intValue() + 1;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return ISFSObject: key(String), score(Double)
     */
    //BAD DESIGN.
    public List<JSONObject> zRangByDesc(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            List<JSONObject> retList = new LinkedList<>();
            Set<Tuple> result = jedis.zrevrangeWithScores(key, start, end);
            if (result != null && result.size() > 0) {
                for (Tuple member : result) {
                    JSONObject obj = new JSONObject();
                    obj.put("key", member.getElement());
                    obj.put("score", member.getScore());
                    retList.add(obj);
                }
            }
            return retList;
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return null;
    }

    public void subscribe(JedisPubSub listener, List<String> channels) {
        Jedis jedis = getJedis();
        try {
            jedis.subscribe(listener, channels.toArray(new String[]{}));
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
    }

    public long publish(String channel, String msg) {
        Jedis jedis = getJedis();
        try {
            return jedis.publish(channel, msg);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }

    public void setPExpireTime(String key, long seconds) {
        Jedis jedis = getJedis();
        try {
            jedis.expireAt(key, seconds);
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
    }

    public void flushDB() {
        Jedis jedis = getJedis();
        try {
            jedis.flushDB();
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
    }

    public long dbSize() {
        Jedis jedis = getJedis();
        try {
            return jedis.dbSize();
        } catch (Exception e) {
            logger.error("ERROR", e);
        } finally {
            jedis.close();
        }
        return 0;
    }


    private static final Logger logger = LoggerFactory.getLogger(RedisProxy.class);

}
