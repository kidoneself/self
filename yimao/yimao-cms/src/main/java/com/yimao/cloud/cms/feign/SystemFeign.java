package com.yimao.cloud.cms.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @auther: liu.lin
 * @date: 2018/12/14
 */
@FeignClient(name = Constant.MICROSERVICE_SYSTEM)
public interface SystemFeign {

    /**
     * 根据字典类型查询 字典数据列表
     */
    @RequestMapping(value = "/dictionary/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<DictionaryDTO> findDictionaryByType(@PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize,
                                               @RequestParam(value = "name") String name,
                                               @RequestParam(value = "code") String code,
                                               @RequestParam(value = "groupCode") String groupCode,
                                               @RequestParam(value = "pid") Integer pid);

}
