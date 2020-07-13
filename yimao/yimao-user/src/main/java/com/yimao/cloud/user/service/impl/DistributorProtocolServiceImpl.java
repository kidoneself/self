package com.yimao.cloud.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NoNeedSignException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.properties.YunSignProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.yunSignUtil.*;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.query.user.DistributorProtocolQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.feign.OrderFeign;
import com.yimao.cloud.user.feign.SystemFeign;
import com.yimao.cloud.user.mapper.*;
import com.yimao.cloud.user.po.*;
import com.yimao.cloud.user.service.DistributorOrderService;
import com.yimao.cloud.user.service.DistributorProtocolService;
import com.yimao.cloud.user.service.DistributorRoleService;
import com.yimao.cloud.user.service.DistributorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lizhqiang
 * @date 2019/1/2
 */
@Service
@Slf4j
public class DistributorProtocolServiceImpl implements DistributorProtocolService {

    @Resource
    private YunSignApi yunSignApi;

    @Resource
    private DomainProperties domainProperties;

    @Resource
    private DistributorProtocolMapper distributorProtocolMapper;

    @Resource
    private DistributorOrderMapper distributorOrderMapper;

    @Resource
    private DistributorMapper distributorMapper;

    @Resource
    private SystemFeign systemFeign;

    @Resource
    private UserDistributorApplyMapper userDistributorApplyMapper;

    @Resource
    private DistributorOrderService distributorOrderService;

    @Resource
    private DistributorRoleService distributorRoleService;

    @Resource
    private YunSignProperties yunSignProperties;

    @Resource
    private OrderFeign orderFeign;

    @Resource
    private DistributorService distributorService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserCompanyApplyMapper userCompanyApplyMapper;

    @Resource
    private YunSignRegister yunSignRegister;


