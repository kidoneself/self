package com.yimao.cloud.hra.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.user.UserDistributorDTO;
import com.yimao.cloud.pojo.dto.wechat.WxUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(name = Constant.MICROSERVICE_USER)
public interface UserFeign {

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    UserDTO getUserById(@PathVariable(value = "id") Integer id);

    /**
     * 根据用户ID获取用户openid
     *
     * @param id 用户ID
     */
    @GetMapping(value = "/user/{id}/openid")
    String getOpenidByUserId(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = "/user/process", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    UserDTO userProcess(@RequestParam(value = "identityType") Integer identityType,
                        @RequestParam(value = "sharerId", required = false) Integer sharerId,
                        @RequestBody WxUserInfo wxUserInfo);

    /**
     * 根据用户信息获取经销商信息
     *
     * @param userId 用户id
     * @return 经销商信息
     * @author liuhao@yimaokeji.com
     */
    @RequestMapping(value = "/dist/{userId}", method = RequestMethod.GET)
    UserDistributorDTO getUserDistributor(@PathVariable(value = "userId") Integer userId);

    /**
     * 查询评估卡所有者的信息(包含id和openid(发送模板消息))
     *
     * @param userIds
     * @return
     */
    @RequestMapping(value = "/user/ids", method = RequestMethod.GET)
    List<UserDTO> userByIds(@RequestParam("userIds") Set<Integer> userIds);


    @RequestMapping(value = "/user/usertype", method = RequestMethod.GET)
    List<Integer> getUserByUserType(@RequestParam(value = "userType", required = false) Integer userType);


}
