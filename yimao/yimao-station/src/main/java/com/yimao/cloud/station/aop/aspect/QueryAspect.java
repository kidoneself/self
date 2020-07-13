package com.yimao.cloud.station.aop.aspect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.StationQueryEnum;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.query.station.BaseQuery;
import com.yimao.cloud.station.aop.annotation.StationQuery;
import com.yimao.cloud.station.controller.StationAdminController;

import lombok.extern.slf4j.Slf4j;
/**
 * 查询后台数据切面
 * @author yaoweijun
 *
 */
@Aspect
@Component
@Slf4j
public class QueryAspect {
		@Resource
		private UserCache userCache;
	
	 	@Pointcut("@annotation(com.yimao.cloud.station.aop.annotation.StationQuery)")
	    public void BrokerAspect(){
	 
	    }
	 	
	 	@Around(value="BrokerAspect() && @annotation(stationQuery)" , argNames="stationQuery")
	 	public Object around(ProceedingJoinPoint joinPoint,StationQuery stationQuery) throws Throwable {
	 		log.info("进入切面");
	 		
 			//获取参数
	 		Object[] args=joinPoint.getArgs();
	 		//获取方法
	 		Signature sig = joinPoint.getSignature();
	        String method = joinPoint.getTarget().getClass().getName() + "." + sig.getName();
	        log.info("method={}",method);
	        
	        //查询售前售后类型
	        PermissionTypeEnum serviceType = stationQuery.serviceType();
	        log.info("该接口查询售前售后类型为={}",serviceType.name);
	        
	        //获取用户可查询门店id
			Set<Integer> userStations= userCache.getStationUserAreas(0,null);
			log.info("用户绑定门店id={}",JSON.toJSONString(userStations));
			if(CollectionUtil.isEmpty(userStations)) {
				throw new YimaoException("用户未绑定门店");
			}
			
			//根据售前售后类型获取用户可查询区域id
			Set<Integer> userAreas;
			if(PermissionTypeEnum.ALL.value == serviceType.value) {
				userAreas=userCache.getStationUserAreas(1,PermissionTypeEnum.ALL.value);
			}else if(PermissionTypeEnum.PRE_SALE.value == serviceType.value) {
				userAreas=userCache.getStationUserAreas(1,PermissionTypeEnum.PRE_SALE.value);
			}else if(PermissionTypeEnum.AFTER_SALE.value == serviceType.value) {
				userAreas=userCache.getStationUserAreas(1,PermissionTypeEnum.AFTER_SALE.value);
			}else {
				throw new YimaoException("售前售后类型错误");
			}
			
			
			log.info("用户拥有区域={}",JSON.toJSONString(userAreas));
			if(CollectionUtil.isEmpty(userAreas)) {
				throw new YimaoException("用户未绑定区域");
			}

	        //获取注解上的值
	        StationQueryEnum enumVal= stationQuery.value();
	        //判断是查询列表还是查询详情
	        if(enumVal.equals(StationQueryEnum.ListQuery)){
	        	//列表查询逻辑
	     		for (Object object : args) {
					if(object instanceof BaseQuery) {
						log.info("进入列表查询");
						BaseQuery query=(BaseQuery) object;
						log.info("query={}",JSON.toJSONString(query));
						
						//判断查询条件门店
						Set<Integer> queryStations=query.getStations();
						log.info("查询参数门店={}",JSON.toJSONString(queryStations));
						if(CollectionUtil.isEmpty(queryStations)) {
							query.setStations(userStations);
						}else {
							Iterator<Integer> iterator= queryStations.iterator();

							while(iterator.hasNext()) {
								Integer stationId=iterator.next();
								//判断用户是否有该区域
								if(! userStations.contains(stationId)) {
									iterator.remove();
								}
							}
							log.info("校验后查询服务站门店id={}",JSON.toJSONString(queryStations));

							if(CollectionUtil.isEmpty(queryStations)) {
								throw new YimaoException("用户服务站门店查询错误");
							}
						}
						
						//判断查询条件是否选择区域
						Set<Integer> queryAreas=query.getAreas();
						log.info("查询参数区域={}",JSON.toJSONString(queryAreas));
						if(CollectionUtil.isEmpty(queryAreas)) {
							query.setAreas(userAreas);
						}else {
							Iterator<Integer> iterator= queryAreas.iterator();

							while(iterator.hasNext()) {
								Integer area=iterator.next();
								//判断用户是否有该区域
								if(! userAreas.contains(area)) {
									iterator.remove();
								}
							}
							log.info("校验后查询参数区域={}",JSON.toJSONString(queryAreas));

							if(CollectionUtil.isEmpty(queryAreas)) {
								throw new YimaoException("用户区域查询错误");
							}
						}

					}
				}
	     		//返回数据
	     		return joinPoint.proceed();

	        }else if(enumVal.equals(StationQueryEnum.InfoQuery)) {
	        	//查询数据详情逻辑判断
	        	log.info("进入详情查询");
	        	//获取调用方法
	        	Class<?>[] classes = new Class[args.length];
	        	for (int i = 0; i < args.length; i++) {
	        		classes[i]=args[i].getClass();
	        	}
	        	log.info("参数类型集合={}",JSON.toJSONString(classes));
	        	Method m=joinPoint.getTarget().getClass().getDeclaredMethod(joinPoint.getSignature().getName(), classes);
	        	//获取方法的返回值类型
	        	Class cls=m.getReturnType();
	        	log.info("返回值类型={}",cls.getName());
	        	Object res=joinPoint.proceed();

	        	try {
	        		Field field=cls.getDeclaredField("areaId");
	        		field.setAccessible(true);
	        		Integer area=(Integer) field.get(res);
	        		if(Objects.nonNull(area) && ! userAreas.contains(area)) {
	        			throw new YimaoException("用户没有权限查询该详情");
	        		}
				} catch (NoSuchFieldException e) {
					log.info("不存在区域字段");
				}

	        	return res;

	        }else {
	        	return joinPoint.proceed();
	        }


	 	}

}
