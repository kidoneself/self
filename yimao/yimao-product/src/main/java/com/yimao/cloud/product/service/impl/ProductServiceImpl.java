package com.yimao.cloud.product.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.CopyUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.ThreadUtil;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.product.VirtualProductConfigDTO;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.query.product.ProductQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductStatusStatisticVO;
import com.yimao.cloud.product.constant.ProductConstant;
import com.yimao.cloud.product.feign.OrderFeign;
import com.yimao.cloud.product.feign.SystemFeign;
import com.yimao.cloud.product.feign.UserFeign;
import com.yimao.cloud.product.mapper.*;
import com.yimao.cloud.product.po.*;
import com.yimao.cloud.product.service.ProductActivityService;
import com.yimao.cloud.product.service.ProductCategoryService;
import com.yimao.cloud.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

/**
 * 产品业务类
 *
 * @author Zhang Bo
 * @date 2018/11/8.
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserCache userCache;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductCompanyMapper productCompanyMapper;
    @Resource
    private ProductDetailMapper productDetailMapper;
    @Resource
    private ProductCategoryMapper productCategoryMapper;
    @Resource
    private ProductCostMapper productCostMapper;
    @Resource
    private ProductProductCostMapper productProductCostMapper;
    @Resource
    private ProductCategoryService productCategoryService;
    @Resource
    private ProductProductFrontCategoryMapper productProductFrontCategoryMapper;
    @Resource
    private VirtualProductConfigMapper virtualProductConfigMapper;
    @Resource
    private ProductDistributorMapper productDistributorMapper;
    @Resource
    private ProductActivityService productActivityService;

    @Resource
    private UserFeign userFeign;

    /**
     * 创建产品
     *
     * @param product          产品
     * @param frontCategoryIds 前台类目
     * @param incomeRuleIds    收益分配规则
     * @param costIds          水机计费方式
     * @param distributorIds   折机经销商
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void save(Product product, Set<Integer> frontCategoryIds, Set<Integer> incomeRuleIds, Set<Integer> costIds, Set<Integer> distributorIds, VirtualProductConfigDTO virtualProductConfigDTO) {
        //校验产品
        this.checkProduct(product, frontCategoryIds, incomeRuleIds, costIds, distributorIds);
        //设置产品状态
        if (product.getOnShelfType() == ProductOnShelfType.ONSHELFNOW.value) {
            product.setStatus(ProductStatus.ONSHELF.value);
            //product.setOnShelfTime(new Date());
        } else {
            product.setStatus(ProductStatus.NOTONSHELF.value);
        }
        String creator = userCache.getCurrentAdminRealName();
        product.setLogisticsFee(new BigDecimal("0"));
        product.setCreator(creator);
        product.setCreateTime(new Date());
        product.setUpdater(creator);
        product.setUpdateTime(product.getCreateTime());
        product.setId(null);

        //新建产品保存OldCategoryId
        ProductCategory categery = productCategoryService.getProductCategoryById(product.getCategoryId());
        if (categery != null && StringUtil.isNotEmpty(categery.getOldId())) {
            product.setOldCategoryId(categery.getOldId());
        }
        //保存产品
        int res = productMapper.insert(product);
        if (res > 0) {
            //回写oldId=主键id
            Product updateProduct = new Product();
            updateProduct.setId(product.getId());
            updateProduct.setOldId(product.getId().toString());
            productMapper.updateByPrimaryKeySelective(updateProduct);
        }
        //虚拟产品保存虚拟产品配置信息
        if (product.getMode() == ProductModeEnum.VIRTUAL.value && virtualProductConfigDTO != null) {
            this.saveVirtualProductConfig(product.getId(), virtualProductConfigDTO);
        }

        //保存detail信息
        if (StringUtil.isNotEmpty(product.getTextDetail())) {
            this.saveProductDetail(product.getId(), product.getTextDetail());
        }

        //保存产品-产品前台类目关联关系
        this.saveProductFrontCategory(product.getId(), frontCategoryIds);
        if (product.getMode() == ProductModeEnum.LEASE.value) {
            //保存产品-产品计费方式关联关系
            this.saveProductProductCost(product.getId(), costIds);
        }

        // 租赁产品校验特有属性 活动类型：1-普通产品 2-折机产品 3-180产品；
        if (product.getMode() == ProductModeEnum.LEASE.value && product.getActivityType() != null && ProductActivityType.PRODUCT_CONVERT.value == product.getActivityType()) {
            //折机经销商校验
            if (distributorIds == null || distributorIds.size() == 0) {
                throw new BadRequestException("请选择折机经销商。");
            }
            //保存产品对应的折机经销商
            saveProductDistributor(product.getId(), distributorIds);
        }

        //保存产品-收益分配关联关系
        orderFeign.saveProductIncomeRule(product.getId(), incomeRuleIds);

        //TODO 保存产品-产品属性关联关系
        //TODO 保存产品-购买权限关联关系
    }

    /**
     * 修改产品
     *
     * @param product          产品
     * @param frontCategoryIds 前台类目
     * @param incomeRuleIds    收益分配规则
     * @param costIds          水机计费方式
     * @param distributorIds   折机经销商
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void update(Product product, Set<Integer> frontCategoryIds, Set<Integer> incomeRuleIds, Set<Integer> costIds, Set<Integer> distributorIds, VirtualProductConfigDTO virtualProductConfigDTO) {
        //校验产品
        this.checkProduct(product, frontCategoryIds, incomeRuleIds, costIds, distributorIds);
        //设置产品状态
        if (product.getOnShelfType() == ProductOnShelfType.ONSHELFNOW.value) {
            product.setStatus(ProductStatus.ONSHELF.value);
        } else {
            product.setStatus(ProductStatus.NOTONSHELF.value);
        }
        product.setUpdateTime(new Date());
        //保存产品
        productMapper.updateByPrimaryKeySelective(product);

        //保存detail信息
        if (StringUtil.isNotEmpty(product.getTextDetail())) {
            this.saveProductDetail(product.getId(), product.getTextDetail());
        }

        //保存产品-产品前台类目关联关系
        this.saveProductFrontCategory(product.getId(), frontCategoryIds);
        if (product.getMode() == ProductModeEnum.LEASE.value) {
            //保存产品-产品计费方式关联关系
            this.saveProductProductCost(product.getId(), costIds);
        }
        //保存产品-产品计费方式关联关系
        orderFeign.saveProductIncomeRule(product.getId(), incomeRuleIds);

        //判断是否是虚拟卡
        if (Objects.equals(product.getMode(), ProductModeEnum.VIRTUAL.value) && virtualProductConfigDTO != null) {
            VirtualProductConfig virtualProductConfig = new VirtualProductConfig(virtualProductConfigDTO);
            //更新虚拟卡的有效期
            virtualProductConfig.setProductId(product.getId());
            Integer update = virtualProductConfigMapper.update(virtualProductConfig);
            virtualProductConfigMapper.updateByPrimaryKey(virtualProductConfig);
            // virtualProductConfigMapper.updateByPrimaryKeySelective(virtualProductConfig);

            if (update < 1) {
                throw new YimaoException("更新有效期失败");
            }
        }

        // 租赁产品校验特有属性 活动类型：1-普通产品 2-折机产品 3-180产品；
        if (ProductModeEnum.LEASE.value == product.getMode() && product.getActivityType() != null && ProductActivityType.PRODUCT_CONVERT.value == product.getActivityType()) {
            //折机经销商校验
            if (distributorIds == null || distributorIds.size() == 0) {
                throw new BadRequestException("请选择折机经销商。");
            }
            //保存产品对应的折机经销商
            saveProductDistributor(product.getId(), distributorIds);
        }

        //TODO 保存产品-产品属性关联关系
    }

    /**
     * 修改产品状态、排序、库存等信息
     *
     * @param record 产品
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void update(Product record) {
        Product product = productMapper.selectByPrimaryKey(record.getId());
        if (product == null) {
            throw new BadRequestException("该产品不存在，不能修改。");
        }
        if (product.getStatus() == ProductStatus.DELETED.value) {
            throw new BadRequestException("该产品已被删除，不能修改。");
        }
        if (product.getStock() != null && record.getStock() != null) {
            Integer oldStock = product.getStock();
            //库存大于购买数量时，可以减库存
            if (oldStock > record.getStock()) {
                record.setStock(oldStock - record.getStock());
            }
        }

        //2020-03-16
        //当对活动商品进行下架或删除操作时，同时将该商品对应的活动设置为有【已终止】状态
        if (record.getStatus() != null) {
            if ((record.getStatus() == ProductStatus.OFFSHELF.value || record.getStatus() == ProductStatus.DELETED.value) && product.getActivityType() != null
                    && product.getActivityType() == ProductActivityType.PANIC_BUYING.value) {
                productActivityService.stopProductActivity(record.getId());
                record.setActivityType(ProductActivityType.PRODUCT_COMMON.value);
            }
        }

        String updater = userCache.getCurrentAdminRealName();
        record.setUpdater(updater);
        record.setUpdateTime(new Date());
        productMapper.updateByPrimaryKeySelective(record);

        //2020-03-17
        //下架商品时需要将所有【待付款】的订单设置为【已取消】
        orderFeign.cancelOrderWhenOffshelf(record.getId());
    }

    /**
     * 校验产品属性
     *
     * @param product          产品
     * @param frontCategoryIds 前台类目
     * @param incomeRuleIds    收益分配规则
     * @param costIds          水机计费方式
     */
    private void checkProduct(Product product, Set<Integer> frontCategoryIds, Set<Integer> incomeRuleIds, Set<Integer> costIds, Set<Integer> distributorIds) {
        //产品名称
        if (StringUtil.isBlank(product.getName())) {
            throw new BadRequestException("产品名称不能为空。");
        }
        //产品模式：1-租赁；2-实物；3-虚拟；
        if (product.getMode() == null || ProductModeEnum.find(product.getMode()) == null) {
            throw new BadRequestException("产品模式不存在。");
        }
        //产品类目（后天）校验
        ProductCategory category = productCategoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            throw new BadRequestException("产品后台类目不存在。");
        }
        //设置产品后台三级类目名称
        product.setCategoryName(category.getName());
        //产品公司校验
        ProductCompany company = productCompanyMapper.selectByPrimaryKey(product.getCompanyId());
        if (company == null) {
            throw new BadRequestException("产品公司不存在。");
        }
        //设置产品公司名称
        product.setCompanyName(company.getName());
        //产品销售栏目类型
        /*if (StringUtil.isBlank(product.getSupplyCode()) || ProductSupplyCode.find(product.getSupplyCode()) == null) {
            throw new BadRequestException("产品供应类型错误。");
        }*/
        //前台类目校验
        if (frontCategoryIds == null || frontCategoryIds.size() == 0) {
            throw new BadRequestException("请选择产品所属的前台类目。");
        }
        Example categoryExample = new Example(ProductCategory.class);
        Example.Criteria categoryCriteria = categoryExample.createCriteria();
        categoryCriteria.andIn("id", frontCategoryIds);
        int count = productCategoryMapper.selectCountByExample(categoryExample);
        if (count != frontCategoryIds.size()) {
            throw new BadRequestException("产品前台类目选择有误。");
        }
        //收益分配规则校验
        if (incomeRuleIds == null || incomeRuleIds.size() == 0) {
            throw new BadRequestException("请选择收益分配规则。");
        }
        for (Integer ruleId : incomeRuleIds) {
            IncomeRuleDTO incomeRule = orderFeign.getIncomeRuleById(ruleId);
            if (incomeRule == null) {
                throw new BadRequestException("收益分配规则选择有误。");
            }
        }
        //租赁产品校验特有属性
        if (product.getMode() == ProductModeEnum.LEASE.value) {
            //计费方式校验
            if (costIds == null || costIds.size() == 0) {
                throw new BadRequestException("请选择产品计费方式。");
            }
            Example costExample = new Example(ProductCost.class);
            Example.Criteria costCriteria = costExample.createCriteria();
            costCriteria.andIn("id", costIds);
            count = productCostMapper.selectCountByExample(costExample);
            if (count != costIds.size()) {
                throw new BadRequestException("产品计费方式选择有误。");
            }

            //租赁商品+折机经销商
            if (product.getActivityType() != null && ProductActivityType.PRODUCT_CONVERT.value == product.getActivityType()) { //活动类型：1-普通产品 2-折机产品 3-180产品；
                //折机经销商校验
                if (distributorIds == null || distributorIds.size() == 0) {
                    throw new BadRequestException("请选择折机经销商。");
                }
            }
        }

        //产品购买权限校验
        String buyPermission = product.getBuyPermission();
        if (StringUtil.isNotBlank(buyPermission)) {
            String[] permissions = buyPermission.split(",");
            for (String s : permissions) {
                if (ProductBuyPermission.find(s) == null) {
                    throw new BadRequestException("产品购买权限选择有误。");
                }
            }
        }

        //产品价格
        if (product.getMarketPrice() == null) {
            throw new BadRequestException("产品价格有误。");
        }
        //产品上架类型
        if (product.getOnShelfType() == null || ProductOnShelfType.find(product.getOnShelfType()) == null) {
            throw new BadRequestException("产品上架类型选择错误。");
        }
        //如果选择了自定义上架，则上架时间必须填写
        if (product.getOnShelfType() == ProductOnShelfType.SELECTTIME.value && product.getWillOnShelfTime() == null) {
            throw new BadRequestException("请选择自定义上架时间。");
        }
    }

    private void saveVirtualProductConfig(Integer productId, VirtualProductConfigDTO virtualProductConfigDTO) {
        VirtualProductConfig virtualProductConfig = new VirtualProductConfig(virtualProductConfigDTO);
        virtualProductConfig.setProductId(productId);
        virtualProductConfigMapper.insert(virtualProductConfig);
    }


    /**
     * 保存产品详细文本
     *
     * @param productId 产品ID
     * @param text      文本内容
     */
    private void saveProductDetail(Integer productId, String text) {
        Date now = new Date();
        ProductDetail query = new ProductDetail();
        query.setProductId(productId);
        ProductDetail productDetail = productDetailMapper.selectOne(query);
        if (productDetail != null) {
            if (!text.equalsIgnoreCase(productDetail.getText())) {
                productDetail.setText(text);
                productDetail.setUpdateTime(now);
                productDetailMapper.updateByPrimaryKey(productDetail);
            }
        } else {
            productDetail = new ProductDetail();
            productDetail.setProductId(productId);
            productDetail.setText(text);
            productDetail.setCreateTime(now);
            productDetailMapper.insert(productDetail);
        }
    }

    /**
     * 保存产品-产品前台类目关联关系
     *
     * @param productId        产品ID
     * @param frontCategoryIds 前台类目ID
     */
    private void saveProductFrontCategory(Integer productId, Set<Integer> frontCategoryIds) {
        //第一步先删除之前的关联
        ProductProductFrontCategory record = new ProductProductFrontCategory();
        record.setProductId(productId);
        productProductFrontCategoryMapper.delete(record);

        //第二步保存现在的关联
        List<ProductProductFrontCategory> list = new ArrayList<>();
        //所有前端类目集合（包括一级、二级、三级）
        Set<Integer> categoryIds = new HashSet<>();
        for (Integer id : frontCategoryIds) {
            Set<Integer> ids = productCategoryService.getSelfAndParentId(id);
            categoryIds.addAll(ids);
        }
        for (Integer id : categoryIds) {
            ProductProductFrontCategory ppfc = new ProductProductFrontCategory();
            ppfc.setProductId(productId);
            ppfc.setFrontCategoryId(id);
            list.add(ppfc);
        }
        productProductFrontCategoryMapper.batchInsert(list);
    }

    /**
     * 保存产品-折机经销商关联关系
     *
     * @param productId      产品ID
     * @param distributorIds 折机经销商ID
     */
    private void saveProductDistributor(Integer productId, Set<Integer> distributorIds) {
        //第一步先删除之前的关联
        ProductDistributor record = new ProductDistributor();
        record.setProductId(productId);
        productDistributorMapper.delete(record);

        //第二步保存现在的关联
        List<ProductDistributor> pdList = new ArrayList<>();
        ProductDistributor pd;
        for (Integer distributorId : distributorIds) {
            pd = new ProductDistributor();
            pd.setProductId(productId);
            pd.setDistributorId(distributorId);
            pdList.add(pd);
        }
        productDistributorMapper.batchInsert(pdList);
    }

    /**
     * 折机经销商关联关系经销商转让转移
     *
     * @param origDistributorId 原经销商id
     * @param newDistributorId  转让经销商id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void changeProductDistributor(Integer origDistributorId, Integer newDistributorId) {

        ProductDistributor record = new ProductDistributor();
        record.setDistributorId(newDistributorId);

        Example example = new Example(ProductDistributor.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("distributorId", origDistributorId);

        productDistributorMapper.updateByExampleSelective(record, example);
    }

    /**
     * 保存产品-产品计费方式关联关系
     *
     * @param productId 产品ID
     * @param costIds   计费方式ID
     */
    private void saveProductProductCost(Integer productId, Set<Integer> costIds) {
        //第一步先删除之前的关联
        ProductProductCost record = new ProductProductCost();
        record.setProductId(productId);
        productProductCostMapper.delete(record);

        //第二步保存现在的关联
        List<ProductProductCost> ppcList = new ArrayList<>();
        for (Integer costId : costIds) {
            ProductProductCost ppc = new ProductProductCost();
            ppc.setProductId(productId);
            ppc.setCostId(costId);
            ppcList.add(ppc);
        }
        productProductCostMapper.batchInsert(ppcList);
    }

    /**
     * 查询产品（单个，基本信息）
     *
     * @param id 产品ID
     */
    @Override
    public ProductDTO getProductById(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        if (product == null) {
            return null;
        }
        ProductDTO productDTO = new ProductDTO();
        product.convert(productDTO);
        //收益分配id
        // List<Integer> IncomeRuleIdsList = productIncomeRuleMapper.listIncomeRuleIdByProductId(id);
        // Set<Integer> IncomeRuleIdsSet = new HashSet<>(IncomeRuleIdsList);
        Set<Integer> IncomeRuleIdsSet = orderFeign.listIncomeRuleIdByProductId(id);
        productDTO.setIncomeRuleIds(IncomeRuleIdsSet);
        //前段分类id
        List<Integer> ProductFrontCategoryIdsList = productProductFrontCategoryMapper.listProductProductFrontCategoryId(id);
        Set<Integer> ProductFrontCategoryIdsSet = new HashSet<>(ProductFrontCategoryIdsList);
        productDTO.setFrontCategoryIds(ProductFrontCategoryIdsSet);
        if (product.getMode() == ProductModeEnum.LEASE.value) {
            List<ProductCostDTO> productCostList = productCostMapper.listProductCostByProductId(id);
            productDTO.setProductCostList(productCostList);
        }
        return productDTO;
    }

    /**
     * 查询虚拟产品配置
     *
     * @param id 产品ID
     */
    @Override
    public VirtualProductConfig getVirtualProductConfigByProductId(Integer id) {
        return virtualProductConfigMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询产品（单个，扩展信息）
     *
     * @param id       产品ID
     * @param terminal 平台1为公众号 3为app
     */
    @Override
    public ProductDTO getFullProductById(Integer id, Integer terminal) {
        //查询产品基本信息
        Product product = productMapper.selectByPrimaryKey(id);
        if (product == null) {
            return null;
        }
        ProductDTO productDTO = new ProductDTO();
        product.convert(productDTO);
        //1-查询产品详细文本
        ProductDetail productDetail;
        //2-查询产品后台类目
        ProductCategory backstageCategory;
        //3-查询产品前台类目
        List<ProductCategoryDTO> frontCategoryList;
        //4-查询水机产品计费方式
        List<ProductCostDTO> productCostList = null;
        //5-如果是虚拟产品，获取虚拟产品配置
        VirtualProductConfig virtualProductConfig = null;
        //6-查询收益分配
        List<IncomeRuleDTO> incomeRuleList;
        try {
            //1-查询产品详细文本
            Callable<ProductDetail> productDetailCallable = () -> productDetailMapper.selectByPrimaryKey(id);
            FutureTask<ProductDetail> productDetailTask = new FutureTask<>(productDetailCallable);
            ThreadUtil.executor.submit(productDetailTask);

            //2-查询产品后台类目
            Callable<ProductCategory> backstageCategoryCallable = () -> productCategoryMapper.selectByPrimaryKey(productDTO.getCategoryId());
            FutureTask<ProductCategory> backstageCategoryTask = new FutureTask<>(backstageCategoryCallable);
            ThreadUtil.executor.submit(backstageCategoryTask);

            //3-查询产品前台类目
            Callable<List<ProductCategoryDTO>> frontCategoryCallable = () -> productCategoryMapper.listFrontCategoryByProductId(product.getId(), terminal);
            FutureTask<List<ProductCategoryDTO>> frontCategoryTask = new FutureTask<>(frontCategoryCallable);
            ThreadUtil.executor.submit(frontCategoryTask);

            //4-查询水机产品计费方式
            FutureTask<List<ProductCostDTO>> costTask = null;
            if (productDTO.getMode() == ProductModeEnum.LEASE.value) {
                Callable<List<ProductCostDTO>> costCallable = () -> productCostMapper.listProductCostByProductId(id);
                costTask = new FutureTask<>(costCallable);
                ThreadUtil.executor.submit(costTask);
            }
            //5-如果是虚拟产品，获取虚拟产品配置
            FutureTask<VirtualProductConfig> virtualProductTask = null;
            if (productDTO.getMode() == ProductModeEnum.VIRTUAL.value) {
                Callable<VirtualProductConfig> virtualProductCallable = () -> virtualProductConfigMapper.selectByPrimaryKey(id);
                virtualProductTask = new FutureTask<>(virtualProductCallable);
                ThreadUtil.executor.submit(virtualProductTask);
            }

            // 租赁产品校验特有属性 活动类型：1-普通产品 2-折机产品 3-180产品；
            if (ProductModeEnum.LEASE.value == product.getMode() && product.getActivityType() != null && ProductActivityType.PRODUCT_CONVERT.value == product.getActivityType()) {
                Example example = new Example(ProductDistributor.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("productId", id);

                List<ProductDistributor> list = productDistributorMapper.selectByExample(example);
                if (CollectionUtil.isNotEmpty(list)) {
                    List<DistributorDTO> distributorList = new ArrayList<>();
                    for (ProductDistributor pd : list) {
                        DistributorDTO dto = new DistributorDTO();
                        dto.setId(pd.getDistributorId());
                        distributorList.add(dto);
                    }
                    //设置对应的折机经销商
                    productDTO.setDistributorList(distributorList);
                }
            }
            //6-查询收益分配
            // Callable<List<IncomeRuleDTO>> incomeRuleCallable = () -> {
            //     ProductIncomeRule record = new ProductIncomeRule();
            //     record.setProductId(id);
            //     List<Integer> incomeRuleIds = productIncomeRuleMapper.listIncomeRuleIdByProductId(id);
            //     if (CollectionUtil.isEmpty(incomeRuleIds)) {
            //         return null;
            //     } else {
            //         return orderFeign.listIncomeRuleByIds(incomeRuleIds);
            //     }
            // };
            // FutureTask<List<IncomeRuleDTO>> incomeRuleTask = new FutureTask<>(incomeRuleCallable);
            // ThreadUtil.executor.submit(incomeRuleTask);


            //1-查询产品详细文本
            productDetail = productDetailTask.get();
            //2-查询产品后台类目
            backstageCategory = backstageCategoryTask.get();
            //3-查询产品前台类目
            frontCategoryList = frontCategoryTask.get();
            //4-查询水机产品计费方式
            if (productDTO.getMode() == ProductModeEnum.LEASE.value && costTask != null) {
                productCostList = costTask.get();
            }
            //5-如果是虚拟产品，获取虚拟产品配置
            if (productDTO.getMode() == ProductModeEnum.VIRTUAL.value) {
                virtualProductConfig = virtualProductTask.get();
            }
            //6-查询收益分配
            incomeRuleList = orderFeign.listIncomeRuleByProductId(id);
            if (CollectionUtil.isEmpty(incomeRuleList)) {
                log.error("该产品没有配置收益分配规则，产品ID={}", id);

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NotFoundException("查询产品出错，请稍后重试。");
        }
        //1-设置产品详细文本
       /* if (productDetail != null) {
            productDTO.setTextDetail(productDetail.getText());
        }*/
        //2-设置产品后台类目
        if (backstageCategory != null) {
            ProductCategoryDTO dto = new ProductCategoryDTO();
            backstageCategory.convert(dto);
            productDTO.setBackstageCategory(dto);
        }
        //3-设置产品前台类目
        productDTO.setFrontCategoryList(frontCategoryList);
        Set<Integer> frontCategoryIds = null;
        if (CollectionUtil.isNotEmpty(frontCategoryList)) {
            frontCategoryIds = frontCategoryList.stream().map(ProductCategoryDTO::getId).collect(Collectors.toSet());
        }
        productDTO.setFrontCategoryIds(frontCategoryIds);
        //4-设置水机产品计费方式
        productDTO.setProductCostList(productCostList);
        Set<Integer> costIds = null;
        if (CollectionUtil.isNotEmpty(productCostList)) {
            costIds = productCostList.stream().map(ProductCostDTO::getId).collect(Collectors.toSet());
        }
        productDTO.setCostIds(costIds);
        //5-如果是虚拟产品，获取虚拟产品配置
        if (virtualProductConfig != null) {
            VirtualProductConfigDTO dto = new VirtualProductConfigDTO();
            virtualProductConfig.convert(dto);
            productDTO.setVirtualProductConfig(dto);
        }
        //6-查询收益分配
        productDTO.setIncomeRuleList(incomeRuleList);
        Set<Integer> incomeRuleIds = null;
        if (CollectionUtil.isNotEmpty(incomeRuleList)) {
            incomeRuleIds = incomeRuleList.stream().map(IncomeRuleDTO::getId).collect(Collectors.toSet());
        }
        productDTO.setIncomeRuleIds(incomeRuleIds);
        return productDTO;
    }

    /**
     * 查询产品（分页）
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @Override
    public PageVO<ProductDTO> page(Integer pageNum, Integer pageSize, ProductQuery query) {
        if (query.getCategoryId() != null) {
            // ProductCategory productCategory = productCategoryMapper.selectByPrimaryKey(query.getCategoryId());
            // if (productCategory == null) {
            //     throw new YimaoException("分类查询异常");
            // }
            // Integer level = productCategory.getLevel();
            // List<Integer> ids = new ArrayList<>();
            // if (level == ProductCategoryLevel.FIRST.value) {
            //     ids = productCategoryMapper.getNextCategory(query.getCategoryId());
            // }
            // if (level == ProductCategoryLevel.SECOND.value) {
            //     ids = productCategoryMapper.getNextCategoryT(query.getCategoryId());
            // }
            //
            // //2020-3-20 一、二级分类没有查到下级分类,使用当前分类id查
            // if (level == ProductCategoryLevel.THIRD.value || CollectionUtil.isEmpty(ids)) {
            //     ids.add(query.getCategoryId());
            // }
            query.setIds(this.getSubCategoryIds(query.getCategoryId()));
        }

        PageHelper.startPage(pageNum, pageSize);
        Page<ProductDTO> page = productMapper.listProduct(query);

        PageVO<ProductDTO> pageVO = new PageVO<>(pageNum, page);

        List<ProductDTO> result = pageVO.getResult();
        if (CollectionUtil.isNotEmpty(result)) {
            for (ProductDTO dto : result) {
                //1-后台产品级联类目名称
                String cascadeBackstageCategoryName = productCategoryService.buildCascadeCategoryName(dto.getCategoryId());
                dto.setCascadeBackstageCategoryName(cascadeBackstageCategoryName);

                //2-前台产品类目名称级联显示
                ProductProductFrontCategory record = new ProductProductFrontCategory();
                record.setProductId(dto.getId());
                //先获取产品对应的前台类目集合
                List<ProductProductFrontCategory> frontCategoryList = productProductFrontCategoryMapper.select(record);
                if (CollectionUtil.isNotEmpty(frontCategoryList)) {
                    List<String> cascadeFrontCategoryNameList = new ArrayList<>();
                    for (ProductProductFrontCategory ppfc : frontCategoryList) {
                        //分别获取每个前台类目对应的级联类目名称
                        String cascadeFrontCategoryName = productCategoryService.buildCascadeCategoryName(ppfc.getFrontCategoryId());
                        cascadeFrontCategoryNameList.add(cascadeFrontCategoryName);
                    }
                    dto.setCascadeFrontCategoryNameList(cascadeFrontCategoryNameList);
                }
            }
        }
        if (query.getNeed() != null && query.getNeed() == 1) {
            List<ProductDTO> list = page.getResult();
            Iterator<ProductDTO> iterator = list.iterator();
            while (iterator.hasNext()) {
                ProductDTO next = iterator.next();
                IncomeRuleDTO incomeRuleDTO = orderFeign.getIncomeRule(next.getId());
                if (incomeRuleDTO != null) {
                    List<IncomeRuleDTO> incomeRuleDTOS = new ArrayList<>();
                    incomeRuleDTOS.add(incomeRuleDTO);
                    next.setIncomeRuleList(incomeRuleDTOS);
                }
            }
        }
        return pageVO;
    }


    /**
     * 根据自己及其所有子分类ID
     *
     * @param categoryId
     */
    private List<Integer> getSubCategoryIds(Integer categoryId) {
        ProductCategory productCategory = productCategoryMapper.selectByPrimaryKey(categoryId);
        if (productCategory == null) {
            throw new YimaoException("分类查询异常");
        }
        Integer level = productCategory.getLevel();
        List<Integer> ids = new ArrayList<>();
        if (level == ProductCategoryLevel.FIRST.value) {
            List<Integer> ids2 = productCategoryMapper.getNextCategoryT(categoryId);
            if (CollectionUtil.isNotEmpty(ids2)) {
                ids.addAll(ids2);
                for (Integer id : ids2) {
                    List<Integer> ids3 = productCategoryMapper.getNextCategoryT(id);
                    if (CollectionUtil.isNotEmpty(ids3)) {
                        ids.addAll(ids3);
                    } else {
                        ids.add(id);
                    }
                }
            } else {
                ids.add(categoryId);
            }
        }
        if (level == ProductCategoryLevel.SECOND.value) {
            List<Integer> ids3 = productCategoryMapper.getNextCategoryT(categoryId);
            if (CollectionUtil.isNotEmpty(ids3)) {
                ids.addAll(ids3);
            } else {
                ids.add(categoryId);
            }
        }

        //2020-3-20 一、二级分类没有查到下级分类,使用当前分类id查
        if (level == ProductCategoryLevel.THIRD.value || CollectionUtil.isEmpty(ids)) {
            ids.add(categoryId);
        }
        return ids;
    }

    /**
     * 客户端查询产品（经销商APP、公众号等）
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @Override
    public PageVO<ProductDTO> listProductForClient(Integer pageNum, Integer pageSize, ProductQuery query) {
        if (StringUtil.isNotEmpty(query.getSupplyCode()) && Objects.equals(ProductSupplyCode.PTPSJ.code, query.getSupplyCode())) {
            UserDTO user = userFeign.getBasicUserById(userCache.getUserId());
            if (null == user) {
                throw new YimaoException("未找到用户相关信息！");
            }
            Integer distributorId = user.getMid();
            if (null == distributorId) {
                throw new YimaoException("用户经销商信息不存在！");
            }
            query.setDistributorId(distributorId);
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ProductDTO> page = productMapper.listProductForClient(query);
        //添加收益规则
        List<ProductDTO> list = page.getResult();
        Iterator<ProductDTO> iterator = list.iterator();
        while (iterator.hasNext()) {
            ProductDTO next = iterator.next();
            IncomeRuleDTO incomeRuleDTO = orderFeign.getIncomeRule(next.getId());
            if (incomeRuleDTO != null) {
                List<IncomeRuleDTO> incomeRuleDTOS = new ArrayList<>();
                incomeRuleDTOS.add(incomeRuleDTO);
                next.setIncomeRuleList(incomeRuleDTOS);
            }
        }

        return new PageVO<>(pageNum, page);
    }

    /**
     * 描述：APP端获取当前登录用户有权销售的产品销售类型：1-经销产品；2-站长专供产品；3-特供产品；4-特批水机；
     *
     * @param
     */
    @Override
    public List<DictionaryDTO> listProductSupplyCode(String userName) {
        UserDTO currentUser = userFeign.getBasicUserById(userCache.getUserId());
        //获取所有栏目
        List<DictionaryDTO> codeList = systemFeign.listDictionaryByGroup(ProductConstant.BRANCH_APP);
        //获取用户mid，无mid为普通用户/有mid为经销商（折机）代理商创始人
        Integer mid = currentUser.getMid();
        Iterator<DictionaryDTO> iterator = codeList.iterator();
        if (!UserType.isDistributor(currentUser.getUserType()) && mid == null) {//用户
            while (iterator.hasNext()) {
                DictionaryDTO next = iterator.next();
                if (Objects.equals(next.getCode(), ProductSupplyCode.PTPSJ.code) || Objects.equals(next.getCode(), ProductSupplyCode.PZZZG.code)) {
                    iterator.remove();
                }
            }
        } else {
            //获取经销商信息
            DistributorDTO distributor = userFeign.getBasicDistributorById(mid);
            if (Objects.equals(distributor.getRoleLevel(), DistributorRoleLevel.DISCOUNT.value)) {
                //折机版经销商只能查看折机水机
                while (iterator.hasNext()) {
                    DictionaryDTO next = iterator.next();
                    if (Objects.equals(next.getCode(), ProductSupplyCode.PJXCP.code) || Objects.equals(next.getCode(), ProductSupplyCode.PZZZG.code)) {
                        iterator.remove();
                    }
                }
            } else {
                //非折机版经销商
                //1-先把特批水机产品移除，因为只有折机经销商才能查看
                while (iterator.hasNext()) {
                    DictionaryDTO next = iterator.next();
                    if (Objects.equals(next.getCode(), ProductSupplyCode.PTPSJ.code)) {
                        iterator.remove();
                    }
                }
                //2-如果不是服务站站长则不能查看站长专供产品//是站长且服务站需要承包
                //获取服务站承包状态
                Boolean contract = systemFeign.getStationStatusByDistributorId(distributor.getId());
                if (distributor.getStationMaster() == null || !distributor.getStationMaster() || contract == null || !contract) {
                    iterator = codeList.iterator();
                    while (iterator.hasNext()) {
                        DictionaryDTO next = iterator.next();
                        if (Objects.equals(next.getCode(), ProductSupplyCode.PZZZG.code)) {
                            iterator.remove();
                        }
                    }
                }
                //3-不是代理商不能购买特供产品
                if (distributor.getAgentLevel() == null) {
                    iterator = codeList.iterator();
                    while (iterator.hasNext()) {
                        DictionaryDTO next = iterator.next();
                        if (Objects.equals(next.getCode(), ProductSupplyCode.PTGCP.code)) {
                            iterator.remove();
                        }
                    }
                }
                //4-剩下的产品就是该经销商能查看的产品
            }
        }
        if (CollectionUtil.isNotEmpty(codeList)) {
            for (DictionaryDTO dto : codeList) {
                dto.setCreator(null);
                dto.setCreateTime(null);
                dto.setUpdater(null);
                dto.setUpdateTime(null);
                dto.setDeleted(null);
                dto.setGroupCode(null);
                dto.setPid(null);
            }
        }
        return codeList;
    }

    @Override
    public ProductStatusStatisticVO findProductStatistics() {
        return productMapper.findProductStatistics();
    }

    /**
     * @return java.util.List<com.yimao.cloud.pojo.dto.product.ProductDTO>
     * @description 查询YM 卡的产品数据
     * @author liulin
     * @date 2019/2/23 9:27
     */
    @Override
    public List<ProductDTO> findMYCardProductList(String categoryName) {
        return productMapper.findMYCardProductList(categoryName);
    }


    /**
     * @return java.util.List<com.yimao.cloud.pojo.dto.product.ProductDTO>
     * @description 查询YM 卡的产品数据
     * @author liulin
     * @date 2019/2/23 9:27
     */
    @Override
    public List<ProductDTO> findMCardProductList() {
        return productMapper.findMCardProductList();
    }

    /**
     * @return java.util.List<com.yimao.cloud.pojo.dto.product.ProductDTO>
     * @description 查询F卡的产品数据
     * @author liulin
     * @date 2019/2/27 17:52
     */
    @Override
    public List<ProductDTO> findFCardProductList() {
        return productMapper.findFCardProductList();
    }

    /**
     * 描述：根据产品分类名称获取产品ID集合
     *
     * @param categoryName  产品分类名称
     * @param categoryLevel 几级产品分类
     */
    @Override
    public List<Integer> listProductIdsByCategoryName(String categoryName, Integer categoryLevel) {
        return productMapper.listProductIdsByCategoryName(categoryName, categoryLevel);
    }

    /**
     * 获取产品列表
     */
    @Override
    public List<ProductDTO> findProductList() {
        Product product = new Product();
        product.setStatus(ProductStatus.ONSHELF.value);
        List<Product> list = productMapper.select(product);
        return CopyUtil.copyList(list, Product.class, ProductDTO.class);
    }

    @Override
    public void updateBuyCount(Integer id, Integer count) {
        productMapper.updateSaleCountById(id, count);
    }

    /**
     * 批量上架、下架、删除
     *
     * @param updater
     * @param ids
     * @param status
     */
    @Override
    public void updateBatch(String updater, List<Integer> ids, Integer status) {
        Example example = new Example(Product.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        Product product = new Product();
        product.setStatus(status);
        if (status.equals(ProductStatus.ONSHELF.value)) {
            product.setOnShelfTime(new Date());
        }
        if (status.equals(ProductStatus.OFFSHELF.value)) {
            product.setOffShelfTime(new Date());
        }
        product.setUpdateTime(new Date());
        product.setUpdater(updater);
        productMapper.updateByExampleSelective(product, example);
    }

    /**
     * 后台修改订单产品型号时查询同类产品列表（二级类目一致，价格一致，计费方式一致）
     *
     * @param productId 产品ID
     * @param costId    计费方式ID
     */
    @Override
    public List<ProductDTO> listProductForModifyOrder(Long orderId, Integer productId, Integer costId) {
        OrderSubDTO orderInfo = orderFeign.findOrderInfoById(orderId);
        if (orderInfo == null) {
            throw new YimaoException("获取不到订单信息");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return null;
        }

        //产品价格
        BigDecimal price = product.getPrice();
        List<ProductDTO> productList;
        //如果是折机商品【折机产品只能更换自己的折机产品】
        if (Objects.nonNull(product.getActivityType()) && Objects.equals(product.getActivityType(), ProductActivityType.PRODUCT_CONVERT.value)) {
            productList = productMapper.listProductByActivityTypeAndDistributor(product.getActivityType(), orderInfo.getDistributorId(), price);
        } else {
            //三级类目ID
            Integer thirdCategoryId = product.getCategoryId();
            ProductCategory category = productCategoryMapper.selectByPrimaryKey(thirdCategoryId);
            //二级类目ID
            Integer secondCategoryId = category.getPid();
            ProductProductFrontCategory query = new ProductProductFrontCategory();
            query.setProductId(productId);
            List<ProductProductFrontCategory> ppfcList = productProductFrontCategoryMapper.select(query);
            //前端类目ID
            List<Integer> fcidList;
            if (CollectionUtil.isEmpty(ppfcList)) {
                return null;
            }
            fcidList = ppfcList.stream().map(ProductProductFrontCategory::getFrontCategoryId).collect(Collectors.toList());
            productList = productMapper.listProductForModifyOrder(costId, price, secondCategoryId, fcidList);
        }

        //如果是净水产品，查询出计费方式
        if (CollectionUtil.isNotEmpty(productList)) {
            for (ProductDTO dto : productList) {
                if (product.getMode() == ProductModeEnum.LEASE.value) {
                    List<ProductCostDTO> productCostList = productCostMapper.listProductCostByProductId(dto.getId());
                    dto.setProductCostList(productCostList);
                }
            }
        }
        return productList;
    }

    @Override
    public List<DictionaryDTO> getAllProductSupplyCode() {
        return systemFeign.listDictionaryByGroup(ProductConstant.BRANCH_APP);
    }

    @Override
    public List<ProductDTO> getProductByFrontId(Integer id) {
        DistributorDTO distributor;
        List<ProductDTO> list = productMapper.getProductByFrontId(id);
        UserDTO currentUser = userFeign.getBasicUserById(userCache.getUserId());
        Integer mid = currentUser.getMid();
        Iterator<ProductDTO> iterator = list.iterator();
        if (!UserType.isDistributor(currentUser.getUserType()) && mid == null) {
            //普通用户，只展示经销产品
            while (iterator.hasNext()) {
                ProductDTO next = iterator.next();
                String code = next.getSupplyCode();
                if (!Objects.equals(code, ProductSupplyCode.PJXCP.code)) {
                    iterator.remove();
                }
            }
        } else {
            //经销商，站长，特批经销商
            //username不为空时是经销商执行原先的逻辑
            distributor = userFeign.getBasicDistributorById(mid);
            Integer roleLevel = distributor.getRoleLevel();
            if (roleLevel == null || roleLevel != DistributorRoleLevel.DISCOUNT.value) {
                //有经销商身份
                //不是折机经销商
                while (iterator.hasNext()) {
                    ProductDTO next = iterator.next();
                    String code = next.getSupplyCode();
                    if (Objects.equals(code, ProductSupplyCode.PTPSJ.code)) {
                        iterator.remove();
                    }
                }
            }
            if (distributor.getStationMaster() == null || !distributor.getStationMaster()) {
                //不是站长
                while (iterator.hasNext()) {
                    ProductDTO next = iterator.next();
                    String code = next.getSupplyCode();
                    if (!Objects.equals(code, ProductSupplyCode.PZZZG.code)) {
                        iterator.remove();
                    }
                }
            }
        }
        //会员立省多少
        Iterator<ProductDTO> iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            ProductDTO next = iterator2.next();
            IncomeRuleDTO incomeRuleDTO = orderFeign.getIncomeRule(next.getId());
            if (incomeRuleDTO != null) {
                List<IncomeRuleDTO> incomeRuleDTOS = new ArrayList<>();
                incomeRuleDTOS.add(incomeRuleDTO);
                next.setIncomeRuleList(incomeRuleDTOS);
            }
        }


        return list;
    }


    @Override
    public List<Integer> getProductBySupplyCode(String code) {
        List<Integer> list = productMapper.getProductBySupplyCode(code);

        return list;
    }

}
