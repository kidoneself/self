package com.yimao.cloud.out.controller.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@Api(tags = "RedLogController")
@RequestMapping({"/web/assembly/red/records"})
public class RedLogController {

    @RequestMapping(value = {"/page"}, method = {RequestMethod.GET})
    @ApiOperation(value = "打开我的红包历史记录主页面")
    public String page() {

        return "web/assembly/red/redRecordsPage";
    }

    @RequestMapping(value = {"/page/list"},method = {RequestMethod.GET})
    @ApiOperation(value = "读取我的红包历史记录列表")
    public String page(HttpServletRequest request, HttpServletResponse response, @RequestParam("page") Integer page, String fromUserId, String toUserAccountId, Integer status, HttpSession httpSession, ModelMap modelMap) {
       /* UserAccountEntity userAccountEntity = this.getAccount(httpSession);
        if (page == null) {
            page = 1;
        }

        String isBack = "";
        String finishStatus = "";
        String bindStatus = "";
        switch(status) {
        case 1:
            bindStatus = StatusEnum.YES.value();
            isBack = StatusEnum.NO.value();
            break;
        case 2:
            bindStatus = StatusEnum.NO.value();
            finishStatus = StatusEnum.NO.value();
            break;
        case 3:
            isBack = StatusEnum.YES.value();
            finishStatus = StatusEnum.YES.value();
            break;
        case 4:
            isBack = StatusEnum.YES.value();
            finishStatus = StatusEnum.YES.value();
        }

        modelMap.addAttribute("currentStatus", status);
        PageResult<RedEntity> redRecordsPage = null;
        if ("service_engineer".equals(userAccountEntity.getGroupId())) {
            redRecordsPage = this.redRecordsServiceApi.page(page, defaultH5PageSize, fromUserId, userAccountEntity.getYimaoOldSystemId(), bindStatus, isBack, finishStatus);
            modelMap.addAttribute("currentRole", 1);
        } else {
            redRecordsPage = this.redRecordsServiceApi.page(page, defaultH5PageSize, userAccountEntity.getYimaoOldSystemId(), toUserAccountId, bindStatus, isBack, finishStatus);
            modelMap.addAttribute("currentRole", 2);
        }

        if (!ObjectUtil.isNull(redRecordsPage.getContent())) {
            modelMap.addAttribute("redRecordsResult", redRecordsPage.getContent());
        }*/

        return "web/assembly/red/redRecordsList";
    }
}
