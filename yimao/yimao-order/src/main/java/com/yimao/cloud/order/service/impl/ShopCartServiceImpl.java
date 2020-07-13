package com.yimao.cloud.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.DialogException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.ResultUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.feign.ProductFeign;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.mapper.ShopCartMapper;
import com.yimao.cloud.order.po.ShopCart;
import com.yimao.cloud.order.service.OrderCheckService;
import com.yimao.cloud.order.service.ShopCartService;
import com.yimao.cloud.pojo.dto.order.BaseOrder;
import com.yimao.cloud.pojo.dto.order.OrderDTO;
import com.yimao.cloud.pojo.dto.order.ShopCartDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.user.UserIncomeAccountDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author zhilin.he
 * @description 购物车服务实现层
 * @date 2018/12/25 11:30
 **/
@Service
@Slf4j
public class ShopCartServiceImpl implements ShopCartService {

    @Resource
    private UserCache userCache;
    @Resource
    private UserFeign userFeign;
    @Resource
    private ShopCartMapper shopCartMapper;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private OrderCheckService orderCheckService;

    /**
     * 添加购物车
     *
     * @param shopCart 购物车信息
     */
    @Override
    public void save(ShopCart shopCart) {
        if (shopCart.getProductId() == null) {
            throw new BadRequestException("请选择加购产品。");
        }
        if (shopCart.getProductCategoryId() == null) {
            throw new BadRequestException("产品前端一级类目id不能为空。");
        }
        if (StringUtil.isEmpty(shopCart.getProductCategoryName())) {
            throw new BadRequestException("产品前端一级类目名称不能为空。");
        }
        if (shopCart.getTerminal() == null || Terminal.find(shopCart.getTerminal()) == null) {
            throw new BadRequestException("加购终端类型错误。");
        }
        if (shopCart.getCount() == null || shopCart.getCount() == 0) {
            throw new BadRequestException("加购数量错误。");
        }

        //获取加购产品信息
        ProductDTO product = productFeign.getFullProductById(shopCart.getProductId());
        if (product == null || product.getStatus() == null || product.getStatus() == ProductStatus.DELETED.value) {
            throw new BadRequestException("加购失败，产品已下架或删除。");
        }
        shopCart.setProductName(product.getName());
        // 购物车添加产品封面 -> coverImg ; img - 轮播图
        shopCart.setProductImage(product.getCoverImg());

        if (product.getStatus() == ProductStatus.OFFSHELF.value) {
            throw new BadRequestException("产品已下架！");
        }
        // 限购数为零则不限制
        Integer maxMoq = product.getMaxMoq();
        if (maxMoq != null && maxMoq < shopCart.getCount() && maxMoq != 0) {
            throw new BadRequestException("该产品设置了最大限订数，最大限订" + maxMoq + "件。");
        }

        Integer totalNumber = this.sumCount(shopCart.getUserId(), shopCart.getTerminal());
        if (totalNumber != null && totalNumber + shopCart.getCount() > 200) {
            throw new BadRequestException("您购物车内的商品数量过多，清理后方可加入购物车。");
        }

        //type 商品类型（大类）:1实物商品，2电子卡券，3租赁商品
        ProductCostDTO productCost = null;
        if (product.getMode() == ProductModeEnum.LEASE.value) {
            if (shopCart.getCostId() == null) {
                throw new BadRequestException("请选择计费方式。");
            }
            List<ProductCostDTO> productCostList = product.getProductCostList();
            if (CollectionUtil.isNotEmpty(productCostList)) {
                productCost = productCostList.stream().filter(cost -> Objects.equals(cost.getId(), shopCart.getCostId())).findFirst().orElse(null);
            }
            if (productCost == null) {
                throw new BadRequestException("请选择计费方式。");
            }
            shopCart.setCostName(productCost.getName());
            //设置购物车单价（如果是水机已经加上了安装费）
            shopCart.setProductAmountFee(productCost.getTotalFee());
        } else {
            //设置购物车单价（如果是水机已经加上了安装费）
            shopCart.setProductAmountFee(product.getPrice());
        }

        Date date = new Date();
        ShopCart cart = new ShopCart();
        cart.setUserId(shopCart.getUserId());
//        cart.setType(shopCart.getType());
        cart.setTerminal(shopCart.getTerminal());
        cart.setProductId(shopCart.getProductId());
        cart.setCostId(shopCart.getCostId());
        List<ShopCart> shopCartList = shopCartMapper.select(cart);

        //如果已存在，则数量累加
        if (CollectionUtil.isNotEmpty(shopCartList)) {
            ShopCart sc = shopCartList.get(0);
            ShopCart update = new ShopCart();
            update.setId(sc.getId());
            Integer number = sc.getCount() + shopCart.getCount();
            // 限购数为零则不限制
            if (maxMoq != null && number > product.getMaxMoq() && maxMoq != 0) {
                throw new BadRequestException("该产品设置了最大限订数，最大限订" + product.getMaxMoq() + "件。");
            }
            update.setCount(number);
            update.setUpdateTime(date);
            shopCartMapper.updateByPrimaryKeySelective(update);
        } else {
            //不存在，则加购成功，保存数据
            shopCart.setCreateTime(date);
            shopCart.setUpdateTime(date);
            shopCartMapper.insertSelective(shopCart);
        }
    }

