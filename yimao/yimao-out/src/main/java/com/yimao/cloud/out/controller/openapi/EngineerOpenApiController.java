package com.yimao.cloud.out.controller.openapi;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.out.enums.OpenApiStatusCode;
import com.yimao.cloud.out.feign.SystemFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.utils.OpenApiResult;
import com.yimao.cloud.out.vo.EngineerOpenApiVO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
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
 * 描述：安装工同步（售后系统调用）
 *
 * @Author Liu long jie
 * @Date 2019/10/9
 * @Version 1.0
 */
@RestController
@Api(tags = "EngineerOpenApiController")
@Slf4j
public class EngineerOpenApiController {

    @Resource
    private UserFeign userFeign;
    
    @Resource
    private SystemFeign systemFeign;

    /**
     * 安装工程师解除绑定
     *
     * @param request
     * @param id      老安装工id
     * @return
     */
    @GetMapping(value = "/openapi/engineer/unbundingById")
    @ApiOperation(value = "安装工程师解除绑定(原云平台)")
    public Map<String, Object> unbundingById(
            HttpServletRequest request,
            @RequestParam("customerId") String id) {
        Map<String, Object> map = new HashMap<>();
        EngineerDTO engineer = userFeign.getEngineerByOldId(id);
        if (engineer != null) {
            userFeign.unbindIccid(engineer.getOldId(), 2);
        } else {
            throw new NotFoundException("未找到安装工信息");
        }
        map = OpenApiResult.success(request);
        return map;
    }

    /**
     * 根据省市区查询安装工程师
     *
     * @param request
     * @param province
     * @param city
     * @param region
     * @return
     */
    @GetMapping(value = "/openapi/engineer/getDataList")
    @ApiOperation(value = "根据省市区查询安装工程师(原云平台)")
    public Map<String, Object> getDataList(
            HttpServletRequest request,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String region) {

        log.info("售后开始调用接口");
        Map<String, Object> map = new HashMap<>();
        //先根据省市区查询区域id
        Integer areaId=systemFeign.getRegionIdByPCR(province, city, region);
        List<EngineerDTO> engineerDTOList = userFeign.listEngineerByRegion(areaId);
        List<EngineerOpenApiVO> voList = new ArrayList<>();
        for (EngineerDTO engineerDTO : engineerDTOList) {
            EngineerOpenApiVO vo = new EngineerOpenApiVO();
            vo.setId(engineerDTO.getOldId());
            vo.setLoginName(engineerDTO.getUserName());
            vo.setRealName(engineerDTO.getRealName());
            vo.setIdCard(engineerDTO.getIdCard());
            vo.setMail(engineerDTO.getEmail());
            vo.setForbidden(engineerDTO.getForbidden());
            vo.setPhone(engineerDTO.getPhone());
            vo.setProvince(engineerDTO.getProvince());
            vo.setCity(engineerDTO.getCity());
            vo.setRegion(engineerDTO.getRegion());
            if (engineerDTO.getSex() != null) {
                vo.setSex(engineerDTO.getSex() == 1 ? "M" : "F");
            }
            vo.setSiteName(engineerDTO.getStationName());
            vo.setSiteId(engineerDTO.getOldSiteId());
            voList.add(vo);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("data", voList);
        map = OpenApiResult.result(request, data);
        log.info("售后调用接口完毕");
        return map;
    }

    /**
     * 根据Id查询安装工程师
     *
     * @param request
     * @param id      老安装工id
     * @return
     */
    @GetMapping(value = "/openapi/engineer/getDataById")
    @ApiOperation(value = "根据Id查询安装工程师(原云平台)")
    public Map<String, Object> getDataById(
            HttpServletRequest request,
            @RequestParam("customerId") String id) {

        Map<String, Object> map = new HashMap<>();
        EngineerDTO engineerDTO = userFeign.getEngineerByOldId(id);

        log.info("百得调用翼猫平台业务系统：/openapi/engineer/getDataById，{}", JSONObject.toJSONString(engineerDTO));

        if (engineerDTO != null) {
            EngineerOpenApiVO vo = new EngineerOpenApiVO();
            vo.setId(engineerDTO.getOldId());
            vo.setLoginName(engineerDTO.getUserName());
            vo.setRealName(engineerDTO.getRealName());
            vo.setIdCard(engineerDTO.getIdCard());
            vo.setMail(engineerDTO.getEmail());
            vo.setForbidden(engineerDTO.getForbidden());
            vo.setPhone(engineerDTO.getPhone());
            vo.setProvince(engineerDTO.getProvince());
            vo.setCity(engineerDTO.getCity());
            vo.setRegion(engineerDTO.getRegion());
            if (engineerDTO.getSex() != null) {
                vo.setSex(engineerDTO.getSex() == 1 ? "M" : "F");
            }
            vo.setSiteName(engineerDTO.getStationName());
            vo.setSiteId(engineerDTO.getOldSiteId());
            map = OpenApiResult.result(request, vo);
        } else {
            map = OpenApiResult.error(request, OpenApiStatusCode.DATA_NOT_FOUND);
        }
        return map;
    }

}
