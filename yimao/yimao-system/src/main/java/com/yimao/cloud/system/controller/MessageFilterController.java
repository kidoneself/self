//package com.yimao.cloud.system.controller;
//
//import com.yimao.cloud.base.exception.YimaoException;
//import com.yimao.cloud.base.utils.SFTPUtil;
//import com.yimao.cloud.pojo.dto.system.MessageFilterDTO;
//import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
//import com.yimao.cloud.pojo.vo.PageVO;
//import com.yimao.cloud.system.feign.WaterFeign;
//import com.yimao.cloud.system.service.AreaService;
//import com.yimao.cloud.system.service.MessageFilterService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.Resource;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///***
// * 功能描述:消息过滤
// *
// * @auther: liu yi
// * @date: 2019/5/5 9:35
// */
//@Slf4j
//@RestController
//@Api(tags = "MessageFilterController")
//public class MessageFilterController {
//    @Resource
//    private MessageFilterService messageFilterService;
//
//    /***
//     * 功能描述:分页查询
//     *
//     * @param: [model, province, city, region, pageSize, pageNum]
//     * @auther: liu yi
//     * @date: 2019/4/29 15:54
//     * @return: java.lang.Object
//     */
//    @RequestMapping(value = {"/messageFilter/{pageSize}/{pageNum}"}, method = {RequestMethod.GET})
//    @ApiOperation("分页查询消息过滤")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "province", value = "省份", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "city", value = "城市", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "region", value = "区县", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
//            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
//    })
//    public Object page(@RequestParam(required = false) String province,
//                       @RequestParam(required = false) String city,
//                       @RequestParam(required = false) String region,
//                       @PathVariable Integer pageSize,
//                       @PathVariable Integer pageNum) {
//        PageVO<MessageFilterDTO> page = this.messageFilterService.page(province, city, region, pageSize, pageNum);
//        return ResponseEntity.ok(page);
//    }
//
//    /***
//     * 功能描述:新增
//     *
//     * @param: [messageFilter, request, productsModelId, productsScopeId, productsId]
//     * @auther: liu yi
//     * @date: 2019/4/29 16:06
//     * @return: java.lang.Object
//     */
//    @PostMapping(value = {"/messageFilter"})
//    @ApiOperation("新增消息过滤")
//    @ApiImplicitParam(name = "dto", value = "消息过滤实体类", required = true, dataType = "MessageFilterDTO", paramType = "body")
//    public Object add(@RequestBody MessageFilterDTO dto) {
//        //三级分类id categoryId
//        if(dto ==null || dto.getCategoryId()==null){
//            throw new YimaoException("请选择产品！");
//        }
//        dto.setCreateTime(new Date());
//        this.messageFilterService.createMessageFilter(dto);
//        log.info("添加了消息推送过滤");
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping(value = {"/messageFilter/{id}"})
//    @ApiOperation("根据id查询过滤")
//    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long", paramType = "path")
//    public Object findMessageFilterById(@PathVariable Integer id) {
//        MessageFilterDTO dto = messageFilterService.getMessageFilterById(id);
//        log.info("根据id查询过滤");
//        return ResponseEntity.ok(dto);
//    }
//
//    @GetMapping(value = {"/messageFilter"})
//    @ApiOperation("根据条件查询过滤")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
//            @ApiImplicitParam(name = "categoryId", value = "产品分类id", dataType = "Long", paramType = "query"),
//            @ApiImplicitParam(name = "type", value = "推送内容:0-推送内容 1-余额不足 4-滤芯报警", dataType = "String", paramType = "query")
//    })
//    public Object findMessageFilterList(@RequestParam(required = false) String province,
//                                        @RequestParam(required = false) String city,
//                                        @RequestParam(required = false) String region,
//                                        @RequestParam(required = false) Integer categoryId,
//                                        @RequestParam(required = false) Integer type) {
//        List<MessageFilterDTO> dtoList = messageFilterService.findMessageFilterList(province, city, region, categoryId, type);
//        log.info("更新了消息推送过滤");
//        return ResponseEntity.ok(dtoList);
//    }
//
//    @PutMapping(value = {"/messageFilter"})
//    @ApiOperation("更新过滤")
//    @ApiImplicitParam(name = "dto", value = "消息过滤实体类", required = true, dataType = "MessageFilterDTO", paramType = "body")
//    public Object update(@RequestBody MessageFilterDTO dto) {
//        this.messageFilterService.updateMessageFilter(dto);
//        log.info("更新了消息推送过滤");
//        return ResponseEntity.noContent().build();
//    }
//
//    /***
//     * 功能描述:根据id删除
//     *
//     * @param: [id]
//     * @auther: liu yi
//     * @date: 2019/4/29 16:23
//     * @return: java.lang.Object
//     */
//    @RequestMapping(value = {"/messageFilter/{id}"}, method = {RequestMethod.DELETE})
//    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long", paramType = "path")
//    @ApiOperation("根据id删除过滤")
//    public Object delete(@PathVariable Integer id) {
//        this.messageFilterService.deleteMessageFilterById(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping(value = {"/messageFilter/downloadModel"})
//    @ApiOperation("下载模版")
//    public Object downloadModel(HttpServletRequest request, HttpServletResponse response) {
//        String fileName = "推送过滤配置导入.xls";
//        String path = request.getSession().getServletContext().getRealPath("/");
//        File destFile = new File(path + fileName);
//        try {
//            boolean result = SFTPUtil.downloadLocal("/static/excel/", fileName, destFile, response);
//            if (!result) {
//                throw new YimaoException("推送过滤配置导入下载失败！");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new YimaoException("推送过滤配置导入模板下载失败！");
//        }
//
//        return ResponseEntity.noContent().build();
//    }
//
//    /***
//     * 功能描述:导入
//     *
//     * @param: [multipartFile]
//     * @auther: liu yi
//     * @date: 2019/5/7 15:27
//     * @return: java.lang.Object
//     */
//    @PostMapping(value = "/messageFilter/import")
//    @ApiOperation("导入模版")
//    public Object importExcel(@RequestParam(required = false) MultipartFile multipartFile) {
//        messageFilterService.importMessageFilterExcel(multipartFile);
//        return ResponseEntity.noContent().build();
//    }
//
//}
