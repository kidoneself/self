package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.pojo.dto.cms.CardDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.CmsFeign;
import com.yimao.cloud.wechat.feign.SystemFeign;
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
import javax.smartcardio.Card;

/**
 * 代言卡
 *
 * @Author lizhiqiang
 * @Date 2019/4/3
 * @Param
 * @return
 */

@RestController
@Slf4j
@Api(tags = {"CardController"})
public class CardController {


    @Resource
    private CmsFeign cmsFeign;

    @Resource
    private SystemFeign systemFeign;

    /**
     * 分页查询卡券列表
     *
     * @param pageNum
     * @param pageSize
     * @param cardType
     * @param status
     * @param typeCode
     * @param title
     * @return
     */
    @GetMapping("/card/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询卡券列表", notes = "分页查询卡券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, defaultValue = "1", value = "当前页", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "pageSize", required = true, defaultValue = "10", value = "页大小", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "status", value = "状态", defaultValue = "1", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "typeCode", value = "卡code", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "cardType", value = "卡类型 1 代言卡 2 宣传卡", paramType = "query", dataType = "Integer")
    })
    public ResponseEntity<PageVO<CardDTO>> findPage(@PathVariable("pageNum") Integer pageNum,
                                                    @PathVariable("pageSize") Integer pageSize,
                                                    @RequestParam(value = "cardType", required = false) Integer cardType,
                                                    @RequestParam(value = "status", required = false) Integer status,
                                                    @RequestParam(value = "typeCode", required = false) String typeCode,
                                                    @RequestParam(value = "title", required = false) String title) {
        return ResponseEntity.ok(cmsFeign.findPage(pageNum, pageSize, cardType, 1, typeCode, title));
    }


    /**
     * 查询所有卡的code
     */
    @GetMapping("/card/endorsement/cardCode")
    @ApiOperation(value = "查询所有代言卡类型", notes = "查询所有代言卡类型")
    @ApiImplicitParam(name = "groupCode", value = "卡类型 1 代言卡 2 宣传卡", required = true, paramType = "query", dataType = "String")
    public ResponseEntity findEndorsementCardCode(@RequestParam(value = "groupCode") String groupCode) {
        return ResponseEntity.ok(cmsFeign.findEndorsementCardCode(groupCode));
    }

    /**
     * @param cardId
     * @return java.lang.Object
     * @description 描述
     * @author liulin
     * @date 2019/1/22 18:02
     */
    @GetMapping("/card/{cardId}")
    @ApiOperation(value = "查询单个卡", notes = "查询单个卡")
    @ApiImplicitParam(name = "cardId", value = "单个卡券id", required = true, dataType = "Long", paramType = "path")
    public Object findById(@PathVariable("cardId") Integer cardId) {

        CardDTO cardDto = cmsFeign.queryById(cardId);
        return ResponseEntity.ok(cardDto);
    }

}
