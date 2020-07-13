package com.yimao.cloud.system.processor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.system.mapper.StoreHouseMapper;
import com.yimao.cloud.system.po.StoreHouse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 更新库存
 *
 * @author Zhang Bo
 * @date 2019/11/1
 */
@Component
@Slf4j
public class StoreHouseProcessor {

    @Resource
    private StoreHouseMapper storeHouseMapper;

    /**
     * 增/减库存
     */
    @RabbitListener(queues = RabbitConstant.INCREASE_OR_DECREASE_STOCK)
    @RabbitHandler
    public void processor(Map<String, Object> map) {
        try {
            String province = (String) map.get("province");
            String city = (String) map.get("city");
            String region = (String) map.get("region");
            String productModel = (String) map.get("productModel");
            Integer special = (Integer) map.get("special");
            //count为正数就是加库存，为负数就是减库存
            Integer count = (Integer) map.get("count");

            log.info("增/减库存逻辑执行了，province={}，city={}，region={}，productModel={}，count={}", province, city, region, productModel, count);

            if (StringUtil.isEmpty(province) || StringUtil.isEmpty(city) || StringUtil.isEmpty(region) || StringUtil.isEmpty(productModel) || count == null || special == null) {
                return;
            }
            StoreHouse query = new StoreHouse();
            query.setProvince(province);
            query.setCity(city);
            query.setRegion(region);
            query.setSpecial(special);

            StoreHouse store = storeHouseMapper.selectOne(query);
            //库存不为空才去进行增/减库存动作
            if (store != null && StringUtil.isNotEmpty(store.getStocks())) {
                log.info("store.getStocks()={}", store.getStocks());
                Gson gson = new Gson();
                Map<String, Integer> stocks = gson.fromJson(store.getStocks(), new TypeToken<Map<String, Integer>>() {
                }.getType());
                Integer stockCount = 0;
                if (stocks != null) {
                    stockCount = stocks.get(productModel) != null ? stocks.get(productModel) : 0;
                } else {
                    stocks = new HashMap<>();
                }
                int c = stockCount + count;
                stocks.put(productModel, c >= 0 ? c : 0);
                StoreHouse update = new StoreHouse();
                update.setId(store.getId());
                update.setStocks(stocks.toString());
                storeHouseMapper.updateByPrimaryKeySelective(update);
            }
        } catch (Exception e) {
        }
    }

}
