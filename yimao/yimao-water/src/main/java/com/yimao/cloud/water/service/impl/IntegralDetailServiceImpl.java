package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.StatusTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.aop.OperationType;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import com.yimao.cloud.framework.aop.parser.DefaultContentParse;
import com.yimao.cloud.pojo.dto.water.IntegralDetailDTO;
import com.yimao.cloud.pojo.dto.water.IntegralDetailExportDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.mapper.IntegralDetailMapper;
import com.yimao.cloud.water.po.FlowRule;
import com.yimao.cloud.water.po.IntegralDetail;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.service.FlowRuleService;
import com.yimao.cloud.water.service.IntegralDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/***
 * 功能描述:积分详情
 *
 * @auther: liu yi
 * @date: 2019/6/3 17:06
 */
@Service
@Slf4j
public class IntegralDetailServiceImpl implements IntegralDetailService {
    @Resource
    private IntegralDetailMapper integralDetailMapper;
    @Resource
    private FlowRuleService flowRuleService;

    /*@Override
    public void batchSavePadIntegralDetail(List<IntegralDetailDTO> list, String sn) {
        if (list == null || list.size() == 0) {
            throw new BadRequestException("传入积分详情不能为空！");
        }
        if (StringUtil.isBlank(sn)) {
            throw new BadRequestException("传入SN不能为空！");
        }

        *//*WaterDevice waterDevice = waterDeviceService.getBySnCode(sn);
        if (waterDevice == null) {
            throw new BadRequestException("设备不存在！");
        }*//*
        List<IntegralDetail> integralDetailList = new ArrayList<>();
        for (IntegralDetailDTO dto : list) {
            IntegralDetail detail = new IntegralDetail();
            detail.setIntegralRuleId(dto.getIntegralRuleId());
            detail.setIntegralRuleName(dto.getIntegralRuleName());
            detail.setSn(dto.getSn());
            detail.setModel(dto.getModel());
            detail.setProvince(dto.getProvince());
            detail.setCity(dto.getCity());
            detail.setRegion(dto.getRegion());
            detail.setAddress(dto.getAddress());
            detail.setType(dto.getType());
            detail.setNum(dto.getNum());
            detail.setId(null);
            //detail.setDescription("新增积分");
            detail.setCreateTime(DateUtil.transferStringToDate(dto.getPadCreateTime()));
            integralDetailList.add(detail);
        }

        integralDetailMapper.batchInsert(integralDetailList);
    }*/

    @Override
    public void savePadIntegralDetail(IntegralDetailDTO dto) {
        FlowRule rule=flowRuleService.getFlowRule();
        if(rule!=null && rule.getAllIntegralUpload()== StatusTypeEnum.YES.value){
            if (dto==null) {
                throw new BadRequestException("传入积分详情不能为空！");
            }
            if (StringUtil.isBlank(dto.getSn())) {
                throw new BadRequestException("传入SN不能为空！");
            }
            IntegralDetail detail=getBySn(dto.getSn());

            if(detail !=null){
                Integer num=dto.getNum()!=null?dto.getNum()+detail.getNum():0;
                detail.setNum(num);
                integralDetailMapper.updateByPrimaryKeySelective(detail);
            }else{
                detail=new IntegralDetail(dto);
                if(detail.getCreateTime()==null){
                    detail.setCreateTime(new Date());
                }
                detail.setId(null);
                integralDetailMapper.insertSelective(detail);
            }
        }
    }

    /**
     * 根据ID获取积分详情信息
     *
     * @param id 积分详情ID
     */
    @Override
    public IntegralDetailDTO getById(Integer id) {
        if (id == null) {
            throw new BadRequestException("传入id不能为空！");
        }
        IntegralDetail detail = integralDetailMapper.selectByPrimaryKey(id);
        if (Objects.nonNull(detail)) {
            IntegralDetailDTO dto = new IntegralDetailDTO();
            detail.convert(dto);
            return dto;
        }

        return null;
    }