    /**
     * 修改购物车
     *
     * @param shopCart 购物车信息
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void update(ShopCart shopCart) {
        String info = JSON.toJSONString(shopCart);
        ShopCart record = shopCartMapper.selectByPrimaryKey(shopCart.getId());
        if (record == null) {
            log.error("修改购物车，购物车校验失败：{}", info);
            throw new BadRequestException("购物车已删除。");
        }
        //当前登录用户和购物车用户身份校验
        Integer userId = userCache.getUserId();
        if (!Objects.equals(userId, record.getUserId())) {
            log.error("修改购物车，用户身份校验失败：{}", info);
            throw new BadRequestException("操作失败。");
        }
        if (shopCart.getCount() == null) {
            log.error("修改购物车，数量不能为空");
            throw new BadRequestException("修改购物车，数量不能为空。");
        }

        shopCart.setUserId(userId);
        //购物车数量不能超过200个
        Integer totalNumber = this.sumCount(userId, shopCart.getTerminal());
        if (totalNumber != null && (totalNumber - record.getCount() + shopCart.getCount()) > 200) {
            log.error("修改购物车，购物车内的商品数量过多");
            throw new BadRequestException("您购物车内的商品数量过多，清理后方可加入购物车。");
        }

        //获取加购产品信息
        ProductDTO product = productFeign.getProductById(record.getProductId());
        if (product == null || product.getStatus() == null || product.getStatus() == ProductStatus.DELETED.value) {
            log.error("修改购物车，产品已下架或删除：{}", info);
            throw new BadRequestException("操作失败，产品已下架或删除。");
        }

        // 限购数为零则不限制
        Integer maxMoq = product.getMaxMoq();
        if (maxMoq != null && maxMoq < shopCart.getCount() && maxMoq != 0) {
            log.error("修改购物车，产品设置了最大限订数，最大限订{}：{}", maxMoq, info);
            throw new BadRequestException("产品设置了最大限订数，最大限订" + maxMoq + "件。");
        }
        if (shopCart.getCount() > 200) {
            log.error("修改购物车，您购物车内的商品数量过多，清理后方可加入购物车：{}", info);
            throw new BadRequestException("您购物车内的商品数量过多，清理后方可加入购物车。");
        }

        //如果已存在，则数量累加
        ShopCart update = new ShopCart();
        //①修改计费方式
        if (shopCart.getCostId() != null && !Objects.equals(record.getCostId(), shopCart.getCostId()) && product.getMode() == ProductModeEnum.LEASE.value) {
            //计费方式校验
            ProductCostDTO productCost;
            //租赁产品、计费方式不等于空，且与原先的计费方式不同才需要更新
            List<ProductCostDTO> costList = productFeign.listProductCostByProductId(record.getProductId());
            if (CollectionUtil.isNotEmpty(costList)) {
                productCost = costList.stream().filter(cost -> Objects.equals(cost.getId(), shopCart.getCostId())).findFirst().orElse(null);
            } else {
                log.error("修改购物车，产品尚未设置计费方式：{}", info);
                throw new BadRequestException("产品尚未设置计费方式。");
            }

            //根据产品ID、计费方式、端，查询购物车是否存在此产品
            if (productCost != null) {
                update.setTerminal(shopCart.getTerminal());
                update.setCostId(shopCart.getCostId());
                update.setUserId(userId);
                update.setProductId(shopCart.getProductId());
                List<ShopCart> cartList = shopCartMapper.select(update);
                //同一个产品，新的计费方式已存在，则合并数量
                if (CollectionUtil.isNotEmpty(cartList)) {
                    ShopCart cart = cartList.get(0);
                    update.setId(cart.getId());
                    update.setCount(cart.getCount() + record.getCount());
                    shopCart.setCount(cart.getCount() + record.getCount());

                    //购物车合并-删除
                    update.setUpdateTime(new Date());
                    int count = shopCartMapper.updateByPrimaryKeySelective(update);
                    if (count < 1) {
                        throw new BadRequestException("修改购物车失败。");
                    }
                    int result = shopCartMapper.deleteByPrimaryKey(shopCart.getId());
                    if (result < 1) {
                        throw new BadRequestException("删除购物车失败。");
                    }
                } else {
                    update.setId(shopCart.getId());
                    update.setCostId(shopCart.getCostId());
                    update.setCostName(productCost.getName());
                    shopCartMapper.updateByPrimaryKeySelective(update);
                }
            } else {
                //如果填写的计费方式不正确，则报错。
                log.error("修改购物车，产品计费方式选择错误：{}", info);
                throw new BadRequestException("产品计费方式选择错误。");
            }
        } else {
            update.setId(shopCart.getId());
            update.setCount(shopCart.getCount());
            shopCartMapper.updateByPrimaryKeySelective(update);
        }
    }

    /**
     * 删除购物车
     *
     * @param cartIds 所选购物车ID
     */
    @Override
    public void delete(List<Integer> cartIds) {
        if (CollectionUtil.isNotEmpty(cartIds)) {
            shopCartMapper.deleteShopCart(cartIds, userCache.getUserId());
        }
    }

