package com.yimao.cloud.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.DialogException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.JsonUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.order.feign.HraFeign;
import com.yimao.cloud.order.feign.ProductFeign;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.mapper.OrderAddressMapper;
import com.yimao.cloud.order.mapper.OrderMainMapper;
import com.yimao.cloud.order.mapper.OrderPayCheckMapper;
import com.yimao.cloud.order.mapper.OrderSubMapper;
import com.yimao.cloud.order.mapper.ShopCartMapper;
import com.yimao.cloud.order.mapper.SubOrderDetailMapper;
import com.yimao.cloud.order.mapper.WorkOrderMapper;
import com.yimao.cloud.order.po.OrderAddress;
import com.yimao.cloud.order.po.OrderMain;
import com.yimao.cloud.order.po.OrderPayCheck;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.po.ShopCart;
import com.yimao.cloud.order.po.SubOrderDetail;
import com.yimao.cloud.order.po.WorkOrder;
import com.yimao.cloud.order.service.OrderCheckService;
import com.yimao.cloud.order.service.OrderMainService;
import com.yimao.cloud.order.service.ProductIncomeRecordService;
import com.yimao.cloud.order.service.QuotaChangeRecordService;
import com.yimao.cloud.order.service.ShopCartService;
import com.yimao.cloud.order.service.WorkOrderService;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.dto.order.BaseOrder;
import com.yimao.cloud.pojo.dto.order.OrderDTO;
import com.yimao.cloud.pojo.dto.order.OrderMainDTO;
import com.yimao.cloud.pojo.dto.order.OrderPayCheckDTO;
import com.yimao.cloud.pojo.dto.order.ShopCartDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderQueryDTO;
import com.yimao.cloud.pojo.dto.product.ProductActivityDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.SmsMessageDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.CustomerAddressDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.PersonCustomerDTO;
import com.yimao.cloud.pojo.dto.user.UserAddressDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.user.UserIncomeAccountDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.format.SignStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@Service
@Slf4j
public class OrderMainServiceImpl implements OrderMainService {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private UserCache userCache;
    @Resource
    private RedisCache redisCache;
    @Resource
    private UserFeign userFeign;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private OrderMainMapper orderMainMapper;
    @Resource
    private OrderSubMapper orderSubMapper;
    @Resource
    private SubOrderDetailMapper subOrderDetailMapper;
    @Resource
    private OrderAddressMapper orderAddressMapper;
    @Resource
    private ShopCartMapper shopCartMapper;
    @Resource
    private ShopCartService shopCartService;
    @Resource
    private WorkOrderMapper workOrderMapper;
    @Resource
    private OrderPayCheckMapper orderPayCheckMapper;
    @Resource
    private WorkOrderService workOrderService;
    @Resource
    private HraFeign hraFeign;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ProductIncomeRecordService productIncomeRecordService;
    @Resource
    private QuotaChangeRecordService quotaChangeRecordService;
    @Resource
    private MailSender mailSender;
    @Resource
    private RedisLock redisLock = new RedisLock();
    @Resource
    private OrderCheckService orderCheckService;

