package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**redis连接池
 * Created by lucky on 2019/3/15.
 */
public class RedisPool {

    private static String IP = PropertiesUtil.getProperty("redis.ip");
    private static Integer PORT = 6379;
    private  static JedisPool jedisPool = null;

    //可用连接实例的最大数目，默认为8；
    //如果赋值为-1，则表示不限制，如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)
    private static Integer MAX_TOTAL = 1024;
    //控制一个pool最多有多少个状态为idle(空闲)的jedis实例，默认值是8
    private static Integer MAX_IDLE = 200;
    //等待可用连接的最大时间，单位是毫秒，默认值为-1，表示永不超时。
    //如果超过等待时间，则直接抛出JedisConnectionException
    private static Integer MAX_WAIT_MILLIS = 10000;
    private static Integer TIMEOUT = 10000;
    //在borrow(用)一个jedis实例时，是否提前进行validate(验证)操作；
    //如果为true，则得到的jedis实例均是可用的
    private static Boolean TEST_ON_BORROW = true;

    static{
        try{
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

            jedisPoolConfig.setMaxTotal(MAX_TOTAL);
            jedisPoolConfig.setMaxWaitMillis(MAX_WAIT_MILLIS);
            jedisPoolConfig.setMaxIdle(MAX_IDLE);
            jedisPoolConfig.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(jedisPoolConfig, IP, PORT, TIMEOUT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis实例
     */
    public synchronized static Jedis getJedis(){
        if(jedisPool != null){
            return jedisPool.getResource();
        }else{
            return null;
        }
    }

    /**
     * 归还连接
     */
    public static void returnResource(final Jedis jedis){
        //方法参数被声明为final，表示它是只读的。
        if(jedis != null){
            //jedis.close()取代jedisPool.returnResource(jedis)方法将3.0版本开始
            jedis.close();
        }
    }
//    public static void main(String[] args){
//        Jedis jedis = getJedis();
//        jedis.set("123", "456");
//        returnResource(jedis);
//    }
}
