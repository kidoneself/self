package com.yimao.cloud.framework.cache;

import com.google.gson.Gson;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhang Bo
 * @date 2018/7/10.
 */
@Component
public class RedisCache {

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "redisTemplateMini")
    private RedisTemplate<String, String> redisTemplateMini;
    @Resource(name = "valueOperations")
    private ValueOperations<String, String> valueOperations;
    @Resource(name = "hashOperations")
    private HashOperations<String, String, Object> hashOperations;
    
    //默认过期时长，单位：秒
    private final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    //不设置过期时长
    private final static long NOT_EXPIRE = -1;
    //private final static int DEFAULT_DATABASE = 0;
    //谷歌的JSON转换工具
    private final static Gson gson = new Gson();

    /**
     * 获取过期时间
     *
     * @param key 健
     */
    public int getExpire(String key) {
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (expire != null) {
            return expire.intValue();
        }
        return 0;
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     * @param expire
     */
    public void set(String key, Object value, long expire) {
        valueOperations.set(key, toJson(value));
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE);
    }

    /**
     * 获取指定类型对象
     *
     * @param key
     * @param clazz
     * @param expire
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    /**
     * 获取指定类型对象
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    /**
     * 获取String类型对象
     *
     * @param key
     * @param expire
     * @return
     */
    public String get(String key, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return get(key, NOT_EXPIRE);
    }

    /**
     * 根据key删除缓存的值
     *
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 根据key判断缓存是否存在
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
    
//-----------redis hash应用-----------------------
    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key,String item){
        return hashOperations.get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<String,Object> hmget(String key){
        return hashOperations.entries(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String,Object> map){
        try {
        	hashOperations.putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param expire 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String,Object> map, long expire){
        try {
        	hashOperations.putAll(key, map);
            if(expire != NOT_EXPIRE){
            	redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value) {
        try {
        	hashOperations.put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param expire 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value,long expire) {
        try {
        	hashOperations.put(key, item, value);
            if(expire != NOT_EXPIRE){
            	redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item){
    	hashOperations.delete(key,item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item){
        return hashOperations.hasKey(key, item);
    }
       

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }

        return gson.toJson(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public <T> void setCacheList(String key, Object value, Class<T> clazz) {
        setCacheList(key, value, clazz, NOT_EXPIRE);
    }

    public <T> void setCacheList(String key, Object value, Class<T> clazz, long expire) {
        valueOperations.set(key, parseList2String(value, clazz));
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    public <T> List<T> getCacheList(String key, Class<T> clazz) {
        return getCacheList(key, clazz, NOT_EXPIRE);
    }

    public <T> List<T> getCacheList(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : parseString2List(value, clazz);
    }

    private <T> String parseList2String(Object object, Class<T> clazz) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        Type type = new ParameterizedTypeImpl(clazz);
        return gson.toJson(object, type);
    }

    private <T> List<T> parseString2List(String json, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(clazz);
        return gson.fromJson(json, type);
    }

    private class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    /**
     * 健康自测小程序专用
     *
     * @param key
     * @param value
     * @param expire
     */
    public void setForHealthTestMini(String key, String value, long expire) {
        redisTemplateMini.opsForValue().set(key, value);
        if (expire != NOT_EXPIRE) {
            redisTemplateMini.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    /**
     * 健康自测小程序专用
     *
     * @param key
     * @return
     */
    public String getForHealthTestMini(String key) {
        return redisTemplateMini.opsForValue().get(key);
    }

}
