package com.yimao.cloud.base.utils;

import com.github.pagehelper.Page;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/3.
 */
public class PageUtil {

    /**
     * 由于pagehelper设置了reasonable=true，
     * 当当前页码大于总页数时还是会返回最后一页数据，
     * 所以要做一次判断处理，返回空或最后一页数据。
     *
     * @param page
     * @param clazz
     * @param pageNum
     * @param <T>
     * @return
     */
    public static <T> List<T> pageResult(Page<T> page, Integer pageNum) {
        if (page == null || page.getResult() == null) {
            return null;
        }
        if (pageNum != null && pageNum > page.getPages()) {
            return null;
        } else {
            return page.getResult();
        }
    }
}
