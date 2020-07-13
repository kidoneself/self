package com.yimao.cloud.hra.service;


import com.yimao.cloud.hra.po.DiscountCardRecord;
import com.yimao.cloud.pojo.dto.user.UserDTO;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/6/22.
 */
public interface DiscountCardRecordService {
    void saveRecord(DiscountCardRecord record);

    void update(DiscountCardRecord record);

    DiscountCardRecord getRecordByGiveName(UserDTO userDTO);

   DiscountCardRecord getHistoryRecord(UserDTO userDTO);
    // Integer getHistoryRecord(UserDTO userDTO);

    int countVipUserQuotaRecord(UserDTO userDTO);

    Integer listNotReceivedCardCount(UserDTO userDTO);

    List<DiscountCardRecord> listNotReceivedRecords(Integer userId, Integer giveType);

    void grantDiscountCard(UserDTO userDTO);
}
