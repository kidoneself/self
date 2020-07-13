package com.yimao.cloud.out.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@Api(tags = "RedBillController")
@RequestMapping({"/web/assembly/red/bill"})
public class RedBillController{

    /**
     * @description   打开我的红包账单主页面
     * @author Liu Yi
     * @date 2019/9/10 13:40
     * @param
     * @return java.lang.String
     */

    @RequestMapping(value = {"/page"}, method = {RequestMethod.GET})
    @ApiOperation(value = "打开我的红包账单主页面")
    public String page() {
        return "web/assembly/red/redBillPage";
    }

    /**
     * @description   读取我的红包账单
     * @author Liu Yi
     * @date 2019/9/10 13:41
     * @param
     * @return java.lang.String
     */
    @RequestMapping(value = {"/page/list"},method = {RequestMethod.GET})
    @ApiOperation(value = "读取我的红包账单")
    public String billList(@RequestParam(name = "page") Integer page, HttpSession httpSession, ModelMap modelMap) {
       /* if (page == null) {
            page = 1;
        }

        UserAccountEntity userAccountEntity = this.getAccount(httpSession);
        if (!ObjectUtil.isNull(userAccountEntity)) {
            String userId = this.getUserId(httpSession);
            if (!StringUtil.isEmpty(userId)) {
                PageResult<RedAccountBillEntity> redBillPage = this.redBillServiceApi.page(page, defaultH5PageSize, userId, "");
                if (!ObjectUtil.isNull(redBillPage.getContent())) {
                    modelMap.addAttribute("redBillResult", redBillPage.getContent());
                }
            }
        }*/

        return "web/assembly/red/redBillList";
    }
}
