package com.yimao.cloud.product.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @auther: liu.lin
 * @date: 2018/12/14
 */
@FeignClient(name = Constant.MICROSERVICE_SYSTEM)
public interface SystemFeign {

    /**
     * 根据字典分组查询字典数据
     *
     * @param groupCode 分组CODE
     */
    @GetMapping(value = "/dictionary")
    List<DictionaryDTO> listDictionaryByGroup(@RequestParam(value = "groupCode", required = false) String groupCode);


    /**
     * 根据站长经销商id查询服务站是否上线
     *
     * @param distributorId
     * @return
     */
    @RequestMapping(value = "/station/distributor/{id}", method = RequestMethod.GET)
    Boolean getStationStatusByDistributorId(@PathVariable(value = "id") Integer distributorId);
}