    @Override
    public Map<String, String> checkUserSignStatus(Long orderId) {
        Map<String, String> resultMap = new HashMap<>();
        Example example = new Example(DistributorProtocol.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        DistributorProtocol distributorProtocol = distributorProtocolMapper.selectOneByExample(example);
        if (distributorProtocol == null) {
            resultMap.put("err", "合同不存在，用户未签署");
            return resultMap;
        }
        if (distributorProtocol.getUserSignState() != null && distributorProtocol.getUserSignState() == 1) {
            //用户已签署
            resultMap.put("success", "用户已签署");
            return resultMap;
        }
        resultMap.put("err", "用户未进行签署");
        return resultMap;
    }

    @Override
    public List<DistributorProtocolDTO> queryDistributorProtocolByDistIdAndSignStatus(Integer distributorId) {
        List<DistributorProtocolDTO> list = distributorProtocolMapper.queryDistributorProtocolByDistIdAndSignStatus(distributorId);
        return list;
    }

    /**
     * 根据订单号查询合同状况
     *
     * @param distributorOrderId
     * @return
     */
    @Override
    public DistributorProtocolDTO getDistributorProtocolByOrderId(Long distributorOrderId) {
        DistributorProtocolDTO dto = distributorProtocolMapper.getDistributorProtocolByOrderId(distributorOrderId);
        return dto;
    }

    /**
     * 查询合同列表
     *
     * @param dto
     * @return
     */
    @Override
    public PageVO<DistributorProtocolDTO> getDistributorProtocolList(DistributorProtocolQueryDTO dto, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        Page<DistributorProtocolDTO> page = distributorProtocolMapper.distributorProtocolList(dto);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 查询合同列表根据经销商账号
     *
     * @param distributorAccount
     * @return
     */
    @Override
    public List<DistributorProtocol> getDistributorProtocolListByDistributorAccount(String distributorAccount) {
        Example example = new Example(DistributorProtocol.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("distributorAccount", distributorAccount);
        List<DistributorProtocol> list = distributorProtocolMapper.selectByExample(example);
        return list;
    }

    /**
     * 云签回调，修改签署状态
     *
     * @param userId
     * @param orderId
     * @param signer
     * @param updateTime
     * @param syncOrderId
     * @param status
     * @param appSecretKey
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void backCallYunSignOrderUpdate(String userId, String orderId, String signer, String updateTime, String syncOrderId, String status, String appSecretKey) {
        if (status.trim().equals("1") || status.trim().equals("2")) {
            log.info("开始处理回调");
            DistributorOrder distributorOrder = null;
            if (StringUtil.isEmpty(orderId)) {
                throw new YimaoException("订单id为空!");
            } else {
                //先匹配是否是老订单号
                Example example = new Example(DistributorOrder.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("oldOrderId", orderId);
                distributorOrder = distributorOrderMapper.selectOneByExample(example);
                if (distributorOrder == null) {
                    //订单id
                    Long id = Long.valueOf(orderId);
                    //获取订单信息
                    distributorOrder = distributorOrderMapper.selectByPrimaryKey(id);
                }
            }
            if (distributorOrder == null) {
                throw new YimaoException("没有找到订单信息");
            }
            //查询订单对应合同信息
            Example example = new Example(DistributorProtocol.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("orderId", distributorOrder.getId());
            DistributorProtocol distributorProtocol = distributorProtocolMapper.selectOneByExample(example);
            if (distributorProtocol == null) {
                throw new YimaoException("没有找到对应合同信息!");
            }

            String phone = "";
            String province = "";
            String city = "";
            String region = "";
            if (distributorOrder.getDistributorId() != null) {
                //升级订单，续费订单，经销商id可直接在订单中获取
                Distributor distributor = distributorMapper.selectByPrimaryKey(distributorOrder.getDistributorId());
                phone = distributor.getPhone();
                province = distributor.getProvince();
                city = distributor.getCity();
                region = distributor.getRegion();
            }

            if (distributorOrder.getOrderType() == DistributorOrderType.REGISTER.value) {
                //注册订单，用户信息得从用户申请信息中获取
                Example example1 = new Example(UserDistributorApply.class);
                example1.createCriteria().andEqualTo("orderId", distributorOrder.getId());
                UserDistributorApply userDistributorApply = userDistributorApplyMapper.selectOneByExample(example1);
                phone = userDistributorApply.getPhone();
                province = userDistributorApply.getProvince();
                city = userDistributorApply.getCity();
                region = userDistributorApply.getRegion();
            }
            //用户信息非空校验
            checkUserInfo(phone, province, city, region);

            //根据省市区查询服务站所属服务站公司
            StationCompanyDTO stationCompanyDTO = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
            if (stationCompanyDTO == null) {
                throw new YimaoException("服务站公司不存在!");
            }
            if (stationCompanyDTO.getYunSignId() != null && stationCompanyDTO.getYunSignId().equals(userId)) {
                log.info("处理 服务站回调数据 ");
                if (distributorProtocol.getStationSignState() != null && distributorProtocol.getStationSignState() == DistributorProtocolSignStateEnum.SIGN.value) {
                    //服务站已签署
                    return;
                }
                log.info("合同没有问题");
                distributorProtocol.setStationSignState(DistributorProtocolSignStateEnum.SIGN.value);
                distributorProtocol.setStationSignTime(new Date());
                int count = distributorProtocolMapper.updateByPrimaryKeySelective(distributorProtocol);
                if (count != 1) {
                    throw new YimaoException("合同签署状态修改失败");
                }
            } else if (phone.equals(userId)) {
                log.info("处理 用户签署回调数据");
                if (distributorProtocol.getUserSignState() != null && distributorProtocol.getUserSignState() == DistributorProtocolSignStateEnum.SIGN.value) {
                    //用户已签署
                    return;
                }
                distributorProtocol.setUserSignState(DistributorProtocolSignStateEnum.SIGN.value);
                distributorProtocol.setUserSignTime(new Date());
                int count = distributorProtocolMapper.updateByPrimaryKeySelective(distributorProtocol);
                if (count != 1) {
                    throw new YimaoException("合同签署状态修改失败");
                }
                if (distributorOrder.getEnterpriseState() == EnterpriseStateEnum.UN_AUDIT.value) {
                    //如果需要企业审核，将订单状态设为待审核
                    distributorOrder.setOrderState(DistributorOrderStateEnum.WAITING_AUDIT.value);
                    count = distributorOrderMapper.updateByPrimaryKeySelective(distributorOrder);
                    if (count != 1) {
                        throw new YimaoException("订单信息修改失败");
                    }
                } else if (distributorOrder.getPayState() != null && distributorOrder.getEnterpriseState() == EnterpriseStateEnum.NO_NEED_AUDIT.value
                        && distributorOrder.getPayState() == PayStateEnum.PAY.value) {
                    //如果支付状态为支付成功并且无需企业审核，完成订单
                    if (distributorOrder.getOrderType() == DistributorOrderType.REGISTER.value) {//0-注册、1-升级、2-续费
                        distributorOrderService.finishRegisterOrder(distributorOrder);
                    } else if (distributorOrder.getOrderType() == DistributorOrderType.UPGRADE.value) {
                        distributorOrderService.finishUpgradeOrder(distributorOrder);
                    } else if (distributorOrder.getOrderType() == DistributorOrderType.RENEW.value) {
                        distributorOrderService.finishRenewOrder(distributorOrder);
                    }
                    if (distributorOrder.getOrderState() == DistributorOrderStateEnum.COMPLETED.value) { //订单完成才进行收益分配
                        try {
                            DistributorOrderDTO distributorOrderDTO = new DistributorOrderDTO();
                            distributorOrder.convert(distributorOrderDTO);
                            DistributorDTO distributorDTO = distributorService.getBasicInfoById(distributorOrder.getDistributorId());
                            distributorOrderDTO.setDistributor(distributorDTO);
                            User user = userMapper.selectByPrimaryKey(distributorDTO.getUserId());
                            UserDTO userDTO = new UserDTO();
                            userInfoConvert(user, userDTO);
                            distributorOrderDTO.setUser(userDTO);
                            DistributorDTO recommendDistributor = distributorService.getRecommendByDistributorId(distributorDTO.getId());
                            distributorOrderDTO.setRecommendDistributor(recommendDistributor);
                            //收益收益分配
                            orderFeign.serviceAllot(distributorOrderDTO);
                        } catch (Exception e) {
                            throw new YimaoException("招商收益分配失败，经销商订单id：：" + distributorOrder.getId());
                        }
                    }
                }
            } else if (YunSignConfig.YUNSIGN_YIMAO_ACCOUNT.equals(userId)) {
                log.info("处理 翼猫签署回调数据");
                if (distributorProtocol.getYmSignState() != null && distributorProtocol.getYmSignState() == DistributorProtocolSignStateEnum.SIGN.value) {
                    //翼猫签署状态为已签署
                    return;
                }
                distributorProtocol.setYmSignState(DistributorProtocolSignStateEnum.SIGN.value);
                distributorProtocol.setYmSignTime(new Date());
                int count = distributorProtocolMapper.updateByPrimaryKeySelective(distributorProtocol);
                if (count != 1) {
                    throw new YimaoException("合同签署状态修改失败");
                }
            }
            //平台方自动签署
            yunSignApi.autoSignContract(userId, orderId);
            log.info("回调成功");
        }
    }

    private void userInfoConvert(User user, UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setUserName(user.getUserName());
        userDTO.setMobile(user.getMobile());
    }

    private void checkUserInfo(String phone, String province, String city, String region) {
        if (StringUtil.isEmpty(phone)) {
            throw new YimaoException("用户手机号不能为空!");
        }
        if (StringUtil.isEmpty(province)) {
            throw new YimaoException("用户所属省不能为空!");
        }
        if (StringUtil.isEmpty(city)) {
            throw new YimaoException("用户手所属市不能为空!");
        }
        if (StringUtil.isEmpty(region)) {
            throw new YimaoException("用户所属区不能为空!");
        }
    }

    /**
     * 创建合同
     *
     * @param distributorProtocol
     */
    @Override
    public void create(DistributorProtocol distributorProtocol) {
        long startTime = System.currentTimeMillis();
        if (distributorProtocol == null) {
            throw new YimaoException("合同实体类不能为空！");
        }
        //订单id
        Long orderId = distributorProtocol.getOrderId();
        if (orderId == null) {
            throw new YimaoException("订单id不能为空");
        }
        DistributorProtocolDTO query = this.getDistributorProtocolByOrderId(orderId);
        if (query != null) {
            throw new BadRequestException("订单对应合同已存在，无需再次创建");
        }
        //根据经销商订单id查询订单信息
        DistributorOrder distributorOrder = distributorOrderMapper.selectByPrimaryKey(orderId);
        if (distributorOrder == null) {
            throw new YimaoException("订单不存在");
        }
        //拦截不需要创建合同的订单（体验版经销商不走这个流程无需拦截）
        if (distributorOrder.getOrderType() == DistributorOrderType.UPGRADE.value) {
            if (distributorOrder.getRoleLevel() == DistributorRoleLevel.D_350.value &&
                    distributorOrder.getDestRoleLevel() == DistributorRoleLevel.D_650.value) {
                //微创版经销商升级个人经销商如果在30天升级有效期内升级，则不需要签署合同
                DistributorRoleDTO origDistributorRole = distributorRoleService.getByLevel(distributorOrder.getRoleLevel());
                if (origDistributorRole == null) {
                    throw new YimaoException("经销商类型不存在！");
                }
                DistributorDTO distributorDTO = distributorService.getDistributorById(distributorOrder.getDistributorId());
                if (distributorDTO == null) {
                    throw new YimaoException("经销商信息不存在！");
                }
                //判断是否在有效期内
                boolean flag = distributorOrderService.upgradeValidityTime(origDistributorRole.getUpgradeLimitDays(),
                        new Distributor(distributorDTO));
                if (flag) {
                    //如果在升级剩余有效期内则不需要签署合同
                    log.info("当前订单不需要生成合同");
                    throw new NoNeedSignException("当前订单不需要生成合同");
                }
            } else if ((distributorOrder.getRoleLevel() == DistributorRoleLevel.D_350.value || distributorOrder.getRoleLevel() == DistributorRoleLevel.D_650.value) &&
                    distributorOrder.getDestRoleLevel() == DistributorRoleLevel.D_950.value) {
                //微创版,个人版升级体验版不需要签署合同
                log.info("当前订单不需要生成合同");
                throw new NoNeedSignException("当前订单不需要生成合同");
            }
        }

        try {
            String title = "经销代理合同";
            String identityNumber = "";
            String mobile = "";
            String userName = "";
            String companyName = "";
            // 服务站公司绑定的云签账号id
            String serverSiteId = "";
            String serverSiteName = "";
            Integer roleLevel = null;
            String email = "";
            Distributor distributor = null;
            StationCompanyDTO stationCompanyDTO = null;
            UserDistributorApply userDistributorApply = null;
            //升级注册订单，订单中有绑定经销id
            if (distributorOrder.getOrderType() != DistributorOrderType.REGISTER.value) {
                if (distributorOrder.getDistributorId() == null) {
                    throw new YimaoException("当前为升级续费订单，经销商id不能为空！");
                }
                //根据经销id查询经销商
                DistributorDTO distributorDTO = distributorService.getDistributorById(distributorOrder.getDistributorId());
                if (distributorDTO == null) {
                    throw new YimaoException("经销商信息不存在");
                }
                distributor = new Distributor(distributorDTO);
                identityNumber = distributor.getIdCard();
                mobile = distributor.getPhone();
                userName = distributor.getRealName();
                email = distributor.getEmail();
                //根据省市区查询经销商所属服务站公司
                stationCompanyDTO = getStationCompanyByPCR(distributor.getProvince(), distributor.getCity(), distributor.getRegion());
                if (stationCompanyDTO != null) {
                    serverSiteId = stationCompanyDTO.getYunSignId();
                    serverSiteName = stationCompanyDTO.getName();
                } else {
                    throw new YimaoException("服务站公司不存在");
                }
            } else {
                //如果是注册订单，那么订单中是没有经销商信息的，用户信息得去注册经销商申请信息去找
                userDistributorApply = userDistributorApplyMapper.getByOrderId(orderId);
                if (userDistributorApply == null) {
                    throw new YimaoException("当前为注册订单，经销商注册申请信息不存在！");
                }
                identityNumber = userDistributorApply.getIdCard();
                mobile = userDistributorApply.getPhone();
                userName = userDistributorApply.getRealName();
                email = userDistributorApply.getEmail();
                //根据省市区查询经销商所属服务站公司
                stationCompanyDTO = getStationCompanyByPCR(userDistributorApply.getProvince(), userDistributorApply.getCity(), userDistributorApply.getRegion());
                if (stationCompanyDTO != null) {
                    serverSiteId = stationCompanyDTO.getYunSignId();
                    serverSiteName = stationCompanyDTO.getName();
                } else {
                    throw new YimaoException("服务站公司不存在");
                }
            }
            //信息非空校验
            checkInfo(userName, mobile, identityNumber, serverSiteName);
            String userId = mobile; // 用户ID 为 手机号码
            Boolean boo = yunSignApi.checkUserExisted(mobile).isSuccess();
            if (!boo) {
                long startTime2 = System.currentTimeMillis();
                //云签个人用户未注册，进行注册
                YunSignResult yunSignResult = yunSignRegister.registerPersonalUser(identityNumber, userName, mobile, email, userId, true);
                if (!yunSignResult.isSuccess()) {
                    log.error("云签注册个人用户失败 , 注册结果 " + yunSignResult.isSuccess() + " " + yunSignResult.getCode() + yunSignResult.getResultData());
                    throw new YimaoException("云签注册个人用户失败！" + yunSignResult.getResultData());
                } else {
                    log.info("云签个人用户注册成功，注册结果" + yunSignResult.isSuccess());
                }
                long endTime2 = System.currentTimeMillis();
                log.info("云签注册个人用户总耗时：" + (endTime2 - startTime2) + "毫秒");
            } else {
                //云签个人用户已存在，无需再次注册
                log.info("用户已注册过云签用户，无需再次注册");
            }
            if (distributorOrder.getOrderType() == DistributorOrderType.REGISTER.value || distributorOrder.getOrderType() == DistributorOrderType.RENEW.value) {
                roleLevel = distributorOrder.getRoleLevel();
            }
            if (distributorOrder.getOrderType() == DistributorOrderType.UPGRADE.value) {
                roleLevel = distributorOrder.getDestRoleLevel();
            }
            if (roleLevel == null) {
                throw new YimaoException("经销商类型不能为空！");
            }
            if (roleLevel == DistributorRoleLevel.D_650.value) {
                title = "个人（" + userName + "）" + title;
            } else if (roleLevel == DistributorRoleLevel.D_950.value) {
                if (distributorOrder.getOrderType() == DistributorOrderType.RENEW.value) {
                    if (distributor == null) {
                        throw new YimaoException("经销商信息不能为空");
                    }
                    //续费订单，经销商所属公司名称直接从经销商表中获取
                    companyName = distributor.getCompanyName();
                } else {
                    //升级注册订单，经销商所属公司名称从企业信息表中获取
                    companyName = userCompanyApplyMapper.getCompanyNameByOrderId(orderId);
                    if (StringUtil.isEmpty(companyName)) {
                        throw new YimaoException("注册或升级为企业版经销商，需提交企业申请信息");
                    }
                }
                title = companyName + "(" + userName + ")" + title;
            } else if (roleLevel == DistributorRoleLevel.D_350.value) {
                title = "微创（" + userName + "）" + title;
            }
            if (!yunSignApi.checkUserExisted(stationCompanyDTO.getYunSignId()).isSuccess()) {
                log.info(" ----   服务站 没有注册云签账户 ！ " + " " + serverSiteName + " 合同 不予创建 ！");
                throw new YimaoException(" 服务站公司没有注册云签账户！ ");
            }
            String customsId = userId + "," + YunSignConfig.YUNSIGN_YIMAO_ACCOUNT + "," + serverSiteId;
            String data = YunSignData.distributorContractData(roleLevel, orderId + "", serverSiteName, userName, identityNumber,
                    "29800.00", "2980.00", "59800.00", "贰万玖仟捌佰",
                    "贰仟玖佰捌拾", "伍万玖仟捌佰", "300", "20", 30,
                    "600");
            log.info("data:" + data);
            log.info(yunSignProperties.getTempleid());
            YunSignResult createResult = yunSignApi.createContract(userId, orderId + "", customsId, title, yunSignProperties.getTempleid(), data);
            // Boolean ifHaveOrderCheck = yunSignApi.queryContract(orderId + "").isSuccess(); //注释减少访问云签时间
            if (!createResult.isSuccess()) {
                if (!createResult.getCode().equals("4020") && !createResult.getCode().equals("4231")) {
                    log.info("云签合同创建失败！" + createResult.getCode() + createResult.getResultData());
                    throw new YimaoException("云签合同创建失败");
                }
            } else {
                log.info("云签合同创建成功！ ---- \n\t ： " + createResult.getResultData() + " \t\t >>>> : 订单号为：" + orderId);
                distributorProtocol.setState(1);//合同是否创建  1-已创建 0-未创建
                distributorProtocol.setCreateTime(new Date());
                distributorProtocol.setUserSignState(DistributorProtocolSignStateEnum.NOT_SIGN.value);//用户签署状态
                distributorProtocol.setStationSignState(DistributorProtocolSignStateEnum.NOT_SIGN.value);//服务站签署状态
                distributorProtocol.setYmSignState(DistributorProtocolSignStateEnum.NOT_SIGN.value);//翼猫签署状态
                distributorProtocol.setStationRenewState(0);//合同复核状态
                distributorProtocol.setTitle(title);
                if (distributorOrder.getDistributorId() != null) {
                    distributorProtocol.setDistributorId(distributorOrder.getDistributorId());
                }
                Integer orderType = distributorOrder.getOrderType();
                distributorProtocol.setOrderType(orderType);
                distributorProtocol.setLinkMan(stationCompanyDTO.getContact());
                distributorProtocol.setLinkPhone(stationCompanyDTO.getContactPhone());
                if (orderType == DistributorOrderType.REGISTER.value) {
                    //注册订单，信息得从注册申请信息里获取
                    if (userDistributorApply == null) {
                        throw new YimaoException("当前是注册订单，经销商申请信息不能为空！");
                    }
                    distributorProtocol.setProvince(userDistributorApply.getProvince());
                    distributorProtocol.setCity(userDistributorApply.getCity());
                    distributorProtocol.setRegion(userDistributorApply.getRegion());
                    distributorProtocol.setRoleId(userDistributorApply.getRoleId());
                } else {
                    //升级续费订单，信息从经销商信息中获取
                    if (distributor == null) {
                        throw new YimaoException("当前是升级续费订单，经销商信息不能为空！");
                    }
                    distributorProtocol.setProvince(distributor.getProvince());
                    distributorProtocol.setCity(distributor.getCity());
                    distributorProtocol.setRegion(distributor.getRegion());
                    distributorProtocol.setRoleId(distributor.getRoleId());
                    distributorProtocol.setDistributorAccount(distributor.getUserName());
                }
                //获取合同地址
                // String url = previewDistributorProtocol(orderId);
                // distributorProtocol.setContract(url);
                if (createResult.isSuccess()) {
                    log.info("中国云签 》》 固定合同签名位置 =================================== " + orderId + " " + mobile);
                    YunSignResult yunsignResult = yunSignApi.addUserSignInfo(
                            new String[]{userId, YunSignConfig.YUNSIGN_YIMAO_ACCOUNT, serverSiteId}, orderId + "", new String[]{"2", "3", "1"});

                    log.info("中国云签》》 固定合同签名位置接口 ：  " + yunsignResult.getCode() + " " + yunsignResult.getResultData());
                }
            }
            int count = distributorProtocolMapper.insertSelective(distributorProtocol);
            if (count != 1) {
                throw new YimaoException("新增合同信息出现异常");
            }
            long endTime = System.currentTimeMillis();
            log.info("访问云签操作总耗时：" + (endTime - startTime) + "毫秒");
        } catch (Exception e) {
            log.info("创建合同出错，使订单失效");
            //如果在创建合同的过程中出现异常，则直接让订单失效
            distributorOrder.setOrderState(DistributorOrderStateEnum.CLOSE.value);
            distributorOrderMapper.updateByPrimaryKeySelective(distributorOrder);
            throw new YimaoException(e.getMessage());
        }
    }

    private StationCompanyDTO getStationCompanyByPCR(String province, String city, String region) {
        if (StringUtil.isEmpty(province)) {
            throw new YimaoException("经销商省不能为空");
        }
        if (StringUtil.isEmpty(city)) {
            throw new YimaoException("经销商市不能为空");
        }
        if (StringUtil.isEmpty(region)) {
            throw new YimaoException("经销商区不能为空");
        }
        //根据省市区查询服务站所属服务站公司
        StationCompanyDTO stationCompanyDTO = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
        if (stationCompanyDTO == null) {
            throw new YimaoException("你的所属区域还未上线服务站，暂不支持升级续费。");
        }
        return stationCompanyDTO;
    }

    private void checkInfo(String userName, String mobile, String identityNumber, String serverSiteName) {
        if (StringUtil.isEmpty(userName)) {
            throw new YimaoException("用户姓名不能为空！");
        }
        if (StringUtil.isEmpty(mobile)) {
            throw new YimaoException("用户手机不能为空！");
        }
        if (StringUtil.isEmpty(identityNumber)) {
            throw new YimaoException("用户身份证号码不能为空！");
        }
        if (StringUtil.isEmpty(serverSiteName)) {
            throw new YimaoException("服务站公司名称不能为空！");
        }
    }


    /**
     * 签署合同(已作废)
     *
     * @param orderId
     */
    @Override
    public void sign(Long orderId) {
        //变更合同状态
        if (orderId == null) {
            throw new BadRequestException("经销商订单id不能为空！");
        }
        DistributorProtocolDTO dto = this.getDistributorProtocolByOrderId(orderId);
        if (dto == null) {
            throw new BadRequestException("合同不存在！");
        }

        DistributorProtocol protocol = new DistributorProtocol(dto);
        protocol.setId(dto.getId());
        protocol.setUserSignState(DistributorProtocolSignStateEnum.SIGN.value);
        protocol.setUserSignTime(new Date());
        protocol.setYmSignTime(new Date());
        protocol.setYmSignState(DistributorProtocolSignStateEnum.SIGN.value);
        protocol.setStationSignState(DistributorProtocolSignStateEnum.SIGN.value);
        protocol.setStationRenewTime(new Date());
        protocol.setState(1);

        distributorProtocolMapper.updateByPrimaryKeySelective(protocol);
    }

    @Override
    public String previewDistributorProtocol(Long distributorOrderId) {
        DistributorOrder distributorOrder = distributorOrderMapper.selectByPrimaryKey(distributorOrderId);
        if (distributorOrder == null) {
            throw new YimaoException("经销商订单不存在");
        }
        String orderId = distributorOrder.getOldOrderId();
        if (orderId == null) {
            orderId = distributorOrder.getId() + "";
        }
        Boolean existed = yunSignApi.queryContract(orderId).isSuccess();
        if (!existed) {
            //不存在合同信息
            throw new YimaoException("未对该订单生成合同！");
        }
        String userId = null;
        if (distributorOrder.getDistributorId() != null) {
            //查询订单所对应的经销商信息
            Distributor distributor = distributorMapper.selectByPrimaryKey(distributorOrder.getDistributorId());
            userId = distributor.getPhone();
        }
        if (distributorOrder.getOrderType() == DistributorOrderType.REGISTER.value) {
            //注册订单，订单表中没有经销商的id，无法获取到userId，所以userId得从注册经销商申请信息表中获取
            UserDistributorApply query = new UserDistributorApply();
            query.setOrderId(distributorOrderId);
            UserDistributorApply userDistributorApply = userDistributorApplyMapper.selectOne(query);
            userId = userDistributorApply.getPhone();
        }
        String appId = yunSignProperties.getServiceAppid();
        String appSecretKey = yunSignProperties.getServiceKey();
        String signType = "MD5";
        String time = System.currentTimeMillis() + "";
        String md5str = appId + "&" + orderId + "&" + time + "&" + userId + "&" +
                appSecretKey;
        log.info(md5str);
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        log.info(sign);
        // appId在前端做拼接
        String url = domainProperties.getYunSign() + "/mmecserver3.0/showContract.do?time=" + time + "&sign=" + sign
                + "&signType=" + signType + "&userId=" + userId + "&orderId=" + orderId;
        return url;
    }

    @Override
    public String signDistributorProtocol(Long distributorOrderId) {
        DistributorOrder distributorOrder = distributorOrderMapper.selectByPrimaryKey(distributorOrderId);
        if (distributorOrder == null) {
            throw new YimaoException("经销商订单不存在");
        }
        String orderId = distributorOrder.getOldOrderId();
        if (orderId == null) {
            orderId = distributorOrder.getId() + "";
        }
        Boolean existed = yunSignApi.queryContract(orderId).isSuccess();
        if (!existed) {
            //不存在合同信息
            throw new YimaoException("未对该订单生成合同！");
        }
        String userId = null;
        if (distributorOrder.getDistributorId() != null) {
            //查询订单所对应的经销商信息
            Distributor distributor = distributorMapper.selectByPrimaryKey(distributorOrder.getDistributorId());
            userId = distributor.getPhone();
        }
        if (distributorOrder.getOrderType() == DistributorOrderType.REGISTER.value) {
            //注册订单，订单表中没有经销商的id，无法获取到userId，所以userId得从注册经销商申请信息表中获取
            UserDistributorApply query = new UserDistributorApply();
            query.setOrderId(distributorOrderId);
            UserDistributorApply userDistributorApply = userDistributorApplyMapper.selectOne(query);
            userId = userDistributorApply.getPhone();
        }
        String appId = yunSignProperties.getServiceAppid();
        String appSecretKey = yunSignProperties.getServiceKey();
        String signType = "MD5";
        String isForceSeal = "Y";
        String isHandWrite = "Y";               // 是否需要手写
        String isSeal = "N";                    //是否需要盖章
        String isSignFirst = "N";               //是否优先盖章
        String validType = "VALID";              //
        String time = System.currentTimeMillis() + "";
        String md5str = appId + "&" + orderId + "&" + time + "&" + userId + "&" +
                appSecretKey;
        log.info(md5str);
        String sign = StringUtil.encodeMD5(md5str).toLowerCase();
        log.info(sign);
        // appId在前端做拼接
        String url = domainProperties.getYunSign() + "/mmecserver3.0/sign.do?time=" + time + "&sign=" + sign
                + "&signType=" + signType + "&userId=" + userId + "&orderId=" + orderId + "&isHandWrite=" + isHandWrite + "&isForceSeal=" + isForceSeal
                + "&isSignFirst=" + isSignFirst + "&isSeal=" + isSeal + "&validType=" + validType;
        return url;
    }

    /**
     * 服务站复查合同
     *
     * @param id
     */
    @Override
    public void renew(Integer id) {

        DistributorProtocol distributorProtocol = distributorProtocolMapper.selectByPrimaryKey(id);
        if (distributorProtocol == null) {
            throw new YimaoException("未查询到相关合同信息");
        }
        distributorProtocol.setStationRenewState(1);
        distributorProtocol.setStationRenewTime(new Date());
        distributorProtocolMapper.updateByPrimaryKeySelective(distributorProtocol);
    }

}