    /**
     * 查询购物车列表
     *
     * @param userId            用户id
     * @param terminal          1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；
     * @param productCategoryId 产品前台一级类目ID
     */
    @Override
    public JSONObject listShopCart(Integer userId, Integer terminal, Integer productCategoryId) {
        JSONObject object = new JSONObject();

        //修改：后添加或修改的放前面
        Example example = new Example(ShopCart.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("terminal", terminal);
        criteria.andEqualTo("productCategoryId", productCategoryId);
        example.orderBy("updateTime").desc();
        List<ShopCart> shopCartList = shopCartMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(shopCartList)) {
            List<ShopCartDTO> dtoList = new ArrayList<>();
            //购物车总数量
            Integer counts = 0;
            ////购物车失效商品总数量
            Integer invalidCounts = 0;
            for (ShopCart cart : shopCartList) {
                List<ProductCostDTO> costList = productFeign.listProductCostByProductId(cart.getProductId());
                ShopCartDTO dto = new ShopCartDTO();
                cart.convert(dto);
                dto.setCostList(costList);
                dto.setCreateTime(null);
                dto.setUpdateTime(null);
                dto.setInvalid(false);
                //获取加购产品信息
                ProductDTO product = productFeign.getProductById(cart.getProductId());
                if (product == null || product.getStatus() == ProductStatus.DELETED.value) {
                    dto.setInvalid(true);
                    dto.setInvalidReason("下架");
                    invalidCounts += cart.getCount();
                } else if (product.getStatus() == ProductStatus.OFFSHELF.value) {
                    dto.setInvalid(true);
                    dto.setInvalidReason("下架");
                    invalidCounts += cart.getCount();
                } else {
                    if (product.getStock() != null && product.getStock() <= 0 && product.getStock() != -500) { // stock = -500 不限制库存
                        dto.setInvalid(true);
                        dto.setInvalidReason("售罄");
                        invalidCounts += cart.getCount();
                    }
                }
                if (product != null) {
                    if (product.getMode() != null) {
                        dto.setMode(product.getMode());
                    }
                    if (product.getMarketPrice() != null) {
                        //产品价格
                        dto.setProductMarketFee(product.getMarketPrice());
                    }
                    if (product.getSupplyCode() != null) {
                        dto.setSupplyCode(product.getSupplyCode());
                    }
                    dto.setLogisticsFee(product.getLogisticsFee());
                    dto.setTransportType(product.getTransportType());
                }
                counts += cart.getCount();
                dtoList.add(dto);
            }
            object.put("shopCartList", dtoList);
            object.put("counts", counts - invalidCounts); //有效购物车商品数量
            return object;
        }
        return null;
    }

