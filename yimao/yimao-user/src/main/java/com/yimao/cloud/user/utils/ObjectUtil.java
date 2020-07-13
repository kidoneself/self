package com.yimao.cloud.user.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.user.service.impl.DistributorServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * 对象工具类
 * @author yaoweijun
 *
 */
@Slf4j
public class ObjectUtil {
	

/**
 * 比较对象中相同属性并且值相同设置为null
 * 
 * 
 */
	public static <T> Integer compareObject(T obj1,T obj2,List<String> fieldNames){
		
		String fieldName ="";
		int count=0;
		
		 try {
				//获取所有参数
				Field[] fieldList=obj1.getClass().getDeclaredFields();
			
				for (Field field : fieldList) {
					
					field.setAccessible(true);
					
					fieldName =field.getName();
					//System.out.println(fieldName);
					if(fieldNames.contains(fieldName)) {
						//Class type=field.getType();						
						Object fieldVal1= field.get(obj1);
						Object fieldVal2= field.get(obj2);
						
						if(Objects.isNull(fieldVal1) || Objects.isNull(fieldVal2)) {
							if(Objects.isNull(fieldVal1) && Objects.isNull(fieldVal2)) {
								continue;
							}
							count++;
							continue;
						}

						
						if(fieldVal1.equals(fieldVal2)) {
							field.set(obj1, null);
							field.set(obj2,null);
							continue;
						}else {
							count++;
							continue;
						}
						
						
					}
					
					//都置为空
					field.set(obj1, null);
					field.set(obj2,null);
					
					
				}
				return count;
		} catch (Exception e) {
			log.info("参数设置空值报错,参数="+fieldName);
			throw new BadRequestException("经销商代理商编辑内容设置错误");
		}
	
	}

}
