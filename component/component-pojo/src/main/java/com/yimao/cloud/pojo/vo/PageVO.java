package com.yimao.cloud.pojo.vo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页类
 *
 * @author Zhang Bo
 * @date 2018/7/22.
 */
@Slf4j
@Data
public class PageVO<T> implements Serializable {

    private static final long serialVersionUID = 4276234710689081681L;

    private int pageNum;
    private int pageSize;
    private long total;
    private int pages;
    private List<T> result;

    public PageVO() {
    }

    public PageVO(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /**
     * T为DTO对象，无需再进行转换
     *
     * @param pageNum
     * @param page
     */
    public PageVO(Integer pageNum, com.github.pagehelper.Page<T> page) {
        this.pageNum = page.getPageNum();
        this.pageSize = page.getPageSize();
        this.total = page.getTotal();
        this.pages = page.getPages();
        this.result = page.getResult();
        // 当前页码大于总页数的情况，针对mybatis配置了页码大于总页数返回最后一页数据的情况
        if (pageNum != null && pageNum > this.pages) {
            this.pageNum = pageNum;
            this.result = null;
        }
    }

    /**
     * S为数据库实体对象，需要将其转换成对应的DTO对象
     *
     * @param pageNum
     * @param page
     * @param sClass
     * @param <S>
     */
    public <S> PageVO(Integer pageNum, com.github.pagehelper.Page<S> page, Class<S> sClass, Class<T> tClass) {
        this.pageNum = page.getPageNum();
        this.pageSize = page.getPageSize();
        this.total = page.getTotal();
        this.pages = page.getPages();
        // 当前页码大于总页数的情况，针对mybatis配置了页码大于总页数返回最后一页数据的情况
        if (pageNum != null && pageNum > this.pages) {
            this.pageNum = pageNum;
            this.result = null;
        } else {
            // 原始PO对象集合
            List<S> sResult = page.getResult();
            try {
                if (sResult != null && sResult.size() > 0) {
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
                    this.result = tResult;
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
                this.result = null;
            }
        }
    }

    /**
     * S为数据库实体对象，需要将其转换成对应的DTO对象（mongo查询用）
     *
     * @param pageNum 第几页
     * @param sResult 原始类型集合
     * @param sClass  原始类型
     * @param tClass  目标类型
     * @param <S>
     */
    public <S> void setData(Integer pageNum, List<S> sResult, Class<S> sClass, Class<T> tClass) {
        // 当前页码大于总页数的情况，针对mybatis配置了页码大于总页数返回最后一页数据的情况
        if (pageNum != null && pageNum > this.pages) {
            this.pageNum = pageNum;
            this.result = null;
        } else {
            // 原始PO对象集合
            try {
                if (sResult != null && sResult.size() > 0) {
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
                    this.result = tResult;
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
                this.result = null;
            }
        }
    }

}
