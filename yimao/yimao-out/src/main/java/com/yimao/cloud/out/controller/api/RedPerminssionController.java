package com.yimao.cloud.out.controller.api;


import com.yimao.cloud.out.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @description   组件服务-红包组件-鉴权API
 * @author Liu Yi
 * @date 2019/9/11 9:28
 */
@Api("RedPerminssionController")
@RestController
@RequestMapping({"/webapi/assembly/red/permission"})
public class RedPerminssionController {
    @ResponseBody
    @RequestMapping(value = {"/permission"}, method = {RequestMethod.GET})
    @ApiOperation(value = "用户角色红包权限检查")
    public Map roleCheck(HttpServletRequest request, HttpServletResponse response) {
      /*  String roleId = this.getRoleId(request.getSession());
        this.getRoleGroupId(request);
        this.getUserId(request);*/
       /* EngineerDTO engineer = this.getAccount(request.getSession());
        String regionId = userAccountEntity.getAddrRegionId();
        ResultBean<RoleEntity> roleGive = this.redPermissionServiceApi.roleGiveCheck(roleId);
        ResultBean<RoleEntity> roleCollect = this.redPermissionServiceApi.roleCollectCheck(roleId);
        Map map = new HashMap();
        map.put("redPermission", roleCollect.isSuccess() || roleGive.isSuccess());
        map.put("give", roleGive.isSuccess());
        map.put("collect", roleCollect.isSuccess());
        map.put("giveText", "是否允许发起红包 : " + roleGive.isSuccess());
        map.put("collectText", "是否允许收红包 : " + roleCollect.isSuccess());
        map.put("redPermissionText", "红包功能权限 ：" + (roleCollect.isSuccess() || roleGive.isSuccess()));*/

        Map map = new HashMap();
        map.put("redPermission", true);
        map.put("give", false);
        map.put("collect",false);
        map.put("giveText", "是否允许发起红包 : " + false);
        map.put("collectText", "是否允许收红包 : " + false);
        map.put("redPermissionText", "红包功能权限 ：" + true);
        return ApiResult.result(request, map);
    }

    /**
     * @description   检查经销商红包权限检查
     * @author Liu Yi
     * @date 2019/9/11 9:11
     * @param
     * @return java.util.Map
     */
   /* @ResponseBody
    @RequestMapping({"/addressPermission"})
    @ApiOperation(value = "省市区县红包权限检查")
    public Map redAddressCheck(HttpServletRequest request, String province, String city, String region) {
        Map map = new HashMap();
      *//*  CommonAddressInfoEntity addressInfoEntity = this.addressApi.getAddressInfo(province, city, region);
        if (addressInfoEntity != null && addressInfoEntity.isRegion()) {
            ResultBean<CommonAddressInfoEntity> resultBean = this.redPermissionServiceApi.addrRegionCheck(addressInfoEntity.getId());
            if (resultBean.isSuccess()) {
                map.put("redStatus", addressInfoEntity.getRedModsStatus());
                map.put("text", addressInfoEntity.getRedModsStatusText());
                map.put("region", region);
                return ApiResult.result(request, map);
            } else {*//*
                map.put("redStatus", "Y");
                map.put("text", "该地区不支持红包功能");
                map.put("region", region);
                return ApiResult.result(request, map);
          *//*   }
       } else {
            return ApiResult.error(request, "错误的区县参数信息");
        }*//*
    }*/
}
