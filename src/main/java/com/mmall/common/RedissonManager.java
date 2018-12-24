package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by 谭皓文 on 2018/9/18.
 */
@Component
@Slf4j
public class RedissonManager {
    private Config config = new Config();

    private Redisson redisson = null;

    public Redisson getRedisson(){
        return redisson;
    }
    private static String redisIp1 = PropertiesUtil.getProperty("redis.ip1");
    private static Integer redisPort1 = Integer.valueOf(PropertiesUtil.getProperty("redis.port1"));

    private static String redisIp2 = PropertiesUtil.getProperty("redis.ip2");
    private static Integer redisPort2 = Integer.valueOf(PropertiesUtil.getProperty("redis.port2"));

    @PostConstruct
    public void init(){
        try {
            config.useSingleServer().setAddress(new StringBuffer("redisIp1").append(":").append(redisPort1).toString());
            redisson = (Redisson) Redisson.create(config);

            log.info("初始Redisson结束");
        } catch (Exception e) {
            log.error("redisson init error",e);
        }

    }

}
