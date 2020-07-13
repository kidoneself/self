package com.yimao.cloud.out.controller.openapi;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.out.enums.OpenApiStatusCode;
import com.yimao.cloud.out.feign.SystemFeign;
import com.yimao.cloud.out.utils.OpenApiResult;
import com.yimao.cloud.out.vo.ServiceSiteOpenApiVO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
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
 * 描述：服务站同步（售后系统调用）
 *
 * @Author Liu long jie
 * @Date 2019/10/14
 */
@RestController
@Api(tags = "StationOpenApiController")
@Slf4j
public class StationOpenApiController {

    @Resource
    private SystemFeign systemFeign;

    /**
     * 根据老Id获取服务站信息(原云平台)
     * @param request
     * @param id 老服务站id
     * @return
     */
    @GetMapping("/openapi/servicesite/getDataById")
    @ApiOperation(value = "根据老Id获取服务站信息(原云平台)")
    public Map<String, Object> getDataById(
            HttpServletRequest request,
            @RequestParam("servicesiteId") String id
    ) {
        Map<String, Object> map = new HashMap<>();
        StationDTO stationDTO = systemFeign.getStationByOldId(id);
        if (stationDTO != null) {
            ServiceSiteOpenApiVO vo = new ServiceSiteOpenApiVO();
            List<StationCompanyDTO> stationCompanyDTOList = systemFeign.getStationCompanyByStationId(stationDTO.getId());
            if (CollectionUtil.isNotEmpty(stationCompanyDTOList)) {
                //获取服务站公司 （一般情况下一个服务站门店只对应一个服务站公司）
                StationCompanyDTO stationCompanyDTO = stationCompanyDTOList.get(0);
                vo.setMail(stationCompanyDTO.getEmail());
                vo.setLegalPerson(stationCompanyDTO.getLegalPerson());
                vo.setCreditCode(stationCompanyDTO.getCreditCode());
                vo.setCompany(stationCompanyDTO.getName());
            }
            vo.setServicesiteId(stationDTO.getOldId());
            vo.setProvince(stationDTO.getProvince());
            vo.setCity(stationDTO.getCity());
            vo.setRegion(stationDTO.getRegion());
            vo.setAddress(stationDTO.getAddress());
            vo.setName(stationDTO.getName());
            vo.setPerson(stationDTO.getContact());
            vo.setPhone(stationDTO.getContactPhone());
            vo.setMasterName(stationDTO.getMasterName());
            vo.setMasterPhone(stationDTO.getMasterPhone());
            vo.setMasterIdCard(stationDTO.getMasterIdCard());

            map = OpenApiResult.result(request, vo);
        } else {
            map = OpenApiResult.error(request,OpenApiStatusCode.DATA_NOT_FOUND) ;
        }
        return map;
    }

    /**
     * 根据省市区获取服务站信息(原云平台)
     * @param request
     * @param province
     * @param city
     * @param region
     * @return
     */
    @GetMapping(value="/openapi/servicesite/getDataList")
    @ApiOperation(value = "根据省市区获取服务站信息(原云平台)")
    public Map<String, Object> getDataList(
            HttpServletRequest request,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue="0") Integer pagesize,
            @RequestParam(required = false, defaultValue="0") Integer index){
        Map<String, Object> map = new HashMap<>();
        List<StationDTO> list = systemFeign.getStationByPCR(province, city, region, name);
        List<ServiceSiteOpenApiVO> voList = new ArrayList<>();
        for (StationDTO stationDTO : list) {
            ServiceSiteOpenApiVO vo = new ServiceSiteOpenApiVO();
            List<StationCompanyDTO> stationCompanyDTOList = systemFeign.getStationCompanyByStationId(stationDTO.getId());
            if (CollectionUtil.isNotEmpty(stationCompanyDTOList)) {
                //获取服务站公司 （一般情况下一个服务站门店只对应一个服务站公司）
                StationCompanyDTO stationCompanyDTO = stationCompanyDTOList.get(0);
                vo.setMail(stationCompanyDTO.getEmail());
                vo.setLegalPerson(stationCompanyDTO.getLegalPerson());
                vo.setCreditCode(stationCompanyDTO.getCreditCode());
                vo.setCompany(stationCompanyDTO.getName());
            }
            vo.setServicesiteId(stationDTO.getOldId());
            vo.setProvince(stationDTO.getProvince());
            vo.setCity(stationDTO.getCity());
            vo.setRegion(stationDTO.getRegion());
            vo.setAddress(stationDTO.getAddress());
            vo.setName(stationDTO.getName());
            vo.setPerson(stationDTO.getContact());
            vo.setPhone(stationDTO.getContactPhone());
            vo.setMasterName(stationDTO.getMasterName());
            vo.setMasterPhone(stationDTO.getMasterPhone());
            vo.setMasterIdCard(stationDTO.getMasterIdCard());

            voList.add(vo);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("data", voList);
        map = OpenApiResult.result(request, data);
        return map;
    }


}
