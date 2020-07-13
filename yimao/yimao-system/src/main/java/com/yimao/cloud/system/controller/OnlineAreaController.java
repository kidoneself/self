package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.OnlineArea;
import com.yimao.cloud.system.service.OnlineAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：上线地区
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
@RestController
@Api(tags = "OnlineAreaController")
public class OnlineAreaController {

    @Resource
    private OnlineAreaService onlineAreaService;

    /**
     * 新增上线地区
     *
     * @param dto 上线地区
     */
    @PostMapping(value = "/onlinearea")
    @ApiOperation(value = "新增上线地区")
    @ApiImplicitParam(name = "dto", value = "上线地区", required = true, dataType = "OnlineAreaDTO", paramType = "body")
    public ResponseEntity save(@RequestBody OnlineAreaDTO dto) {
        OnlineArea record = new OnlineArea(dto);
        onlineAreaService.save(record);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据省市区获取上线地区
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @GetMapping(value = "/onlinearea")
    @ApiOperation(value = "根据省市区获取上线地区")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query")
    })
    public OnlineAreaDTO get(@RequestParam String province, @RequestParam String city, @RequestParam String region) {
        OnlineArea onlineArea = onlineAreaService.getOnlineAreaByPCR(province, city, region, 3);
        if (onlineArea == null) {
            return null;
        }
        OnlineAreaDTO dto = new OnlineAreaDTO();
        onlineArea.convert(dto);
        return dto;
    }

    /**
     * 根据省市区获取上线地区列表
     *
     */
    @GetMapping("/onlinearea/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据省市区获取上线地区列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")})
    public ResponseEntity orderList(@RequestParam(value = "province",required = false) String province,
                                    @RequestParam(value = "city",required = false) String city,
                                    @RequestParam(value = "region",required = false) String region,
                                    @PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<OnlineArea> pageVO = onlineAreaService.onlineAreaList(province, city, region, pageNum, pageSize);
        return ResponseEntity.ok(pageVO);
    }

    /**
     * 根据id删除上线地区
     *
     */
    @DeleteMapping("/onlinearea/{id}")
    @ApiOperation(value = "根据id删除上线地区")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "path")
    public ResponseEntity deleteOnlineAreaById(@PathVariable Integer id) {
        onlineAreaService.deleteOnlineAreaById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 设置地区上线
     *
     */
    @PatchMapping("/onlinearea/{id}")
    @ApiOperation(value = "设置地区上线")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Long", paramType = "path")
    public ResponseEntity setAreaOnline(@PathVariable Integer id) {
        onlineAreaService.setAreaOnline(id);
        return ResponseEntity.noContent().build();
    }

}
