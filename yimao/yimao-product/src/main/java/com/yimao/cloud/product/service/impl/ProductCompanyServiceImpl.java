package com.yimao.cloud.product.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductCompanyVO;
import com.yimao.cloud.product.mapper.ProductCompanyMapper;
import com.yimao.cloud.product.po.ProductCompany;
import com.yimao.cloud.product.service.ProductCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 产品公司服务实现类
 *
 * @author Liu Yi
 * @date 2018/11/26.
 */
@Service
@Slf4j
public class ProductCompanyServiceImpl implements ProductCompanyService {

    @Resource
    private UserCache userCache;
    @Resource
    private ProductCompanyMapper productCompanyMapper;

    /**
     * 创建产品公司信息
     *
     * @param productCompany 产品公司信息
     */
    @Override
    public void saveProductCompany(ProductCompany productCompany) {
        if (StringUtil.isEmpty(productCompany.getName())) {
            throw new BadRequestException("缺少产品公司名称信息!");
        }
        if (StringUtil.isEmpty(productCompany.getShortName())) {
            throw new BadRequestException("缺少产品公司简称信息!");
        }
        if (StringUtil.isEmpty(productCompany.getCode())) {
            throw new BadRequestException("缺少产品公司编码信息!");
        }
        String creator = userCache.getCurrentAdminRealName();
        productCompany.setCreateTime(new Date());
        productCompany.setCreator(creator);
        productCompany.setUpdater(creator);
        productCompany.setUpdateTime(productCompany.getCreateTime());
        productCompany.setDeleted(false);
        int i = productCompanyMapper.insert(productCompany);
        if (i < 1) {
            throw new YimaoException("操作失败。");
        }
    }

    /**
     * 根据ID查询产品公司信息
     *
     * @param id 产品公司ID
     */
    @Override
    public ProductCompany getProductCompanyById(Integer id) {
        return productCompanyMapper.selectByPrimaryKey(id);
    }

    /**
     * 更新产品公司信息
     *
     * @param productCompany 产品公司信息
     */
    @Override
    public void update(ProductCompany productCompany) {
        if (StringUtil.isEmpty(productCompany.getName())) {
            throw new BadRequestException("名称不能为空!");
        }
        if (StringUtil.isEmpty(productCompany.getShortName())) {
            throw new BadRequestException("简称不能为空!");
        }
        if (StringUtil.isEmpty(productCompany.getCode())) {
            throw new BadRequestException("编号信息不能为空!");
        }
        String updater = userCache.getCurrentAdminRealName();
        productCompany.setUpdateTime(new Date());
        productCompany.setUpdater(updater);
        productCompany.setDeleted(false);
        productCompanyMapper.updateByPrimaryKeySelective(productCompany);
    }

    /**
     * 删除产品公司信息
     *
     * @param id 管理员ID
     */
    @Override
    public void deleteProductCompanyById(Integer id) {
        ProductCompany productCompany = new ProductCompany();
        String updater = userCache.getCurrentAdminRealName();
        productCompany.setId(id);
        productCompany.setUpdateTime(new Date());
        productCompany.setUpdater(updater);
        productCompany.setDeleted(true);
        productCompanyMapper.updateByPrimaryKeySelective(productCompany);
    }

    /**
     * 查询产品公司信息列表
     *
     * @param pageNum   第几页
     * @param pageSize  每页大小
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param name      公司名称
     * @param code      公司编码
     */
    @Override
    public PageVO<ProductCompanyVO> listProductCompany(Integer pageNum, Integer pageSize, String code, String name, Date startTime, Date endTime) {
        Example example = new Example(ProductCompany.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotBlank(code)) {
            criteria.andLike("code", "%" + code + "%");
        }
        if (StringUtil.isNotBlank(name)) {
            criteria.andLike("name", "%" + name + "%");
        }
        if (startTime != null) {
            criteria.andGreaterThanOrEqualTo("createTime", startTime);
        }
        if (endTime != null) {
            criteria.andLessThanOrEqualTo("createTime", endTime);
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ProductCompany> page = (Page<ProductCompany>) productCompanyMapper.selectByExample(example);
        return new PageVO<>(pageNum, page, ProductCompany.class, ProductCompanyVO.class);

    }
}
