package com.yimao.cloud.order.service.impl;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.order.constant.OrderConstant;
import com.yimao.cloud.order.mapper.OrderConfigMapper;
import com.yimao.cloud.order.po.OrderConfig;
import com.yimao.cloud.order.service.OrderConfigService;
import com.yimao.cloud.pojo.dto.order.OrderConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-08-08 16:26:33
 **/
@Slf4j
@Service
public class OrderConfigServiceImpl implements OrderConfigService {

    @Resource
    private RedisCache redisCache;
    @Resource
    private OrderConfigMapper orderConfigMapper;


    @Override
    public void addOrderConfig(OrderConfig orderConfig) {
        if (orderConfig.getOrderTimeOut() == null) {
            throw new BadRequestException("超时时间不能为空");
        }

        if (orderConfig.getReturnDays() == null) {
            throw new BadRequestException("退货天数不能为空");
        }

        if (orderConfig.getDeliveryDays() == null) {
            throw new BadRequestException("发货天数不能为空");
        }

        orderConfig.setCreateTime(new Date());
        int count = orderConfigMapper.insert(orderConfig);
        if (count == 0) {
            throw new BadRequestException("新增订单配置失败");
        }
    }

    @Override
    public void updateOrderConfig(OrderConfig orderConfig) {
        if (orderConfig.getOrderTimeOut() == null) {
            throw new BadRequestException("超时时间不能为空");
        }

        if (orderConfig.getReturnDays() == null) {
            throw new BadRequestException("退货天数不能为空");
        }

        if (orderConfig.getDeliveryDays() == null) {
            throw new BadRequestException("发货天数不能为空");
        }

        OrderConfig config = orderConfigMapper.selectByPrimaryKey(orderConfig.getId());
        if (null != config) {
            config.setDeliveryDays(orderConfig.getDeliveryDays());
            config.setOrderTimeOut(orderConfig.getOrderTimeOut());
            config.setReturnDays(orderConfig.getReturnDays());
            config.setUpdateTime(new Date());
        }

//        int num = orderConfigMapper.updateByPrimaryKey(config);
        int num = orderConfigMapper.updateByPrimaryKeySelective(config);
        if (num == 0) {
            throw new BadRequestException("编辑订单配置失败");
        }
        //删除缓存
        redisCache.delete(OrderConstant.ORDER_CONFIG_CACHE);
    }

    @Override
    public OrderConfigDTO getOrderConfig() {
        //先取缓存
        OrderConfigDTO dto = redisCache.get(OrderConstant.ORDER_CONFIG_CACHE, OrderConfigDTO.class);
        if (dto != null) {
            return dto;
        } else {
            //缓存中没有，则查询数据库
            List<OrderConfig> list = orderConfigMapper.selectAll();
            if (CollectionUtil.isNotEmpty(list)) {
                OrderConfig config = list.get(0);
                if (config != null) {
                    dto = new OrderConfigDTO();
                    config.convert(dto);
                }
            }
        }
        return dto;
    }
}
