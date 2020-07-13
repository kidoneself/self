package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.CmsFeign;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Lizhqiang
 * @date 2019-08-28
 */
@RestController
public class ContentUserController {

    @Resource
    private CmsFeign cmsFeign;


    @PutMapping("/user/read")
    @ApiOperation(value = "已读未读删除未删除", notes = "已读未读删除未删除")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "contentId", name = "资讯id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(value = "type", name = "1-读 2-删", dataType = "Long", paramType = "query")
    })
    public void userRead(@RequestParam(value = "contentId") Integer contentId,
                         @RequestParam(value = "type") Integer type) {

        cmsFeign.userRead(contentId, type);

    }


    @GetMapping("/user/read/count")
    @ApiOperation(value = "未读条数", notes = "未读条数")
    public Map userReadCount() {
        Map<String, Integer> map = cmsFeign.userReadCount();
        return map;
    }
}
