//package com.yimao.cloud.wechat.controller;
//
//import com.yimao.cloud.wechat.feign.SystemFeign;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//
///**
// * 我的代言卡
// *
// * @author liuhao@yimaokeji.com
// * 2018022018/2/6
// */
//@RestController
//@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
//@Slf4j
//@Api(tags = {"ProductSpeakCardController"})
//public class ProductSpeakCardController {
//
//    @Resource
//    private SystemFeign systemFeign;
//
//    /**
//     * 根据id查询产品代言卡
//     *
//     * @param id 代言卡ID
//     * @return dto
//     */
//    @GetMapping("/speak/card/{id}")
//    @ApiOperation(value = "根据ID查询产品代言卡", notes = "根据ID查询产品代言卡")
//    @ApiImplicitParam(name = "id", value = "代言卡ID", dataType = "Long", required = true, paramType = "path")
//    public Object speakCardById(@PathVariable(value = "id") Integer id) {
//        return ResponseEntity.ok(systemFeign.getSpeakCardById(id));
//    }
//
//    /**
//     * 代言卡
//     *
//     * @param cardCategory 代言卡类型 1：产品代言卡  2：H5宣传代言卡
//     * @return list
//     */
//    @GetMapping("/speak/card")
//    @ApiOperation(value = "查询代言卡", notes = "查询代言卡")
//    @ApiImplicitParam(name = "cardCategory", value = "代言卡类型", dataType = "Long", required = true, paramType = "query")
//    public Object propagandaList(@RequestParam(value = "cardCategory", defaultValue = "1") Integer cardCategory) {
//        return ResponseEntity.ok(systemFeign.listSpeakCardByCategory(cardCategory));
//    }
//
//
//    /**
//     * 后台管理：代言卡
//     *
//     * @param cardCategory 代言卡类型  1：产品代言卡 2：品牌代言卡
//     * @param cardTypeCode 产品类型
//     * @param title        标题
//     * @param online       是否上线
//     * @param pageNum      页码
//     * @param pageSize     页数
//     * @return page
//     */
//    @GetMapping("/speak/card/{pageNum}/{pageSize}")
//    @ApiOperation(value = "根据ID查询产品代言卡", notes = "根据ID查询产品代言卡")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
//            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path"),
//            @ApiImplicitParam(name = "cardCategory", value = "代言卡类型", dataType = "Long", required = true, paramType = "query"),
//            @ApiImplicitParam(name = "cardTypeCode", value = "产品类型", dataType = "Long", required = true, paramType = "query"),
//            @ApiImplicitParam(name = "title", value = "标题", dataType = "String", required = true, paramType = "query"),
//            @ApiImplicitParam(name = "online", value = "是否上线", dataType = "Long", required = true, paramType = "query")
//    })
//    public Object list(@RequestParam(value = "cardCategory", defaultValue = "1") Integer cardCategory,
//                       @RequestParam(value = "cardTypeCode", required = false) Integer cardTypeCode,
//                       @RequestParam(value = "title", required = false) String title,
//                       @RequestParam(value = "online", required = false) Integer online,
//                       @PathVariable("pageNum") Integer pageNum,
//                       @PathVariable("pageSize") Integer pageSize) {
//        return ResponseEntity.ok(systemFeign.listSpeakCard(cardCategory, cardTypeCode, title, online, pageNum, pageSize));
//    }
//
//}
