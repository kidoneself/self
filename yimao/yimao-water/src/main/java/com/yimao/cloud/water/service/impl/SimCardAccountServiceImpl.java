package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.water.SimCardAccountDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.mapper.SimCardAccountMapper;
import com.yimao.cloud.water.po.SimCardAccount;
import com.yimao.cloud.water.service.SimCardAccountService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 描述：SIM运营商分配的权限账号
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@Service
public class SimCardAccountServiceImpl implements SimCardAccountService {

    @Resource
    private UserCache userCache;
    @Resource
    private SimCardAccountMapper simCardAccountMapper;

    /**
     * 创建SIM运营商分配的权限账号
     *
     * @param simCardAccount SIM运营商分配的权限账号
     */
    @Override
    public void save(SimCardAccount simCardAccount) {
        this.check(simCardAccount);
        SimCardAccount record = simCardAccountMapper.selectByUsername(simCardAccount.getUsername());
        if (record != null) {
            throw new BadRequestException("该账号已被使用，请重新输入。");
        }
        simCardAccount.setCreator(userCache.getCurrentAdminRealName());
        simCardAccount.setCreateTime(new Date());
        simCardAccount.setId(null);
        simCardAccountMapper.insert(simCardAccount);
    }

    /**
     * 修改SIM运营商分配的权限账号
     *
     * @param simCardAccount SIM运营商分配的权限账号
     */
    @Override
    public void update(SimCardAccount simCardAccount) {
        this.check(simCardAccount);
        SimCardAccount record = simCardAccountMapper.selectByUsername(simCardAccount.getUsername());
        if (record != null && !Objects.equals(record.getId(), simCardAccount.getId())) {
            throw new BadRequestException("该账号已被使用，请重新输入。");
        }
        simCardAccount.setUpdater(userCache.getCurrentAdminRealName());
        simCardAccount.setUpdateTime(new Date());
        simCardAccountMapper.updateByPrimaryKeySelective(simCardAccount);
    }

    /**
     * 查询SIM运营商分配的权限账号（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    @Override
    public PageVO<SimCardAccountDTO> page(Integer pageNum, Integer pageSize) {
        Example example = new Example(SimCardAccount.class);
        example.orderBy("createTime").desc();
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<SimCardAccount> page = (Page<SimCardAccount>) simCardAccountMapper.selectByExample(example);
        // PO对象转成DTO对象
        return new PageVO<>(pageNum, page, SimCardAccount.class, SimCardAccountDTO.class);
    }

    /**
     * 获取所有SIM运营商
     */
    @Override
    public List<SimCardAccountDTO> list() {
        return simCardAccountMapper.listAll();
    }

    /**
     * 参数校验
     *
     * @param simCardAccount SIM运营商分配的权限账号
     */
    private void check(SimCardAccount simCardAccount) {
        if (StringUtil.isBlank(simCardAccount.getUsername())) {
            throw new BadRequestException("请输入账号。");
        }
        if (StringUtil.isBlank(simCardAccount.getPassword())) {
            throw new BadRequestException("请输入密码。");
        }
        if (StringUtil.isBlank(simCardAccount.getLicenseKey())) {
            throw new BadRequestException("请输入权限密钥。");
        }
        if (StringUtil.isBlank(simCardAccount.getCompanyName())) {
            throw new BadRequestException("请输入运营商名称。");
        }
    }

}
