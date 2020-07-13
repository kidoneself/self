package com.yimao.cloud.station.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.StationQueryEnum;
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StationQuery {
	StationQueryEnum value() default StationQueryEnum.ListQuery;
	
	PermissionTypeEnum serviceType() default PermissionTypeEnum.ALL;


}
