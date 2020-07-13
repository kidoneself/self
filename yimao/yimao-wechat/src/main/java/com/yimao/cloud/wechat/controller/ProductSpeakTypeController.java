//package com.yimao.cloud.wechat.controller;
//
//import com.yimao.cloud.wechat.feign.SystemFeign;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//
///**
// * 代言卡类型
// * created by liuhao@yimaokeji.com
// * 2018052018/5/9
// */
//@RestController
//@Slf4j
//@Api(tags = "ProductSpeakTypeController")
//public class ProductSpeakTypeController {
//
//    @Resource
//    private SystemFeign systemFeign;
//
//    /**
//     * 根据型号查询代言卡类型
//     *
//     * @param cardCategory 1-产品型  2-宣传型
//     * @return page
//     */
//    @GetMapping("/speak/card/type/{pageNum}/{pageSize}")
//    @ApiOperation(value = "查询代言卡", notes = "查询代言卡")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "cardCategory", value = "代言卡类型", dataType = "Long", paramType = "query"),
//            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
//            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path"),
//    })
//    public Object list(@RequestParam(value = "cardCategory", required = false) Integer cardCategory,
//                       @PathVariable(value = "pageNum") Integer pageNum,
//                       @PathVariable(value = "pageSize") Integer pageSize) {
//        return ResponseEntity.ok(systemFeign.listProductSpeakType(cardCategory, pageNum, pageSize));
//    }
//
//}
