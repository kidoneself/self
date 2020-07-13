package com.yimao.cloud.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.ThreadUtil;
import com.yimao.cloud.pojo.dto.system.AppMessageDTO;
import com.yimao.cloud.pojo.dto.system.SmsMessageDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.feign.OrderFeign;
import com.yimao.cloud.user.mapper.*;
import com.yimao.cloud.user.po.*;
import com.yimao.cloud.user.service.DistributorOrderService;
import com.yimao.cloud.user.service.DistributorService;
import com.yimao.cloud.user.service.UserCompanyApplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Slf4j
@Service
public class UserCompanyApplyServiceImpl implements UserCompanyApplyService {

    @Resource
    private UserCompanyApplyMapper userCompanyApplyMapper;

    @Resource
    private DistributorOrderAuditRecordMapper auditRecordMapper;

    @Resource
    private DistributorMapper distributorMapper;

    @Resource
    private DistributorOrderMapper distributorOrderMapper;

    @Resource
    private DistributorRoleMapper configurationMapper;

    @Resource
    private DistributorProtocolMapper distributorProtocolMapper;

    @Resource
    private OrderFeign orderFeign;

    @Resource
    private UserDistributorApplyMapper userDistributorApplyMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private DistributorOrderAuditRecordMapper distributorOrderAuditRecordMapper;

    @Resource
    private DistributorOrderService distributorOrderService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private DistributorService distributorService;

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserCompany
     * @description 根据ID查询经企业详情
     * @author Liu Yi
     * @date 2019/8/23 14:26
     */
    public UserCompanyApply getCompanyById(Integer id) {
        if (id == null) {
            throw new BadRequestException("id必须传参!");
        }
        UserCompanyApply userCompany = userCompanyApplyMapper.selectByPrimaryKey(id);
        return userCompany;
    }

