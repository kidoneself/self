package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.query.water.SimCardNumberQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.SimCardNumberVO;
import com.yimao.cloud.water.mapper.SimCardAccountMapper;
import com.yimao.cloud.water.mapper.SimCardNumberMapper;
import com.yimao.cloud.water.po.SimCardNumber;
import com.yimao.cloud.water.service.SimCardNumberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * 描述：SIM号码段
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@Service
public class SimCardNumberServiceImpl implements SimCardNumberService {

    @Resource
    private UserCache userCache;
    @Resource
    private SimCardNumberMapper simCardNumberMapper;
    @Resource
    private SimCardAccountMapper simCardAccountMapper;

    /**
     * 创建SIM号码段
     *
     * @param simCardNumber SIM号码段
     */
    @Override
    public void save(SimCardNumber simCardNumber) {
        this.check(simCardNumber);
        SimCardNumber record = simCardNumberMapper.selectByCardNumber(simCardNumber.getCardNumber(), simCardNumber.getMaxNumber(), simCardNumber.getMinNumber());
        if (record != null) {
            throw new BadRequestException("相同号码段的号码区间已存在。");
        }
        simCardNumber.setCreator(userCache.getCurrentAdminRealName());
        simCardNumber.setCreateTime(new Date());
        simCardNumber.setId(null);
        simCardNumberMapper.insert(simCardNumber);
    }

    /**
     * 修改SIM号码段
     *
     * @param simCardNumber SIM号码段
     */
    @Override
    public void update(SimCardNumber simCardNumber) {
        this.check(simCardNumber);
        SimCardNumber record = simCardNumberMapper.selectByCardNumber(simCardNumber.getCardNumber(), simCardNumber.getMaxNumber(), simCardNumber.getMinNumber());
        if (record != null && !Objects.equals(record.getId(), simCardNumber.getId())) {
            throw new BadRequestException("相同号码段的号码区间已存在。");
        }
        simCardNumber.setUpdater(userCache.getCurrentAdminRealName());
        simCardNumber.setUpdateTime(new Date());
        simCardNumberMapper.updateByPrimaryKeySelective(simCardNumber);
    }

    /**
     * 查询SIM号码段（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @Override
    public PageVO<SimCardNumberVO> page(Integer pageNum, Integer pageSize, SimCardNumberQuery query) {
        String iccid = query.getIccid();
        if (StringUtil.isNotBlank(iccid)) {
            query.setPrefixNum(iccid.substring(0, 10));
            query.setSuffixNum(iccid.substring(10, 19));
        }
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<SimCardNumberVO> page = simCardNumberMapper.selectPage(query);
        // PO对象转成DTO对象
        return new PageVO<>(pageNum, page);
    }

    /**
     * 参数校验
     *
     * @param simCardNumber SIM号码段
     */
    private void check(SimCardNumber simCardNumber) {
        if (simCardNumber.getSimCardAccountId() == null) {
            throw new BadRequestException("请选择运营商。");
        }
        boolean exists = simCardAccountMapper.existsWithPrimaryKey(simCardNumber.getSimCardAccountId());
        if (!exists) {
            throw new BadRequestException("运营商选择错误。");
        }
        if (StringUtil.isBlank(simCardNumber.getCardNumber())) {
            throw new BadRequestException("请输入前10位号码段。");
        }
        if (StringUtil.isBlank(simCardNumber.getMinNumber())) {
            throw new BadRequestException("请输入最小值11-19位。");
        }
        if (StringUtil.isBlank(simCardNumber.getMaxNumber())) {
            throw new BadRequestException("请输入最大值11-19位。");
        }
        if (Long.parseLong(simCardNumber.getMaxNumber()) <= Long.parseLong(simCardNumber.getMinNumber())) {
            throw new BadRequestException("最大号码不能小于最小号码。");
        }
    }

}
