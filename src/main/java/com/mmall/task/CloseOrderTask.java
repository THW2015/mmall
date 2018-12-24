package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedisShardedPool;
import com.mmall.common.RedissonManager;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by 谭皓文 on 2018/9/11.
 */
@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    IOrderService iOrderService;

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV1(){
        log.info("关闭订单定时任务启动");
        iOrderService.closeOrder(Integer.valueOf(PropertiesUtil.getProperty("close.order.task.time.hour","2")));
        log.info("关闭订单定时任务结束");
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV2(){
        log.info("关闭订单定时任务启动");
        Long lockTime = Long.valueOf(PropertiesUtil.getProperty("lock.timeout","5000"));
        Long setResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTime));
        if(setResult != null || setResult == 1){
            CloseOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,lockTime);

        }else{
            String lockValueStr = RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            if(lockValueStr != null && System.currentTimeMillis() > Long.valueOf(lockValueStr)){
                //说明仍然可以获得锁
                String getSetResult = RedisShardedPoolUtil.getset(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTime));

                if(getSetResult == null || (getSetResult != null && StringUtils.equals(getSetResult,lockValueStr))){
                    CloseOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,lockTime);
                }else{
                    log.info("没有获得redis分布式锁{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }

            }else{
                log.info("没有获得redis分布式锁{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
            log.info("没有获得redis分布式锁{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("关闭订单定时任务结束");
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV3(){
        Redisson redisson = new RedissonManager().getRedisson();
        RLock lock = redisson.getLock(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        boolean getLock = false;
        try {
            if(getLock = lock.tryLock(0,5, TimeUnit.SECONDS)){
                log.info("获得分布式锁{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
                int hour = Integer.valueOf(PropertiesUtil.getProperty("lock.timeout","5000"));
                iOrderService.closeOrder(hour);

            }
        } catch (Exception e) {
            log.error("获取锁异常",e);
        } finally {
            if(!getLock){
                return;
            }
            lock.unlock();
            log.info("关闭分布式锁{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        }

    }

    //防止死锁
    public void CloseOrder(String lock,Long timeout){
        RedisShardedPoolUtil.expire(lock,timeout.intValue());
        log.info("获取当前锁{},ThreadName:{}",lock,Thread.currentThread().getName());
        iOrderService.closeOrder(timeout.intValue());
        RedisShardedPoolUtil.del(lock);
        log.info("释放当前锁{},ThreadName:{}",lock,Thread.currentThread().getName());
    }
}
