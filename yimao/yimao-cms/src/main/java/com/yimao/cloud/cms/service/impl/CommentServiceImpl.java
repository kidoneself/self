package com.yimao.cloud.cms.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.cms.mapper.CommentMapper;
import com.yimao.cloud.cms.po.Comment;
import com.yimao.cloud.cms.service.CommentService;
import com.yimao.cloud.pojo.dto.cms.CommentDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 评论
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@Service("commentService")
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Comment queryById(Integer id) {
        return commentMapper.selectByPrimaryKey(id);
    }


    /**
     * 保存
     *
     * @param comment
     * @return
     */
    @Override
    public Integer save(Comment comment) {
        return commentMapper.insertSelective(comment);
    }

    /**
     * 更新
     *
     * @param comment
     * @return
     */
    @Override
    public Integer update(Comment comment) {
        return commentMapper.updateByPrimaryKeySelective(comment);
    }

    /**
     * 分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param comment
     * @return
     */
    @Override
    public PageVO<CommentDTO> findPage(Integer pageNum, Integer pageSize, CommentDTO comment) {
        PageHelper.startPage(pageNum,pageSize);
        Page<CommentDTO> page = commentMapper.findPage(comment);
        return new PageVO<>(pageNum,page);
    }
}