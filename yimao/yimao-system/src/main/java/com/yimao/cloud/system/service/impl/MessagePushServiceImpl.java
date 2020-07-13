package com.yimao.cloud.system.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.MessagePushDTO;
import com.yimao.cloud.pojo.dto.system.MessagePushExportDTO;
import com.yimao.cloud.pojo.query.station.StationMessageQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.MessagePushMapper;
import com.yimao.cloud.system.po.MessagePush;
import com.yimao.cloud.system.service.MessagePushService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/***
 * 功能描述:
 *
 * @param: 消息推送服务
 * @auther: liu yi
 * @date: 2019/5/23 10:07
 * @return:
 */
@Slf4j
@Service
public class MessagePushServiceImpl implements MessagePushService {
    @Resource
    private MessagePushMapper messagePushMapper;

    @Override
    public PageVO<MessagePushDTO> page(Integer receiverId, Integer pushType, String content, Integer devices,Integer app, Date startTime, Date endTime, Integer pageSize, Integer pageNum) {
        Example example = new Example(MessagePush.class);
        Example.Criteria criteria = example.createCriteria();

        if (receiverId !=null) {
            criteria.andEqualTo("receiverId", receiverId);
        }
        if (pushType != null && pushType != 0) {
            criteria.andEqualTo("pushType", pushType);
        }
        if (StringUtil.isNotBlank(content)) {
            criteria.andLike("content", "%" + content + "%");
        }
        if (devices != null) {
            criteria.andEqualTo("devices", devices);
        }
        if (app != null) {
            criteria.andEqualTo("app", app);
        }
        if (startTime != null) {
            criteria.andGreaterThanOrEqualTo("createTime", startTime);
        }
        if (endTime != null) {
            criteria.andLessThanOrEqualTo("createTime", endTime);
        }

        criteria.andEqualTo("isDelete", 0);
        PageHelper.startPage(pageNum, pageSize);
        example.orderBy("createTime").desc();
        Page<MessagePush> page = (Page<MessagePush>) messagePushMapper.selectByExample(example);
        return new PageVO<>(pageNum, page, MessagePush.class, MessagePushDTO.class);
    }


    @Override
    public MessagePushDTO getMessagePushById(Integer id) {
        if (id == null) {
            throw new BadRequestException("id不能为空！");
        }

        MessagePushDTO dto = new MessagePushDTO();
        MessagePush messagePush = messagePushMapper.selectByPrimaryKey(id);
        if (null == messagePush) {
            return null;
        }
        messagePush.convert(dto);

        return dto;
    }

    @Override
    public void batchDeleteMessagePushByIds(String ids) {
        if (StringUtil.isBlank(ids)) {
            throw new BadRequestException("id不能为空！");
        }

        String[] tempIds = ids.split(",");
        Integer[] array = (Integer[]) ConvertUtils.convert(tempIds, Integer.class);
        List<Integer> list = Arrays.asList(array);

        Example example = new Example(MessagePush.class);
        example.createCriteria().andIn("id", list);
        MessagePush mp = new MessagePush();
        mp.setIsDelete(1);

        messagePushMapper.updateByExampleSelective(mp, example);
    }

    @Override
    public void deleteMessagePushById(Integer id) {
        if (null == id) {
            throw new BadRequestException("id不能为空！");
        }

        Example example = new Example(MessagePush.class);
        example.createCriteria().andEqualTo("id", id);
        MessagePush mp = new MessagePush();
        mp.setIsDelete(1);

        messagePushMapper.updateByExampleSelective(mp, example);
    }


    @Override
    public void setMessagePushIsRead(Integer id) {
        if (id == null) {
            throw new BadRequestException("参数id不能为空！");
        }

        Example example = new Example(MessagePush.class);
        example.createCriteria().andEqualTo("id", id);
        MessagePush mp = new MessagePush();
        mp.setReadStatus(1);

        messagePushMapper.updateByExampleSelective(mp, example);
    }

    @Override
    public void insert(MessagePush messagePush) {
        messagePush.setReadStatus(0);
        messagePush.setIsDelete(0);
        messagePushMapper.insert(messagePush);
    }

