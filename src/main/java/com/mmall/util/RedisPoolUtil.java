package com.mmall.util;

import com.mmall.common.RedisPool;
import redis.clients.jedis.Jedis;

/**
 * Created by lucky on 2019/3/16.
 */
public class RedisPoolUtil {
    public static String set(String key, String value){
        Jedis jedis = RedisPool.getJedis();
        String res = jedis.set(key, value);
        RedisPool.returnResource(jedis);
        return res;
    }

    public static Long expire(String key, int time){
        Jedis jedis = RedisPool.getJedis();
        Long res = jedis.expire(key, time);
        RedisPool.returnResource(jedis);
        return res;
    }

//    public static void main(String[] args){
//        set("123","456");
//    }
}
