package com.yimao.cloud.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.ProductActivityStatusEnum;
import com.yimao.cloud.base.enums.ProductActivityType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CalcActivityStartTimeUtil;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.order.feign.ProductFeign;
import com.yimao.cloud.order.mapper.OrderSubMapper;
import com.yimao.cloud.order.service.OrderCheckService;
import com.yimao.cloud.pojo.dto.order.BaseOrder;
import com.yimao.cloud.pojo.dto.product.ProductActivityDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 描述：下单时的校验逻辑
 *
 * @Author Zhang Bo
 * @Date 2020/3/13
 */
@Service
@Slf4j
public class OrderCheckServiceImpl implements OrderCheckService {

    @Resource
    private RedisCache redisCache;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private OrderSubMapper orderSubMapper;

    /**
     * 描述：活动商品下单时相关校验
     *
     * @param product
     * @param baseOrder
     */
    @Override
    public void checkProductActivity(ProductDTO product, BaseOrder baseOrder, UserDTO user) {
        if (product.getActivityType() != null && product.getActivityType() == ProductActivityType.PANIC_BUYING.value) {
            List<Integer> productActivityIdList = baseOrder.getProductActivityIdList();
            if (CollectionUtil.isEmpty(productActivityIdList)) {
                throw new BadRequestException("活动商品信息有误，暂不能购买。");
            }
            if (productActivityIdList.size() > 1) {
                throw new BadRequestException("暂不支持同一个商品参加多个活动。");
            }
            Integer productActivityId = productActivityIdList.get(0);
            ProductActivityDTO pa = redisCache.get(Constant.PRODUCT_ACTIVITY_CACHE + productActivityId, ProductActivityDTO.class);
            if (pa == null) {
                pa = productFeign.getProductActivityById(productActivityId);
                if (pa != null) {
                    redisCache.set(Constant.PRODUCT_ACTIVITY_CACHE + productActivityId, pa, 3600L);
                }
            }
            //活动是否有效或者存在校验
            if (pa == null || pa.getOpening() == null || !pa.getOpening()) {
                throw new BadRequestException("活动不存在。");
            }
            if (pa.getStatus() == ProductActivityStatusEnum.TERMINATED.value) {
                throw new BadRequestException("活动已终止。");
            }
            //活动时间校验
            Date now = new Date();
            Date startDate = pa.getStartTime();
            Date endDate = pa.getEndTime();
            if (startDate == null || endDate == null) {
                throw new BadRequestException("活动时间配置错误。");
            }
            //先校验大的时间范围
            if (now.before(startDate)) {
                throw new BadRequestException("活动尚未开始。");
            }
            if (now.after(endDate)) {
                throw new BadRequestException("活动已结束。");
            }
            Date startTime = startDate;
            Date endTime = endDate;
            //周期性活动校验每个周期的时间范围
            if (pa.getCycle() != null && pa.getCycle()) {
                String cycleTime = pa.getCycleTime();
                JSONObject json = JSON.parseObject(cycleTime);
                String suffix1 = json.getString("startTime");
                String suffix2 = json.getString("endTime");
                String prefix = CalcActivityStartTimeUtil.getDayShortTime(now);
                if (pa.getCycleType() == 1) {
                    //每天
                    prefix = CalcActivityStartTimeUtil.getDayShortTime(now);
                } else if (pa.getCycleType() == 2) {
                    //每周几
                    prefix = CalcActivityStartTimeUtil.getWeekDayShortTime(json.getInteger("weekDay"));
                } else if (pa.getCycleType() == 3) {
                    //每月几号
                    prefix = CalcActivityStartTimeUtil.getMonthDayShortTime(json.getInteger("day"));
                }
                startTime = DateUtil.transferStringToDate(prefix + " " + suffix1, DateUtil.defaultDateTimeFormat);
                endTime = DateUtil.transferStringToDate(prefix + " " + suffix2, DateUtil.defaultDateTimeFormat);

                if (startTime == null || endTime == null) {
                    throw new BadRequestException("活动时间配置错误。");
                }
                if (now.before(startTime)) {
                    throw new BadRequestException("活动尚未开始。");
                }
                //如果当前时间晚于当前活动结束时间，则获取下一次活动开始时间结束时间
                if (now.after(endTime)) {
                    //下次活动开始时间
                    Date nextStartTime = CalcActivityStartTimeUtil.getNextTime(startTime, pa.getCycleType());
                    if (nextStartTime == null) {
                        throw new BadRequestException("活动时间配置错误。");
                    }
                    if (nextStartTime.after(endDate)) {
                        throw new BadRequestException("活动已结束。");
                    } else {
                        throw new BadRequestException("活动尚未开始。");
                    }
                }
            }

            //商品购买数量
            Integer count = baseOrder.getCount();
            //用户加购数量是否≤活动限购数
            if (pa.getLimitStatus() != null && pa.getLimitStatus()) {
                if (count > pa.getLimitNum()) {
                    throw new BadRequestException("该商品每人限购" + pa.getLimitNum() + "件。");
                }
                //校验用户是否已经购买过
                Integer alreadyBuyCount = orderSubMapper.getAlreadyBuyCount(product.getId(), user.getId(), startTime, endTime);
                if (alreadyBuyCount != null && count + alreadyBuyCount > pa.getLimitNum()) {
                    throw new BadRequestException("该商品每人限购" + pa.getLimitNum() + "件。");
                }
            }
            //用户加购数量是否≤活动剩余库存
            if (count > pa.getRemainingStock()) {
                throw new BadRequestException("商品加购件数已超过活动库存。");
            }
        }
    }

}
