package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.hra.mapper.DiscountCardSettingMapper;
import com.yimao.cloud.hra.po.DiscountCardSetting;
import com.yimao.cloud.hra.service.DiscountCardSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2018/1/24.
 */
@Service
@Slf4j
public class DiscountCardSettingServiceImpl implements DiscountCardSettingService {

    @Resource
    private DiscountCardSettingMapper discountCardSettingMapper;

    @Override
    public DiscountCardSetting getGiveCount(Integer userType, String company) {
        Example example = new Example(DiscountCardSetting.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userType", userType);
        boolean flag = StringUtil.isNotEmpty(company) && UserType.isCompanyDistributor(userType);
        if (flag) {
            criteria.andEqualTo("company", company);
        }
        //按创建时间倒序排列
        example.orderBy("createTime").desc();
        List<DiscountCardSetting> list = discountCardSettingMapper.selectByExample(example);
        DiscountCardSetting setting;
        if (CollectionUtil.isEmpty(list)) {
            setting = new DiscountCardSetting();
            if (flag) {
                setting.setUserType(userType);
                setting.setCompany(company);
                setting.setTicketStyle(1);
                String companyName = "翼猫科技发展（上海）有限公司";
                if (Objects.equals(company, companyName)) {
                    setting.setWatermark(companyName);
                } else {
                    setting.setWatermark("翼猫科技与" + company + "联合推出");
                }
                if (UserType.DISTRIBUTOR_950.value == userType) {
                    setting.setGiveCount(20);
                } else {
                    setting.setGiveCount(5);
                }
            } else {
                log.error("优惠卡发放失败-没有查询到发放配置信息");
            }
            return setting;
        }
        return list.get(0);
    }

}