    /**
     * 根据主订单号查询主订单
     *
     * @param id 主订单号（支付订单号）
     */
    @Override
    public OrderMain findById(Long id) {
        return orderMainMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据主订单ID更新数据
     *
     * @param mainOrder 主订单
     * @return
     */
    @Override
    public Integer update(OrderMain mainOrder) {
        return orderMainMapper.updateByPrimaryKeySelective(mainOrder);
    }


    /**
     * 下单
     *
     * @param orderInfo 前端传递的订单信息
     * @param type      1-单件商品下单；2-购物车下单
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public OrderMain createOrder(OrderDTO orderInfo, Integer type) {
        if (!Constant.PRO_ENVIRONMENT) {
            log.info("下单type={}，数据为：{}", type, JSONObject.toJSONString(orderInfo));
        }
        //获取到下单来源终端终端 终端：1-用户终端APP；2-微信公众号；3-经销商APP；4-小猫店小程序
        Integer terminal = orderInfo.getTerminal();
        if (terminal == null || OrderFrom.find(terminal) == null) {
            throw new BadRequestException("下单失败，终端类型错误。");
        }
        //获取登录用户信息
        UserDTO user = userFeign.getBasicUserById(userCache.getUserId());
        //下单基本参数合法性检查
        this.checkOrderParams(orderInfo, user);
        //创建订单
        log.info("===前端传递的订单信息为1===" + JSON.toJSONString(orderInfo));
        return this.create(orderInfo, user, type);
    }

//    private DistributorDTO getDistributorInventory(UserDTO user) {
//        DistributorDTO distributor;
//        //type当前用户的上级经销商身份：false、上级经销商;true、自己
//        Integer userType = user.getUserType();
////        if (userType == UserType.USER_4.value) {
////            //普通用户下单，获取系统配置的默认经销商
////            UserIncomeAccountDTO incomeAccount = userFeign.getIncomeAccount();
////            if (Objects.isNull(incomeAccount) || Objects.isNull(incomeAccount.getDistributorId())) {
////                log.error("下单失败，未获取系统配置的默认经销商。");
////                throw new BadRequestException("下单失败，未获取系统配置的默认经销商。");
////            }
////            distributor = userFeign.getDistributorBasicInfoById(incomeAccount.getDistributorId());
////            if (Objects.isNull(distributor)) {
////                log.error("默认经销商不存在，不能下单。");
////                throw new BadRequestException("默认经销商不存在，不能下单。");
////            }
////        } else {
////            //如果是经销商，经销商就是他自己
////            Integer distributorId;
////            if (UserType.isDistributor(userType)) {
////                distributorId = user.getMid();
////            } else {
////                distributorId = user.getDistributorId();
////            }
////            distributor = userFeign.getDistributorBasicInfoById(distributorId);
////        }
//        log.info("*************经销商信息:distributor=" + JsonUtil.objectToJson(distributor) + "*************");
//        if (distributor == null) {
//            throw new BadRequestException("下单失败，经销商信息不存在。");
//        }
//        if (distributor.getRoleLevel() == DistributorRoleLevel.D_1000.value) {
//            //获取企业版主账号
//            distributor = userFeign.getDistributorBasicInfoById(distributor.getPid());
//            if (distributor == null) {
//                throw new BadRequestException("下单失败，经销商信息不存在。");
//            }
//        }
//        return distributor;
//    }

    /**
     * 下单基本参数合法性检查
     *
     * @param orderInfo 订单信息
     * @param user      用户信息
     */
    private void checkOrderParams(OrderDTO orderInfo, UserDTO user) {
        log.debug("传入参数：" + JsonUtil.objectToJson(orderInfo));

        if (Objects.isNull(user.getUserType())) {
            throw new YimaoException("对不起，您还没有购买该产品的权限。");
        }

        //下单用户没绑定手机号，不允许下单
        if (StringUtil.isEmpty(user.getMobile())) {
            throw new BadRequestException("您还没有绑定手机号。");
        }

        //校验是否选择购买商品
//        if (orderInfo.getProductId() == null) {
//            throw new YimaoException("下单失败，请选择要购买的产品！");
//        }

        //校验订单金额
        log.debug("订单金额====" + orderInfo.getOrderAmountFee());
        if (orderInfo.getOrderAmountFee() == null || new BigDecimal(0).compareTo(orderInfo.getOrderAmountFee()) > 0) {
            throw new BadRequestException("下单失败，订单总金额有误。");
        }

        //校验运费金额
//        log.info("运费金额====" + orderInfo.getLogisticsFee());
//        if (orderInfo.getLogisticsFee() == null || new BigDecimal(0).compareTo(orderInfo.getLogisticsFee()) > 0) {
//            throw new BadRequestException("下单失败，运费金额有误。");
//        }

        //校验订单数量
        if (orderInfo.getCount() == null || orderInfo.getCount() < 1) {
            throw new BadRequestException("下单失败，订单产品数量有误。");
        }

        //校验水机订单类型
//        if (orderInfo.getType() == null || WaterOrderType.find(orderInfo.getType()) == null) {
//            throw new BadRequestException("下单失败,水机订单类型错误。");
//        }

        //校验支付类型
//        if (orderInfo.getPayType() == null || PayType.find(orderInfo.getPayType()) == null) {
//            throw new BadRequestException("下单失败，支付类型参数错误。");
//        }

        //公众号下单不需要校验下单类型
//        if (orderInfo.getTerminal() != Terminal.WECHAT.value) {
//            //校验下单类型：1-为自己下单；2-为客户下单；
//            if (orderInfo.getSubType() == null || DistributorCreateOrderType.find(orderInfo.getSubType()) == null) {
//                throw new BadRequestException("下单失败，下单类型参数错误。");
//            }
//        }

//        // app用户下单校验
//        if (orderInfo.getTerminal() == Terminal.ENGINEER_APP.value) {
//            // 1.用户购物权限校验
//            Integer productId = orderInfo.getProductId();
//            ProductDTO product = productFeign.getProductById(productId);
//            if (product != null) {
//                if (!ProductBuyPermissionUtil.check(product.getBuyPermission(), user.getUserType())) {
//                    throw new YimaoException("对不起，您还没有购买该产品的权限。");
//                }
//            }
//            // 2.购买起订量校验
//            Integer count = orderInfo.getCount();
//            // 获取商品的前台一级类目
//            ProductCategoryDTO productCategory = productFeign.getFrontCategoryByProductId(productId);
//            if (productCategory != null) {
//                Integer minMoq = productCategory.getMinMoq();
//                if (minMoq != null && count < minMoq) {
//                    throw new BadRequestException("该产品设置了最小起订量，最小起购" + minMoq + "件。");
//                }
//            }
//            // 3.水机产品下单配额校验 经销商处
//        }

    }

    /**
     * 下单后台参数合法性检查
     *
     * @param orderInfo 订单信息
     * @param user      用户信息
     */
    private OrderMain create(OrderDTO orderInfo, UserDTO user, Integer type) {
        //订单来源：1-用户终端APP；2-微信公众号；3-经销商APP；4-小猫店小程序；6-H5分享页面
        Integer terminal = orderInfo.getTerminal();
        OrderMain mainOrder;
        if (terminal == OrderFrom.WECHAT.value || terminal == OrderFrom.H5.value) {
            //公众号
            if (type == OrderCreateType.SINGLE.value) {
                //单件商品下单
                //获取产品信息
                ProductDTO product = productFeign.getFullProductById(orderInfo.getProductId());
                //创建子订单对象，在校验的过程中设置一些属性
                OrderSub subOrder = new OrderSub();
                //创建子订单详细信息对象，在校验的过程中设置一些属性
                SubOrderDetail subOrderDetail = new SubOrderDetail();
                //订单收货地址信息
                OrderAddress orderAddress = new OrderAddress();
                //校验起订量
                ProductCategoryDTO productCategory = productFeign.getFrontCategoryByProductId(product.getId(), terminal);
                if (productCategory != null) {
                    Integer minMoq = productCategory.getMinMoq();
                    if (minMoq != null && orderInfo.getCount() < minMoq) {
                        throw new BadRequestException("亲，该产品" + minMoq + "盒/件起订哦。");
                    }
                }

                //设置支付类型
                subOrder.setPayTerminal(orderInfo.getPayTerminal() == null ? PayTerminal.DEALER.value : orderInfo.getPayTerminal());
                //校验下单信息
                this.checkAll(orderInfo, product, user, subOrder, subOrderDetail, orderInfo.getSubType(), orderInfo.getAddressType(), orderAddress);
                //保存主订单
                mainOrder = this.saveMainOrder(orderInfo, user.getId());
                // data.setId(mainOrder.getId());
                // data.setOrderAmountFee(mainOrder.getOrderAmountFee());
                //保存子订单
                if (product.getMode() == ProductModeEnum.LEASE.value) {
                    Integer count = orderInfo.getCount();
                    BigDecimal fee = orderInfo.getOrderAmountFee().divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
                    for (int i = 0; i < count; i++) {
                        orderInfo.setCount(1);
                        //订单金额
                        subOrder.setFee(fee);
                        this.saveSubOrder(mainOrder, orderInfo, subOrder, subOrderDetail, orderAddress, user, product.getMode());
                    }
                } else {
                    //订单金额
                    subOrder.setFee(orderInfo.getOrderAmountFee());
                    this.saveSubOrder(mainOrder, orderInfo, subOrder, subOrderDetail, orderAddress, user, product.getMode());

                    //公众号：活动产品只有H5页面才有
                    if (terminal == OrderFrom.H5.value) {
                        //活动商品减库存
                        this.subtractProductActivityStock(product, orderInfo);
                    }
                }
                return mainOrder;
            } else if (type == OrderCreateType.SHOPCART.value) {
                //公众号购物车下单
                return this.createFromShopCart(orderInfo, user);
            }
        } else if (terminal == OrderFrom.DIS_APP.value) {
            //经销商APP
            if (type == OrderCreateType.SHOPCART.value) {
                //购物车
                return this.createFromShopCart(orderInfo, user);
            } else if (type == OrderCreateType.SINGLE.value) {
                //单件商品下单
                //获取产品信息
                ProductDTO product = productFeign.getFullProductById(orderInfo.getProductId());
                //创建子订单对象，在校验的过程中设置一些属性
                OrderSub subOrder = new OrderSub();
                //创建子订单详细信息对象，在校验的过程中设置一些属性
                SubOrderDetail subOrderDetail = new SubOrderDetail();
                //订单收货地址信息
                OrderAddress orderAddress = new OrderAddress();
                //设置支付类型
                subOrder.setPayTerminal(orderInfo.getPayTerminal() == null ? PayTerminal.DEALER.value : orderInfo.getPayTerminal());
                //校验下单信息
                this.checkAll(orderInfo, product, user, subOrder, subOrderDetail, orderInfo.getSubType(), orderInfo.getAddressType(), orderAddress);
                //保存主订单
                mainOrder = this.saveMainOrder(orderInfo, user.getId());
                // data.setId(mainOrder.getId());
                // data.setOrderAmountFee(mainOrder.getOrderAmountFee());
                //保存子订单
                //翼猫APP直接下单只支持实物活动商品
                if (product.getMode() != ProductModeEnum.REALTHING.value) {
                    throw new BadRequestException("下单失败，目前只支持实物商品进行抢购。");
                }
                //订单金额
                subOrder.setFee(orderInfo.getOrderAmountFee());
                this.saveSubOrder(mainOrder, orderInfo, subOrder, subOrderDetail, orderAddress, user, product.getMode());
                //2020-03-13
                //抢购活动商品减库存
                this.subtractProductActivityStock(product, orderInfo);
//                if (product.getMode() == ProductModeEnum.REALTHING.value) {
//                    if (product.getActivityType() != null && product.getActivityType() == ProductActivityType.PANIC_BUYING.value) {
//                        Integer productActivityId = orderInfo.getProductActivityIdList().get(0);
//                        Integer count = orderInfo.getCount();
//                        try {
//                            int num = productFeign.subtractProductActivityStock(productActivityId, count);
//                            if (num <= 0) {
//                                log.error("活动商品下单减库存失败，商品已被抢购完");
//                                throw new BadRequestException("商品已被抢购完。");
//                            }
//                        } catch (Exception e) {
//                            log.error("活动商品下单减库存失败，发生异常", e.getMessage());
//                            //报错回滚事务
//                            throw new BadRequestException("商品已被抢购完。");
//                        }
//                    }
//                }
                return mainOrder;
            } else {
                //经销商APP只有购物车下单一种场景
                log.info("*************下单失败，不合法的请求*************");
                throw new BadRequestException("下单失败，不合法的请求。");
            }
        }
        return null;
    }


    /**
     * 活动商品减库存
     *
     * @param product   产品
     * @param orderInfo 订单
     */
    private void subtractProductActivityStock(ProductDTO product, OrderDTO orderInfo) {
        //活动商品减库存
        if (product.getMode() == ProductModeEnum.REALTHING.value) {
            if (product.getActivityType() != null && product.getActivityType() == ProductActivityType.PANIC_BUYING.value) {
                Integer productActivityId = orderInfo.getProductActivityIdList().get(0);
                Integer count = orderInfo.getCount();
                try {
                    int num = productFeign.subtractProductActivityStock(productActivityId, count);
                    if (num <= 0) {
                        log.error("活动商品下单减库存失败，商品已被抢购完");
                        throw new BadRequestException("商品已被抢购完。");
                    }
                } catch (Exception e) {
                    log.error("活动商品下单减库存失败，发生异常", e.getMessage());
                    //报错回滚事务
                    throw new BadRequestException("商品已被抢购完。");
                }
            }
        }
    }

    /**
     * 购物车下单逻辑（公众号和经销商APP逻辑一致，所以提取成共通方法）
     *
     * @param orderInfo 订单信息
     * @param user      用户信息
     */
    private OrderMain createFromShopCart(OrderDTO orderInfo, UserDTO user) {
        Set<ShopCartDTO> shopCartSet = orderInfo.getShopCartSet();
        if (CollectionUtil.isEmpty(shopCartSet)) {
            throw new BadRequestException("下单失败，请选择要购买的产品。");
        }

        //校验起订量
        this.moqCount(shopCartSet, orderInfo.getTerminal());
        //保存主订单
        OrderMain mainOrder = this.saveMainOrder(orderInfo, user.getId());
        List<Integer> ids = new ArrayList<>();
        for (ShopCartDTO shopCart : shopCartSet) {
            //校验商品是否存在
            ShopCart cart = shopCartMapper.selectByPrimaryKey(shopCart.getId());
            if (Objects.isNull(cart)) {
                throw new YimaoException("不存在该商品的购物车");
            }

            ids.add(shopCart.getId());
            //获取产品信息
            ProductDTO product = productFeign.getProductById(shopCart.getProductId());
            //创建子订单对象，在校验的过程中设置一些属性
            OrderSub subOrder = new OrderSub();
            subOrder.setPayTerminal(orderInfo.getPayTerminal() == null ? PayTerminal.DEALER.value : orderInfo.getPayTerminal());
            //创建子订单详细信息对象，在校验的过程中设置一些属性
            SubOrderDetail subOrderDetail = new SubOrderDetail();
            //订单收货地址信息
            OrderAddress orderAddress = new OrderAddress();
            //校验下单信息
            this.checkAll(shopCart, product, user, subOrder, subOrderDetail, orderInfo.getSubType(), orderInfo.getAddressType(), orderAddress);

            //备注
            if (!StringUtil.isEmpty(orderInfo.getRemark())) {
                shopCart.setRemark(orderInfo.getRemark());
            }
            //保存子订单
            if (product.getMode() == ProductModeEnum.LEASE.value) {
                Integer count = shopCart.getCount();
                BigDecimal fee = shopCart.getOrderAmountFee().divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
                for (int i = 0; i < count; i++) {
                    shopCart.setCount(1);
                    //订单金额
                    subOrder.setFee(fee);
                    this.saveSubOrder(mainOrder, shopCart, subOrder, subOrderDetail, orderAddress, user, product.getMode());
                }
            } else {
                //订单金额
                subOrder.setFee(shopCart.getOrderAmountFee());
                this.saveSubOrder(mainOrder, shopCart, subOrder, subOrderDetail, orderAddress, user, product.getMode());
            }
        }

        //下单完成删除购物车内容
        int result = shopCartMapper.deleteShopCart(ids, user.getId());
        if (result < 1) {
            throw new BadRequestException("删除购物车失败。");
        }
        // data.setId(mainOrder.getId());
        // data.setOrderAmountFee(mainOrder.getOrderAmountFee());
        return mainOrder;
    }

    /**
     * 起订量判断
     */
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
     * 校验下单信息
     *
     * @param baseOrder      订单基本信息
     * @param product        商品信息
     * @param subOrder       子订单（设置订单属性）
     * @param subOrderDetail 子订单详细信息
     * @param orderAddress   订单收货地址信息
     */
    private void checkAll(BaseOrder baseOrder, ProductDTO product, UserDTO user, OrderSub subOrder, SubOrderDetail subOrderDetail, Integer subType, Integer addressType, OrderAddress orderAddress) {
        //校验经销商信息
        this.checkDistributor(user, baseOrder, product, subOrder, subOrderDetail);
        //校验安装工信息
        this.checkEngineer(product.getMode(), baseOrder.getDispatchType(), baseOrder.getEngineerId(), baseOrder.getAddressId(), subOrder, subOrderDetail, subType, addressType);
        //校验收货地址信息
        if (product.getMode() != ProductModeEnum.VIRTUAL.value) {
            this.checkAddress(baseOrder.getAddressId(), subOrderDetail, subType, addressType, orderAddress);
        }
    }

    /**
     * 下单后台参数合法性检查
     *
     * @param baseOrder      订单基本信息
     * @param product        商品信息
     * @param subOrder       子订单（设置订单属性）
     * @param subOrderDetail 子订单详细信息
     */
    private void checkProduct(UserDTO user, BaseOrder baseOrder, ProductDTO product, OrderSub subOrder, SubOrderDetail subOrderDetail, DistributorDTO distributor, boolean type) {
        //商品购买数量
        Integer count = baseOrder.getCount();
        log.debug("商品购买数量==" + count);
        //订单总金额
        BigDecimal orderTotalFee = baseOrder.getOrderAmountFee();
        //计费方式模板ID
        Integer costId = baseOrder.getCostId();
        //校验产品可用状态
        if (product == null || product.getStatus() == ProductStatus.DELETED.value) {
            throw new BadRequestException("下单失败，商品不存在。");
        }
        if (product.getStatus() == ProductStatus.OFFSHELF.value) {
            throw new BadRequestException("下单失败，产品已下架。");
        }
        log.debug("产品库存==" + product.getStock());
        //产品模式：1-实物；2-虚拟；3-租赁
        Integer productMode = product.getMode();

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

        //校验产品库存
        if (product.getStock() != -500) {
            if (product.getStock() == null || product.getStock() == 0 || product.getStock() < count) {
                throw new BadRequestException("下单失败，产品库存不足。");
            }
        }

        Integer productActivityId = null;

        //校验订单价格
        if (productMode == ProductModeEnum.VIRTUAL.value) {
            //电子卡券不涉及物流费用和安装费用
            //故，总费用 = 产品价格*订单数量
            BigDecimal amountFee = product.getPrice().multiply(new BigDecimal(count));
            //与订单总价比较，不一致说明价格有误
            if (amountFee.compareTo(orderTotalFee) != 0) {
                throw new BadRequestException("下单失败，订单金额与系统金额不匹配。");
            }
        } else if (productMode == ProductModeEnum.REALTHING.value) {
            BigDecimal productFee = product.getPrice();
            //2020-03-13
            //抢购互动产品校验逻辑
            orderCheckService.checkProductActivity(product, baseOrder, user);
            if (product.getActivityType() != null && product.getActivityType() == ProductActivityType.PANIC_BUYING.value) {
                productActivityId = baseOrder.getProductActivityIdList().get(0);
                //因为上面orderCheckService.checkProductActivity(product, baseOrder);已经执行了，所以这里缓存里已经可以取到值，不必再去数据库中取
                ProductActivityDTO productActivity = redisCache.get(Constant.PRODUCT_ACTIVITY_CACHE + productActivityId, ProductActivityDTO.class);
                //因为上面orderCheckService.checkProductActivity(product, baseOrder);已经做过校验，这里只比较价格即可
                productFee = productActivity.getActivityPrice();
            }
            //实物商品涉及物流费用，商品数量不影响物流费用
            //故，总费用 = 价格*数量+物流费用
            log.debug("产品价格：" + productFee + ",产品数量:" + count + ",订单运费:" + product.getLogisticsFee());
            BigDecimal amountFee = productFee.multiply(new BigDecimal(count)).add(product.getLogisticsFee() == null ? new BigDecimal(0) : product.getLogisticsFee());

            log.debug("后台计算总金额amountFee==" + amountFee);
            log.debug("前端入参总金额orderTotalFee==" + orderTotalFee);
            //与订单总价比较，不一致说明价格有误
            if (amountFee.compareTo(orderTotalFee) != 0) {
                throw new BadRequestException("下单失败，订单金额与系统金额不匹配。");
            }
        } else if (productMode == ProductModeEnum.LEASE.value) {
            if (costId == null) {
                throw new BadRequestException("下单失败，计费方式错误。");
            } else {
                ProductCostDTO productCost = null;
                List<ProductCostDTO> productCostList = product.getProductCostList();
                if (CollectionUtil.isNotEmpty(productCostList)) {
                    for (ProductCostDTO costDTO : productCostList) {
                        //获取计费信息
                        if (Objects.equals(costId, costDTO.getId())) {
                            productCost = costDTO;
                            break;
                        }
                    }
                }
                log.debug("productCost:" + JsonUtil.objectToJson(productCost));
                if (productCost == null) {
                    throw new BadRequestException("下单失败，计费方式错误。");
                }

                log.debug("productCost:" + JsonUtil.objectToJson(productCost));
                //修改： 针对租赁商品，价格取价格模板，其他取产品单价【防止：价格模板和产品价格不一样】 2019-10-11
                //租赁商品涉及安装费用，每件商品都需要支付安装费用
                //故，总费用 = （价格+安装费用）*数量
                log.debug("模板价格：" + productCost.getTotalFee());
                BigDecimal amountFee = productCost.getTotalFee().multiply(new BigDecimal(count));
                log.debug("amountFee：" + amountFee + ",orderTotalFee:" + orderTotalFee);
                //与订单总价比较，不一致说明价格有误
                if (amountFee.compareTo(orderTotalFee) != 0) {
                    throw new BadRequestException("下单失败，订单金额与系统金额不匹配。");
                }
                //订单详情-水机产品计费方式详情设置
                subOrderDetail.setCostId(costId);
                subOrderDetail.setCostName(productCost.getName());
                subOrderDetail.setOpenAccountFee(productCost.getInstallationFee());
                subOrderDetail.setDispatchType(baseOrder.getDispatchType());
                subOrderDetail.setServiceTime(baseOrder.getServiceTime());
            }
        }

        //设置产品信息到订单上
        this.setProductInfoToOrder(product, subOrder, subOrderDetail, productActivityId);
    }

    /**
     * 购买权限校验
     */
    private void buyPermission(UserDTO user, BaseOrder baseOrder, ProductDTO product, DistributorDTO distributor, boolean type) {
        //产品模式：1-实物；2-虚拟；3-租赁
        Integer productMode = product.getMode();
        //产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
        String supplyCode = product.getSupplyCode();
        //用户身份类型
        Integer userType = user.getUserType();

        //①从用户角度校验商品是否可购买
        //type当前用户的上级经销商身份：1、自己，0、上级经销商
//        if (baseOrder.getTerminal() == OrderFrom.DIS_APP.value) {
        if (Objects.equals(supplyCode, ProductSupplyCode.PJXCP.code)) {
            //体验版权限校验
            this.experiencePermission(userType, productMode, distributor, type);
        } else if (Objects.equals(supplyCode, ProductSupplyCode.PZZZG.code)) {
            //站长权限校验
            shopCartService.stationMasterPermission(user);
        } else if (Objects.equals(supplyCode, ProductSupplyCode.PTPSJ.code)) {
            if (userType != UserType.DISTRIBUTOR_DISCOUNT_50.value) {
                throw new BadRequestException("您暂无购买权限，特批经销商只能购买特批产品。");
            }
        } else {
            throw new BadRequestException("暂无此类商品");
        }
//        }
//        else {
//            //健康e家
//            //体验版权限校验
//            this.experiencePermission(userType, productMode, distributor, type);
//            //可购买人群：目前只会有站长可选
////            String buyPermission = product.getBuyPermission();
////            if (buyPermission != null && buyPermission.contains(ProductBuyPermission.M_1.code)) {
//            if (Objects.equals(supplyCode, ProductSupplyCode.PZZZG.code)) {
//                //站长权限校验
//                shopCartService.stationMasterPermission(user);
//            }
//        }
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
        //TODO
        if (userType == UserType.DISTRIBUTOR_50.value) {
            if (productMode != ProductModeEnum.LEASE.value) {
                throw new DialogException("您暂无购买权限，如需购买，请尽快完成升级");
            }
        }
        //上级是体验版经销商
        if (!type && distributor.getRoleLevel() == DistributorRoleLevel.D_50.value) {
            throw new BadRequestException("您的上级经销商暂无购买权限，为不影响您的正常使用，请提醒经销商升级");
        }
    }


    /**
     * 设置产品信息到订单上
     *
     * @param product        商品信息
     * @param subOrder       子订单（设置订单属性）
     * @param subOrderDetail 子订单详细信息
     */
    private void setProductInfoToOrder(ProductDTO product, OrderSub subOrder, SubOrderDetail subOrderDetail, Integer productActivityId) {
        //产品ID
        subOrder.setProductId(product.getId());
        //商品单价
        subOrder.setProductPrice(product.getPrice());
        //商品类型（大类）:1-实物商品；2-电子卡券；3-租赁商品；对应product表的type字段
        subOrder.setProductType(product.getMode());
        //活动类型：1-普通商品，2-折机商品 3-180产品 5-抢购产品
        subOrder.setActivityType(product.getActivityType() != null ? product.getActivityType() : ProductActivityType.PRODUCT_COMMON.value);
        //活动ID
        subOrder.setActivityId(productActivityId);
        //产品栏目名称
        subOrder.setProductModel(product.getCategoryName());
        //订单详情-产品信息设置
        subOrderDetail.setProductId(product.getId());
        subOrderDetail.setProductName(product.getName());
        subOrderDetail.setProductImg(product.getCoverImg());
        subOrderDetail.setProductCompanyId(product.getCompanyId());
        subOrderDetail.setProductCompanyName(product.getCompanyName());
        subOrderDetail.setProductCategoryId(product.getCategoryId());
        subOrderDetail.setProductCategoryName(product.getCategoryName());

        ProductCategoryDTO oneCategory = productFeign.getOneProductCategory(product.getCategoryId());
        if (Objects.nonNull(oneCategory)) {
            subOrderDetail.setProductFirstCategoryId(oneCategory.getId());
            subOrderDetail.setProductFirstCategoryName(oneCategory.getName());
        }

        ProductCategoryDTO twoCategory = productFeign.getTwoProductCategory(product.getCategoryId());
        if (Objects.nonNull(twoCategory)) {
            subOrderDetail.setProductTwoCategoryId(twoCategory.getId());
            subOrderDetail.setProductTwoCategoryName(twoCategory.getName());
        }

        //2020-03-17
        if (product.getActivityType() != null && product.getActivityType() == ProductActivityType.PANIC_BUYING.value) {
            //因为上面orderCheckService.checkProductActivity(product, baseOrder);已经执行了，所以这里缓存里已经可以取到值，不必再去数据库中取
            ProductActivityDTO productActivity = redisCache.get(Constant.PRODUCT_ACTIVITY_CACHE + productActivityId, ProductActivityDTO.class);
            //商品活动价格
            subOrder.setProductPrice(productActivity.getActivityPrice());
        }
    }

    /**
     * 下单-校验经销商信息
     *
     * @param user           用户信息
     * @param subOrder       子订单（设置订单属性）
     * @param subOrderDetail 子订单详细信息
     */
    private void checkDistributor(UserDTO user, BaseOrder baseOrder, ProductDTO product, OrderSub subOrder, SubOrderDetail subOrderDetail) {
        // 1.用户的上级经销商账号是否禁用/禁止下单
        DistributorDTO distributor;
        //type当前用户的上级经销商身份：false、上级经销商;true、自己
        boolean type = false;
        DistributorDTO subDistributor = null;
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
                log.error("经销商不存在有误，不能下单。用户e家号" + user.getId());
                throw new BadRequestException("经销商信息有误，不能下单。");
            }
            distributor = userFeign.getDistributorBasicInfoById(distributorId);
        }
        log.info("*************经销商信息:distributor=" + JsonUtil.objectToJson(distributor) + "*************");
        if (distributor == null) {
            throw new BadRequestException("下单失败，经销商信息不存在。");
        }
        if (distributor.getRoleLevel() == DistributorRoleLevel.D_1000.value) {
            //子账号的信息先记录下，后面设置到订单详情里
            subDistributor = distributor;
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
        this.checkProduct(user, baseOrder, product, subOrder, subOrderDetail, distributor, type);

        Integer productMode = product.getMode();
        //3.校验用户的上级经销商水机产品的配额信息
        if (productMode == ProductModeEnum.LEASE.value) {
            Integer remainingQuota = distributor.getRemainingQuota();
            log.debug("经销商的智能净水设备:" + remainingQuota);
            if (userType != UserType.DISTRIBUTOR_DISCOUNT_50.value) {
                if (remainingQuota == null || remainingQuota <= 0 || baseOrder.getCount() > remainingQuota) {
                    if (type) {
                        throw new DialogException("您的水机配额已不足，为不影响您的正常使用，请尽快前往升级/续费。");
                    } else {
                        throw new BadRequestException("您的上级经销商水机配额已不足，为不影响您的正常使用，请提醒经销商升级/续费。");
                    }
                }
            } else {
                //特批经销商
                Boolean flag = (remainingQuota == null || remainingQuota <= 0 || baseOrder.getCount() > remainingQuota) && (distributor.getReplacementAmount() == null || distributor.getRemainingReplacementAmount().compareTo(baseOrder.getOrderAmountFee()) <= 0);
                if (flag) {
                    if (type) {
                        throw new BadRequestException("您的水机剩余配额/剩余金额不足，不能下单");
                    } else {
                        throw new BadRequestException("您的上级经销商是特批经销商，不能下单。如有疑问，请联系客服。");
                    }
                }
            }
        }

        //下单用户ID
        subOrder.setUserId(user.getId());
        //经销商ID
        subOrder.setDistributorId(distributor.getId());

        //下单用户信息
        subOrderDetail.setUserId(user.getId());
        subOrderDetail.setUserType(userType);
        subOrderDetail.setUserTypeName(UserType.getNameByType(userType));//下单用户等级名称
        subOrderDetail.setUserName(user.getRealName());
        subOrderDetail.setUserPhone(user.getMobile());

        //为设置订单会员信息
        UserDTO vipUser = userFeign.getMySaleUserById(user.getId());
        //设置会员用户是否享受收益标识
        subOrder.setUserSaleFlag(0);
        if (vipUser != null) {
            subOrder.setVipUserId(vipUser.getId());
            //设置会员用户是否享受收益标识
            subOrder.setUserSaleFlag(1);

            subOrderDetail.setVipUserId(vipUser.getId());
            subOrderDetail.setVipUserType(vipUser.getUserType());
            subOrderDetail.setVipUserTypeName(UserType.getNameByType(vipUser.getUserType()));
            subOrderDetail.setVipUserName(vipUser.getRealName());
            subOrderDetail.setVipUserPhone(vipUser.getMobile());
            // subOrderDetail.setVipUserHasIncome(true);
        }

        //订单详情-经销商信息设置
        subOrderDetail.setDistributorId(distributor.getId());
        subOrderDetail.setDistributorAccount(distributor.getUserName());
        subOrderDetail.setDistributorName(distributor.getRealName());
        DistributorRoleLevel distributorRole = DistributorRoleLevel.find(distributor.getRoleLevel());
        subOrderDetail.setDistributorTypeName(distributorRole == null ? "" : distributorRole.name);
        subOrderDetail.setDistributorPhone(distributor.getPhone());
        subOrderDetail.setDistributorProvince(distributor.getProvince());
        subOrderDetail.setDistributorCity(distributor.getCity());
        subOrderDetail.setDistributorRegion(distributor.getRegion());
        //经销商所属区域id设置（站务系统使用）
        subOrderDetail.setDistributorAreaId(distributor.getAreaId());

        if (subDistributor != null) {
            //订单详情-企业版子账号信息设置
            subOrderDetail.setSubDistributorId(subDistributor.getId());
            subOrderDetail.setSubDistributorName(subDistributor.getRealName());
            subOrderDetail.setSubDistributorAccount(subDistributor.getUserName());
            subOrderDetail.setSubDistributorPhone(subDistributor.getPhone());
        }

        //订单详情-经销商推荐人信息设置
        DistributorDTO recommend = userFeign.getRecommendByDistributorId(distributor.getId());
        if (recommend != null) {
            subOrderDetail.setRecommendId(recommend.getId());
            subOrderDetail.setRecommendName(recommend.getRealName());
            subOrderDetail.setRecommendPhone(recommend.getPhone());
            subOrderDetail.setRecommendAccount(recommend.getUserName());
            subOrderDetail.setRecommendProvince(recommend.getProvince());
            subOrderDetail.setRecommendCity(recommend.getCity());
            subOrderDetail.setRecommendRegion(recommend.getRegion());
        }

        //订单详情-销售主体信息设置
        if (productMode == ProductModeEnum.LEASE.value) {
            //租赁商品，销售主体为翼猫总部
            subOrderDetail.setSalesSubjectName("翼猫科技发展（上海）有限公司");
            subOrderDetail.setSalesSubjectCompanyName("翼猫科技发展（上海）有限公司");
            subOrderDetail.setSalesSubjectProvince("上海市");
            subOrderDetail.setSalesSubjectCity("上海市");
            subOrderDetail.setSalesSubjectRegion("总部区");
        } else if (productMode == ProductModeEnum.REALTHING.value || productMode == ProductModeEnum.VIRTUAL.value && Objects.equals(subOrderDetail.getProductCategoryName(), HraType.Y.value)) {
            //实物商品或Y卡，销售主体为经销商所在服务站
            String province = distributor.getProvince();
            String city = distributor.getCity();
            String region = distributor.getRegion();
            if (StringUtil.isNotEmpty(province) && StringUtil.isNotEmpty(city) && StringUtil.isNotEmpty(region)) {
                StationDTO station = systemFeign.getStationByPRC(distributor.getProvince(), distributor.getCity(), distributor.getRegion(),PermissionTypeEnum.PRE_SALE.value);
                if (station != null) {
                    subOrderDetail.setSalesSubjectName(station.getName());
                    subOrderDetail.setSalesSubjectCompanyName(station.getStationCompanyName());
                    subOrderDetail.setSalesSubjectProvince(station.getProvince());
                    subOrderDetail.setSalesSubjectCity(station.getCity());
                    subOrderDetail.setSalesSubjectRegion(station.getRegion());
                }
            }
        }
    }