    /**
     * 查询加购的产品前台一级类目列表
     *
     * @param userId   用户id
     * @param terminal 1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；
     */
    @Override
    public JSONObject listShopCartProductCategory(Integer userId, Integer terminal) {
        JSONObject object = new JSONObject();
        ShopCart record = new ShopCart();
        record.setUserId(userId);
        record.setTerminal(terminal);
        List<ShopCart> shopCartList = shopCartMapper.select(record);
        //获取加购产品一级类目列表
        List<ProductCategoryDTO> productCateogryList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(shopCartList)) {
            //获取加购产品一级类目id集合
            List<Integer> cateogryIdList = new ArrayList<>();
            ProductCategoryDTO categoryDTO;
            for (ShopCart cart : shopCartList) {
                if (cateogryIdList.contains(cart.getProductCategoryId())) {
                    continue;
                } else {
                    cateogryIdList.add(cart.getProductCategoryId());
                    categoryDTO = new ProductCategoryDTO();
                    categoryDTO.setId(cart.getProductCategoryId());
                    categoryDTO.setName(cart.getProductCategoryName());
                    productCateogryList.add(categoryDTO);
                }
            }
        }
        object.put("productCateogryList", productCateogryList);
        return object;
    }

    /**
     * 查询购物车产品累计数量
     *
     * @param userId   用户id
     * @param terminal 1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；
     */
    @Override
    public Integer sumCount(Integer userId, Integer terminal) {
        ShopCart record = new ShopCart();
        record.setTerminal(terminal);
        record.setUserId(userId);
        return shopCartMapper.sumCount(record);
    }

    /** ---------------------------------购物车订单结算 start----------------------------------- **/
    /**
     * 购物车商品结算
     *
     * @param orderInfo 前端传递的购物车订单信息
     */
    @Override
    public void orderSettlement(OrderDTO orderInfo) {
        //获取登录用户信息
        UserDTO user = userFeign.getBasicUserById(userCache.getUserId());
        Set<ShopCartDTO> shopCartSet = orderInfo.getShopCartSet();
        if (CollectionUtil.isEmpty(shopCartSet)) {
            throw new BadRequestException("下单失败，请选择要购买的产品。");
        }

        //校验起订量
        this.moqCount(shopCartSet, orderInfo.getTerminal());

        for (ShopCartDTO shopCart : shopCartSet) {
            //校验商品是否存在
            ShopCart cart = shopCartMapper.selectByPrimaryKey(shopCart.getId());
            if (Objects.isNull(cart)) {
                throw new BadRequestException("不存在该商品的购物车。");
            }

            //获取产品信息
            ProductDTO product = productFeign.getProductById(shopCart.getProductId());
            //校验经销商信息
            shopCart.setOrderAmountFee(orderInfo.getOrderAmountFee());
            this.checkDistributor(user, shopCart, product);
        }
    }

    private void moqCount(Set<ShopCartDTO> shopCartSet, Integer terminal) {
        Map<Integer, Integer> mapMoq = new HashMap<>(8);
        Map<Integer, Integer> moqCategory = new HashMap<>(8);
        for (ShopCartDTO sc : shopCartSet) {
            ProductCategoryDTO productCategory = productFeign.getFrontCategoryByProductId(sc.getProductId(), terminal);
            Integer minMoq;
            if (productCategory != null) {
                minMoq = productCategory.getMinMoq();   //产品的起订量
                Integer count = sc.getCount();          //购物车数量
                if (minMoq == null) {
                    continue;
                }
                //获取购买的总数
                if (Objects.nonNull(mapMoq.get(productCategory.getId()))) {
                    mapMoq.put(productCategory.getId(), mapMoq.get(productCategory.getId()) + count);
                } else {
                    mapMoq.put(productCategory.getId(), count);
                    moqCategory.put(productCategory.getId(), minMoq);
                }
            }
        }

        if (Objects.nonNull(mapMoq) && mapMoq.size() > 0) {
            for (Integer key : mapMoq.keySet()) {
                Integer value = mapMoq.get(key);
                if (value < moqCategory.get(key)) {
                    throw new BadRequestException("亲，该产品" + moqCategory.get(key) + "盒/件起订哦。");
                }
            }
        }
    }

    /**
     * 结算-校验经销商信息
     *
     * @param user 用户信息
     */

    private void checkDistributor(UserDTO user, BaseOrder baseOrder, ProductDTO product) {
        // 1.用户的上级经销商账号是否禁用/禁止下单
        DistributorDTO distributor;
        //type当前用户的上级经销商身份：false、上级经销商;true、自己
        boolean type = false;
        Integer userType = user.getUserType();
        if (userType == UserType.USER_4.value) {
            //普通用户下单，获取系统配置的默认经销商
            UserIncomeAccountDTO incomeAccount = userFeign.getIncomeAccount();
            if (Objects.isNull(incomeAccount) || Objects.isNull(incomeAccount.getDistributorId())) {
                log.error("下单失败，未获取系统配置的默认经销商。");
                throw new BadRequestException("下单失败，未获取系统配置的默认经销商。");
            }
            distributor = userFeign.getDistributorBasicInfoById(incomeAccount.getDistributorId());
            if (Objects.isNull(distributor)) {
                log.error("默认经销商不存在，不能下单。");
                throw new BadRequestException("默认经销商不存在，不能下单。");
            }
        } else {
            //如果是经销商，经销商就是他自己
            Integer distributorId;
            if (UserType.isDistributor(userType)) {
                distributorId = user.getMid();
                type = true;
            } else {
                distributorId = user.getDistributorId();
            }
            if (distributorId == null) {
                log.error("经销商不存在有误，不能下单。e家号->" + user.getId());
                throw new BadRequestException("经销商信息有误，不能下单。");
            }
            distributor = userFeign.getDistributorBasicInfoById(distributorId);
        }
        if (distributor == null) {
            throw new BadRequestException("下单失败，经销商信息不存在。");
        }
        if (distributor.getRoleLevel() == DistributorRoleLevel.D_1000.value) {
            //获取企业版主账号
            distributor = userFeign.getDistributorBasicInfoById(distributor.getPid());
            if (distributor == null) {
                throw new BadRequestException("下单失败，经销商信息不存在。");
            }
        }

        if (distributor.getForbidden()) {
            throw new BadRequestException("您的上级经销商账号已被禁用，导致无法正常下单。如有疑问，请联系客服。");
        }
        if (distributor.getForbiddenOrder()) {
            if (type) {
                throw new BadRequestException("您的经销商账号已被禁止下单，为不影响您的正常使用，请尽快联系客服处理。");
            } else {
                throw new BadRequestException("您的上级经销商账号已被禁止下单，导致无法正常下单。如有疑问，请联系客服。");
            }
        }

        //校验产品信息
        this.checkProduct(user, baseOrder, product, distributor, type);

        //产品模式：1-实物；2-虚拟；3-租赁
        Integer productMode = product.getMode();
        //3.校验用户的上级经销商水机产品的配额信息
        if (productMode == ProductModeEnum.LEASE.value) {
            Integer remainingQuota = distributor.getRemainingQuota();
            log.debug("经销商的智能净水设备:" + remainingQuota);
            if ((userType != UserType.DISTRIBUTOR_DISCOUNT_50.value)) {
                if (remainingQuota == null || remainingQuota <= 0 || baseOrder.getCount() > remainingQuota) {
                    if (type) {
                        throw new DialogException("您的水机配额已不足，为不影响您的正常使用，请尽快前往升级/续费。");
                    } else {
                        throw new BadRequestException("您的上级经销商水机配额已不足，为不影响您的正常使用，请提醒经销商升级/续费。");
                    }
                }
            } else {
                //特批经销商
                if (!Objects.equals(distributor.getUserId(), user.getId())) {
                    throw new BadRequestException("您的上级经销商是特批经销商，不能下单。如有疑问，请联系客服。");
                }
                Boolean flag = (remainingQuota == null || remainingQuota <= 0 || baseOrder.getCount() > remainingQuota) && (distributor.getRemainingReplacementAmount() == null || distributor.getRemainingReplacementAmount().compareTo(baseOrder.getOrderAmountFee()) <= 0);
                if (flag) {
                    throw new BadRequestException("您的水机剩余配额/剩余金额不足，不能下单");
                }
            }
        }
    }

    /**
     * 结算后台参数合法性检查
     *
     * @param product 商品信息
     */
    private void checkProduct(UserDTO user, BaseOrder baseOrder, ProductDTO product, DistributorDTO distributor, boolean type) {
        //商品购买数量
        Integer count = baseOrder.getCount();
        log.debug("商品购买数量==" + count);
        //校验产品可用状态
        if (product == null || product.getStatus() == ProductStatus.DELETED.value) {
            throw new BadRequestException("下单失败，商品不存在。");
        }
        if (product.getStatus() == ProductStatus.OFFSHELF.value) {
            throw new BadRequestException("下单失败，产品已下架。");
        }

        //2.购买权限校验
        this.buyPermission(user, baseOrder, product, distributor, type);

        //3.校验用户的上级经销商水机产品的配额,经销商处

        //校验订单数量
        if (count == null || count < 1) {
            throw new BadRequestException("下单失败，订单产品数量有误。");
        }

        //4.购买起订量校验
        // 获取商品的前台一级类目
//        ProductCategoryDTO productCategory = productFeign.getFrontCategoryByProductId(product.getId(), baseOrder.getTerminal());
//        if (productCategory != null) {
//            Integer minMoq = productCategory.getMinMoq();
//            if (minMoq != null && count < minMoq) {
//                throw new BadRequestException("亲，该产品" + minMoq + "盒/件起订哦。");
//            }
//        }
    }

    private void buyPermission(UserDTO user, BaseOrder baseOrder, ProductDTO product, DistributorDTO distributor, boolean type) {
        //产品模式：1-实物；2-虚拟；3-租赁
        Integer productMode = product.getMode();
        //产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
        String supplyCode = product.getSupplyCode();
        //用户身份类型
        Integer userType = user.getUserType();

        //①从用户角度校验商品是否可购买
        //type当前用户的上级经销商身份：1、自己，0、上级经销商
        if (baseOrder.getTerminal() == OrderFrom.DIS_APP.value) {
            if (Objects.equals(supplyCode, ProductSupplyCode.PJXCP.code)) {
                //体验版权限校验
                this.experiencePermission(userType, productMode, distributor, type);
            } else if (Objects.equals(supplyCode, ProductSupplyCode.PZZZG.code)) {
                //站长权限校验
                this.stationMasterPermission(user);
            } else if (Objects.equals(supplyCode, ProductSupplyCode.PTPSJ.code)) {
                if (userType != UserType.DISTRIBUTOR_DISCOUNT_50.value) {
                    throw new BadRequestException("您暂无购买权限，特批经销商只能购买特批产品。");
                }
            } else {
                throw new BadRequestException("暂无此类商品。");
            }
        } else {
            //健康e家
            //体验版权限校验
            this.experiencePermission(userType, productMode, distributor, type);

            //可购买人群：目前只会有站长可选
            String buyPermission = product.getBuyPermission();
            if (buyPermission != null && buyPermission.contains(ProductBuyPermission.M_1.code)) {
                //站长权限校验
                this.stationMasterPermission(user);
            }
        }
//        //②从商品角度校验用户是否可购买
//        String buyPermission = product.getBuyPermission();
//        boolean buy = ProductBuyPermissionUtil.check(buyPermission, user.getUserType());
//        if (!buy) {
//            data.setMsgType(3);
//            return ResultUtil.error(object, data, "抱歉！您没有该产品的购买权限。");
//        }

        //BUG 1304 当用户由于某种情况（绑定了折机办经销商）购物车的产品已经没有购买权限了，不给购买
        if (distributor.getRoleLevel() == DistributorRoleLevel.DISCOUNT.value && !Objects.equals(supplyCode, ProductSupplyCode.PTPSJ.code)) {
            throw new BadRequestException("您暂无购买权限，特批经销商只能购买特批产品。");
        }
    }

    //体验版权限校验
    private void experiencePermission(Integer userType, Integer productMode, DistributorDTO distributor, boolean type) {
        //自己是体验版经销商
        if (userType == UserType.DISTRIBUTOR_50.value) {
            if (productMode != ProductModeEnum.LEASE.value) {
                throw new DialogException("您暂无购买权限，如需购买，请尽快完成升级。");
            }
        }
        //上级是体验版经销商
        if (!type && distributor.getRoleLevel() == DistributorRoleLevel.D_50.value) {
            throw new BadRequestException("您的上级经销商暂无购买权限，为不影响您的正常使用，请提醒经销商升级。");
        }
    }

    /**
     * 站长权限校验
     */
    @Override
    public void stationMasterPermission(UserDTO user) {
        if (Objects.nonNull(user) && Objects.nonNull(user.getMid())) {
            DistributorDTO distributor = userFeign.getDistributorBasicInfoById(user.getMid());
            if (Objects.nonNull(distributor)) {
                String province = distributor.getProvince();
                String city = distributor.getCity();
                String region = distributor.getRegion();
                //自己是服务站站长
                if (Objects.nonNull(distributor.getStationMaster()) && distributor.getStationMaster()) {
                    if (StringUtil.isNotEmpty(province) && StringUtil.isNotEmpty(city) && StringUtil.isNotEmpty(region)) {
                        StationDTO station = systemFeign.getStationByPRC(distributor.getProvince(), distributor.getCity(), distributor.getRegion(), PermissionTypeEnum.PRE_SALE.value);
                        //服务站未承包
                        if (station == null || station.getContract() == null || !station.getContract()) {
                            throw new BadRequestException("您暂无购买权限，站长专供产品只有站长（已承包服务站）才能购买。");
                        }
                    }
                } else {
                    throw new BadRequestException("您暂无购买权限，站长专供产品只有站长（已承包服务站）才能购买。");
                }
            } else {
                throw new BadRequestException("您暂无购买权限，站长专供产品只有站长才能购买。");
            }
        } else {
            throw new BadRequestException("您暂无购买权限，站长专供产品只有站长才能购买。");
        }
    }

    /** ---------------------------------购物车订单结算 end----------------------------------- **/

    /**
     * 填写订单--发票须知
     */
    @Override
    public JSONObject orderInvoiceNotes() {
        JSONObject object = new JSONObject();
        //获取登录用户信息
        UserDTO user = userFeign.getBasicUserById(userCache.getUserId());

        // 用户的上级经销商账号
        DistributorDTO distributor;
        Integer userType = user.getUserType();
        if (userType == UserType.USER_4.value) {
            //普通用户下单，获取系统配置的默认经销商
            UserIncomeAccountDTO incomeAccount = userFeign.getIncomeAccount();
            if (Objects.isNull(incomeAccount) || Objects.isNull(incomeAccount.getDistributorId())) {
                log.error("下单失败，未获取系统配置的默认经销商。");
                throw new BadRequestException("下单失败，未获取到系统配置的默认经销商。");
            }
            distributor = userFeign.getDistributorBasicInfoById(incomeAccount.getDistributorId());
        } else {
            //如果是经销商，经销商就是他自己
            Integer distributorId;
            if (UserType.isDistributor(userType)) {
                distributorId = user.getMid();
            } else {
                distributorId = user.getDistributorId();
            }
            distributor = userFeign.getDistributorBasicInfoById(distributorId);
        }
        if (distributor == null) {
            throw new BadRequestException("下单失败，经销商信息不存在。");
        }
        if (distributor.getRoleLevel() == DistributorRoleLevel.D_1000.value) {
            //获取企业版主账号
            distributor = userFeign.getDistributorBasicInfoById(distributor.getPid());
        }
        if (distributor == null) {
            throw new BadRequestException("下单失败，经销商信息不存在。");
        }

        //======================2019-12-20 liuhao=====================================================
        //用户购买的是实物商品、健康评估卡（Y卡）=> 销售公司： 经销商的服务站公司名称  门店：经销商所属服务站门店
        //用户购买的是经销商品、经销商品         => 销售公司： 翼猫科技发展（上海）有限公司  门店：总部的热线电话400-151-9999
        //上级经销商门店信息
        StationDTO station = null;
        if (StringUtil.isNotEmpty(distributor.getProvince()) && StringUtil.isNotEmpty(distributor.getCity()) && StringUtil.isNotEmpty(distributor.getRegion())) {
            station = systemFeign.getStationByPRC(distributor.getProvince(), distributor.getCity(), distributor.getRegion(),PermissionTypeEnum.PRE_SALE.value);
        }
//        StationDTO station = systemFeign.getStationByDistributorId(distributor.getId());
//        if (station == null) {
//            throw new BadRequestException("您的上级经销商所属门店信息不存在。");
//        }
        //成功返回数据
        object.put("station", station);
        return ResultUtil.success(object);
    }

}
