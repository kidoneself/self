package com.yimao.cloud.cms.service;

import com.yimao.cloud.cms.po.CommentSupport;
import java.util.List;

/**
 * (CommentSupport)表服务接口
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
public interface CommentSupportService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    CommentSupport queryById(Long id);
}