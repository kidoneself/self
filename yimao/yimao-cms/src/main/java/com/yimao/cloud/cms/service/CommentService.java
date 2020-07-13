package com.yimao.cloud.cms.service;

import com.yimao.cloud.cms.po.Comment;
import com.yimao.cloud.pojo.dto.cms.CommentDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import java.util.List;

/**
 * 评论
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
public interface CommentService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Comment queryById(Integer id);


    /**
     *  保存
     * @param comment
     * @return
     */
    Integer save(Comment comment);

    /**
     *  更新
     * @param comment
     * @return
     */
    Integer update(Comment comment);

    /**
     *  分页查询
     * @param pageNum
     * @param pageSize
     * @param comment
     * @return
     */
    PageVO<CommentDTO> findPage(Integer pageNum, Integer pageSize, CommentDTO comment);


}