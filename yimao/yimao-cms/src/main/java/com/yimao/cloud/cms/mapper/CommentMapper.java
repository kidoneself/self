package com.yimao.cloud.cms.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.cms.po.Comment;
import com.yimao.cloud.pojo.dto.cms.CommentDTO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import tk.mybatis.mapper.common.Mapper;

/**
 *
 * 评论
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
public interface CommentMapper extends Mapper<Comment> {

    Page<CommentDTO> findPage(CommentDTO comment);

}