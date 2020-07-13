package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.MessagePushDTO;
import com.yimao.cloud.pojo.dto.system.MessagePushExportDTO;
import com.yimao.cloud.pojo.query.station.StationMessageQuery;
import com.yimao.cloud.pojo.query.system.MessagePushQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.MessagePush;
import tk.mybatis.mapper.common.Mapper;

/***
 * 功能描述:消息推送记录
 *
 * @auther: liu yi
 * @date: 2019/4/29 10:23
 */

public interface MessagePushMapper extends Mapper<MessagePush> {
    Page<MessagePushExportDTO> exportMessagePush(MessagePushQuery query);

    Page<MessagePushDTO> getStationMessage(StationMessageQuery query);
}
