package com.yimao.cloud.framework.configuration;


import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class FastJsonRedisSerializer implements RedisSerializer<Object> {
	  public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	    private Class<Object> clazz;

	    public FastJsonRedisSerializer(Class<Object> clazz) {
	        super();
	        this.clazz = clazz;
	    }

	    @Override
	    public byte[] serialize(Object t) throws SerializationException {
	        if (t == null) {
	            return new byte[0];
	        }
	        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
	    }

	    @Override
	    public Object deserialize(byte[] bytes) throws SerializationException {
	        if (bytes == null || bytes.length <= 0) {
	            return null;
	        }
	        String str = new String(bytes, DEFAULT_CHARSET);
	        return (Object) JSON.parseObject(str, clazz);
	    }
}
