package com.yimao.cloud.system.controller.version;

import com.yimao.cloud.system.mapper.NewAppVersionMapper;
import com.yimao.cloud.system.po.NewAppVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：APP版本更新
 *
 * @Author Zhang Bo
 * @Date 2019/11/8
 */
@RestController
public class NewAppVersionController {

    @Resource
    private NewAppVersionMapper newAppVersionMapper;

    /**
     * APP版本更新查询
     *
     * @param version    当前版本
     * @param systemType 手机系统类型：0-安卓；1-IOS；
     * @param appType    终端：0-翼猫服务APP；1-翼猫APP；
     */
    @GetMapping(value = "/getNewAppVersion")
    public Object pageQueryIncomeRule(@RequestParam Integer version, @RequestParam Integer systemType, @RequestParam Integer appType) {
        Map<String, Object> ru = new HashMap<>();
        ru.put("success", true);
        NewAppVersion newAppVersion = newAppVersionMapper.selectNew(systemType, appType);
        if (newAppVersion != null && newAppVersion.getVersion() > version) {
            ru.put("version", newAppVersion.getVersion());
            ru.put("versionName", newAppVersion.getVersionName());
            ru.put("versionDesc", newAppVersion.getVersionDesc());
            ru.put("popout", newAppVersion.getPopout());
            ru.put("forceUpdate", newAppVersion.getForceUpdate());
            ru.put("outLink", newAppVersion.getOutLink());
        }
        return ru;
    }
}
