package com.yimao.cloud.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.DistributorRoleLevel;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.user.DistributorRoleDTO;
import com.yimao.cloud.pojo.dto.user.RoleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.mapper.DistributorRoleMapper;
import com.yimao.cloud.user.po.DistributorRole;
import com.yimao.cloud.user.service.DistributorRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 经销商角色配置业务类
 *
 * @author Lizhqiang
 * @date 2018/12/17
 */
@Service
@Slf4j
public class DistributorRoleServiceImpl implements DistributorRoleService {

    @Resource
    private UserCache userCache;
    @Resource
    private DistributorRoleMapper distributorRoleMapper;

    /**
     * 新增经销商角色配置
     *
     * @param dto 经销商角色配置
     */
    @Override
    public void save(DistributorRoleDTO dto) {
        DistributorRole distributorRole = new DistributorRole(dto);
        if(!dto.getIsLimitedDays()){
            distributorRole.setUpgradeLimitDays(null);
        }
        //参数正确性校验
        this.checkParams(distributorRole);
        //校验类型名是否已经存在
        DistributorRole query = new DistributorRole();
        query.setName(distributorRole.getName());
        int count = distributorRoleMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("该经销商角色名已被使用，请重新输入。");
        }
        //校验经销商等级是否已经存在
        query.setName(null);
        query.setLevel(distributorRole.getLevel());
        count = distributorRoleMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("该经销商等级已被使用，请重新输入。");
        }
        if (!distributorRole.getHasSubAccount()) {
            distributorRole.setSubAccountAmount(0);
            distributorRole.setSubAccountAmountLimit(true);
        }
        if (!distributorRole.getWaterDeviceQuotaLimit()) {
            distributorRole.setWaterDeviceQuota(0);
        }
        distributorRole.setCreator(userCache.getCurrentAdminRealName());
        distributorRole.setCreateTime(new Date());
        //经销商配置默认禁用
        distributorRole.setForbidden(true);
        distributorRoleMapper.insert(distributorRole);
    }

    /**
     * 禁用/启用经销商角色配置
     *
     * @param id 经销商角色ID
     */
    @Override
    public void forbidden(Integer id) {
        DistributorRole update = new DistributorRole();
        update.setId(id);
        // 设置更新人
        update.setUpdater(userCache.getCurrentAdminRealName());
        // 设置更新时间
        update.setUpdateTime(new Date());
        DistributorRole record = distributorRoleMapper.selectByPrimaryKey(id);
        if (record.getForbidden()) {
            update.setForbidden(false);
        } else {
            update.setForbidden(true);
        }
        distributorRoleMapper.updateByPrimaryKeySelective(update);
    }

    /**
     * 修改经销商角色配置
     *
     * @param dto 经销商角色配置
     */
    @Override
    public void update(DistributorRoleDTO dto) {
        DistributorRole distributorRole = new DistributorRole(dto);
        if(!dto.getIsLimitedDays()){
            distributorRole.setUpgradeLimitDays(null);
        }
        //参数正确性校验
        this.checkParams(distributorRole);

        Example example = new Example(DistributorRole.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", distributorRole.getId());
        criteria.andEqualTo("name", distributorRole.getName());

        Example.Criteria orCriteria = example.createCriteria();
        orCriteria.andNotEqualTo("id", distributorRole.getId());
        orCriteria.andEqualTo("level", distributorRole.getLevel());
        example.or(orCriteria);

        int count = distributorRoleMapper.selectCountByExample(example);
        if (count > 0) {
            throw new BadRequestException("该经销商配置已存在，请重新输入。");
        }
        if (!distributorRole.getHasSubAccount()) {
            distributorRole.setSubAccountAmount(0);
            distributorRole.setSubAccountAmountLimit(true);
        }
        if (!distributorRole.getWaterDeviceQuotaLimit()) {
            distributorRole.setWaterDeviceQuota(0);
        }
        //更新经销商配置
        distributorRole.setUpdater(userCache.getCurrentAdminRealName());
        distributorRole.setUpdateTime(new Date());
        distributorRoleMapper.updateByPrimaryKeySelective(distributorRole);
    }

    /**
     * 分页查询经销商角色配置信息
     *
     * @param pageNum  页数
     * @param pageSize 每页大小
     */
    @Override
    public PageVO<DistributorRoleDTO> page(Integer pageNum, Integer pageSize) {
        Example example = new Example(DistributorRole.class);
        example.orderBy("level");
        PageHelper.startPage(pageNum, pageSize);
        Page<DistributorRole> page = (Page<DistributorRole>) distributorRoleMapper.selectByExample(example);
        PageVO<DistributorRoleDTO> pageVO = new PageVO<>(pageNum, page, DistributorRole.class, DistributorRoleDTO.class);
        if(CollectionUtil.isNotEmpty(pageVO.getResult())){
            for(DistributorRoleDTO roleDTO : pageVO.getResult()){
                if(null == roleDTO.getUpgradeLimitDays()){
                    roleDTO.setIsLimitedDays(false);
                } else {
                    roleDTO.setIsLimitedDays(true);
                }
            }
        }
        return pageVO;
    }

    /**
     * 查询经销商角色配置信息（所有）
     */
    @Override
    public List<DistributorRoleDTO> listAll() {
        return distributorRoleMapper.listAll();
    }

    /**
     * 查询经销商角色配置信息（根据level）
     *
     * @param level 经销商角色等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）
     */
    @Override
    public DistributorRoleDTO getByLevel(Integer level) {
        DistributorRole query = new DistributorRole();
        query.setLevel(level);
        query.setForbidden(false);
        DistributorRole record = distributorRoleMapper.selectOne(query);
        if (record != null) {
            DistributorRoleDTO dto = new DistributorRoleDTO();
            record.convert(dto);
            return dto;
        }
        return null;
    }

    /**
     * 参数正确性校验
     *
     * @param distributorRole 经销商角色配置
     */
    private void checkParams(DistributorRole distributorRole) {
        if (StringUtil.isBlank(distributorRole.getName())) {
            throw new BadRequestException("请设置经销商角色名称。");
        }
        if (distributorRole.getLevel() == null || DistributorRoleLevel.find(distributorRole.getLevel()) == null) {
            throw new BadRequestException("请设置经销商角色等级。");
        }
        if (distributorRole.getPrice() == null) {
            throw new BadRequestException("请设置经销商价格。");
        }
        if (distributorRole.getIncomeRuleId() == null) {
            throw new BadRequestException("请设置招商收益模板。");
        }
        if (distributorRole.getWaterDeviceQuotaLimit() == null) {
            throw new BadRequestException("请设置是否限制经销商净水设备配额。");
        }
        if (distributorRole.getWaterDeviceQuotaLimit() && distributorRole.getWaterDeviceQuota() == null) {
            throw new BadRequestException("请设置经销商净水设备配额。");
        }
        if (distributorRole.getWaterDeviceQuotaThreshold() == null) {
            throw new BadRequestException("请设置经销商水机配额不足提醒剩余值。");
        }
        if (distributorRole.getHasSubAccount() == null) {
            throw new BadRequestException("请设置经销商是否有子账户。");
        }
        if (distributorRole.getHasSubAccount() && distributorRole.getSubAccountAmountLimit() == null) {
            throw new BadRequestException("请设置是否限制经销商子账户的数量。");
        }
        if (distributorRole.getHasSubAccount() && distributorRole.getSubAccountAmountLimit() && distributorRole.getSubAccountAmount() == null) {
            throw new BadRequestException("请设置经销商子账户的限制数量。");
        }
        if (distributorRole.getUpgrade() == null) {
            throw new BadRequestException("请设置经销商是否可以补差价升级。");
        }
//        if (distributorRole.getUpgrade() && distributorRole.getUpgradeLimitDays() == null) {
//            throw new BadRequestException("请设置经销商补差价有效期限制。");
//        }
        if (distributorRole.getRenewLimit() == null) {
            throw new BadRequestException("请设置经销商是否限制续费。");
        }
        if (distributorRole.getRenewLimit() && distributorRole.getRenewLimitTimes() == null) {
            throw new BadRequestException("请设置经销商限制的续费次数。");
        }
    }
}
