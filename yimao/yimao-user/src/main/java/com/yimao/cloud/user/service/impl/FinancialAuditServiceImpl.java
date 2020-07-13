package com.yimao.cloud.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.system.AppMessageDTO;
import com.yimao.cloud.pojo.dto.system.SmsMessageDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.feign.OrderFeign;
import com.yimao.cloud.user.feign.SystemFeign;
import com.yimao.cloud.user.mapper.*;
import com.yimao.cloud.user.po.*;
import com.yimao.cloud.user.service.DistributorOrderService;
import com.yimao.cloud.user.service.DistributorRoleService;
import com.yimao.cloud.user.service.DistributorService;
import com.yimao.cloud.user.service.FinancialAuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Lizhqiang
 * @date 2018/12/21
 */

@Service
@Slf4j
public class FinancialAuditServiceImpl implements FinancialAuditService {


    @Resource
    private FinancialAuditMapper financialAuditMapper;

    @Resource
    private DistributorOrderAuditRecordMapper distributorOrderAuditRecordMapper;

    @Resource
    private DistributorOrderMapper distributorOrderMapper;

    @Resource
    private UserCache userCache;

    @Resource
    private DistributorMapper distributorMapper;

    @Resource
    private DistributorProtocolMapper distributorProtocolMapper;

    @Resource
    private DistributorRoleMapper distributorRoleMapper;

    @Resource
    private OrderFeign orderFeign;

    @Resource
    private DistributorRoleService distributorRoleService;

    @Resource
    private UserDistributorApplyMapper userDistributorApplyMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private DistributorOrderService distributorOrderService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private DistributorService distributorService;


