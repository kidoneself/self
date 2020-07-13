package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.SystemFeign;
import com.yimao.cloud.app.feign.UserFeign;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 安装工
 * @author: yu chunlei
 * @create: 2019-03-20 16:28:08
 **/
@RestController
@Slf4j
@Api(tags = "EngineerController")
public class EngineerController {

    @Resource
    private UserFeign userFeign;
    
    @Resource
    private SystemFeign systemFeign;


    /**
     * @Description: 根据区域查询安装工
     * @author ycl
     * @param: * @param
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/3/20 17:19
     */
    @GetMapping(value = "/engineers")
    @ApiOperation(value = "根据区域查询安装工", notes = "根据区域查询安装工")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", required = true, dataType = "String", paramType = "query")
    })
    public ResponseEntity<List<EngineerDTO>> getEngineerByPRC(@RequestParam String province,
                                                              @RequestParam String city,
                                                              @RequestParam String region) {
    	
    	//先根据省市区获取区域id
    	Integer areaId=systemFeign.getRegionIdByPCR(province, city, region);
    	if(areaId==null) {
    		 throw new YimaoException("该地区没有服务区域");
    	}
        List<EngineerDTO> list = userFeign.listEngineerByRegion(areaId);
        if (CollectionUtil.isEmpty(list)) {
            throw new YimaoException("未查询到安装工信息");
        }
        return ResponseEntity.ok(list);
    }


}
