package com.yimao.cloud.hra.service;


import com.yimao.cloud.pojo.dto.hra.ActivityExchangeDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/4/10
 */
public interface ActivityExchangeService {

    PageVO<ActivityExchangeDTO> page(Integer side, Integer channel, String exchangeCode, String batchNumber, String exchangeStatus, String ticketNo, Integer ticketStatus, Integer pageNum, Integer pageSize);

    void saveHraExchange(Integer count, Date beginTime, Date endTime, Integer side, Integer channel, String channelName);

    String exChangeTicketByCode(String exchangeCode, Integer exchangeFrom, String channel, String ip);

    void exChangeSet(Integer terminal, Integer limitType, Integer times);

}
