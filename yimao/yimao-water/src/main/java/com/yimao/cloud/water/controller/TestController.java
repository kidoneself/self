package com.yimao.cloud.water.controller;

import com.yimao.cloud.pojo.dto.out.DeviceQuery;
import com.yimao.cloud.water.service.WaterDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "TestController")
public class TestController {

    @Resource
    private WaterDeviceService waterDeviceService;

    @GetMapping("/test/countDevice")
    @ApiOperation(value = "测试")
    public Object test() {
        DeviceQuery query = new DeviceQuery();
        List<Map<String, String>> pcr = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("province", "辽宁省");
        map.put("city", "大连市");
        map.put("region", "沙河口区");
        Map<String, String> map1 = new HashMap<>();
        map1.put("province", "江苏省");
        map1.put("city", "宿迁市");
        map1.put("region", "泗阳县");
        pcr.add(map);
        pcr.add(map1);
        query.setPcr(pcr);

        List<String> models = new ArrayList<>();
        models.add("1601T");
        models.add("1602T");
        models.add("1603T");
        models.add("1601L");
        query.setModels(models);

        query.setConnectionType(1);
        query.setOnline(true);
        query.setLocation("商场");

        return waterDeviceService.countDevice(query);
    }

}
