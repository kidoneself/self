package com.yimao.cloud.base.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Zhang Bo
 * @Date Created in 2017/10/21.
 */
public final class CollectionUtil {

    public static boolean isEmpty(Collection collection) {
        if (collection == null || collection.size() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static <S, T> List<T> batchConvert(List<S> sResult, Class<S> sClass, Class<T> tClass) {
        try {
            if (isNotEmpty(sResult)) {
                // 获取PO对象的convert方法
                Method method = sClass.getMethod("convert", tClass);
                // 定义转换后的DTO对象集合
                List<T> tResult = new ArrayList<>();
                T target;
                for (S s : sResult) {
                    target = tClass.newInstance();
                    // 反射调用PO对象的convert方法，target为参数
                    method.invoke(s, target);
                    tResult.add(target);
                }
                return tResult;
            }
            return null;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }
}
