package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 谭皓文 on 2018/9/2.
 */
public class RedisShardedPool {
    private static ShardedJedisPool pool; //jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20")); //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","20")); //最大空闲jedis实例数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","20")); //最小空闲jedis实例数
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","false"));//在borrow一个jedis实例的时候，是否进行验证操作，如果赋值true,则得到的jedis肯定可以用的
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","false"));;//在return一个jedis实例的时候，是否进行验证操作，如果赋值true,则返回的jedis肯定可以用的
    private static String redisIp1 = PropertiesUtil.getProperty("redis.ip1");
    private static Integer redisPort1 = Integer.valueOf(PropertiesUtil.getProperty("redis.port1"));
    private static String redisIp2 = PropertiesUtil.getProperty("redis.ip2");
    private static Integer redisPort2 = Integer.valueOf(PropertiesUtil.getProperty("redis.port2"));
    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true); //连接耗尽时，false抛出异常，默认为true
        JedisShardInfo info1 = new JedisShardInfo(redisIp1,redisPort1,1000*2);
        JedisShardInfo info2 = new JedisShardInfo(redisIp2,redisPort2,1000*2);
        List<JedisShardInfo> jedisShardInfoList = new ArrayList<>(2);
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        pool = new ShardedJedisPool(config,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);

    }

    static {
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
        for(int i = 0;i < 10;i++){
            jedis.set("key"+i,"value"+i);
        }
        returnResource(jedis);
        System.out.println("program is end");
    }

}
