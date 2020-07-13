package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.CmsFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Lizhqiang
 * @date 2019/4/1
 */
@RestController
@Slf4j
@Api(tags = "ContentController")
public class ContentController {

    @Resource
    private CmsFeign cmsFeign;


    @GetMapping("/content/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询内容分页信息", notes = "查询内容分页信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, value = "页码", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", defaultValue = "10", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "parentCategoryId", value = "类型ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "location", value = "位置：1资讯 2公号 3协议", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "userId", value = "当前登录用户e家号", dataType = "Long", paramType = "query")
    })
    public ResponseEntity<PageVO<ContentDTO>> findPage(@PathVariable(value = "pageNum") Integer pageNum,
                                                       @PathVariable(value = "pageSize") Integer pageSize,
                                                       @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId,
                                                       @RequestParam(value = "location") Integer location,
                                                       @RequestParam(value = "userId", required = false) Integer userId) {
        PageVO<ContentDTO> page = cmsFeign.getAgreementList(pageNum, pageSize, 2, parentCategoryId, 1, 1, null, location, userId);
        return ResponseEntity.ok(page);
    }
}
