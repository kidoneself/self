package com.yimao.cloud.out.controller.openapi;

import com.yimao.cloud.out.feign.SystemFeign;
import com.yimao.cloud.out.utils.OpenApiResult;
import com.yimao.cloud.out.vo.AreaOpenApiVO;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.system.AreaManageDTO;
import com.yimao.cloud.pojo.vo.out.AreaVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 描述：同步地区数据（售后系统调用）
 *
 * @Author Liu long jie
 * @Date 2019/10/18
 * @Version 1.0
 */
@RestController
@Api(tags = "AreaOpenApiController")
@Slf4j
public class AreaOpenApiController {

    @Resource
    private SystemFeign systemFeign;


    @GetMapping("/openapi/area/getAreaData")
    @ApiOperation(value = "同步地区数据(原云平台)")
    public Map<String, Object> getAreaData(
            HttpServletRequest request,
            @RequestParam(required = false) String pId){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        Integer pIdInt = null;
        if (pId != null) {
            //将字符串类型的pid转为数直类型的
            pIdInt = Integer.valueOf(pId);
        }
        List<AreaDTO> list = systemFeign.findCityOrCounty(pIdInt);
        List<AreaOpenApiVO> volist = new ArrayList<>();
        for(AreaDTO area : list){
            AreaOpenApiVO vo = new AreaOpenApiVO();
            vo.setAId(area.getId()+"");
            vo.setName(area.getName());
            vo.setPId(area.getPid()+"");
            volist.add(vo);
        }
        map.put("data", volist);
        returnMap = OpenApiResult.result(request, map);
        return returnMap;
    }
}
