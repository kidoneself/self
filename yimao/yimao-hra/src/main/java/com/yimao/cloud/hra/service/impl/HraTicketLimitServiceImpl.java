package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.hra.mapper.HraTicketLimitMapper;
import com.yimao.cloud.hra.mapper.HraTicketMapper;
import com.yimao.cloud.hra.po.HraTicket;
import com.yimao.cloud.hra.po.HraTicketLimit;
import com.yimao.cloud.hra.service.HraTicketLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2018/6/27.
 */
@Service
@Slf4j
public class HraTicketLimitServiceImpl implements HraTicketLimitService {

    @Resource
    private HraTicketMapper hraTicketMapper;
    @Resource
    private HraTicketLimitMapper hraTicketLimitMapper;
    @Resource
    private RedisCache redisCache;

    /**
     * 优惠卡使用次数限制判断逻辑
     *
     * @param userId
     * @return
     */
    @Override
    public String verify(Integer userId) {
        try {
            List<HraTicketLimit> limitList = redisCache.getCacheList(Constant.HRA_TICKET_LIMIT, HraTicketLimit.class);
            if (CollectionUtils.isEmpty(limitList)) {
                limitList = hraTicketLimitMapper.selectAll();
                if (!CollectionUtils.isEmpty(limitList)) {
                    redisCache.setCacheList(Constant.HRA_TICKET_LIMIT, limitList, HraTicketLimit.class, -1);
                }
            }
            if (CollectionUtils.isEmpty(limitList)) {
                return null;
            }
            HraTicketLimit limit = null;
            HraTicketLimit personalLimit = null;
            for (HraTicketLimit htl : limitList) {
                if (htl.getUserId() == null) {
                    limit = htl;
                }
                if (Objects.equals(htl.getUserId(), userId)) {
                    personalLimit = htl;
                }
            }
            if (personalLimit != null) {
                limit = personalLimit;
            }
            if (limit == null) {
                return null;
            }
            Date beginTime = null;
            Date endTime = null;
            Integer limitType = limit.getLimitType();
            Integer times = limit.getTimes();
            if (limitType == null || times == null || times == 0) {//times=0表示不限制
                return null;
            }
            String prefix = "每个用户";
            if (limit.getUserId() != null) {
                prefix = "用户" + limit.getUserId();
            }
            switch (limitType) {
                case 1://每人每天
                    beginTime = DateUtil.getCurrentDayBeginTime();
                    endTime = DateUtil.getCurrentDayEndTime();
                    prefix = "每天";
                    break;
                case 2://每人每周
                    beginTime = DateUtil.getCurrentWeekBeginTime();
                    endTime = DateUtil.getCurrentWeekEndTime();
                    prefix = "每周";
                    break;
                case 3://每人每月
                    beginTime = DateUtil.getCurrentMonthBeginTime();
                    endTime = DateUtil.getCurrentMonthEndTime();
                    prefix = "每月";
                    break;
                case 4://每人每季度
                    break;
                case 5://每人每年
                    beginTime = DateUtil.getCurrentYearBeginTime();
                    endTime = DateUtil.getCurrentYearEndTime();
                    prefix = "每年";
                    break;
                default:
                    break;
            }

            if (beginTime != null) {
                Example example = new Example(HraTicket.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("ticketType", "M");//只查询M卡，因为只对M卡做限制
                criteria.andEqualTo("userId", userId);
                criteria.andEqualTo("ticketStatus", 2);//已使用
                criteria.andGreaterThanOrEqualTo("useTime", beginTime);
                criteria.andLessThanOrEqualTo("useTime", endTime);
                int useCount = hraTicketMapper.selectCountByExample(example);
                if (useCount >= times) {
                    return prefix + "仅限使用" + times + "张优惠卡，您已超出，请赠送给他人后使用。";
                }
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