    @Override
    public Integer getUnReadNum(Integer receiverId, Integer pushType, String content, Integer app) {
        Example example = new Example(MessagePush.class);
        Example.Criteria criteria = example.createCriteria();
        if (receiverId==null ) {
            throw new BadRequestException("接收者id不能能为空！");
        }
            criteria.andEqualTo("receiverId", receiverId);

        if (StringUtil.isNotBlank(content)) {
            criteria.andEqualTo("content", content);
        }
        if (pushType != null) {
            criteria.andEqualTo("pushType", pushType);
        }
        if (app != null) {
            criteria.andEqualTo("app", app);
        }

        criteria.andEqualTo("isDelete", 0);
        criteria.andEqualTo("readStatus", 0);

        return messagePushMapper.selectCountByExample(example);
    }

    @Override
    public MessagePushDTO findLastMessagePush(String deviceId, Integer filterType, Date compareDate) {
        Example example = new Example(MessagePush.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotBlank(deviceId)) {
            criteria.andEqualTo("deviceId", deviceId);
        }
        if (filterType != null) {
            criteria.andEqualTo("filterType", filterType);
        }
        if (compareDate != null) {
            criteria.andGreaterThanOrEqualTo("createTime", compareDate);
        }
        example.orderBy("createTime").desc();
        List<MessagePush> list = messagePushMapper.selectByExample(example);
        MessagePushDTO dto = null;
        if (list != null && list.size() > 0) {
            dto = new MessagePushDTO();
            MessagePush mp = list.get(0);
            mp.convert(dto);
        }
        return dto;
    }

    /**
     * 云平台导出工单信息
     */
    @Override
    public List<MessagePushExportDTO> exportMessagePush(String userName, Integer pushType, String content, Integer devices, Date startTime, Date endTime) {
        Example example = new Example(MessagePush.class);
        Example.Criteria criteria = example.createCriteria();
        List<MessagePushExportDTO> dtoList = new ArrayList<>();
        if (StringUtil.isNotBlank(userName)) {
            criteria.andEqualTo("userName", userName);
        }
        if (pushType != null) {
            criteria.andEqualTo("pushType", pushType);
        }
        if (StringUtil.isNotBlank(content)) {
            criteria.andLike("content", "%" + content + "%");
        }
        if (devices != null) {
            criteria.andEqualTo("devices", devices);
        }
        if (startTime != null) {
            criteria.andGreaterThanOrEqualTo("createTime", startTime);

        }
        if (endTime != null) {
            criteria.andLessThanOrEqualTo("createTime", endTime);
        }
        criteria.andEqualTo("isDelete", 0);

        List<MessagePush> list = messagePushMapper.selectByExample(example);
        if (list == null || list.size() <= 0) {
            throw new YimaoException("没有查到相关记录！");
        }

        String deviceTypeStr;
        MessagePushExportDTO dto;
        for (int i = 0; i < list.size(); i++) {
            dto = new MessagePushExportDTO();
            if (list.get(i).getDevices() != null && list.get(i).getDevices() == 1) {
                deviceTypeStr = "Android设备";
            } else if (list.get(i).getDevices() != null && list.get(i).getDevices() == 2) {
                deviceTypeStr = "IOS设备";
            } else {
                deviceTypeStr = "所有设备";
            }
            if (list.get(i).getCreateTime() != null) {
                dto.setCreateTimeStr(DateUtil.transferDateToString(list.get(i).getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            }
            dto.setContent(list.get(i).getContent());
            dto.setDeviceTypeStr(deviceTypeStr);
            dto.setTitle(list.get(i).getTitle());
            dtoList.add(dto);
        }

        return dtoList;
    }


	/**
	 * 站务系统消息查询
	 */
	public PageVO<MessagePushDTO> stationPage(Integer pageNum, Integer pageSize, StationMessageQuery query) {
		PageHelper.startPage(pageNum, pageSize);
		Page page=messagePushMapper.getStationMessage(query);
		return new PageVO<>(pageNum, page);
	}
}
