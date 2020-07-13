package com.yimao.cloud.system;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.system.mapper.AreaMapper;
import com.yimao.cloud.system.po.Area;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Chen Hui Yang
 * @date 2019/4/15
 */
@RestController
public class TestController {

    @Resource
    private AreaMapper areaMapper;

    @GetMapping("/test/concurrent")
    public Object test() throws InterruptedException {
        Thread.sleep(200L);
        JSONObject result = new JSONObject();
        Area query = new Area();
        query.setLevel(1);
        List<Area> list = areaMapper.select(query);
        result.put("test02", list);
        return result;
    }

}
