package com.yimao.cloud.system.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.MessageTemplateDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.MessageTemplateMapper;
import com.yimao.cloud.system.po.MessageTemplate;
import com.yimao.cloud.system.service.MessageTemplateService;
import org.springframework.data.redis.connection.ConnectionUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class MessageTemplateServiceImpl implements MessageTemplateService {

    @Resource
    private MessageTemplateMapper messageTemplateMapper;

    @Override
    public PageVO<MessageTemplateDTO> page(Integer type, Integer mechanism, Integer pushObject, Integer pushMode, int pageSize, int pageNum) {
        Example example = new Example(MessageTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        if (type!=null) {
            criteria.andEqualTo("type", type);
        }
        if (mechanism!=null) {
            criteria.andEqualTo("mechanism", mechanism);
        }
        if (pushObject !=null) {
            criteria.andEqualTo("pushObject", pushObject);
        }
        if (pushMode !=null) {
            criteria.andEqualTo("pushMode", pushMode);
        }
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<MessageTemplate> page = (Page<MessageTemplate>) messageTemplateMapper.selectByExample(example);
        return new PageVO<>(pageNum, page, MessageTemplate.class, MessageTemplateDTO.class);
    }

    @Override
    public boolean exists(Integer type, Integer mechanism, Integer pushObject, Integer pushMode) {
        Example example = new Example(MessageTemplate.class);
        Example.Criteria criteria = example.createCriteria();

        if (type==null) {
            throw new BadRequestException("请选择类别");
        }
        if (mechanism==null) {
            throw new BadRequestException("请选择机制");
        }
        if (pushMode==null) {
            throw new BadRequestException("请选择推送方式");
        }
        if (pushObject==null) {
            throw new BadRequestException("请选择推送对象");
        }

        criteria.andEqualTo("type", type);
        criteria.andEqualTo("mechanism", mechanism);
        criteria.andEqualTo("pushObject", pushObject);
        criteria.andEqualTo("pushMode", pushMode);
        List<MessageTemplate> list = messageTemplateMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void addMessageTemplate(MessageTemplateDTO dto) {
        if (dto == null) {
            throw new BadRequestException("传参不能为空！");
        }

        if (dto.getType() == null) {
            throw new BadRequestException("请选择类别");
        }
        if (dto.getMechanism()==null) {
            throw new BadRequestException("请选择机制");
        }
        if (dto.getPushMode()==null) {
            throw new BadRequestException("请选择推送方式");
        }
        if (dto.getPushObject()==null) {
            throw new BadRequestException("请选择推送对象");
        }

        boolean flag = exists(dto.getType(), dto.getMechanism(), dto.getPushObject(), dto.getPushMode());
        if (flag) {
            throw new BadRequestException("该模版已存在！");
        }

        MessageTemplate messageTemplate = new MessageTemplate(dto);
        messageTemplate.setCreateTime(new Date());
        messageTemplate.setUpdateTime(new Date());

        messageTemplateMapper.insert(messageTemplate);
    }

    @Override
    public void updateMessageTemplate(MessageTemplateDTO dto) {
        if (dto == null) {
            throw new BadRequestException("传参不能为空！");
        }
        boolean flag = exists(dto.getType(), dto.getMechanism(), dto.getPushObject(), dto.getPushMode());

        if (flag) {
            throw new BadRequestException("该模版已存在！");
        }

        MessageTemplate messageTemplate = new MessageTemplate(dto);
        messageTemplate.setUpdateTime(new Date());
        messageTemplateMapper.updateByPrimaryKey(messageTemplate);
    }

    @Override
    public MessageTemplateDTO getMessageTemplateById(Integer id) {
        if (id == null) {
            throw new BadRequestException("id不能为空！");
        }

        MessageTemplate messageTemplate = messageTemplateMapper.selectByPrimaryKey(id);
        MessageTemplateDTO dto = new MessageTemplateDTO();
        messageTemplate.convert(dto);

        return dto;
    }

    @Override
    public MessageTemplateDTO getMessageTemplateByParam(Integer type, Integer mechanism, Integer pushObject, Integer pushMode) {
        if (type==null) {
            throw new BadRequestException("请选择类别");
        }
        if (mechanism==null) {
            throw new BadRequestException("请选择机制");
        }
        if (pushMode==null) {
            throw new BadRequestException("请选择推送方式");
        }
        if (pushObject==null) {
            throw new BadRequestException("请选择推送对象");
        }

        Example example = new Example(MessageTemplate.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("type", type);
        criteria.andEqualTo("mechanism", mechanism);
        criteria.andEqualTo("pushObject", pushObject);
        criteria.andEqualTo("pushMode", pushMode);
        List<MessageTemplate> list = messageTemplateMapper.selectByExample(example);

        MessageTemplateDTO dto = null;
        if (list != null && list.size() > 0) {
            dto = new MessageTemplateDTO();
            MessageTemplate mt = list.get(0);
            mt.convert(dto);
        }

        return dto;
    }
}
