// package com.yimao.cloud.base.annotation;
//
// import com.yimao.cloud.base.enums.OperationType;
//
// import java.lang.annotation.Documented;
// import java.lang.annotation.ElementType;
// import java.lang.annotation.Retention;
// import java.lang.annotation.RetentionPolicy;
// import java.lang.annotation.Target;
//
// /**
//  * 在方法上设置此注解标识需要进行操作日志记录
//  *
//  * @author Zhang Bo
//  */
// @Retention(RetentionPolicy.RUNTIME)
// @Target(value = {ElementType.METHOD})
// @Documented
// public @interface EnableOperationLog {
//
//     /**
//      * 具体业务操作名称
//      *
//      * @return
//      */
//     String opeName() default "";
//
//     /**
//      * 操作的中文说明，可以直接调用OperationType
//      *
//      * @return
//      */
//     OperationType opeType() default OperationType.UPDATE;
//
//     /**
//      * 查询数据库所调用的class文件
//      *
//      * @return
//      */
//     Class<?> daoClass();
//
//     /**
//      * 获取编辑信息的解析类，目前为使用id获取，复杂的解析需要自己实现，默认不填写
//      * 则使用默认解析类
//      *
//      * @return
//      */
//     Class parseClass();
//
//     /**
//      * 核心业务字段
//      */
//     String[] feildName() default {"id"};
//
// }
