package com.yimao.cloud.product.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.DistributorRoleLevel;
import com.yimao.cloud.base.enums.ProductCategoryLevel;
import com.yimao.cloud.base.enums.ProductCategoryType;
import com.yimao.cloud.base.enums.ProductSupplyCode;
import com.yimao.cloud.base.enums.Terminal;
import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.product.ProductCategoryCascadeDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.query.product.ProductCategoryQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.product.feign.SystemFeign;
import com.yimao.cloud.product.feign.UserFeign;
import com.yimao.cloud.product.mapper.ProductCategoryMapper;
import com.yimao.cloud.product.mapper.ProductMapper;
import com.yimao.cloud.product.po.Product;
import com.yimao.cloud.product.po.ProductCategory;
import com.yimao.cloud.product.service.ProductCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 产品类目
 *
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@Service
@Slf4j
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Resource
    private UserCache userCache;
    @Resource
    private ProductCategoryMapper productCategoryMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserFeign userFeign;

    /**
     * 保存产品类目
     *
     * @param productCategory 产品类目
     */
    @Override
    public void saveProductCategory(ProductCategory productCategory) {
        //校验
        this.checkProductCategory(productCategory);
        String creator = userCache.getCurrentAdminRealName();
        productCategory.setCreator(creator);
        productCategory.setCreateTime(new Date());
        productCategory.setUpdater(creator);
        productCategory.setUpdateTime(productCategory.getCreateTime());
        productCategory.setId(null);
        productCategory.setDeleted(false);
        productCategory.setCode(null);//TODO，产品类目CODE值
        productCategoryMapper.insert(productCategory);

        //新增产品类目oldid是自己
        ProductCategory cate = new ProductCategory();
        cate.setId(productCategory.getId());
        cate.setOldId(productCategory.getId().toString());
        productCategoryMapper.updateByPrimaryKeySelective(cate);
        //同步售后
        //找到该产品的三级类目
        ProductCategoryCascadeDTO productcascade = getProductCategoryCascadeById(productCategory.getId());
        try {
            BaideApiUtil.syncProductAddOrUpdate(BaideApiUtil.INSERT, productCategory.getId().toString(), productcascade.getOldProductCategoryTwoId().toString(), productcascade.getOldProductCategoryFirstId().toString(), productCategory.getName(), 0);
        } catch (Exception e) {
            log.error("==========产品类目同步百得失败(id=" + productCategory.getId() + ")=======" + e.getMessage());
        }
    }

    /**
     * 删除产品类目
     *
     * @param id 产品类目ID
     */
    @Override
    public void deleteProductCategoryById(Integer id) {
        ProductCategory query = new ProductCategory();
        query.setPid(id);
        query.setDeleted(false);
        int count = productCategoryMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("该产品类目存在子类目，请先删除其子类目。");
        }
        ProductCategory category = productCategoryMapper.selectByPrimaryKey(id);
        if (category.getType() == ProductCategoryType.BACKEND.value && category.getLevel() == ProductCategoryLevel.THIRD.value) {
            //校验后台产品类目关联关系是否存在，存在则不能删除
            boolean b = productCategoryMapper.checkProductUsing(id);
            if (b) {
                throw new BadRequestException("有商品在使用该类目，不能删除。");
            }
        }
        if (category.getType() == ProductCategoryType.FRONT.value) {
            //校验前台产品类目关联关系是否存在，存在则不能删除
            boolean b = productCategoryMapper.checkProductFrontUsing(id);
            if (b) {
                throw new BadRequestException("有商品在使用该类目，不能删除。");
            }
        }

        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(id);
        productCategory.setDeleted(true);
        productCategoryMapper.updateByPrimaryKeySelective(productCategory);

        //同步售后
        try {
            BaideApiUtil.syncProductDelete(BaideApiUtil.DELETE, id.toString());
        } catch (Exception e) {
            log.error("==========产品类目同步百得失败(id=" + productCategory.getId() + ")=======" + e.getMessage());
        }
    }

    /**
     * 修改产品类目
     *
     * @param productCategory 产品类目
     */
    @Override
    public void updateProductCategory(ProductCategory productCategory) {
        this.checkProductCategory(productCategory);
        String updater = userCache.getCurrentAdminRealName();
        productCategory.setUpdater(updater);
        productCategory.setUpdateTime(new Date());

        productCategoryMapper.updateByPrimaryKeySelective(productCategory);

        //同步售后
        try {
            //找到该产品的三级类目
            ProductCategoryCascadeDTO productcascade = getProductCategoryCascadeById(productCategory.getId());
            BaideApiUtil.syncProductAddOrUpdate(BaideApiUtil.UPDATE, productCategory.getOldId(), productcascade.getOldProductCategoryTwoId().toString(), productcascade.getOldProductCategoryFirstId().toString(), productCategory.getName(), 0);
        } catch (Exception e) {
            log.error("==========产品类目同步百得失败(id=" + productCategory.getId() + ")=======" + e.getMessage());
        }
    }

    /**
     * 查询产品类目
     *
     * @param id 产品类目ID
     */
    @Override
    public ProductCategory getProductCategoryById(Integer id) {
        return productCategoryMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询产品类目（分页）
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @Override
    public PageVO<ProductCategoryDTO> page(Integer pageNum, Integer pageSize, ProductCategoryQuery query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ProductCategoryDTO> page = productCategoryMapper.listProductCategory(query);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 获取级联的产品类目名称，分隔符为“ > ”
     *
     * @param categoryId 产品类目ID
     */
    @Override
    public String buildCascadeCategoryName(Integer categoryId) {
        Map<Integer, String> map = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        //当前类目
        ProductCategory category = productCategoryMapper.selectByPrimaryKey(categoryId);
        //所有类目
        List<ProductCategory> categories = productCategoryMapper.selectAll();
        if (category != null && CollectionUtil.isNotEmpty(categories)) {
            int bLevel = category.getLevel();
            int level = bLevel;
            int pid = category.getPid();
            String name = category.getName();
            if (level == 1) {
                //如果是一级类目，直接返回类目名称即可
                return name;
            } else {
                //如果不是一级类目，循环获取上级类目，一直获取到一级类目，level为key，name为value把每一级类目都先存放到MAP中。
                map.put(level, name);
                while (level > 1 && pid != 0) {
                    for (ProductCategory pc : categories) {
                        if (Objects.equals(pc.getId(), pid)) {
                            level = pc.getLevel();
                            pid = pc.getPid();
                            name = pc.getName();
                            map.put(level, name);
                        }
                    }
                }
            }
            //把MAP中的所有类目按级别高低依次取出，按规则组合好名称
            for (int i = 1; i <= bLevel; i++) {
                if (i != bLevel) {
                    sb.append(map.get(i)).append(" > ");
                } else {
                    sb.append(map.get(i));
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * APP查询产品供应栏目下的产品前端类目
     *
     * @param supplyCode 产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     */
    @Override
    public List<ProductCategoryDTO> listCategoryBySupplyCode(String supplyCode) {
        return productCategoryMapper.listCategoryBySupplyCode(supplyCode);
    }

    /**
     * 更新产品分类排序
     *
     * @param id    产品类目ID
     * @param sorts 排序值
     */
    @Override
    public void updateCategorySorts(Integer id, Integer sorts) {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(id);
        productCategory.setSorts(sorts);
        productCategory.setUpdater(userCache.getCurrentAdminRealName());
        productCategory.setUpdateTime(new Date());
        productCategoryMapper.updateByPrimaryKeySelective(productCategory);
    }

    /**
     * 获取自身以及上级类目的ID集合
     *
     * @param id 产品类目ID
     */
    @Override
    public Set<Integer> getSelfAndParentId(Integer id) {
        Set<Integer> ids = new HashSet<>();
        //当前类目
        ProductCategory category = productCategoryMapper.selectByPrimaryKey(id);
        if (category.getLevel() == ProductCategoryLevel.FIRST.value) {
            ids.add(id);
        } else if (category.getLevel() == ProductCategoryLevel.SECOND.value) {
            ProductCategory first = productCategoryMapper.selectByPrimaryKey(category.getPid());
            ids.add(first.getId());//一级类目ID
            ids.add(id);//二级类目ID
        } else if (category.getLevel() == ProductCategoryLevel.THIRD.value) {
            ProductCategory second = productCategoryMapper.selectByPrimaryKey(category.getPid());
            ProductCategory first = productCategoryMapper.selectByPrimaryKey(second.getPid());
            ids.add(first.getId());//一级类目ID
            ids.add(second.getId());//二级类目ID
            ids.add(id);//三级类目ID
        }
        return ids;
    }

    @Override
    public List<ProductCategoryDTO> getBottomCatgory(Integer id) {
        return productCategoryMapper.getBottomCatgory(id);
    }

    @Override
    public ProductCategoryDTO getOneCategory(Integer categoryId) {
        return productCategoryMapper.getOneCategory(categoryId);
    }

    @Override
    public ProductCategoryDTO getTwoCategory(Integer categoryId) {
        return productCategoryMapper.getTwoCategory(categoryId);
    }


    /**
     * 校验产品类目必要信息
     *
     * @param productCategory 产品类目
     */
    private void checkProductCategory(ProductCategory productCategory) {
        //1-校验产品类目名称
        if (StringUtil.isEmpty(productCategory.getName())) {
            throw new BadRequestException("产品类目名称不能为空。");
        }
        Example example = new Example(ProductCategory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", productCategory.getName());
        criteria.andEqualTo("type", productCategory.getType());
        criteria.andEqualTo("terminal", productCategory.getTerminal());
        criteria.andEqualTo("deleted", false);
        if (productCategory.getId() != null) {
            criteria.andNotEqualTo("id", productCategory.getId());
        }

//        int count = productCategoryMapper.selectCountByExample(example);
//        if (count > 0) {
//            throw new BadRequestException("产品类目名称已被使用，请重新输入。");
//        }

        //2-校验前台台类目正确性
        if (productCategory.getType() == null || ProductCategoryType.find(productCategory.getType()) == null) {
            throw new BadRequestException("前台台类目选择错误。");
        }
        //3-校验类目等级正确性
        if (productCategory.getLevel() == null || ProductCategoryLevel.find(productCategory.getLevel()) == null) {
            throw new BadRequestException("产品类目等级选择错误。");
        }
        //4-后台一级类目产品公司ID为必传
        if (productCategory.getType() == ProductCategoryType.BACKEND.value) {
            if (productCategory.getLevel() == ProductCategoryLevel.FIRST.value && productCategory.getCompanyId() == null) {
                throw new BadRequestException("请选择产品公司。");
            }
        } else {
            //5-校验前台类目适用终端正确性
            if (productCategory.getTerminal() == null || Terminal.find(productCategory.getTerminal()) == null) {
                throw new BadRequestException("前台类目适用终端选择错误。");
            }
        }
        //校验父级类目
        if (productCategory.getLevel() > ProductCategoryLevel.FIRST.value) {
            ProductCategory p = productCategoryMapper.selectByPrimaryKey(productCategory.getPid());
            if (p == null || p.getLevel() + 1 != productCategory.getLevel()) {
                throw new BadRequestException("父级类目错误。");
            }
            if (!Objects.equals(p.getType(), productCategory.getType())) {
                throw new BadRequestException("后台类目与前台类目不能混淆关联。");
            }
            //如果是后台分类的三级类目，需判断其一级类目是否为净水服务，净水服务一级类目需绑定库存物资id
            if (productCategory.getType() == ProductCategoryType.BACKEND.value && productCategory.getLevel() == ProductCategoryLevel.THIRD.value) {
                //查询一级分类
                ProductCategory pp = productCategoryMapper.selectByPrimaryKey(p.getPid());
                if (pp.getCompanyId() == 10000) { //产品公司为“翼猫科技(上海)发展有限公司”
                    if (productCategory.getStoreGoodsId() == null) {
                        throw new BadRequestException("一级类目为净水服务需绑定库存物资。");
                    }
                }
            }
            //创建后台一级一下类目时，设置产品公司
            if (productCategory.getId() == null && productCategory.getType() == ProductCategoryType.BACKEND.value) {
                productCategory.setCompanyId(p.getCompanyId());
                productCategory.setCompanyName(p.getCompanyName());
            }
        } else {
            productCategory.setPid(0);
        }
    }

    @Override
    public List<ProductCategoryDTO> getFirstProductCategory() {
        List<ProductCategoryDTO> list = new ArrayList<>();
        Example example = new Example(ProductCategory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleted", 0);
        criteria.andEqualTo("level", ProductCategoryLevel.FIRST.value);
        criteria.andEqualTo("type", ProductCategoryType.BACKEND.value);
        example.orderBy("sorts").desc();
        List<ProductCategory> productTypeList = productCategoryMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(productTypeList)) {
            productTypeList.forEach(o -> {
                ProductCategoryDTO dto = new ProductCategoryDTO();
                o.convert(dto);
                list.add(dto);
            });
            return list;
        }
        return null;
    }

    /**
     * 根据产品三级类目ID查询类目级联信息
     */
    @Override
    public ProductCategoryCascadeDTO getProductCategoryCascadeById(Integer id) {
        ProductCategoryCascadeDTO dto = null;
        ProductCategory category = productCategoryMapper.selectByPrimaryKey(id);
        if (category != null) {
            dto = new ProductCategoryCascadeDTO();
            dto.setProductCategoryId(category.getId());
            dto.setProductCategoryName(category.getName());
            dto.setOldProductCategoryId(category.getOldId());
            ProductCategory categoryTwo = productCategoryMapper.selectByPrimaryKey(category.getPid());
            if (categoryTwo != null) {
                dto.setProductCategoryTwoId(categoryTwo.getId());
                dto.setProductCategoryTwoName(categoryTwo.getName());
                dto.setOldProductCategoryTwoId(categoryTwo.getOldId());
                ProductCategory categoryFirst = productCategoryMapper.selectByPrimaryKey(categoryTwo.getPid());
                if (categoryFirst != null) {
                    dto.setProductCategoryFirstId(categoryFirst.getId());
                    dto.setProductCategoryFirstName(categoryFirst.getName());
                    dto.setOldProductCategoryFirstId(categoryFirst.getOldId());
                }
            }
        }
        return dto;
    }

    /**
     * 获取产品前台一级类目
     *
     * @author hhf
     * @date 2019/7/11
     */
    @Override
    public ProductCategoryDTO getFrontCategoryByProductId(Integer productId, Integer terminal) {
        return productCategoryMapper.getFrontCategoryByProductId(productId, terminal);
    }

    @Override
    public List<ProductCategoryDTO> getAllFirstCategory() {
        //获取所有一级分类
        // ArrayList<ProductCategoryDTO> ids = new ArrayList<>();
        List<ProductCategoryDTO> categoryDTOList = productCategoryMapper.getFirstCategoryListByParam(ProductCategoryType.FRONT.value, Terminal.YIMAO_APP.value, null);
        //先删除没有产品的分类
        Iterator<ProductCategoryDTO> it = categoryDTOList.iterator();
        while (it.hasNext()) {
            ProductCategoryDTO next = it.next();
            Integer frontId = next.getId();
            List<ProductDTO> productByFrontId = productMapper.getProductByFrontId(frontId);
            if (CollectionUtil.isEmpty(productByFrontId)) {
                it.remove();
            }
        }
        //获取用户身份，获取到用户有权展示的产品栏目
        Integer userId = userCache.getUserId();
        UserDTO currentUser = userFeign.getBasicUserById(userId);
        //获取用户mid，无mid为普通用户/有mid为经销商（折机）代理商创始人
        Integer mid = currentUser.getMid();
        Iterator<ProductCategoryDTO> iterator = categoryDTOList.iterator();
        if (!UserType.isDistributor(currentUser.getUserType()) && mid == null) {
            //用户身份只展示经销产品分类
            while (iterator.hasNext()) {
                ProductCategoryDTO next = iterator.next();
                Integer frontId = next.getId();
                List<ProductDTO> products = productMapper.getProductByFrontId(frontId);
                //把该前端分类下非经销产品的产品移除
                products.removeIf(p -> !Objects.equals(p.getSupplyCode(), ProductSupplyCode.PJXCP.code));
                //如果该分类下没有产品了，则将该分类移除
                if (products.size() <= 0) {
                    iterator.remove();
                }
            }
        } else {
            //获取经销商信息
            DistributorDTO distributor = userFeign.getBasicDistributorById(mid);
            if (Objects.equals(distributor.getRoleLevel(), DistributorRoleLevel.DISCOUNT.value)) {
                //折机版经销商只能查看折机水机
                while (iterator.hasNext()) {
                    ProductCategoryDTO next = iterator.next();
                    Integer frontId = next.getId();
                    List<ProductDTO> products = productMapper.getProductByFrontId(frontId);
                    //把该前端分类下非经销产品的产品移除
                    products.removeIf(p -> !Objects.equals(p.getSupplyCode(), ProductSupplyCode.PTPSJ.code));
                    //如果该分类下没有产品了，则将该分类移除
                    if (products.size() <= 0) {
                        iterator.remove();
                    }
                }
            } else {
                //非折机版经销商
                //1-先把特批水机的产品都移除
                while (iterator.hasNext()) {
                    ProductCategoryDTO next = iterator.next();
                    Integer frontId = next.getId();
                    List<ProductDTO> products = productMapper.getProductByFrontId(frontId);
                    //把该前端分类下非经销产品的产品移除
                    products.removeIf(p -> Objects.equals(p.getSupplyCode(), ProductSupplyCode.PTPSJ.code));
                    //如果该分类下没有产品了，则将该分类移除
                    if (products.size() <= 0) {
                        iterator.remove();
                    }
                }
                //2-如果不是服务站站长则不能查看站长专供产品
                //已承包的服务站站长才能购买站长专供产品
                //未承包的服务站站长不能购买站长专供产品
                //该站长所在的服务站是否已经承包
                Boolean contract = systemFeign.getStationStatusByDistributorId(distributor.getId());
                if (distributor.getStationMaster() == null || !distributor.getStationMaster() || contract == null || !contract) {
                    iterator = categoryDTOList.iterator();
                    while (iterator.hasNext()) {
                        ProductCategoryDTO next = iterator.next();
                        Integer frontId = next.getId();
                        List<ProductDTO> products = productMapper.getProductByFrontId(frontId);
                        //把该前端分类下非经销产品的产品移除
                        products.removeIf(p -> Objects.equals(p.getSupplyCode(), ProductSupplyCode.PZZZG.code));
                        //如果该分类下没有产品了，则将该分类移除
                        if (products.size() <= 0) {
                            iterator.remove();
                        }
                    }
                }
                //3-不是代理商不能购买特供产品
                if (distributor.getAgentLevel() == null) {
                    iterator = categoryDTOList.iterator();
                    while (iterator.hasNext()) {
                        ProductCategoryDTO next = iterator.next();
                        Integer frontId = next.getId();
                        List<ProductDTO> products = productMapper.getProductByFrontId(frontId);
                        //把该前端分类下非经销产品的产品移除
                        products.removeIf(p -> Objects.equals(p.getSupplyCode(), ProductSupplyCode.PTGCP.code));
                        //如果该分类下没有产品了，则将该分类移除
                        if (products.size() <= 0) {
                            iterator.remove();
                        }
                    }
                }
            }

            // if (distributor.getStationMaster()) {//站长
            //     Boolean isOnline = systemFeign.getStationStatusByDistributorId(distributor.getId());
            //     if (isOnline != null && isOnline) {
            //         while (iterator.hasNext()) {
            //             ProductCategoryDTO next = iterator.next();
            //             Integer frontId = next.getId();
            //             List<ProductDTO> productByFrontId = productMapper.getProductByFrontId(frontId);
            //             Set<String> supplyCodes = productByFrontId.stream().map(ProductDTO::getSupplyCode).collect(Collectors.toSet());
            //             if (!supplyCodes.contains(ProductConstant.PTGCP) && !supplyCodes.contains(ProductConstant.PTPSJ)) {
            //                 ids.add(next);
            //             }
            //         }
            //     } else {
            //         //没上线的站长也只展示经销产品
            //         while (iterator.hasNext()) {
            //             ProductCategoryDTO next = iterator.next();
            //             Integer frontId = next.getId();
            //             List<ProductDTO> productByFrontId = productMapper.getProductByFrontId(frontId);
            //             Set<String> supplyCodes = productByFrontId.stream().map(ProductDTO::getSupplyCode).collect(Collectors.toSet());
            //             if (!supplyCodes.contains(ProductConstant.PTGCP) && !supplyCodes.contains(ProductConstant.PTPSJ) || supplyCodes.contains(ProductConstant.PZZZG)) {
            //                 ids.add(next);
            //             }
            //         }
            //     }
            // } else if (distributor.getRoleLevel().equals(DistributorRoleLevel.DISCOUNT.value)) {//折机
            //     while (iterator.hasNext()) {
            //         ProductCategoryDTO next = iterator.next();
            //         Integer frontId = next.getId();
            //         List<ProductDTO> productByFrontId = productMapper.getProductByFrontId(frontId);
            //         List<String> supplyCodes = productByFrontId.stream().map(ProductDTO::getSupplyCode).collect(Collectors.toList());
            //         if (!supplyCodes.contains(ProductConstant.PTGCP) && !supplyCodes.contains(ProductConstant.PZZZG) && !supplyCodes.contains(ProductConstant.PJXCP)) {
            //             ids.add(next);
            //         }
            //     }
            // } else if (distributor.getRoleLevel() != null && !distributor.getStationMaster()) {//普通经销商
            //     while (iterator.hasNext()) {
            //         ProductCategoryDTO next = iterator.next();
            //         Integer frontId = next.getId();
            //         List<ProductDTO> productByFrontId = productMapper.getProductByFrontId(frontId);
            //         Set<String> supplyCodes = productByFrontId.stream().map(ProductDTO::getSupplyCode).collect(Collectors.toSet());
            //         if (supplyCodes.contains(ProductConstant.PJXCP)) {
            //             ids.add(next);
            //         }
            //     }
            // } else if (distributor.getAgentLevel() != null) {//代理商
            //     while (iterator.hasNext()) {
            //         ProductCategoryDTO next = iterator.next();
            //         Integer frontId = next.getId();
            //         List<ProductDTO> productByFrontId = productMapper.getProductByFrontId(frontId);
            //         Set<String> supplyCodes = productByFrontId.stream().map(ProductDTO::getSupplyCode).collect(Collectors.toSet());
            //         if (supplyCodes.contains(ProductConstant.PJXCP)) {
            //             ids.add(next);
            //         }
            //     }
            // }
        }
        return categoryDTOList;
    }

    /**
     * 获取产品前台一级类目
     *
     * @author Liu Yi
     * @date 2019/11/7
     */
    @Override
    public List<ProductCategoryDTO> getFirstCategoryForAppProductIncome() {
        //获取经销商APP 经销产品一级分类
        List<ProductCategoryDTO> categoryList = productCategoryMapper.getFirstCategoryListByParam(ProductCategoryType.FRONT.value, Terminal.YIMAO_APP.value, ProductSupplyCode.PJXCP.code);
        return categoryList;
    }

    @Override
    public Integer getGoodsIdByProductId(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            throw new BadRequestException("产品不存在！");
        }
        ProductCategory productCategory = productCategoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (productCategory == null) {
            throw new BadRequestException("产品三级分类不存在！");
        }
        return productCategory.getStoreGoodsId();
    }
}
