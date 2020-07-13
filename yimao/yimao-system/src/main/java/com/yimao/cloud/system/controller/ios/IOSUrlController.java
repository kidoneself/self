package com.yimao.cloud.system.controller.ios;

import com.yimao.cloud.pojo.dto.system.AppUrlDTO;
import com.yimao.cloud.system.mapper.AppUrlMapper;
import com.yimao.cloud.system.po.AppUrl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/11/12
 */
@RestController
public class IOSUrlController {

    @Resource
    private AppUrlMapper appUrlMapper;

    /**
     * 翼猫APP（IOS版本）获取域名列表
     */
    @GetMapping(value = "/ios/getUrl")
    public AppUrlDTO getUrl(@RequestParam Integer version) {
        //用vesion作为条件查询，预发布版本的版本号会和客户端约定好，并配置一条数据在数据库
        AppUrl appUrl = appUrlMapper.selectOneByVersion(version);
        if (appUrl == null) {
            //如果没查询到说明是线上正在运行的版本，应该返回正式环境的域名
            appUrl = appUrlMapper.selectOneByVersionIsNull();
        }
        if (appUrl != null) {
            AppUrlDTO dto = new AppUrlDTO();
            appUrl.convert(dto);
            return dto;
        }
        return null;
    }

}