    public IntegralDetail getBySn(String sn) {
        Example example = new Example(IntegralDetail.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("sn", sn);
        IntegralDetail detail = integralDetailMapper.selectOneByExample(example);

        return detail;
    }


    @Override
    public PageVO<IntegralDetailDTO> getDetailListByRuleId(Integer ruleId, String sn, String province, String city, String region, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<IntegralDetailDTO> page = integralDetailMapper.getDetailListByRuleId(sn, ruleId, province, city, region);

        return new PageVO<>(pageNum, page);
    }

    @Override
    public List<IntegralDetailDTO> getExportDetailListByRuleId(Integer ruleId, String sn, String province, String city, String region) {
        Page<IntegralDetailDTO> page = integralDetailMapper.getDetailListByRuleId(sn, ruleId, province, city, region);

        return page.getResult();
    }

    @Override
    public int getCountBySn(String sn, Date startTime, Date endTime) {
        if (StringUtil.isBlank(sn)) {
            throw new BadRequestException("SN不能为空！");
        }
        Integer count = integralDetailMapper.getCountBySn(sn, startTime, endTime);
        if (count == null) {
            return 0;
        }
        return count;
    }

    @Override
    public int getPadEffectiveTotalIntegralBySn(String sn) {
        if (StringUtil.isBlank(sn)) {
            throw new BadRequestException("SN不能为空！");
        }
        Integer count = integralDetailMapper.getPadEffectiveTotalIntegralBySn(sn);
        if (count == null) {
            return 0;
        }
        return count;
    }


    @Override
    public PageVO<IntegralDetailDTO> getDetailListBySn(String sn, Date startTime, Date endTime, Integer pageNum, Integer pageSize) {
        Example example = new Example(IntegralDetail.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isBlank(sn)) {
            throw new BadRequestException("SN不能为空！");
        }
        criteria.andEqualTo("sn", sn);

        if (startTime != null) {
            criteria.andGreaterThanOrEqualTo("createTime", startTime);
        }
        if (endTime != null) {
            criteria.andLessThanOrEqualTo("createTime", endTime);
        }
        example.orderBy("createTime").desc();
        PageHelper.startPage(pageNum, pageSize);
        Page<IntegralDetail> page = (Page<IntegralDetail>) integralDetailMapper.selectByExample(example);
        return new PageVO<>(pageNum, page, IntegralDetail.class, IntegralDetailDTO.class);
    }

    @Override
    public List<IntegralDetailExportDTO> getExportDetailListBySn(String sn, Date startTime, Date endTime) {
        Example example = new Example(IntegralDetail.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isBlank(sn)) {
            throw new BadRequestException("SN不能为空！");
        }
        criteria.andEqualTo("sn", sn);

        if (startTime != null) {
            criteria.andGreaterThanOrEqualTo("createTime", endTime);
        }
        if (endTime != null) {
            criteria.andLessThanOrEqualTo("createTime", endTime);
        }
        example.orderBy("createTime").desc();

        List<IntegralDetail> list = integralDetailMapper.selectByExample(example);
        List<IntegralDetailExportDTO> exportList = new ArrayList<>();
        IntegralDetailExportDTO dto;
        for (int i = 0; i < list.size(); i++) {
            dto = new IntegralDetailExportDTO();
            if (list.get(i).getCreateTime() != null) {
                dto.setCreateTime(DateUtil.transferDateToString(list.get(i).getCreateTime(), "yyyy-MM-dd"));
            }
            if (list.get(i).getType() != null) {
                if (list.get(i).getType() == 1) {
                    dto.setType("点击广告");
                } else if (list.get(i).getType() == 2) {
                    dto.setType("单日在线时长");
                } else {
                    dto.setType("未知");
                }
            }
            dto.setSn(list.get(i).getSn());
            dto.setIntegralRuleName(list.get(i).getIntegralRuleName());
            dto.setNum(list.get(i).getNum());
            exportList.add(dto);
        }

        return exportList;
    }


    /**
     * 更新
     *
     * @param dto 积分详情
     */
    @EnableOperationLog(
            name = "更新积分详情",
            type = OperationType.UPDATE,
            daoClass = IntegralDetailMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id", "sn"},
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void update(IntegralDetailDTO dto) {
        if (dto == null) {
            throw new BadRequestException("传入参数不能为空！");
        }

        IntegralDetail detail = new IntegralDetail(dto);
        integralDetailMapper.updateByPrimaryKeySelective(detail);
    }


    /**
     * 根据ID删除积分详情
     *
     * @param id 积分详情ID
     */
    @EnableOperationLog(
            name = "删除积分详情",
            type = OperationType.DELETE,
            daoClass = IntegralDetailMapper.class,
            parseClass = DefaultContentParse.class,
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void delete(IntegralDetail detail) {
            int result = integralDetailMapper.deleteByPrimaryKey(detail.getId());
            if (result < 1) {
                throw new YimaoException("删除积分详情失败！");
            }
    }
}
