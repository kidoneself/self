package com.yimao.cloud.cms.service.impl;

import com.yimao.cloud.cms.po.CommentSupport;
import com.yimao.cloud.cms.mapper.CommentSupportMapper;
import com.yimao.cloud.cms.service.CommentSupportService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (CommentSupport)表服务实现类
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@Service("commentSupportService")
public class CommentSupportServiceImpl implements CommentSupportService {
    @Resource
    private CommentSupportMapper commentSupportMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public CommentSupport queryById(Long id) {
        return commentSupportMapper.selectByPrimaryKey(id);
    }
}