    /**
     * 下单-校验安装工信息
     *
     * @param productMode    产品类型
     * @param dispatchType   派单方式：1-手动派单；2-自动派单；
     * @param engineerId     安装工ID
     * @param subOrder       子订单（设置订单属性）
     * @param subOrderDetail 子订单详细信息
     */
    private void checkEngineer(Integer productMode, Integer dispatchType, Integer engineerId, Integer addressId, OrderSub subOrder, SubOrderDetail subOrderDetail, Integer subType, Integer addressType) {
        if (productMode == ProductModeEnum.LEASE.value || productMode == ProductModeEnum.REALTHING.value) {

            //查询收货地址 评估卡不需要校验
//            UserAddressDTO address = userFeign.getUserAddressById(addressId);
//            Object addressByType = userFeign.getAddressByType(addressId, subType);
            UserAddressDTO address = null;
            try {
                address = this.parseUserAddressInfo(subType, addressType, addressId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (address == null) {
                throw new YimaoException("下单失败,收货地址不存在!");
            }
            //校验服务站
            log.info("收货地址:" + address.getProvince() + "," + address.getCity() + "," + address.getRegion());
            StationDTO station = systemFeign.getStationByPRC(address.getProvince(), address.getCity(), address.getRegion(),PermissionTypeEnum.AFTER_SALE.value);

            //针对租赁商品
            if (productMode == ProductModeEnum.LEASE.value) {
                //校验水机派单方式的选择
                if (dispatchType == null || DispatchType.find(dispatchType) == null) {
                    throw new BadRequestException("下单失败，派单方式错误。");
                }
                if (dispatchType == DispatchType.OWN_SELECT.value && engineerId == null) {
                    throw new BadRequestException("下单失败，手动派单时，需要选择一位安装工程师。");
                }
                
                //所选择区域服务站不存在或者服务站未上线,都提示[所选收货区域内暂时没有服务站,无法安装,请重选收货地址！]
                if (station == null||station.getOnline()==null||station.getOnline()==0) {
                    throw new YimaoException("所选收货区域内暂时没有服务站,无法安装,请重选收货地址！");
                }

                //根据省市区查询安装工是否存在
                String province=address.getProvince();
                String city=address.getCity();
                String region=address.getRegion();
                Integer areaId=systemFeign.getRegionIdByPCR(province, city, region);
                boolean exists = userFeign.checkEngineerExistsByArea(areaId);
                if (!exists) {
                    throw new YimaoException("该地区的服务站没有安装工,不能下单!");
                }

                //手动选择安装工
                if (dispatchType == DispatchType.OWN_SELECT.value) {
                    //校验安装工信息
                    EngineerDTO engineer = userFeign.getEngineerById(engineerId);
                    if (engineer == null || engineer.getForbidden()) {
                        log.error("下单失败，安装工程师已禁用");
                        throw new BadRequestException("下单失败，安装工程师信息有误。");
                    }
                    
                    //判断安装工的服务站id是否与客户的服务站相同
                    if (engineer.getStationId()!=null && !engineer.getStationId().equals(station.getId())) {
                        log.error("下单失败，安装工服务站与客服服务站不匹配");
                        throw new BadRequestException("单失败，安装工服务站与客服服务站不匹配");
                    }

                    if (Objects.nonNull(subOrder.getEngineerId())) {
                        List<EngineerDTO> engineers = userFeign.listEngineerByRegion(areaId);
                        if (CollectionUtil.isNotEmpty(engineers)) {
                            boolean flag = false;
                            for (EngineerDTO e : engineers) {
                                if (Objects.equals(e.getId(), subOrder.getEngineerId())) {
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag) {
                                throw new YimaoException("安装工距离太远不能服务此区域,请重新选择安装工!");
                            }
                        }
                    }

                    //安装工ID
                    subOrder.setEngineerId(engineerId);
                    //订单详情-安装工程师信息设置
                    subOrderDetail.setMongoEngineerId(engineer.getOldId());
                    subOrderDetail.setEngineerId(engineer.getId());
                    subOrderDetail.setEngineerName(engineer.getRealName());
                    subOrderDetail.setEngineerIdCard(engineer.getIdCard());
                    subOrderDetail.setEngineerPhone(engineer.getPhone());
                    subOrderDetail.setEngineerProvince(engineer.getProvince());
                    subOrderDetail.setEngineerCity(engineer.getCity());
                    subOrderDetail.setEngineerRegion(engineer.getRegion());
                }
            }

            //修改 实物产品设置服务站公司
            if (station != null) {
                //子订单服务站ID设置
                subOrder.setStationId(station.getId());
                //订单详情-服务站信息设置
                subOrderDetail.setStationId(station.getId());
                subOrderDetail.setStationName(station.getName());
                subOrderDetail.setStationPhone(station.getContactPhone());
                subOrderDetail.setStationProvince(station.getProvince());
                subOrderDetail.setStationCity(station.getCity());
                subOrderDetail.setStationRegion(station.getRegion());
            }

            //获取区县级公司信息   -》修改：未指定安装工【安装工的省市区=收货地址的省市区】
            String province=address.getProvince();
            String city=address.getCity();
            String region=address.getRegion();
            StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.AFTER_SALE.value);
            if (stationCompany != null) {
                //子订单区县级公司ID设置
                subOrder.setStationCompanyId(stationCompany.getId());
                //订单详情-区县级公司信息设置
                subOrderDetail.setStationCompanyId(stationCompany.getId());
                subOrderDetail.setStationCompanyName(stationCompany.getName());
                subOrderDetail.setStationCompanyProvince(stationCompany.getProvince());
                subOrderDetail.setStationCompanyCity(stationCompany.getCity());
                subOrderDetail.setStationCompanyRegion(stationCompany.getRegion());
            }
        }
    }

    /**
     * 下单-校验收货地址信息
     *
     * @param addressId      收货地址ID
     * @param subOrderDetail 订单详情
     * @param orderAddress   收货地址
     */
    private void checkAddress(Integer addressId, SubOrderDetail subOrderDetail, Integer subType, Integer addressType, OrderAddress orderAddress) {
        if (addressId == null) {
            throw new BadRequestException("下单失败，请选择收货地址。");
        }
        UserAddressDTO dto = null;
        //下单类型 1-为自己下单 2-为客户下单
        if (subType == null) {
            subType = DistributorCreateOrderType.OWN.value;
        }

        try {
            //如果是企业客户  addressType 1-个人客户 2-企业客户
            dto = this.parseUserAddressInfo(subType, addressType, addressId);
        } catch (Exception e) {
            log.error("收货地址转换出错");
            e.printStackTrace();
        }
        if (null == dto) {
            throw new BadRequestException("下单失败，请选择收货地址！");
        }

        //设置订单地址信息
        this.setOrderAddress(subOrderDetail, orderAddress, dto);
    }

    /**
     * 对客户地址进行转换
     *
     * @param subType     下单类型 1-自己下单 2-给客户下单
     * @param addressType 地址类型 1-个人客户地址 2-企业客户地址
     * @param addressId   地址ID
     * @return
     */
    private UserAddressDTO parseUserAddressInfo(Integer subType, Integer addressType, Integer addressId) throws Exception {
        UserAddressDTO dto = null;
        //如果是企业客户  addressType 1-个人客户 2-企业客户
        if (subType == 2 && addressType != null && addressType == 2) {
            subType = 3;
        }
        Object object = userFeign.getAddressByType(addressId, subType);
        if (Objects.nonNull(object)) {
            Map map = JSONObject.parseObject(JSONObject.toJSONString(object), Map.class);
            if (map != null && map.size() > 0) {
                String address = map.get("address") + "";
                if (subType == 1) {
                    JSONObject jsStr = JSONObject.parseObject(address);
                    dto = JSONObject.toJavaObject(jsStr, UserAddressDTO.class);

                } else if (subType == 2) {
                    dto = new UserAddressDTO();
                    JSONObject jsStr = JSONObject.parseObject(address);
                    PersonCustomerDTO personCustomerDTO = JSONObject.toJavaObject(jsStr, PersonCustomerDTO.class);
                    BeanCopier copier = BeanCopier.create(PersonCustomerDTO.class, UserAddressDTO.class, false);
                    copier.copy(personCustomerDTO, dto, null);
                } else if (subType == 3) {
                    dto = new UserAddressDTO();
                    JSONObject jsStr = JSONObject.parseObject(address);
                    CustomerAddressDTO customerAddressDTO = JSONObject.toJavaObject(jsStr, CustomerAddressDTO.class);
                    BeanCopier copier = BeanCopier.create(CustomerAddressDTO.class, UserAddressDTO.class, false);
                    copier.copy(customerAddressDTO, dto, null);
                }
            }
        }
        return dto;
    }

    /**
     * 设置订单收货地址信息
     *
     * @param subOrderDetail 订单详情
     * @param orderAddress   订单地址
     * @param dto            用户地址
     */
    private void setOrderAddress(SubOrderDetail subOrderDetail, OrderAddress orderAddress, UserAddressDTO dto) {
        subOrderDetail.setAddresseeName(dto.getContact());
        subOrderDetail.setAddresseePhone(dto.getMobile());
        subOrderDetail.setAddresseeProvince(dto.getProvince());
        subOrderDetail.setAddresseeCity(dto.getCity());
        subOrderDetail.setAddresseeRegion(dto.getRegion());
        subOrderDetail.setAddresseeStreet(dto.getStreet());
        subOrderDetail.setAddresseeSex(dto.getSex());

        orderAddress.setContact(dto.getContact());
        orderAddress.setPhone(dto.getMobile());
        orderAddress.setSex(dto.getSex());
        orderAddress.setProvince(dto.getProvince());
        orderAddress.setCity(dto.getCity());
        orderAddress.setRegion(dto.getRegion());
        orderAddress.setStreet(dto.getStreet());
    }


    /**
     * 保存主订单
     *
     * @param orderInfo 订单信息
     * @param userId    用户ID
     */
    private OrderMain saveMainOrder(OrderDTO orderInfo, Integer userId) {
        //创建主订单
        OrderMain orderMain = new OrderMain();
        //主订单ID
        orderMain.setId(UUIDUtil.buildMainOrderId());
        //数量
        orderMain.setCount(orderInfo.getCount());
        //订单类型：1-普通订单  2-续费订单
        orderMain.setType(orderInfo.getType());
        //子订单类型：1-为自己下单；2-为客户下单
        orderMain.setSubType(orderInfo.getSubType() == null ? 1 : orderInfo.getSubType());
        //用户ID
        orderMain.setUserId(userId);
        //商品总价
        orderMain.setProductAmountFee(orderInfo.getProductAmountFee() == null ? orderInfo.getOrderAmountFee() : orderInfo.getProductAmountFee());
        //订单总价
        orderMain.setOrderAmountFee(orderInfo.getOrderAmountFee());
        //运费
        orderMain.setLogisticsFee(orderInfo.getLogisticsFee() == null ? new BigDecimal(0) : orderInfo.getLogisticsFee());
        //支付类型：1-微信；2-支付宝；3-POS机；4-转账；
        if (orderInfo.getPayType() != null && orderInfo.getPayType() != PayType.WECHAT.value && orderInfo.getPayType() != PayType.ALIPAY.value) {
            orderMain.setPayType(orderInfo.getPayType());
        }
        //是否支付：0-未支付，1-已支付
        orderMain.setPay(false);
        //是否他人代付
        orderMain.setIsReplacePay(orderInfo.getIsReplacePay() == null ? false : orderInfo.getIsReplacePay());
        Date now = new Date();
        //创建时间
        orderMain.setCreateTime(now);
        //更新时间
        orderMain.setUpdateTime(now);
        int i = orderMainMapper.insertSelective(orderMain);
        if (i < 1) {
            throw new BadRequestException("下单失败，创建支付订单时遇到错误。");
        }
        return orderMain;
    }

    /**
     * 保存子订单
     *
     * @param mainOrder      主订单
     * @param baseOrder      订单信息
     * @param subOrderDetail 子订单详细信息
     * @param orderAddress   订单收货地址信息
     */
    private void saveSubOrder(OrderMain mainOrder, BaseOrder baseOrder, OrderSub subOrder, SubOrderDetail subOrderDetail, OrderAddress orderAddress, UserDTO user, Integer productModel) {
        if (!Constant.PRO_ENVIRONMENT) {
            log.info("mainOrder={}", JSONObject.toJSONString(mainOrder));
            log.info("subOrder={}", JSONObject.toJSONString(subOrder));
            log.info("subOrderDetail={}", JSONObject.toJSONString(subOrderDetail));
            log.info("orderAddress={}", JSONObject.toJSONString(orderAddress));
        }

        //创建子订单
        //主订单号
        subOrder.setMainOrderId(mainOrder.getId());
        //订单号，按照订单号规则生成
        subOrder.setId(UUIDUtil.buildOrderId(subOrder.getProductType()));
        //订单运费金额（子订单不需要设置运费）
        subOrder.setLogisticsFee(new BigDecimal(0));
        //状态：0-待付款；1-待审核；2-待发货；3-待出库；4-待收货；5-交易成功；6-售后中；7-交易关闭；8-已取消；
        subOrder.setStatus(0);

        //如果是货到付款，状态为待发货[未支付]
        log.info("=============保存子订单====================" + subOrder.getPayTerminal());
        if (Objects.nonNull(subOrder.getPayTerminal()) && subOrder.getPayTerminal() == 2) {
            subOrder.setStatus(2);
        }
        //折机商品 -> 直接设置为待发货[未支付]
        if (Objects.nonNull(subOrder.getActivityType()) && Objects.equals(subOrder.getActivityType(), 2)) {
            subOrder.setStatus(2);
        }

        //是否支付
        subOrder.setPay(false);
        //未支付
        subOrder.setPayStatus(1);
        //是否他人代付
        subOrder.setIsReplacePay(baseOrder.getIsReplacePay() == null ? false : baseOrder.getIsReplacePay());
        //支付类型：1-微信；2-支付宝；3-POS机；4-转账；
        if (baseOrder.getPayType() != null && baseOrder.getPayType() != PayType.WECHAT.value && baseOrder.getPayType() != PayType.ALIPAY.value) {
            subOrder.setPayType(baseOrder.getPayType());
        }
        //所购买的商品数量
        subOrder.setCount(baseOrder.getCount());
        //订单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；
        subOrder.setTerminal(baseOrder.getTerminal());
        //收货地址ID
        subOrder.setAddressId(baseOrder.getAddressId());
        //是否删除
        subOrder.setDeleted(false);
        if (subOrder.getProductType() == ProductModeEnum.VIRTUAL.value
                && StringUtil.isNotEmpty(subOrder.getRefer())
                && subOrder.getRefer().startsWith("M")) {
            subOrder.setDeleted(true);
        }
        //备注
        subOrder.setRemark(baseOrder.getRemark());
        Date now = new Date();
        subOrder.setCreateTime(now);


        boolean isDiscount = subOrder.getProductType() == ProductModeEnum.LEASE.value
                && Objects.nonNull(subOrderDetail.getUserType())
                && subOrderDetail.getUserType() == UserType.DISTRIBUTOR_DISCOUNT_50.value
                && subOrder.getActivityType() == ProductActivityType.PRODUCT_CONVERT.value;
        if (isDiscount) {
            //是否支付
            subOrder.setPay(true);
            //未支付
            subOrder.setPayStatus(PayStatus.PAY.value);
            //支付类型：1-微信；2-支付宝；3-POS机；4-转账；
            subOrder.setPayType(null);
            subOrder.setPayTime(now);

            OrderMain updateMainOrder = new OrderMain();
            updateMainOrder.setId(mainOrder.getId());
            updateMainOrder.setPay(true);
            updateMainOrder.setPayType(null);
            updateMainOrder.setPayTime(now);
            orderMainMapper.updateByPrimaryKeySelective(updateMainOrder);
        }

        //保存子订单信息
        log.info("===前端传递的订单信息为2===" + JSON.toJSONString(baseOrder));
        log.info("===保存的子订单信息为===" + JSON.toJSONString(subOrder));
        int i = orderSubMapper.insertSelective(subOrder);
        if (i < 0) {
            throw new BadRequestException("下单失败，创建订单时遇到错误。");
        }

        //保存子订单详细信息
        subOrderDetail.setSubOrderId(subOrder.getId());
        subOrderDetail.setCreateTime(subOrder.getCreateTime());
        i = subOrderDetailMapper.insertSelective(subOrderDetail);
        if (i < 0) {
            throw new BadRequestException("下单失败，创建订单时遇到错误。");
        }

        //如果是货到付款/折机水机 -> 直接生成工单
        if (subOrder.getProductType() == ProductModeEnum.LEASE.value) {
            //货到付款 || 下单用户是折机版经销商下的也是水机
            boolean flag = subOrder.getPayTerminal() == PayTerminal.USER.value || isDiscount;
            if (flag) {
                //货到付款，直接创建工单
                this.createWorkOrderBySubOrder(subOrder);
                //货到付款直接创建收益分配
                log.info("支付完成进行收益分配，订单号：{}", subOrder.getId());
                productIncomeRecordService.allotSellIncome(subOrder.getId());
            }
        }

        //保存子订单收货地址信息
        if (orderAddress != null && StringUtil.isNotEmpty(orderAddress.getContact())) {
            orderAddress.setId(subOrder.getId());
            orderAddress.setCreateTime(subOrder.getCreateTime());
            i = orderAddressMapper.insertSelective(orderAddress);
            if (i < 0) {
                throw new BadRequestException("下单失败，创建订单时遇到错误。");
            }
        }

        //减库存
        if (Objects.equals(productModel, ProductModeEnum.LEASE.value)) {
            try {
                //加锁成功
                redisLock.lock(String.valueOf(subOrder.getDistributorId()));

                DistributorDTO distributorDTO = userFeign.getDistributorBasicInfoById(subOrder.getDistributorId());
                //查库存
                if (distributorDTO == null) {
                    log.error("经销商信息不存在");
                    throw new BadRequestException("经销商信息有误，不能下单");
                }

                //如果是折机版经销商
                if (Objects.equals(distributorDTO.getRoleLevel(), DistributorRoleLevel.DISCOUNT.value)) {
                    if (distributorDTO.getRemainingQuota() != null && distributorDTO.getRemainingQuota() > 0) {
                        quotaChangeRecordService.quotaChange(subOrder.getId(), subOrder.getDistributorId(), "下单减配额", 1, -1, null);
                    } else if (Objects.nonNull(distributorDTO.getRemainingReplacementAmount()) && distributorDTO.getRemainingReplacementAmount().compareTo(mainOrder.getOrderAmountFee()) >= 0) {
                        quotaChangeRecordService.quotaChange(subOrder.getId(), subOrder.getDistributorId(), "下单减金额", 1, 0, mainOrder.getOrderAmountFee());
                    } else {
                        throw new BadRequestException("经销商剩余配额/剩余金额不足，不能下单");
                    }
                } else {
                    if (distributorDTO.getRemainingQuota() == null || distributorDTO.getRemainingQuota() < 1) {
                        throw new BadRequestException("经销商配额不足，不能下单");
                    }
                    quotaChangeRecordService.quotaChange(subOrder.getId(), subOrder.getDistributorId(), "下单减配额", 1, -1, null);
                }
            } catch (Exception e) {
                log.error("订单创建-减库存失败", mainOrder.getId() + "," + e.getMessage());
                String content = "订单创建-减库存失败,主订单号：" + mainOrder.getId();
                String subject = "订单创建成功-减库存失败===" + domainProperties.getApi();
                mailSender.send(null, subject, content);
                throw new YimaoException("订单创建-减库存失败");
            } finally {
                //解锁
                redisLock.unLock(String.valueOf(subOrder.getDistributorId()));
            }
        }
    }

    /**
     * M卡下单
     *
     * @param orderInfo 前端传递的订单信息
     * @param userId    下单用户ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public OrderMainDTO createHRAOrder(OrderDTO orderInfo, Integer userId) {
        log.info("*************M卡下单,传入参数:" + JsonUtil.objectToJson(orderInfo) + "*************");
        //订单基本信息校验
        this.checkHRAOrder(orderInfo);
        String ticketNo = orderInfo.getTicketNo();
        if (StringUtil.isBlank(ticketNo)) {
            throw new BadRequestException("请选择优惠卡");
        }

        UserDTO currentUser = userFeign.getBasicUserById(userCache.getUserId());
        if (currentUser == null) {
            currentUser = userFeign.getUserById(userId);
        }
        if (currentUser == null) {
            throw new BadRequestException("下单失败，未获取到用户信息。");
        }

        HraTicketDTO hraTicket = hraFeign.getTicketByNo(ticketNo);
        if (hraTicket == null) {
            throw new BadRequestException("该优惠卡不存在");
        }
        if (hraTicket.getTicketStatus() != HraTicketStatusEnum.HRA_STOP.value) {
            throw new BadRequestException("该优惠卡不可以购买");
        }
        ProductDTO product = productFeign.getProductById(hraTicket.getProductId());
        if (product == null) {
            throw new BadRequestException("产品为空");
        }
        if (product.getStatus() == ProductStatus.DELETED.value) {
            throw new BadRequestException("该产品已被下架，不能购买。");
        }

        //校验订单价格和产品价格是否匹配
        BigDecimal productAmountFee = product.getPrice().multiply(new BigDecimal(orderInfo.getCount()));
        BigDecimal orderAmountFee = orderInfo.getOrderAmountFee();
        if (orderAmountFee.compareTo(productAmountFee) != 0) {
            throw new BadRequestException("订单金额与实际支付金额不一致！");
        }

        OrderDTO order = this.createOrderDTO(orderInfo);
        order.setProductAmountFee(productAmountFee);

        //创建主订单（支付订单）
        OrderMain orderMain = this.saveMainOrder(order, userId);

        //订单详情设置下单用户信息
        SubOrderDetail subOrderDetail = new SubOrderDetail();
        subOrderDetail.setUserId(userId);
        subOrderDetail.setUserName(currentUser.getRealName());
        subOrderDetail.setUserPhone(currentUser.getMobile());
        subOrderDetail.setUserType(currentUser.getUserType());
        subOrderDetail.setUserTypeName(UserType.getNameByType(currentUser.getUserType()));

        OrderSub subOrder = new OrderSub();
        //下单用户
        subOrder.setUserId(userId);
        //设置产品信息到订单上
        this.setProductInfoToOrder(product, subOrder, subOrderDetail, null);
        //订单和评估券关联设置
        subOrder.setRefer(ticketNo);

        //订单金额
        subOrder.setFee(order.getOrderAmountFee());
        this.checkDistributor(currentUser, 0, subOrder, subOrderDetail);
        this.saveSubOrder(orderMain, order, subOrder, subOrderDetail, null, currentUser, product.getMode());

        //HRA支付之前统一把订单设置为删除状态，支付之后再设置回未删除
        orderMain.setDeleted(true);
        orderMainMapper.updateByPrimaryKeySelective(orderMain);

        OrderMainDTO mainDTO = new OrderMainDTO();
        orderMain.convert(mainDTO);
        return mainDTO;
    }


    /**
     * M卡批量下单
     *
     * @param orderInfo 前端传递的订单信息
     * @param userId    下单用户ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public OrderMainDTO batchCreateHRAOrder(OrderDTO orderInfo, Integer userId) {
        UserDTO currentUser = userFeign.getBasicUserById(userCache.getUserId());
        if (currentUser == null) {
            currentUser = userFeign.getUserById(userId);
        }
        if (currentUser == null) {
            throw new BadRequestException("下单失败，未获取到用户信息。");
        }

        //订单基本信息校验
        this.checkHRAOrder(orderInfo);

        Integer count = orderInfo.getCount();
        List<HraTicketDTO> ticketList = hraFeign.listTicketForPay(userId, count);
        if (CollectionUtil.isEmpty(ticketList)) {
            throw new BadRequestException("您没有待支付的优惠卡");
        }
        if (ticketList.size() != count) {
            throw new BadRequestException("拥有优惠卡数量和实际支付的优惠卡数量不一致");
        }
        if (ticketList.get(0).getProductId() == null) {
            throw new BadRequestException("未能通过优惠卡找到相关产品信息");
        }

        ProductDTO product = productFeign.getProductById(ticketList.get(0).getProductId());
        if (product == null) {
            throw new BadRequestException("产品为空");
        }
        if (product.getStatus() == ProductStatus.DELETED.value) {
            throw new BadRequestException("该产品已被下架，不能购买。");
        }

        //校验订单价格和产品价格是否匹配
        BigDecimal productAmountFee = product.getPrice().multiply(new BigDecimal(count));
        BigDecimal orderAmountFee = orderInfo.getOrderAmountFee();
        if (orderAmountFee.compareTo(productAmountFee) != 0) {
            throw new BadRequestException("订单金额与实际支付金额不一致！");
        }

        OrderDTO order = this.createOrderDTO(orderInfo);
        order.setProductAmountFee(productAmountFee);

        //创建主订单（支付订单）
        OrderMain orderMain = this.saveMainOrder(order, userId);

        for (HraTicketDTO ticket : ticketList) {
            OrderSub subOrder = new OrderSub();
            SubOrderDetail subOrderDetail = new SubOrderDetail();
            //下单用户
            subOrder.setUserId(userId);
            //设置产品信息到订单上
            this.setProductInfoToOrder(product, subOrder, subOrderDetail, null);
            //订单和评估券关联设置
            subOrder.setRefer(ticket.getTicketNo());

            order.setCount(1);
            //订单金额
            subOrder.setFee(ticket.getTicketPrice());
            this.checkDistributor(currentUser, 0, subOrder, subOrderDetail);
            this.saveSubOrder(orderMain, order, subOrder, subOrderDetail, null, currentUser, product.getMode());
        }

        //HRA支付之前统一把订单设置为删除状态，支付之后再设置回未删除
        orderMain.setDeleted(true);
        orderMainMapper.updateByPrimaryKeySelective(orderMain);

        OrderMainDTO mainDTO = new OrderMainDTO();
        orderMain.convert(mainDTO);
        return mainDTO;
    }

    /**
     * 构建创建HRA订单时所需的信息
     *
     * @param orderInfo 前端传递的订单信息
     */
    private OrderDTO createOrderDTO(OrderDTO orderInfo) {
        OrderDTO order = new OrderDTO();
        order.setCount(orderInfo.getCount());
        //订单类型：1-普通订单；2-续费订单（水机续费时传2，其它一律传1）
        order.setType(1);
        //经销商下单类型：1-为自己下单；2-为客户下单（经销商为客户下单时传2，其它一律传1）
        order.setSubType(1);
        order.setOrderAmountFee(orderInfo.getOrderAmountFee());
        order.setLogisticsFee(new BigDecimal(0));
        //支付方式：1-微信；2-支付宝；3-其他
        order.setPayType(orderInfo.getPayType());
        //支付类型：1-立即支付；2-货到付款
        order.setPayTerminal(PayTerminal.DEALER.value);
        //终端：1-用户终端APP；2-微信公众号；3-经销商APP；4-小猫店小程序；
        order.setTerminal(orderInfo.getTerminal());
        order.setRemark("HRA评估券订单");
        return order;
    }

    /**
     * HRA评估券下单校验参数
     *
     * @param orderInfo 前端传递的订单信息
     */
    private void checkHRAOrder(OrderDTO orderInfo) {
        if (orderInfo.getOrderAmountFee() == null || new BigDecimal(0).compareTo(orderInfo.getOrderAmountFee()) > 0) {
            throw new BadRequestException("下单失败，订单总金额有误。");
        }
        //校验订单数量
        if (orderInfo.getCount() == null || orderInfo.getCount() < 1) {
            throw new BadRequestException("下单失败，订单产品数量有误。");
        }
        //校验支付类型
        if (orderInfo.getPayType() == null || PayType.find(orderInfo.getPayType()) == null) {
            throw new BadRequestException("下单失败，支付类型参数错误。");
        }
    }


    /**
     * HRA-校验经销商信息
     *
     * @param user           用户信息
     * @param count          购买数量
     * @param subOrder       子订单（设置订单属性）
     * @param subOrderDetail 子订单详细信息
     */
    private void checkDistributor(UserDTO user, Integer count, OrderSub subOrder, SubOrderDetail subOrderDetail) {
        DistributorDTO distributor;
        DistributorDTO subDistributor = null;
        Integer userType = user.getUserType();
        if (userType == UserType.USER_4.value) {
            //普通用户下单，获取系统配置的默认经销商
            UserIncomeAccountDTO incomeAccount = userFeign.getIncomeAccount();
            if (Objects.isNull(incomeAccount) || Objects.isNull(incomeAccount.getDistributorId())) {
                log.error("下单失败，未获取系统配置的默认经销商。");
                throw new YimaoException("下单失败，未获取系统配置的默认经销商。");
            }
            distributor = userFeign.getDistributorBasicInfoById(incomeAccount.getDistributorId());
            if (Objects.isNull(distributor)) {
                log.error("默认经销商不存在，不能下单。");
                throw new YimaoException("默认经销商不存在，不能下单。");
            }
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
        log.info("*************经销商信息:distributor=" + JsonUtil.objectToJson(distributor) + "*************");
        if (distributor == null) {
            throw new BadRequestException("下单失败，经销商信息不存在。");
        }

        if (Objects.isNull(distributor.getRoleLevel())) {
            log.error("下单失败，该经销商没有角色等级");
            throw new BadRequestException("下单失败，暂不支持代理商的客户下单。");
        }

        if (distributor.getRoleLevel() == DistributorRoleLevel.D_50.value) {
            throw new BadRequestException("下单失败，暂不支持体验版经销商的客户下单，请提醒您的经销商升级后再下单。");
        }

        if (distributor.getRoleLevel() == DistributorRoleLevel.D_1000.value) {
            //子账号的信息先记录下，后面设置到订单详情里
            subDistributor = distributor;
            //获取企业版主账号
            distributor = userFeign.getDistributorBasicInfoById(distributor.getPid());
            if (distributor == null) {
                throw new BadRequestException("下单失败，经销商信息不存在。");
            }
        }

        Integer productMode = subOrder.getProductType();

        //校验水机产品的经销商相关信息
        if (productMode == ProductModeEnum.LEASE.value) {
            //TODO 经销商产品购买权限校验
            //TODO 服务站产品购买权限校验
            Integer remainingQuota = distributor.getRemainingQuota();
            log.debug("经销商的智能净水设备:" + remainingQuota);

            if ((userType != UserType.DISTRIBUTOR_DISCOUNT_50.value)) {
                if (remainingQuota == null || remainingQuota <= 0 || count > remainingQuota) {
                    throw new BadRequestException("下单失败，经销商的智能净水设备不足。");
                }
            } else {
                if (!Objects.equals(distributor.getUserId(), user.getId())) {
                    throw new BadRequestException("您的上级经销商是特批经销商，不能下单。如有疑问，请联系客服。");
                }
                Boolean flag = (remainingQuota == null || remainingQuota <= 0 || subOrder.getCount() > remainingQuota) && (distributor.getRemainingReplacementAmount() == null || distributor.getRemainingReplacementAmount().compareTo(subOrder.getFee()) <= 0);
                if (flag) {
                    throw new BadRequestException("下单失败，经销商的智能净水设备不足。");
                }
            }
            if (distributor.getForbidden() || distributor.getForbiddenOrder()) {
                throw new BadRequestException("下单失败，您的经销商暂不能下单。");
            }
        }

        //下单用户ID
        subOrder.setUserId(user.getId());
        //经销商ID
        subOrder.setDistributorId(distributor.getId());

        //下单用户信息
        subOrderDetail.setUserId(user.getId());
        subOrderDetail.setUserType(userType);
        subOrderDetail.setUserTypeName(UserType.getNameByType(userType));//下单用户等级名称
        subOrderDetail.setUserName(user.getRealName());
        subOrderDetail.setUserPhone(user.getMobile());

        //为设置订单会员信息
        UserDTO vipUser = userFeign.getMySaleUserById(user.getId());
        //设置会员用户是否享受收益标识
        subOrder.setUserSaleFlag(0);
        if (vipUser != null) {
            subOrder.setVipUserId(vipUser.getId());
            //设置会员用户是否享受收益标识
            subOrder.setUserSaleFlag(1);

            subOrderDetail.setVipUserId(vipUser.getId());
            subOrderDetail.setVipUserType(vipUser.getUserType());
            subOrderDetail.setVipUserTypeName(UserType.getNameByType(vipUser.getUserType()));
            subOrderDetail.setVipUserName(vipUser.getRealName());
            subOrderDetail.setVipUserPhone(vipUser.getMobile());
            // subOrderDetail.setVipUserHasIncome(true);
        }

        //订单详情-经销商信息设置
        subOrderDetail.setDistributorId(distributor.getId());
        subOrderDetail.setDistributorAccount(distributor.getUserName());
        subOrderDetail.setDistributorName(distributor.getRealName());
        DistributorRoleLevel distributorRole = DistributorRoleLevel.find(distributor.getRoleLevel());
        subOrderDetail.setDistributorTypeName(distributorRole == null ? "" : distributorRole.name);
        subOrderDetail.setDistributorPhone(distributor.getPhone());
        subOrderDetail.setDistributorProvince(distributor.getProvince());
        subOrderDetail.setDistributorCity(distributor.getCity());
        subOrderDetail.setDistributorRegion(distributor.getRegion());


        if (subDistributor != null) {
            //订单详情-企业版子账号信息设置
            subOrderDetail.setSubDistributorId(subDistributor.getId());
            subOrderDetail.setSubDistributorName(subDistributor.getRealName());
            subOrderDetail.setSubDistributorAccount(subDistributor.getUserName());
            subOrderDetail.setSubDistributorPhone(subDistributor.getPhone());
        }

        //订单详情-经销商推荐人信息设置
        DistributorDTO recommend = userFeign.getRecommendByDistributorId(distributor.getId());
        if (recommend != null) {
            subOrderDetail.setRecommendId(recommend.getId());
            subOrderDetail.setRecommendName(recommend.getRealName());
            subOrderDetail.setRecommendPhone(recommend.getPhone());
            subOrderDetail.setRecommendAccount(recommend.getUserName());
            subOrderDetail.setRecommendProvince(recommend.getProvince());
            subOrderDetail.setRecommendCity(recommend.getCity());
            subOrderDetail.setRecommendRegion(recommend.getRegion());
        }

        //订单详情-销售主体信息设置
        if (productMode == ProductModeEnum.LEASE.value) {
            //租赁商品，销售主体为翼猫总部
            subOrderDetail.setSalesSubjectName("翼猫科技发展（上海）有限公司");
            subOrderDetail.setSalesSubjectCompanyName("翼猫科技发展（上海）有限公司");
            subOrderDetail.setSalesSubjectProvince("上海市");
            subOrderDetail.setSalesSubjectCity("上海市");
            subOrderDetail.setSalesSubjectRegion("总部区");
        } else if (productMode == ProductModeEnum.REALTHING.value || productMode == ProductModeEnum.VIRTUAL.value && Objects.equals(subOrderDetail.getProductCategoryName(), HraType.Y.value)) {
            //实物商品或Y卡，销售主体为经销商所在服务站
            String province = distributor.getProvince();
            String city = distributor.getCity();
            String region = distributor.getRegion();
            if (StringUtil.isNotEmpty(province) && StringUtil.isNotEmpty(city) && StringUtil.isNotEmpty(region)) {
                StationDTO station = systemFeign.getStationByPRC(distributor.getProvince(), distributor.getCity(), distributor.getRegion(),PermissionTypeEnum.PRE_SALE.value);
                if (station != null) {
                    subOrderDetail.setSalesSubjectName(station.getName());
                    subOrderDetail.setSalesSubjectCompanyName(station.getStationCompanyName());
                    subOrderDetail.setSalesSubjectProvince(station.getProvince());
                    subOrderDetail.setSalesSubjectCity(station.getCity());
                    subOrderDetail.setSalesSubjectRegion(station.getRegion());
                }
            }
        }
    }

    /**
     * 查询线下支付待审核主订单(立即支付)
     */
    @Override
    public PageVO<OrderMainDTO> orderMainPayCheckList(Integer pageNum, Integer pageSize, OrderMainDTO query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<OrderMainDTO> orderMainPage = orderMainMapper.selectPayCheckList(query);
        return new PageVO<>(pageNum, orderMainPage);
    }

    /**
     * 查询线下支付待审核主订单(货到付款)
     */
    @Override
    public PageVO<WorkOrderDTO> orderDeliveryPayCheckList(Integer pageNum, Integer pageSize, WorkOrderQueryDTO query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WorkOrderDTO> orderDeliveryPage = workOrderMapper.selectDeliveryPayCheckList(query);
        return new PageVO<>(pageNum, orderDeliveryPage);
    }

    /**
     * 线下商品支付审核
     *
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public List<OrderSub> orderMainPayCheckSingle(String id, Boolean pass, String reason, String adminName,
                                                  Integer payTerminal, String userPhone) {
        if (Objects.isNull(id)) {
            throw new YimaoException("id不能为空!");
        }
        if (!pass && reason == null) {
            throw new YimaoException("请填写审核不通过原因。");
        }
        //判断支付类型（货到付款和立即支付）
        if (payTerminal == null) {
            throw new YimaoException("支付类型不能为空!");
        }

        if (payTerminal == 1) {//立即支付
            List<OrderSub> successSubOrderIdList = updateMainStatus(Long.parseLong(id), pass, reason, adminName, userPhone);
            if (pass) {
                return successSubOrderIdList;
            }
        } else if (payTerminal == 2) {//货到付款
            log.info("货到付款");
            updateWorkOrderStatus(id, pass, reason, adminName);
        } else {
            throw new YimaoException("支付类型不存在!");
        }
        //发送短信
        if (!pass) {

            //获取短信模板
            if (Objects.nonNull(userPhone)) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("#code#", id + "");
                map.put("#code1#", reason);
                sendMessage(MessageModelTypeEnum.UNPASS_AUDIT.value, MessagePushObjectEnum.ORDERING_USER.value, MessageMechanismEnum.OFFLINE_FINANCIAL_AUDIT_UNPASS.value, MessagePushModeEnum.YIMAO_APP_NOTICE.value, userPhone, map);
            }
        }
        return null;

    }

    private void updateWorkOrderStatus(String id, Boolean pass, String reason, String adminName) {
        //同步百得审核通过状态
        //根据工单号查询改工单对应订单
        WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(id);
        if (Objects.isNull(workOrder)) {
            throw new YimaoException("工单不存在!");
        }

        OrderSub orderSub = orderSubMapper.selectByPrimaryKey(workOrder.getSubOrderId());
        if (Objects.isNull(orderSub)) {
            throw new YimaoException("订单不存在!");
        }

        Date now = new Date();

        WorkOrder wo = new WorkOrder();
        OrderSub updateOrderSub = new OrderSub();
        updateOrderSub.setId(orderSub.getId());
        if (pass) {
            wo.setPayStatus(PayStatus.PAY.value);
            wo.setPay(true);
            wo.setPayTime(now);

            //订单状态变更
            updateOrderSub.setDeleted(false);
            updateOrderSub.setPay(true);
            updateOrderSub.setPayStatus(PayStatus.PAY.value);
            updateOrderSub.setPayTime(now);
            updateOrderSub.setStatus(OrderStatusEnum.PENDING_RECEIPT.value);
            
            //updateOrderSub.setStatus(OrderStatusEnum.SUCCESSFUL_TRADE.value);//交易完成
            //审核通过需要判断是否已签约,如果未签约,则设置step为SIGN_CONTRACT,已签约设置step为COMPLETE_WORK_ORDER
            if (!StringUtil.isEmpty(wo.getSignStatus())&&wo.getSignStatus().equals("Y")) {
                //已签约
            	wo.setStep(WorkOrderInstallNewStep.SIGN_CONTRACT.value);//签约
            	wo.setNextStep(WorkOrderInstallNewStep.COMPLETE_WORK_ORDER.value);//工单完成
            	
                //wo.setStatus(WorkOrderStatusEnum.COMPLETED.value);
                //wo.setCompleteType(WorkOrderCompleteEnum.NORMAL_COMPLETE.getState());//正常完成
                //wo.setCompleteTime(new Date());
            }else {
				//未签约
            	wo.setStep(WorkOrderInstallNewStep.PAY.value);//支付
            	wo.setNextStep(WorkOrderInstallNewStep.SIGN_CONTRACT.value);//签约
			}

        } else {
            wo.setPayStatus(PayStatus.FAIL.value);
            wo.setPay(false);
            
            //审核失败需要重新支付
            wo.setStep(WorkOrderInstallNewStep.ACTIVATING.value);//激活
        	wo.setNextStep(WorkOrderInstallNewStep.PAY.value);//支付
            
        	//订单状态变更 
            updateOrderSub.setPayStatus(PayStatus.FAIL.value);
            updateOrderSub.setPay(false);

        }
        wo.setUpdateUser(adminName);
        wo.setUpdateTime(now);
        wo.setSubOrderId(workOrder.getSubOrderId());
        log.info("更新工单参数=" + JSON.toJSONString(wo) + ",更新子订单参数=" + JSON.toJSONString(updateOrderSub));
        int woResult = workOrderMapper.updateCheckWorkOrderStatus(wo);
        //更新订单
        int result = orderSubMapper.updateCheckSubOrderStatus(updateOrderSub);
        if (result < 1 || woResult < 1) {
            throw new YimaoException("子订单：" + updateOrderSub.getId() + "审核状态错误");
        }
        if (pass) {
            //判断用户类型，若为普通用户升级为分享用户
            log.info("下单用户id=" + orderSub.getUserId());
            UserDTO user = userFeign.changeUserTypeIfMeetTheConditions(orderSub.getUserId());
            if (user == null) {
                log.error("审核通过升级用户失败");
                throw new YimaoException("审核通过升级用户失败");
            }
        }

        //审核记录
        insertPayCheckRecord(id, 3, pass, reason, adminName);

    }

    private List<OrderSub> updateMainStatus(Long id, Boolean pass, String reason, String adminName, String userPhone) {
        List<OrderSub> successSubOrderIdList = new ArrayList<OrderSub>();
        //查询该主订单的未审核通过的子订单（一对多）
        Example example = new Example(OrderSub.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mainOrderId", id);
        //criteria.andEqualTo("payStatus", 2);
        List<OrderSub> orderSubList = orderSubMapper.selectByExample(example);
        //创建工单或是体检卡导致报错子订单集合
        //List<Long> successSubOrderIdList=new ArrayList<Long>();

        if (CollectionUtil.isNotEmpty(orderSubList)) {

            for (OrderSub orderSub : orderSubList) {

                OrderSub updateSub = new OrderSub();
                updateSub.setId(orderSub.getId());
                updateSub.setUpdateTime(new Date());
                if (pass) {//通过
                    updateSub.setPay(true);
                    updateSub.setPayStatus(3);
                    updateSub.setPayTime(new Date());
                    if (orderSub.getProductType() == 2) {//虚拟商品
                        updateSub.setStatus(5);
                        updateSub.setCompleteTime(new Date());
                    } else {
                        updateSub.setStatus(2);
                    }
                } else {//不通过
                    updateSub.setPayStatus(4);
                    updateSub.setStatus(0);
                }

                log.info("子订单id=" + orderSub.getId());
                //判断商品类别
                int result = orderSubMapper.updateCheckSubOrderStatus(updateSub);
                if (result < 1) {
                    throw new YimaoException("子订单：" + updateSub.getId() + "审核状态错误");
                }

                if (pass) {

                    if (orderSub.getProductType() == 3) {//租赁商品

                        orderSub.setPay(updateSub.getPay());
                        orderSub.setPayStatus(updateSub.getPayStatus());
                        orderSub.setStatus(updateSub.getStatus());
                        orderSub.setPayTime(updateSub.getPayTime());
                        //生成工单信息
                        createWorkOrderBySubOrder(orderSub);

                    } else if (orderSub.getProductType() == 2) {//虚拟商品
                        successSubOrderIdList.add(orderSub);

                    }
                    //收益分配
                    log.info("订单线下支付审核通过进行收益分配，订单号：{}", orderSub.getId());
                    productIncomeRecordService.allotSellIncome(orderSub.getId());

                }
            }
        }
        if (pass) {
            //将主订单改为已支付设置支付时间
            OrderMain updateMain = new OrderMain();
            updateMain.setId(id);
            updateMain.setPay(true);
            updateMain.setPayTime(new Date());
            orderMainMapper.updateByPrimaryKeySelective(updateMain);
            //TODO 判断用户类型，若为普通用户升级为分享用户
            OrderMain main = orderMainMapper.selectByPrimaryKey(id);
            log.info("下单用户id=" + main.getUserId());
            UserDTO user = userFeign.changeUserTypeIfMeetTheConditions(main.getUserId());
            if (user == null) {
                log.error("审核通过升级用户失败");
                //这里不回滚，因为同步售后数据已经成功，回滚导致数据不一致，需后续手工处理
                //throw new YimaoException("审核通过升级用户失败");
            }
        }

        //TODO 新增审核纪录(可以交由队列完成)
        insertPayCheckRecord(id + "", 1, pass, reason, adminName);
        //返回虚拟商品审核成功订单id集合
        return successSubOrderIdList;

    }


    /**
     * @param id(对应主订单id，续费订单id，工单id)
     * @param orderType(1-普通订单(立即支付)  2-续费订单 3-工单(货到付款))
     * @param pass
     */
    public void insertPayCheckRecord(String id, int orderType, Boolean pass, String reason, String adminName) {

        OrderPayCheck payCheck = new OrderPayCheck();
        payCheck.setOrderType(orderType);
        payCheck.setOrderId(id);

        if (!pass) {
            //审核状态：1-通过；2-不通过；
            payCheck.setStatus(2);
            payCheck.setStatusName("不通过");
            payCheck.setReason(reason);

        } else {
            //审核状态：1-通过；2-不通过；
            payCheck.setStatus(1);
            payCheck.setStatusName("通过");
        }
        payCheck.setCreator(adminName);
        payCheck.setCreateTime(new Date());
        orderPayCheckMapper.insert(payCheck);

    }

    /**
     * 后根据子订单创建工单
     *
     * @param orderSub 子订单号
     * @return
     */
    public void createWorkOrderBySubOrder(OrderSub orderSub) {
        log.info("手工处理的子订单=" + JSON.toJSONString(orderSub));
        Map<String, Object> resultMap = Maps.newHashMap();
        //TODO 后期删除该查询操作，验证事务中数据是否一致
//        OrderSub selectOrderSub = orderSubMapper.selectByPrimaryKey(orderSub.getId());
//        log.info("数据库查询的子订单=" + JSON.toJSONString(selectOrderSub));
        SubOrderDetail subOrderDetail = subOrderDetailMapper.selectByPrimaryKey(orderSub.getId());
        if (subOrderDetail == null) {
            log.error("创建工单失败，订单或订单详情为空---subOrderId={}", orderSub.getId());
            throw new YimaoException(orderSub.getId() + "订单或订单详情为空");

        }
        if (Objects.isNull(subOrderDetail.getServiceTime())) {
            throw new YimaoException("订单安装时间不能为空");
        }

        //工单参数
//        String distributorId = null;
//        if (Objects.isNull(subOrder.getDistributorId())) {
//            //查询默认经销商id
//            UserIncomeAccountDTO incomeAccount = userFeign.getIncomeAccount();
//            if (Objects.isNull(incomeAccount)) {
//                throw new BadRequestException("默认经销商id为空");
//            }
//            distributorId = incomeAccount.getDistributorId().toString();
//        } else {
//            distributorId = subOrder.getDistributorId().toString();
//        }
//
//        String costId = subOrderDetail.getCostId().toString();
//        String productId = subOrder.getProductId().toString();
//        Integer count = subOrder.getCount();
//        String userProvince = subOrderDetail.getAddresseeProvince();
//        String userCity = subOrderDetail.getAddresseeCity();
//        String userRegion = subOrderDetail.getAddresseeRegion();
//        String address = subOrderDetail.getAddresseeStreet();
//        String time = DateUtil.transferDateToString(subOrderDetail.getServiceTime(), "yyyy-MM-dd HH:mm:ss");
//        String remark = subOrder.getRemark();
//        Integer payTerminal = subOrder.getPayTerminal();
//        String sex = null;
//        if (Objects.nonNull(subOrderDetail.getAddresseeSex())) {
//            sex = subOrderDetail.getAddresseeSex().toString();
//        }
//
//        String name = subOrderDetail.getAddresseeName();
//        String phone = subOrderDetail.getAddresseePhone();
//        String actId = null;
//        Integer terminal = subOrder.getTerminal();
//        Integer scanCodeType = null;
//        if (Terminal.YIMAO_APP.value == terminal) {
//            scanCodeType = ScanCodeTypeEnum.DISTRIBUTOR.getStatus();
//        } else if (Terminal.WECHAT.value == terminal) {
//            scanCodeType = ScanCodeTypeEnum.MALL.getStatus();
//        }
//        String tradeNo = subOrder.getTradeNo();
//        Integer payType = subOrder.getPayType();
//        String payTime = DateUtil.transferDateToString(subOrder.getPayTime(), "yyyy-MM-dd HH:mm:ss");
//        String addTime = DateUtil.transferDateToString(subOrder.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
//        String customerId=null;
//        if(Objects.nonNull(subOrder.getEngineerId())) {
//        	customerId = subOrder.getEngineerId().toString();
//        }
//        Integer allotType = subOrderDetail.getDispatchType();
//        Long orderMainId=subOrder.getMainOrderId();
        //创建工单
        resultMap = workOrderService.generateWorkOrderAndSyncaAfterSale(orderSub, subOrderDetail);
//        resultMap = outFeign.addWorkOrder(subOrderId,orderMainId,distributorId, costId, productId, count, userProvince, userCity, userRegion, address, time, remark, payTerminal, sex, name, phone, actId, scanCodeType, tradeNo, payType, payTime, addTime, customerId, allotType);
        if (resultMap.size() < 1 || !(boolean) resultMap.get("success")) {
            log.error("审核完成创建工单失败。subOrderId={}", orderSub.getId());
            String errorMsg = (String) resultMap.get("errorMsg");
            throw new YimaoException("审核完成创建工单失败:" + errorMsg);
        }
        if ((boolean) resultMap.get("success") && Objects.nonNull(resultMap.get("workorderId"))) {
            //将工单号回写订单中
            String workorder = (String) resultMap.get("workorderId");
            log.info("回写工单号=" + workorder);
            OrderSub order = new OrderSub();
            order.setId(orderSub.getId());
            order.setRefer(workorder);
            orderSubMapper.updateByPrimaryKeySelective(order);
        }


    }

    @Override
    public List<OrderMainDTO> orderMainPayCheckExport(OrderMainDTO query) {

        return orderMainMapper.selectPayCheckListExport(query);

    }

    @Override
    public List<WorkOrderDTO> orderDeliveryPayCheckListExport(WorkOrderQueryDTO query) {

        return workOrderMapper.selectDeliveryPayCheckListExport(query);

    }

    @Override
    public PageVO<OrderPayCheckDTO> orderPaycheckRecordList(Integer pageNum, Integer pageSize, OrderPayCheckDTO orderPayCheckDTO) {
        PageHelper.startPage(pageNum, pageSize);
        Page<OrderPayCheckDTO> patCheckPage = orderPayCheckMapper.selectPayCheckRecordList(orderPayCheckDTO);
        return new PageVO<>(pageNum, patCheckPage);
    }

    /**
     * 根据订单号查询产品公司ID
     *
     * @param id 主订单号/支付订单号
     */
    @Override
    public Integer getProductCompanyIdByOutTradeNo(Long id) {
        return orderMainMapper.ProductCompanyIdByOutTradeNo(id);
    }

    /**
     * @param
     * @return void
     * @description 注册成功发送经销商短信
     * @author Liu Yi
     * @date 2019/10/10 14:26
     */
    private void sendMessage(Integer type, Integer pushObject, Integer mechanism, Integer pushMode, String phone, Map<String, String> map) {
        SmsMessageDTO smsMessage = new SmsMessageDTO();
        smsMessage.setType(type);
        smsMessage.setPushObject(pushObject);
        smsMessage.setPhone(phone);
        smsMessage.setMechanism(mechanism);
        smsMessage.setPushMode(pushMode);
        smsMessage.setContentMap(map);
        // 发送短信
        rabbitTemplate.convertAndSend(RabbitConstant.SMS_MESSAGE_PUSH, smsMessage);
    }

    /**
     * 线下支付审核记录详情
     */
    @Override
    public OrderPayCheckDTO findPayCheckRecordInfo(Integer id, Integer orderType) {
        if (Objects.isNull(id)) {
            throw new YimaoException("id为空");
        }

        if (Objects.isNull(orderType)) {
            throw new YimaoException("订单类型为空");
        }

        //判别订单类型
        if (orderType == 1) {
            //orderId为子订单号
            OrderPayCheckDTO orderPayCheck = orderPayCheckMapper.selectOrderMainPayCheckRecordInfo(id);
            return orderPayCheck;
        } else if (orderType == 2) {
            //orderId为续费订单号
            OrderPayCheckDTO orderPayCheck = orderPayCheckMapper.selectOrderRenewPayCheckRecordInfo(id);
            return orderPayCheck;
        } else if (orderType == 3) {
            //orderId为工单号
            OrderPayCheckDTO orderPayCheck = orderPayCheckMapper.selectWorkOrderPayCheckRecordInfo(id);
            return orderPayCheck;
        } else {
            throw new YimaoException("订单类型不存在");
        }

    }

    @Override
    public void createHraCard(List<OrderSub> hraSubOrderList) {

        for (OrderSub orderSub : hraSubOrderList) {

            if (Objects.equals(orderSub.getProductModel(), HraType.Y.name())) {
                //生成体检卡
                log.info("线下审核生成体检卡，订单id=" + orderSub.getId());
                hraFeign.createHraCardAndTicket(orderSub.getId());
            } else if (Objects.equals(orderSub.getProductModel(), HraType.M.name())) {
                hraFeign.doRefer(orderSub.getId(), orderSub.getRefer().trim(), orderSub.getTerminal());
            }
        }


    }
}
