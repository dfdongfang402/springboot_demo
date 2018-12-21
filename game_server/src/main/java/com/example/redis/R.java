package com.example.redis;

public class R {

    private final static RedisProxy local = new RedisProxy(LocalJedisPool.INSTANCE);

    public static RedisProxy Local(){
        return local;

    }

    public static RedisProxy Global(){
        return null;
    }

    public static RedisProxy Remote(int serverId){
        return new RedisProxy(new RemoteJedisPool(serverId));

    }

}
