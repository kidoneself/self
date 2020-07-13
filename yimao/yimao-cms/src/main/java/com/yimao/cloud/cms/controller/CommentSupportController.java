package com.yimao.cloud.cms.controller;

import com.yimao.cloud.cms.po.CommentSupport;
import com.yimao.cloud.cms.service.CommentSupportService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 点赞
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@RestController
public class CommentSupportController {
    /**
     * 服务对象
     */
    @Resource
    private CommentSupportService commentSupportService;

}