package com.yimao.cloud.hra.mapper;


import com.github.pagehelper.Page;
import com.yimao.cloud.hra.po.HraCard;
import com.yimao.cloud.pojo.dto.hra.HraCardDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2017/11/29.
 */
public interface HraCardMapper extends Mapper<HraCard> {

    Page<HraCardDTO> selectCardAndTicket(@Param("userId") Integer userId);

    Page<HraCardDTO> selectCardByUser(@Param(value = "userId") Integer userId, @Param(value = "orderId") Long orderId);

    void updateCardUpdateTime(@Param(value = "type") Integer type,
                              @Param(value = "cardIdOrTicketNo") String cardIdOrTicketNo);

    void insertBatch(List<HraCard> cardList);

}
