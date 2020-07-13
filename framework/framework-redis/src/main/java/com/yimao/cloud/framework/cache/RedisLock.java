package com.yimao.cloud.framework.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 描述：redis分布式锁
 *
 * @Author Zhang Bo
 * @Date 2019/7/11
 */
@Component
@Slf4j
public class RedisLock {

    private static final int defaultExpire = 20;

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "valueOperations")
    private ValueOperations<String, String> valueOperations;

    // public boolean lock(String key) {
    //     return lock(key, defaultExpire);
    // }

    // /**
    //  * 加锁
    //  *
    //  * @param key    key
    //  * @param expire 过期时间，单位秒
    //  * @return true-加锁成功；false-加锁失败；
    //  */
    // public boolean lock(String key, int expire) {
    //     Boolean b = valueOperations.setIfAbsent(key, "1");
    //     if (b != null && b) {
    //         redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    //         return true;
    //     }
    //     return false;
    // }
    //
    // /**
    //  * 解锁
    //  *
    //  * @param key key
    //  */
    // public void unLock(String key) {
    //     redisTemplate.delete(key);
    // }

    /**
     * 获取分布式锁（一直等待，直到获取到锁）
     *
     * @param key 每个锁都对应一个全局唯一的键
     */
    public boolean lock(String key) {
        long start = System.currentTimeMillis();
        while (true) {
            if (lock(key, defaultExpire)) {
                return true;
            }
            long end = System.currentTimeMillis();
            log.info("lock---key={}，在等待获取锁，已等待了{}毫秒", key, end - start);
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取分布式锁（millis毫秒没获取到就返回false）
     *
     * @param key    每个锁都对应一个全局唯一的键
     * @param millis 等待获取锁的超时时间，单位：毫秒
     */
    public boolean lockWithTimeout(String key, long millis) {
        long start = System.currentTimeMillis();
        while (true) {
            if (lock(key, defaultExpire)) {
                return true;
            }
            long end = System.currentTimeMillis();
            log.info("lockWithTimeout---key={}，在等待获取锁，已等待了{}毫秒", key, end - start);
            //获取锁的等待时间为30秒，超过此时间返回false，表示没有获取到锁
            if (end - start > millis) {
                return false;
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加锁<br/>
     * 优化了lock中出现死锁的风险
     *
     * @param key    key
     * @param expire 过期时间，单位秒
     * @return true-加锁成功；false-加锁失败；
     */
    public boolean lock(String key, int expire) {
        //1、setnx(lockkey, 当前时间+过期超时时间)，如果返回1，则获取锁成功；如果返回0则没有获取到锁，转向2。
        long value = System.currentTimeMillis() + expire * 1000;
        Boolean b = valueOperations.setIfAbsent(key, String.valueOf(value));
        if (b != null && b) {
            return true;
        }
        //2、get(lockkey)获取值oldExpireTime，并将这个value值与当前的系统时间进行比较，
        //如果小于当前系统时间，则认为这个锁已经超时，可以允许别的请求重新获取，转向3。
        String v = valueOperations.get(key);
        long oldExpireTime = Long.parseLong(v == null ? "0" : v);
        if (oldExpireTime < System.currentTimeMillis()) {
            //3、计算newExpireTime=当前时间+过期超时时间，然后getset(lockkey, newExpireTime)会返回当前lockkey的值currentExpireTime。
            long newExpireTime = System.currentTimeMillis() + expire;
            v = valueOperations.getAndSet(key, String.valueOf(newExpireTime));
            long currentExpireTime = Long.parseLong(v == null ? "0" : v);
            //判断currentExpireTime与oldExpireTime是否相等，如果相等，说明当前getset设置成功，获取到了锁。
            //如果不相等，说明这个锁又被别的请求获取走了，那么当前请求可以直接返回失败，或者继续重试。
            if (currentExpireTime == oldExpireTime) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解锁
     *
     * @param key key
     */
    public void unLock(String key) {
        //在获取到锁之后，当前线程可以开始自己的业务处理，当处理完毕后，比较自己的处理时间和对于锁设置的超时时间，如果小于锁设置的超时时间，则直接执行 delete 释放锁；
        //如果大于锁设置的超时时间，则不需要再锁进行处理。
        String v = valueOperations.get(key);
        long oldExpireTime = Long.parseLong(v == null ? "0" : v);
        if (oldExpireTime > System.currentTimeMillis()) {
            redisTemplate.delete(key);
        }
    }


}
