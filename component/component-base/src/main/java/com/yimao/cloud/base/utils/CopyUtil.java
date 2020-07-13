package com.yimao.cloud.base.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象转换属性设值字符生成工具类
 *
 * @author Zhang Bo
 * @date 2018/11/9.
 */
@Slf4j
public class CopyUtil {

    /**
     * 数据库PO对象批量转换成DTO对象
     *
     * @param sList  数据库PO对象集合
     * @param sClass PO对象数据类型
     * @param tClass DTO对象数据类型
     * @param <S>    PO对象数据类型
     * @param <T>    DTO对象数据类型
     * @return
     */
    public static <S, T> List<T> copyList(List<S> sList, Class<S> sClass, Class<T> tClass) {
        try {
            if (sList != null && sList.size() > 0) {
                // 获取PO对象的convert方法
                Method method = sClass.getMethod("convert", tClass);
                // 定义转换后的集合
                List<T> tList = new ArrayList<>();
                T target;
                for (S s : sList) {
                    target = tClass.newInstance();
                    // 反射调用PO对象的convert方法，target为参数
                    method.invoke(s, target);
                    tList.add(target);
                }
                return tList;
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
