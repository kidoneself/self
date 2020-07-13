package com.yimao.cloud.product.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.DistributorRoleDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@FeignClient(name = Constant.MICROSERVICE_USER)
public interface UserFeign {

    @GetMapping(value = "/user/{id}/single")
    UserDTO getBasicUserById(@PathVariable("id") Integer id);

    @RequestMapping(value = "/distributor/configuration/{pageSize}/{pageNum}",method = RequestMethod.GET)
    PageVO<DistributorRoleDTO> findDistributorConfig(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize);

    // /**
    //  * APP-经销商个人信息
    //  * @param userId
    //  * @return
    //  */
    // @GetMapping("/dist/{userId}")
    // UserDistributorDTO getUserDistributor(@PathVariable(value = "userId") Integer userId);


    @GetMapping(value = "/distributor/{id}")
    DistributorDTO getBasicDistributorById(@PathVariable(value = "id") Integer id);

    /**
     * 根据经销商ID集合获取经销商信息
     *
     * @param distributorIds 经销商集合
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/2/21
     */
    @GetMapping(value = "/distributor/list")
    List<DistributorDTO> getDistributorByDistributorIds(@RequestParam("distributorIds") List<Integer> distributorIds);
}
