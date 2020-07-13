package com.yimao.cloud.out.controller.api;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/3/8 15:55
 */
@Controller
@Slf4j
@Api(tags = "PageApiController")
public class PageApiController {

    // @Resource
    // private MongoTemplate mongoTemplate;

    /**
     * APP操作指南页面（原云平台）
     */
    @GetMapping(value = "/operationNote/detail/{id}")
    public String operationDetail(Model model, @PathVariable String id) {
        // OperationNote note = mongoTemplate.findById(id, OperationNote.class, "operationnote");
        // model.addAttribute("operationNote", note);
        model.addAttribute("type", 1);
        return "page/details";
    }

    /**
     * APP公司公告页面（原云平台）
     */
    @GetMapping(value = "/notice/detail/{id}")
    public String noticeDetail(Model model, @PathVariable String id) {
        // Notice notice = mongoTemplate.findById(id, Notice.class, "notice");
        // model.addAttribute("notice", notice);
        model.addAttribute("type", 2);
        return "page/details";
    }

}
