package com.yimao.cloud.system.service.impl;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.pojo.dto.system.BusinessProfileDTO;
import com.yimao.cloud.pojo.dto.system.BusinessProfileDetailDTO;
import com.yimao.cloud.system.mapper.BusinessProfileDetailMapper;
import com.yimao.cloud.system.mapper.BusinessProfileMapper;
import com.yimao.cloud.system.po.BusinessProfile;
import com.yimao.cloud.system.po.BusinessProfileDetail;
import com.yimao.cloud.system.service.OverviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class OverviewServiceImpl implements OverviewService {

    @Resource
    private BusinessProfileMapper profileMapper;
    @Resource
    private BusinessProfileDetailMapper detailMapper;


    @Override
    public BusinessProfileDTO overviewBusiness() {
        BusinessProfileDTO dto = new BusinessProfileDTO();
        // 基本信息
        Example example = new Example(BusinessProfile.class);
        //Example.Criteria criteria = example.createCriteria();
        example.orderBy("createTime").desc();
        List<BusinessProfile> businessProfiles = profileMapper.selectByExample(example);
        if(CollectionUtil.isNotEmpty(businessProfiles)){
            businessProfiles.get(0).convert(dto);
        }
        Map<Integer,List<BusinessProfileDetailDTO>> map = new HashMap<>();
        // 明细：昨日销售额/销量 7日销售额/销量 1月销售额/销量
        map.put(1,detailMapper.selectByDate(1));
        map.put(7,detailMapper.selectByDate(2));
        map.put(30,detailMapper.selectByDate(3));
        dto.setDtoMap(map);

        // 1个月日明细数据
        List<BusinessProfileDetailDTO> dtos = new ArrayList<>();
        String months = DateUtil.backMonths(new Date(),1,"yyyy-MM-dd");
        Example example1 = new Example(BusinessProfileDetail.class);
        Example.Criteria criteria = example1.createCriteria();
        criteria.andGreaterThan("createTime",months);
        List<BusinessProfileDetail> list = detailMapper.selectByExample(example1);
        if(CollectionUtil.isNotEmpty(list)){
            list.forEach(o->{
                BusinessProfileDetailDTO detailDTO = new BusinessProfileDetailDTO();
                o.convert(detailDTO);
                dtos.add(detailDTO);
            });
        }
        dto.setDetails(dtos);
        return dto;
    }
}
