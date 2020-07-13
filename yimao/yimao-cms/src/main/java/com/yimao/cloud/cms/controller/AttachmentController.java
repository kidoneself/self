package com.yimao.cloud.cms.controller;

import com.yimao.cloud.cms.service.AttachmentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 *
 * 功能描述: cms 中附件的服务
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
@RestController
public class AttachmentController {
    /**
     * 服务对象
     */
    @Resource
    private AttachmentService attachmentService;



}