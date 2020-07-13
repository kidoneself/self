package com.yimao.cloud.cms.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.cms.po.Comment;
import com.yimao.cloud.cms.service.CommentService;
import com.yimao.cloud.pojo.dto.cms.CommentDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 评论
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@RestController
public class CommentController {

    @Resource
    private CommentService commentService;


    @PostMapping(value = "/comment")
    @ApiOperation(value = "新增评论信息", notes = "新增评论信息")
    @ApiImplicitParam(name = "dto", paramType = "body", dataType = "CommentDTO", value = "评论信息")
    public Object add(@RequestBody CommentDTO dto) {
        Comment comment = new Comment(dto);
        Integer count = commentService.save(comment);
        if (count > 0) {
            return ResponseEntity.noContent().build();
        }
        throw new YimaoException("系统异常");
    }

    @PutMapping(value = "/comment")
    @ApiOperation(value = "更新评论", notes = "更新评论")
    @ApiImplicitParam(name = "dto", paramType = "body", dataType = "CommentDTO", value = "评论信息")
    public Object update(@RequestBody CommentDTO dto) {
        if (null == dto.getId()) {
            throw new BadRequestException("参数异常");
        }
        Comment comment = new Comment(dto);
        Integer count = commentService.update(comment);
        if (count > 0) {
            return ResponseEntity.noContent().build();
        }
        throw new YimaoException("系统异常");
    }

    @GetMapping(value = "/comment/{commentId}")
    @ApiOperation(value = "查询单个评论信息", notes = "查询单个评论信息")
    @ApiImplicitParam(name = "commentId", required = true, paramType = "path", dataType = "Long", value = "评论ID")
    public Object findById(@PathVariable("commentId") Integer commentId) {
        Comment comment = commentService.queryById(commentId);
        if (comment != null) {
            CommentDTO dto = new CommentDTO();
            comment.convert(dto);
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.ok();
    }

    @GetMapping("/comment/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询评论信息", notes = "分页查询评论信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, paramType = "path", dataType = "Long", value = "页码"),
            @ApiImplicitParam(name = "pageSize", required = true, dataType = "Long", paramType = "path", value = "页大小")
    })
    public Object findPage(@PathVariable("pageNum") Integer pageNum,
                           @PathVariable("pageSize") Integer pageSize) {
        PageVO<CommentDTO> pageVO = commentService.findPage(pageNum, pageSize, null);
        return ResponseEntity.ok(pageVO);
    }

}