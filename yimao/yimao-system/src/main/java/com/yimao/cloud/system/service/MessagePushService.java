package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.MessagePushDTO;
import com.yimao.cloud.pojo.dto.system.MessagePushExportDTO;
import com.yimao.cloud.pojo.query.station.StationMessageQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.MessagePush;

import java.util.Date;
import java.util.List;

public interface MessagePushService {
    /***
     * 功能描述:分页查询
     *
     * @param: [title, content, devices, startTime, endTime, pageSize, pageNum]
     * @auther: liu yi
     * @date: 2019/5/5 11:25
     * @return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.system.po.MessagePush>
     */
    PageVO<MessagePushDTO> page(Integer receiverId, Integer pushType, String content, Integer devices,Integer app, Date startTime, Date endTime, Integer pageSize, Integer pageNum);

    /***
     * 功能描述:根据id查询
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/5/5 11:25
     * @return:
     */
    MessagePushDTO getMessagePushById(Integer id);

    /***
     * 功能描述:根据id删除
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/5/5 11:26
     * @return: void
     */
    void deleteMessagePushById(Integer id);

    void batchDeleteMessagePushByIds(String ids);

    /***
     * 功能描述:插入信息
     *
     * @param: [messagePush]
     * @auther: liu yi
     * @date: 2019/5/5 11:30
     * @return: void
     */
    void insert(MessagePush messagePush);

    /***
     * 功能描述:设置信息已读
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/5/6 10:27
     * @return: void
     */
    void setMessagePushIsRead(Integer id);

    /***
     * 功能描述:未读信息统计
     *
     * @param: [userName, content, typeName, app]
     * @auther: liu yi
     * @date: 2019/5/6 15:25
     * @return: java.lang.Long
     */
    Integer getUnReadNum(Integer receiverId, Integer pushType, String content, Integer app);

    /***
     * 功能描述:根据条件查找该时间最后一次记录
     *
     * @param: [deviceId, pushType, expireDate]
     * @auther: liu yi
     * @date: 2019/5/10 9:54
     * @return: com.yimao.cloud.pojo.dto.system.MessagePushDTO
     */
    MessagePushDTO findLastMessagePush(String deviceId, Integer filterType, Date compareDate);

    /***
     * 功能描述:导出
     *
     * @param: [userName, title, content, devices, startTime, endTime, response]
     * @auther: liu yi
     * @date: 2019/5/7 11:30
     * @return: void
     */
    List<MessagePushExportDTO> exportMessagePush(String userName, Integer pushType, String content, Integer devices, Date startTime, Date endTime);
   
    /**
     * 站务系统消息查询
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
	PageVO<MessagePushDTO> stationPage(Integer pageNum, Integer pageSize, StationMessageQuery query);

}
