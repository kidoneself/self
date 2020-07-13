package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.hra.mapper.HraTicketLifeCycleMapper;
import com.yimao.cloud.hra.po.HraTicketLifeCycle;
import com.yimao.cloud.hra.service.TicketLifeCycleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaozepeng on 2018/2/1.
 */
@Service
@Slf4j
public class TicketLifeCycleServiceImpl implements TicketLifeCycleService {

    @Resource
    private HraTicketLifeCycleMapper hraTicketLifeCycleMapper;

    @Override
    public HraTicketLifeCycle findTicketLifeCycleByHandselFlag(Long handselFalg) {
        Example example = new Example(HraTicketLifeCycle.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("handselFlag", handselFalg);
        List<HraTicketLifeCycle> ticketLifeCycles = hraTicketLifeCycleMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(ticketLifeCycles)) {
            return null;
        }
        return ticketLifeCycles.get(0);
    }

    @Override
    public int updateTicketLifeCycle(HraTicketLifeCycle ticketLifeCycle) {
        return hraTicketLifeCycleMapper.updateByPrimaryKeySelective(ticketLifeCycle);
    }

    @Override
    public List<HraTicketLifeCycle> listTicketLifeCycle(String cardId, Integer origUserId, Date handselTime, Integer expiredFlag) {
        Example example = new Example(HraTicketLifeCycle.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cardId", cardId);
        criteria.andEqualTo("origUserId", origUserId);
        criteria.andEqualTo("handselTime", handselTime);
        criteria.andEqualTo("expiredFlag", expiredFlag);
        criteria.andIsNull("destUserId");
        return hraTicketLifeCycleMapper.selectByExample(example);
    }

    @Override
    public void updateHandselRecordToTimeOut(HraTicketLifeCycle hraTicketLifeCycle) {
        hraTicketLifeCycleMapper.updateByPrimaryKeySelective(hraTicketLifeCycle);
    }

    @Override
    public HraTicketLifeCycle getLifeCycle(String ticketNo, Integer origUserId, Long handselTime) {
        Example example = new Example(HraTicketLifeCycle.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("origUserId", origUserId);
        criteria.andEqualTo("ticketNo", ticketNo);
        criteria.andEqualTo("handselFlag", handselTime);
        HraTicketLifeCycle lifeCycle = hraTicketLifeCycleMapper.selectOneByExample(example);
        if (lifeCycle != null) {
            return lifeCycle;
        }
        return null;
    }

    @Override
    public HraTicketLifeCycle getLifeCycleByTicketNoAndOrigUserId(String tickedNo, Integer origUserId) {
        Example example = new Example(HraTicketLifeCycle.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("origUserId", origUserId);
        criteria.andEqualTo("ticketNo", tickedNo);
        HraTicketLifeCycle lifeCycle = hraTicketLifeCycleMapper.selectOneByExample(example);
        if (lifeCycle != null) {
            return lifeCycle;
        }
        return null;
    }

    @Override
    public List<HraTicketLifeCycle> getTicketLifeCycle(Integer userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("origUserId", userId);
        List<HraTicketLifeCycle> lifeCycleList = hraTicketLifeCycleMapper.selectLifeCycleByMap(map);
        log.info("lifeCycleList.size()====" + lifeCycleList.size());
        if (CollectionUtil.isNotEmpty(lifeCycleList)) {
            return lifeCycleList;
        }
        return null;
    }

}
