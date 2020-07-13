package com.yimao.cloud.system.jobs;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.system.BusinessProfileDTO;
import com.yimao.cloud.system.feign.OrderFeign;
import com.yimao.cloud.system.feign.UserFeign;
import com.yimao.cloud.system.mapper.BusinessProfileDetailMapper;
import com.yimao.cloud.system.mapper.BusinessProfileMapper;
import com.yimao.cloud.system.po.BusinessProfile;
import com.yimao.cloud.system.po.BusinessProfileDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时任务：每日凌晨更新一次经营概况
 *
 * @author hhf
 * @date 2019/3/25
 */
@Slf4j
@Component
@EnableScheduling
public class UpdateBusinessProfile {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private BusinessProfileMapper businessProfileMapper;
    @Resource
    private BusinessProfileDetailMapper detailMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void getAndUpdateBusinessProfile() {
        log.info("开始更新经营概况");

        List<BusinessProfileDetail> list = new ArrayList<>();
        // 销售额,有效订单数
        BusinessProfileDTO dto = orderFeign.orderOverviewBusiness();
        // 经销商总数，用户总数
        BusinessProfileDTO businessProfileDTO = userFeign.overviewBusiness();
        if (businessProfileDTO != null) {
            dto.setYestDistributorTotal(businessProfileDTO.getYestDistributorTotal());
            dto.setDistributorTotal(businessProfileDTO.getDistributorTotal());
            dto.setYestUserTotal(businessProfileDTO.getYestUserTotal());
            dto.setUserTotal(businessProfileDTO.getUserTotal());
        }
        BusinessProfile profile = new BusinessProfile(dto);
        profile.setCreateTime(new Date());
        int result = businessProfileMapper.insertSelective(profile);
        if (result < 1) {
            throw new YimaoException(500, "操作失败。");
        }
        dto.getDetails().forEach(o->{
            BusinessProfileDetail profileDetail = new BusinessProfileDetail(o);
            profileDetail.setBusinessProfileId(profile.getId());
            profileDetail.setCreateTime(new Date());
            list.add(profileDetail);
        });
        detailMapper.batchInsert(list);
        log.info(JSONObject.toJSONString(dto));
    }
}