package com.yimao.cloud.user.aop.annotation;

import com.yimao.cloud.base.enums.UserChangeRecordEnum;

import java.lang.annotation.*;


/**
 * 在方法注释此方法记录用户的变更记录
 *
 * @author hhf
 * @date 2019/1/10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@Documented
public @interface UserChangeRecordLog {
    /**
     * 操作的中文说明
     *
     * @return
     */
    String opeName() default "";

    /**
     * 变更事件的类型
     *
     * @return
     */
    UserChangeRecordEnum changeType();

}
