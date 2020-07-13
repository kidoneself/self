package com.yimao.cloud.hra.service.impl;

import com.yimao.cloud.base.enums.HraGiveTypeEnum;
import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.hra.mapper.DiscountCardRecordMapper;
import com.yimao.cloud.hra.po.DiscountCardRecord;
import com.yimao.cloud.hra.po.DiscountCardSetting;
import com.yimao.cloud.hra.service.DiscountCardRecordService;
import com.yimao.cloud.hra.service.DiscountCardSettingService;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 优惠卡发放记录业务层
 *
 * @author Zhang Bo
 * @date 2018/6/22.
 */
@Service
@Slf4j
public class DiscountCardRecordServiceImpl implements DiscountCardRecordService {

    @Resource
    private DiscountCardRecordMapper discountCardRecordMapper;
    @Resource
    private DiscountCardRecordService discountCardRecordService;
    @Resource
    private DiscountCardSettingService discountCardSettingService;

    /**
     * 保存需要发放优惠卡的记录
     *
     * @param record
     */
    @Override
    public void saveRecord(DiscountCardRecord record) {
        record.setReceived(false);
        record.setGiveTime(new Date());
        discountCardRecordMapper.insert(record);
    }

    @Override
    public void update(DiscountCardRecord record) {
        discountCardRecordMapper.updateByPrimaryKeySelective(record);
    }

    /**
     * 判断经销商或者分销用户是否已经发放了固定配额的优惠卡
     *
     * @param userDTO
     * @return
     */
    @Override
    public DiscountCardRecord getRecordByGiveName(UserDTO userDTO) {
        Example example = new Example(DiscountCardRecord.class);
        Example.Criteria criteria = example.createCriteria();
        if (UserType.isDistributor(userDTO.getUserType())) {
            criteria.andEqualTo("giveName", userDTO.getUserName());
            criteria.andEqualTo("giveType", 1);//经销商配额
        } else {
//            criteria.andEqualTo("giveName", user.getMobile());
//            criteria.andEqualTo("giveType", 7);//分销用户配额
            return null;
        }
        example.orderBy("giveTime").desc();
        List<DiscountCardRecord> recordList = discountCardRecordMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(recordList)) {
            return null;
        }
        return recordList.get(0);
    }

    /**
     * 获取根据业绩发放的优惠卡记录
     *
     * @param userDTO
     * @return
     */
    @Override
    public DiscountCardRecord getHistoryRecord(UserDTO userDTO) {
        // if (UserType.isDistributor(userDTO.getUserType())) {
        //     return discountCardRecordMapper.getHistoryRecord(userDTO.getUserName(), 2);
        // } else {
        //     return discountCardRecordMapper.getHistoryRecord(userDTO.getMobile(), 3);
        // }
        Example example = new Example(DiscountCardRecord.class);
        Example.Criteria criteria = example.createCriteria();
        if (UserType.isDistributor(userDTO.getUserType())) {
            criteria.andEqualTo("giveName", userDTO.getUserName());
            criteria.andEqualTo("giveType", HraGiveTypeEnum.DIS_SALE.value);//经销商业绩历史发放
        } else {
            criteria.andEqualTo("giveName", userDTO.getMobile());
            criteria.andEqualTo("giveType", HraGiveTypeEnum.USER_SALE.value);//用户业绩历史发放
        }
        example.orderBy("giveTime").desc();
        List<DiscountCardRecord> recordList = discountCardRecordMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(recordList)) {
            return null;
        }
        return recordList.get(0);
    }

    /**
     * 获取分销用户的分销业绩卡记录。
     * 每分销出去一台水机获得一张，总共五张(可配置)
     *
     * @param userDTO
     * @return
     */
    @Override
    public int countVipUserQuotaRecord(UserDTO userDTO) {
        Example example = new Example(DiscountCardRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("giveName", userDTO.getMobile());
        criteria.andEqualTo("giveType", HraGiveTypeEnum.USER_QUOTA.value);//7-分销用户配额卡，每分销出去一台水机获得一张，总共五张(可配置)
        example.orderBy("giveTime").desc();
        List<DiscountCardRecord> recordList = discountCardRecordMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(recordList)) {
            return 0;
        }
        return recordList.get(0).getTotalCount();
    }

    @Override
    public Integer listNotReceivedCardCount(UserDTO userDTO) {

        return 0;
    }


    /**
     * 获取用户未发放的优惠卡记录
     */
    @Override
    public List<DiscountCardRecord> listNotReceivedRecords(Integer userId, Integer giveType) {
        Example example = new Example(DiscountCardRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("received", false);
        if (giveType != null) {
            criteria.andEqualTo("giveType", giveType);
        }
        example.orderBy("giveTime");
        return discountCardRecordMapper.selectByExample(example);
    }

    @Override
    public void grantDiscountCard(UserDTO userDTO) {
        DiscountCardRecord dbRecord = this.getRecordByGiveName(userDTO);
        if (dbRecord == null) {
            DiscountCardRecord record = new DiscountCardRecord();
            record.setUserId(userDTO.getId());
            record.setGiveName(userDTO.getUserName());
            String company = null;
            if (userDTO.getUserType() == 5 || userDTO.getUserType() == 6) {
                company = userDTO.getCompanyName();
            }
            DiscountCardSetting setting = discountCardSettingService.getGiveCount(userDTO.getUserType(), company);
            record.setGiveType(1);//经销商配额
            record.setRemark("经销商优惠卡配额");
            record.setGiveCount(setting.getGiveCount());
            record.setTotalCount(setting.getGiveCount());
            record.setTicketStyle(setting.getTicketStyle());
            record.setWatermark(setting.getWatermark());
            record.setImage(setting.getImage());
            record.setImageUsed(setting.getImageUsed());
            discountCardRecordService.saveRecord(record);
        }
    }
}