    /**
     * 财务审核订单页  可以条件查询
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @Override
    public PageVO<FinancialAuditDTO> page(Integer pageNum, Integer pageSize, FinancialAuditDTO query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<FinancialAuditDTO> page = financialAuditMapper.financialAuditPage(query);
        return new PageVO<>(pageNum, page);
    }


    /**
     * 财务审核  状态修改
     *
     * @param orderId
     * @param financialState
     * @param cause
     * @author Liu Long Jie
     * @date 2019-8-22
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void audit(Long orderId, Integer financialState, String cause) {
        if (orderId == null) {
            throw new YimaoException("订单id不能为空");
        }
        if (financialState == null) {
            throw new YimaoException("财务审核状态不能为空");
        }
        DistributorOrder distributorOrder = distributorOrderMapper.selectByPrimaryKey(orderId);
        if (distributorOrder == null) {
            throw new NotFoundException("未找到相关订单信息。");
        }
        if (distributorOrder.getFinancialState() == FinancialStateEnum.PASS_AUDIT.value ||
                distributorOrder.getFinancialState() == FinancialStateEnum.UN_PASS_AUDIT.value) {
            throw new YimaoException("财务审核已审核，请勿频繁操作！");
        }
        Date auditTime=new Date();
        //修改财务审核状态
        distributorOrder.setFinancialState(financialState);
        distributorOrder.setFinancialAuditor(userCache.getCurrentAdminRealName());
        distributorOrder.setFinancialAuditTime(auditTime);

        //如果财务审核通过
        if (financialState == FinancialStateEnum.PASS_AUDIT.value) {
            //支付状态设为已支付
            distributorOrder.setPayState(PayStateEnum.PAY.value);

            //获取企业审核状态
            Integer enterpriseState = distributorOrder.getEnterpriseState();
            if (enterpriseState == null) {
                throw new YimaoException("企业审核状态不能为空");
            }
            //获取用户合同签署状态
            Integer userSignState = getDistributorProtocolByOrderId(distributorOrder.getId());

            boolean status;
            if (userSignState != null && userSignState == DistributorProtocolSignStateEnum.SIGN.value) {
                //用户已签署合同/财务审核已通过或无需审核则为true
                status = enterpriseState == EnterpriseStateEnum.PASS_AUDIT.value || enterpriseState == EnterpriseStateEnum.NO_NEED_AUDIT.value;
            } else if (distributorOrder.getOrderType() == DistributorOrderType.UPGRADE.value && distributorOrder.getDestRoleLevel() == DistributorRoleLevel.D_950.value &&
                    (distributorOrder.getRoleLevel() == DistributorRoleLevel.D_350.value || distributorOrder.getRoleLevel() == DistributorRoleLevel.D_650.value)) {
                //如果是个人版或者微创版经销商升级为企业版经销商，只需补差价无需签署合同，则没有合同签署状态
                status = enterpriseState == EnterpriseStateEnum.PASS_AUDIT.value || enterpriseState == EnterpriseStateEnum.NO_NEED_AUDIT.value;
            } else if (distributorOrder.getOrderType() == DistributorOrderType.UPGRADE.value && distributorOrder.getRoleLevel() == DistributorRoleLevel.D_350.value && distributorOrder.getDestRoleLevel() == DistributorRoleLevel.D_650.value) {
                //微创版升个人版需要判断是否在有效期内升级
                //获取经销商信息
                Distributor distributor = distributorMapper.selectByPrimaryKey(distributorOrder.getDistributorId());
                if (distributor == null) {
                    throw new YimaoException("经销商不存在");
                }
                DistributorRoleDTO distributorRoleDTO = distributorRoleService.getByLevel(distributor.getRoleLevel());
                if (distributorRoleDTO == null) {
                    throw new YimaoException("经销商类型有误");
                }
                boolean boo = distributorOrderService.upgradeValidityTime(distributorRoleDTO.getUpgradeLimitDays(), distributor);
                if (!boo) {
                    //如果微创版经销商超过了升级有效期，则需要签署合同
                    log.error("用户合同未签署，财务审核不能通过");
                    throw new YimaoException("用户合同未签署，财务审核不能通过");
                }
                status = enterpriseState == EnterpriseStateEnum.PASS_AUDIT.value || enterpriseState == EnterpriseStateEnum.NO_NEED_AUDIT.value;
            } else {
                log.error("用户合同未签署，财务审核不能通过");
                throw new YimaoException("用户合同未签署，财务审核不能通过");
            }

            //审核通过发送短信
            if (distributorOrder.getOrderType() != DistributorOrderType.REGISTER.value) {
                //非注册订单，经销商信息可直接从订单中获取
                //获取经销商的手机号
                Distributor query = distributorMapper.selectByPrimaryKey(distributorOrder.getDistributorId());
                if (query == null) {
                    throw new YimaoException("经销商信息不存在!");
                }
                Map<String, String> map = new HashMap<String, String>();
                map.put("#code#", distributorOrder.getDistributorAccount());
                //消息推送
                pushMsgToApp(MessageModelTypeEnum.PASS_AUDIT.value, query.getUserId(), distributorOrder.getDistributorAccount(), MessageModelTypeEnum.PASS_AUDIT.name, MessagePushModeEnum.YIMAO_APP_NOTICE.value, MessageMechanismEnum.QUICK_ONLINE_FINANCIAL_AUDIT_PASS.value, query.getAppType(), map);
            } else {
                //注册订单
                UserDistributorApply userDistributorApply1 = userDistributorApplyMapper.getByOrderId(distributorOrder.getId());
                if (userDistributorApply1 == null) {
                    throw new YimaoException("注册订单需提交经销商申请信息！");
                }
                String account = userDistributorApply1.getCreator();
                User user = userMapper.selectByPrimaryKey(distributorOrder.getCreator());
                if (user == null) {
                    throw new YimaoException("创建人用户信息不存在");
                }
                Distributor distributor = distributorMapper.selectByPrimaryKey(user.getMid());
                Map<String, String> map = new HashMap<String, String>();
                map.put("#code#", account);
                //消息推送
                pushMsgToApp(MessageModelTypeEnum.PASS_AUDIT.value, distributorOrder.getCreator(), account, MessageModelTypeEnum.PASS_AUDIT.name, MessagePushModeEnum.YIMAO_APP_NOTICE.value, MessageMechanismEnum.QUICK_ONLINE_FINANCIAL_AUDIT_PASS.value, distributor.getAppType(), map);
            }

            log.info("status:" + status);
            if (status) {
                //完成订单
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
                        //招商收益分配
                        orderFeign.serviceAllot(distributorOrderDTO);
                    } catch (Exception e) {
                        throw new YimaoException("招商收益分配失败，经销商订单id：：" + orderId);
                    }
                }
            }
        } else {
            //财务审核不通过，改变支付状态
            distributorOrder.setPayState(PayStateEnum.PAY_FAIL.value);
            //财务审核不通过，发送短信
            if (distributorOrder.getOrderType() != DistributorOrderType.REGISTER.value) {
                Distributor distributor = distributorMapper.selectByPrimaryKey(distributorOrder.getDistributorId());
                if (distributor == null) {
                    throw new YimaoException("经销商信息不存在!");
                }
                //非注册订单
                Map<String, String> map = new HashMap<String, String>();
                map.put("#code#", distributorOrder.getDistributorAccount());
                map.put("#code1#", cause);
                //消息推送
                pushMsgToApp(MessageModelTypeEnum.UNPASS_AUDIT.value, distributor.getUserId(), distributorOrder.getDistributorAccount(), MessageModelTypeEnum.UNPASS_AUDIT.name, MessagePushModeEnum.YIMAO_APP_NOTICE.value, MessageMechanismEnum.QUICK_ONLINE_FINANCIAL_AUDIT_UNPASS.value, distributor.getAppType(), map);
            } else {
                //注册订单
                UserDistributorApply userDistributorApply = userDistributorApplyMapper.getByOrderId(distributorOrder.getId());
                if (userDistributorApply == null) {
                    throw new YimaoException("注册订单需提交经销商申请信息！");
                }
                String account = userDistributorApply.getCreator();
                User user = userMapper.selectByPrimaryKey(distributorOrder.getCreator());
                if (user == null) {
                    throw new YimaoException("创建人用户信息不存在");
                }
                Distributor distributor = distributorMapper.selectByPrimaryKey(user.getMid());
                Map<String, String> map = new HashMap<String, String>();
                map.put("#code#", account);
                map.put("#code1#", cause);
                //消息推送
                pushMsgToApp(MessageModelTypeEnum.UNPASS_AUDIT.value, distributorOrder.getCreator(), account, MessageModelTypeEnum.UNPASS_AUDIT.name, MessagePushModeEnum.YIMAO_APP_NOTICE.value, MessageMechanismEnum.QUICK_ONLINE_FINANCIAL_AUDIT_UNPASS.value, distributor.getAppType(), map);
            }
        }

        int count = distributorOrderMapper.updateByPrimaryKeySelective(distributorOrder);
        if (count != 1) {
            throw new YimaoException("修改订单状态失败");
        }

        //审核记录插入
        DistributorOrderAuditRecord record = new DistributorOrderAuditRecord();
        record.setDistributorId(distributorOrder.getDistributorId());
        if (distributorOrder.getRoleId() != null) {
            DistributorRole oriConfiguration = distributorRoleMapper.selectByPrimaryKey(distributorOrder.getRoleId());
            if (oriConfiguration != null) {
                record.setRoleId(oriConfiguration.getId());
                record.setRoleName(oriConfiguration.getName());
            }
        }

        if (distributorOrder.getDestRoleId() != null) {
            DistributorRole destConfiguration = distributorRoleMapper.selectByPrimaryKey(distributorOrder.getDestRoleId());
            if (destConfiguration != null) {
                record.setDestRoleId(destConfiguration.getId());
                record.setDestRoleName(destConfiguration.getName());
            }
        }

        record.setDistributorOrderId(distributorOrder.getId());
        record.setStatus(financialState);
        record.setAuditor(userCache.getCurrentAdminRealName());
        record.setAuditTime(auditTime);
        record.setAuditType(0);
        record.setCause(cause);

        int insert = distributorOrderAuditRecordMapper.insert(record);
        if (insert < 1) {
            throw new YimaoException("操作失败。");
        }
    }

    private void userInfoConvert(User user, UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setUserName(user.getUserName());
        userDTO.setMobile(user.getMobile());
    }

    //消息推送
    private void pushMsgToApp(Integer pushType, Integer receiverId, String receiverName, String title, Integer pushMode, Integer mechanism, Integer devices, Map<String, String> distributorMessage) {
        //构建APP消息推送实体
        AppMessageDTO appMessage = new AppMessageDTO();
        appMessage.setMsgType(Constant.MSG_TYPE_NOTICE);
        appMessage.setPushType(pushType);
        appMessage.setCreateTime(new Date());
        appMessage.setReceiverId(receiverId);
        appMessage.setReceiver(receiverName);
        appMessage.setTitle(title);
        appMessage.setApp(pushMode);//1-推送给安装工；2-推送消息给经销商
        appMessage.setMechanism(mechanism);
        appMessage.setDevices(devices);
        appMessage.setContentMap(distributorMessage);
        //2-推送消息给经销商
        appMessage.setPushObject(MessagePushObjectEnum.DISTRIBUTOR.value);
        rabbitTemplate.convertAndSend(RabbitConstant.YIMAO_APP_MESSAGE_PUSH, appMessage);
    }


    /**
     *
     * @param distributorOrderId
     * @param orderType
     * @param name
     * @param distributorAccount
     * @param roleId
     * @param destRoleId
     * @param payType
     * @param payStartTime
     * @param payEndTime
     * @return
     */
    @Override
    public List<FinancialAuditExportDTO> exportFinancialAudit(Long distributorOrderId, Integer orderType, String name, String distributorAccount, Integer roleId, Integer destRoleId, Integer payType, String payStartTime, String payEndTime) {
        //查询所有符合条件的财务审核信息
        List<FinancialAuditExportDTO> queryList = financialAuditMapper.listFinancialAudit(distributorOrderId, orderType, name, distributorAccount, roleId, destRoleId, payType, payStartTime, payEndTime);
        if (CollectionUtil.isEmpty(queryList)) {
            throw new YimaoException("没有找到财务审核信息");
        }
        return queryList;
    }

    /**
     * 财务审核批量
     *
     * @param orderIds
     * @param financialState
     */
    @Override
    public void auditBatch(List<Long> orderIds, Integer financialState, String cause) {
        if (CollectionUtil.isEmpty(orderIds)) {
            throw new YimaoException("订单集合不能为空");
        }
        orderIds.stream().forEach(o -> audit(o, financialState, cause));
    }

    private Integer getDistributorProtocolByOrderId(Long orderId) {
        Example example = new Example(DistributorProtocol.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        DistributorProtocol distributorProtocol = distributorProtocolMapper.selectOneByExample(example);
        if (distributorProtocol != null) {
            return distributorProtocol.getUserSignState();
        }
        return null;
    }

}