    @Override
    public UserCompanyApplyDTO getCompanyByOrderId(Long orderId) {
        if (orderId == null) {
            throw new BadRequestException("订单id必须传参!");
        }

        Example example = new Example(UserCompanyApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        UserCompanyApply userCompany = userCompanyApplyMapper.selectOneByExample(example);
        if (userCompany == null) {
            return null;
        }

        UserCompanyApplyDTO dto = new UserCompanyApplyDTO();
        userCompany.convert(dto);

        List<DistributorOrderAuditRecordDTO> distributorOrderAuditRecords = distributorOrderAuditRecordMapper.getDistributorOrderAuditRecordByOrderId(orderId);
        if (CollectionUtil.isNotEmpty(distributorOrderAuditRecords)) {
            //获取最近一次审核记录
            DistributorOrderAuditRecordDTO distributorOrderAuditRecord = distributorOrderAuditRecords.get(0);
            dto.setEnterpriseState(distributorOrderAuditRecord.getStatus());
            dto.setAuditor(distributorOrderAuditRecord.getAuditor());
            dto.setAuditTime(distributorOrderAuditRecord.getAuditTime());
            dto.setCause(distributorOrderAuditRecord.getCause());
        }

        return dto;
    }

    @Override
    public UserCompanyApplyDTO getCompanyByDistributorId(Integer id) {
        UserCompanyApplyDTO userCompanyDTO = new UserCompanyApplyDTO();
        String userName;
        UserCompanyApply userCompany;
        DistributorOrderAuditRecord distributorOrderAuditRecord;
        try {
            // 企业信息
            Example example = new Example(UserCompanyApply.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("distributorId", id);
            Callable<UserCompanyApply> companyCallable = () -> userCompanyApplyMapper.selectOneByExample(example);
            FutureTask<UserCompanyApply> userCompanyFutureTask = new FutureTask<>(companyCallable);
            ThreadUtil.executor.submit(userCompanyFutureTask);

            // 审核信息
            Example example1 = new Example(DistributorOrderAuditRecord.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("distributorId", id);
            Callable<DistributorOrderAuditRecord> recordCallable = () -> auditRecordMapper.selectOneByExample(example1);
            FutureTask<DistributorOrderAuditRecord> futureTask = new FutureTask<>(recordCallable);
            ThreadUtil.executor.submit(futureTask);

            // 经销商信息
            Callable<Distributor> distributorCallable = () -> distributorMapper.selectByPrimaryKey(id);
            FutureTask<Distributor> distributorFutureTask = new FutureTask<>(distributorCallable);
            ThreadUtil.executor.submit(distributorFutureTask);

            userCompany = userCompanyFutureTask.get();

            distributorOrderAuditRecord = futureTask.get();

            userName = distributorFutureTask.get().getUserName();


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NotFoundException("查询出错，请稍后重试。");
        }

        if (userCompany != null) {
            userCompany.convert(userCompanyDTO);
        }
        if (userName != null) {
            userCompanyDTO.setDistributorAccount(userName);
        }
        if (distributorOrderAuditRecord != null) {
            //userCompanyDTO.setOrderId(distributorOrderAuditRecord.getDistributorOrderId());
            userCompanyDTO.setEnterpriseState(distributorOrderAuditRecord.getStatus());
            userCompanyDTO.setCause(distributorOrderAuditRecord.getCause());
            userCompanyDTO.setAuditor(distributorOrderAuditRecord.getAuditor());
            userCompanyDTO.setAuditTime(distributorOrderAuditRecord.getAuditTime());
        }
        return userCompanyDTO;
    }

    @Override
    public PageVO<UserCompanyApplyDTO> pageQuery(Integer pageNum, Integer pageSize, Long orderId, Integer orderType, String companyName, String account, Integer roleLevel) {
        PageHelper.startPage(pageNum, pageSize);
        Page<UserCompanyApplyDTO> page = userCompanyApplyMapper.pageQuery(orderId, orderType, companyName, account, roleLevel);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 企业审核
     *
     * @param updater
     * @param id              订单号
     * @param enterpriseState 审核是否通过
     * @return void
     * @author liulongjie
     * @date 2019/9/2
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void audit(String updater, Long id, Integer enterpriseState, String cause) {
        if (id == null) {
            throw new YimaoException("订单id不能为空");
        }
        if (enterpriseState == null) {
            throw new YimaoException("企业审核状态不能为空");
        }
        DistributorOrder distributorOrder = distributorOrderMapper.selectByPrimaryKey(id);
        if (distributorOrder == null) {
            throw new NotFoundException("未获取审核信息!");
        }
        if (distributorOrder.getEnterpriseState() == EnterpriseStateEnum.PASS_AUDIT.value ||
                distributorOrder.getEnterpriseState() == EnterpriseStateEnum.UN_PASS_AUDIT.value) {
            throw new YimaoException("企业审核已审核，请勿频繁操作！");
        }
        distributorOrder.setEnterpriseState(enterpriseState);
        Date auditTime = new Date();
        //修改财务审核状态
        distributorOrder.setEnterpriseAuditor(updater);
        distributorOrder.setEnterpriseAuditTime(auditTime);

        if (enterpriseState == EnterpriseStateEnum.PASS_AUDIT.value) {

            // 如果财务审核通过/无须审核 + 企业审核通过 + 合同 --> 订单状态完成
            Integer financialState = distributorOrder.getFinancialState();
            if (financialState == null) {
                throw new YimaoException("用户还未支付，企业审核不能通过!");
            }
            //获取用户签署状态
            Integer userSignState = getDistributorProtocolByOrderId(id);

            boolean status;
            if (userSignState != null && userSignState == DistributorProtocolSignStateEnum.SIGN.value) {
                //用户已签署合同/财务审核已通过或无需审核则为true
                status = financialState == FinancialStateEnum.PASS_AUDIT.value || financialState == FinancialStateEnum.NO_NEED_AUDIT.value;
            } else if (distributorOrder.getOrderType() == DistributorOrderType.UPGRADE.value && distributorOrder.getDestRoleLevel() == DistributorRoleLevel.D_950.value &&
                    (distributorOrder.getRoleLevel() == DistributorRoleLevel.D_350.value || distributorOrder.getRoleLevel() == DistributorRoleLevel.D_650.value)) {
                //如果是个人版或者微创版经销商升级为企业版经销商，只需补差价无需签署合同，则没有合同签署状态
                status = financialState == FinancialStateEnum.PASS_AUDIT.value || financialState == FinancialStateEnum.NO_NEED_AUDIT.value;
            } else {
                log.error("用户合同未签署，企业审核不能通过");
                throw new YimaoException("用户合同未签署，企业审核不能通过");
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
                pushMsgToApp(MessageModelTypeEnum.PASS_AUDIT.value, query.getUserId(), distributorOrder.getDistributorAccount(), MessageModelTypeEnum.PASS_AUDIT.name, MessagePushModeEnum.YIMAO_APP_NOTICE.value, MessageMechanismEnum.QUICK_ONLINE_AUDIT_PASS.value, query.getAppType(), map);
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
                //消息推送
                pushMsgToApp(MessageModelTypeEnum.PASS_AUDIT.value, distributorOrder.getCreator(), account, MessageModelTypeEnum.PASS_AUDIT.name, MessagePushModeEnum.YIMAO_APP_NOTICE.value, MessageMechanismEnum.QUICK_ONLINE_AUDIT_PASS.value, distributor.getAppType(), map);
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
                        userInfoConvert(user,userDTO);
                        distributorOrderDTO.setUser(userDTO);
                        DistributorDTO recommendDistributor = distributorService.getRecommendByDistributorId(distributorDTO.getId());
                        distributorOrderDTO.setRecommendDistributor(recommendDistributor);
                        //招商收益分配
                        orderFeign.serviceAllot(distributorOrderDTO);
                    } catch (Exception e) {
                        throw new YimaoException("招商收益分配失败，经销商订单id：：" + id);
                    }
                }
            }
        } else {
            //企业审核不通过，发送短信提醒
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
                pushMsgToApp(MessageModelTypeEnum.UNPASS_AUDIT.value, distributor.getUserId(), distributorOrder.getDistributorAccount(), MessageModelTypeEnum.UNPASS_AUDIT.name, MessagePushModeEnum.YIMAO_APP_NOTICE.value, MessageMechanismEnum.QUICK_ONLINE_AUDIT_UNPASS.value, distributor.getAppType(), map);
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
                pushMsgToApp(MessageModelTypeEnum.UNPASS_AUDIT.value, distributorOrder.getCreator(), account, MessageModelTypeEnum.UNPASS_AUDIT.name, MessagePushModeEnum.YIMAO_APP_NOTICE.value, MessageMechanismEnum.QUICK_ONLINE_AUDIT_UNPASS.value, distributor.getAppType(), map);
            }
        }
        int result = distributorOrderMapper.updateByPrimaryKeySelective(distributorOrder);
        if (result != 1) {
            throw new YimaoException("操作失败。");
        }
        // 插入审核记录表
        DistributorOrderAuditRecord record = new DistributorOrderAuditRecord();
        record.setDistributorId(distributorOrder.getDistributorId());
        if (distributorOrder.getRoleId() != null) {
            DistributorRole oriConfiguration = configurationMapper.selectByPrimaryKey(distributorOrder.getRoleId());
            if (oriConfiguration != null) {
                record.setRoleId(oriConfiguration.getId());
                record.setRoleName(oriConfiguration.getName());
            }
        }

        if (distributorOrder.getDestRoleId() != null) {
            DistributorRole destConfiguration = configurationMapper.selectByPrimaryKey(distributorOrder.getDestRoleId());
            if (destConfiguration != null) {
                record.setDestRoleId(destConfiguration.getId());
                record.setDestRoleName(destConfiguration.getName());
            }
        }

        record.setDistributorOrderId(distributorOrder.getId());
        record.setStatus(enterpriseState);
        record.setAuditor(updater);
        record.setAuditTime(auditTime);
        record.setAuditType(1);
        record.setCause(cause);
        int insert = auditRecordMapper.insert(record);
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
     * @param
     * @return void
     * @description 新增公司信息
     * @author Liu Yi
     * @date 2019/8/21 17:42
     */
    @Override
    public void insert(UserCompanyApply userCompany) {
        if (userCompany.getCompanyType() == null) {
            throw new BadRequestException("公司类型不能为空！");
        }
        if (userCompany.getIndustry() == null) {
            throw new BadRequestException("请选择行业类型！");
        }
        if (StringUtil.isBlank(userCompany.getCompanyName())) {
            throw new BadRequestException("请填写公司名称！");
        }
        if (StringUtil.isBlank(userCompany.getCreditCode())) {
            throw new BadRequestException("请填写信用代码！");
        }
        if (StringUtil.isBlank(userCompany.getTaxInformation())) {
            throw new BadRequestException("请填写税务信息！");
        }
        if (StringUtil.isBlank(userCompany.getCorporateRepresentative())) {
            throw new BadRequestException("请填写法人代表！");
        }
        if (StringUtil.isBlank(userCompany.getBankAccount())) {
            throw new BadRequestException("请填写银行账号！");
        }
        if (StringUtil.isBlank(userCompany.getBank())) {
            throw new BadRequestException("请填写开户银行！");
        }
        if (StringUtil.isBlank(userCompany.getPhone())) {
            throw new BadRequestException("请填写联系电话！");
        }
        if (StringUtil.isBlank(userCompany.getEmail())) {
            throw new BadRequestException("请填写公司邮箱！");
        }
        if (StringUtil.isBlank(userCompany.getAddress())) {
            throw new BadRequestException("请填写公司地址！");
        }
        if (StringUtil.isBlank(userCompany.getBusinessLicense())) {
            throw new BadRequestException("请填写营业执照！");
        }

        userCompanyApplyMapper.insert(userCompany);
    }

    @Override
    public void update(UserCompanyApplyDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("id不能为空！");
        }
        if (dto.getCompanyType() == null) {
            throw new BadRequestException("公司类型不能为空！");
        }
        if (dto.getIndustry() == null) {
            throw new BadRequestException("请选择行业类型！");
        }
        if (StringUtil.isBlank(dto.getCompanyName())) {
            throw new BadRequestException("请填写公司名称！");
        }
        if (StringUtil.isBlank(dto.getCreditCode())) {
            throw new BadRequestException("请填写信用代码！");
        }
        if (StringUtil.isBlank(dto.getTaxInformation())) {
            throw new BadRequestException("请填写税务信息！");
        }
        if (StringUtil.isBlank(dto.getCorporateRepresentative())) {
            throw new BadRequestException("请填写法人代表！");
        }
        if (StringUtil.isBlank(dto.getBankAccount())) {
            throw new BadRequestException("请填写银行账号！");
        }
        if (StringUtil.isBlank(dto.getBank())) {
            throw new BadRequestException("请填写开户银行！");
        }
        if (StringUtil.isBlank(dto.getPhone())) {
            throw new BadRequestException("请填写联系电话！");
        }
        if (StringUtil.isBlank(dto.getEmail())) {
            throw new BadRequestException("请填写公司邮箱！");
        }
        if (StringUtil.isBlank(dto.getAddress())) {
            throw new BadRequestException("请填写公司地址！");
        }
        if (StringUtil.isBlank(dto.getBusinessLicense())) {
            throw new BadRequestException("请填写营业执照！");
        }
        UserCompanyApply userCompanyApply = new UserCompanyApply(dto);
        userCompanyApplyMapper.updateByPrimaryKeySelective(userCompanyApply);
    }

    @Override
    public List<CompanyAuditExportDTO> exportUserCompanyApplyAudit(Long orderId, Integer orderType, String companyName, String account, Integer roleLevel) {
        List<CompanyAuditExportDTO> list = userCompanyApplyMapper.listCompanyAudit(orderId, orderType, companyName, account, roleLevel);
        if (CollectionUtil.isEmpty(list)) {
            throw new YimaoException("没有找到企业审核信息");
        }
        return list;
    }

    @Override
    public void auditBatch(String updater, List<Long> ids, Integer enterpriseState, String cause) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new YimaoException("订单集合不能为空");
        }
        ids.forEach(o -> audit(updater, o, enterpriseState, cause));
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
