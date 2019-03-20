package com.mmall.util;

import com.mmall.common.RedisPool;
import redis.clients.jedis.Jedis;

/**
 * Created by lucky on 2019/3/16.
 */
public class RedisPoolUtil {
    public static boolean set(String key, String value){
        Jedis jedis = RedisPool.getJedis();
        jedis.set(key, value);
        return true;
    }
//    public static void main(String[] args){
//        set("123","456");
//    }
}
