package com.yimao.cloud.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.DialogException;
import com.yimao.cloud.base.exception.NoLoginException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.handler.JWTHandler;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.properties.WechatProperties;
import com.yimao.cloud.base.service.SmsService;
import com.yimao.cloud.base.utils.BeanHelper;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.CookieUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.HttpUtil;
import com.yimao.cloud.base.utils.ImageUtil;
import com.yimao.cloud.base.utils.JsonUtil;
import com.yimao.cloud.base.utils.MD5Util;
import com.yimao.cloud.base.utils.SFTPUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.ThreadUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.order.ProductIncomeRecordDTO;
import com.yimao.cloud.pojo.dto.order.SalesProductDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.station.UserStatisticsDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.dto.wechat.WxUserInfo;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.query.station.UserQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.UserGeneralSituationVO;
import com.yimao.cloud.pojo.vo.station.UserVO;
import com.yimao.cloud.user.enums.StateEnum;
import com.yimao.cloud.user.feign.HraFeign;
import com.yimao.cloud.user.feign.OrderFeign;
import com.yimao.cloud.user.feign.SystemFeign;
import com.yimao.cloud.user.feign.WechatFeign;
import com.yimao.cloud.user.mapper.*;
import com.yimao.cloud.user.po.*;
import com.yimao.cloud.user.service.UserIncomeAccountService;
import com.yimao.cloud.user.service.UserService;
import com.yimao.cloud.user.service.WaterDeviceUserService;
import com.yimao.cloud.user.utils.ObjectUtil;
import com.yimao.cloud.user.utils.WxUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private WechatProperties wechatProperties;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserAuthsMapper userAuthsMapper;
    @Resource
    private DistributorMapper distributorMapper;
    @Resource
    private AmqpTemplate amqpTemplate;
    @Resource
    private UserCache userCache;
    @Resource
    private RedisCache redisCache;
    @Resource
    private JWTHandler jwtHandler;
    @Resource
    private UserCompanyMapper userCompanyMapper;
    @Resource
    private SmsService smsService;
    @Resource
    private UserChangeMapper userChangeMapper;
    @Resource
    private HraFeign hraFeign;
    @Resource
    private UserAliasMapper userAliasMapper;
    @Resource
    private DistributorServiceImpl distributorService;
    @Resource
    private UserIncomeAccountService userIncomeAccountService;
    @Resource
    private WaterDeviceUserService waterDeviceUserService;

    @Resource
    private SystemFeign systemFeign;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private WechatFeign wechatFeign;

    @Resource
    private CompanyCustomerMapper companyCustomerMapper;

    /**
     * 用户登录
     *
     * @param openid openid
     * @return
     */
    @Override
    public UserDTO loginByOpenid(String openid, Integer system) {
        UserAuths userAuths = userAuthsMapper.selectByOpenid(openid.trim(), IdentityTypeEnum.WECHAT_GZH.value);
        if (userAuths == null) {
            //公众号和H5的授权openid是一样的，所以两个都要查询一下，关注过公众号或H5授权过要都可以登录
            userAuths = userAuthsMapper.selectByOpenid(openid.trim(), IdentityTypeEnum.H5.value);
        }
        if (userAuths == null) {
            throw new NotFoundException("登录失败，请取关公众号重新关注。");
        }
        Integer userId = userAuths.getUserId();
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new NotFoundException("登录失败，请取关公众号重新关注。");
        }
        //如果是翼猫系统用户，进行分销身份检查、设置
        //分销身份判定
        if (StringUtil.isNotEmpty(user.getMobile())) {
            this.saveIncomePermission(user.getMobile(), user);
        }
        return this.getUserInfoWithJWTToken(user, system);
    }

    /**
     * 为了提高效率，部分场景只需要获取用户基础信息
     *
     * @param id 用户ID
     * @return
     */
    @Override
    public UserDTO getBasicUserById(Integer id) {
        return userMapper.selectBasicUserById(id);
    }

    /**
     * 获取用户信息，并设置相关额外属性
     *
     * @param userId 用户ID
     * @return
     */
    @Override
    public UserDTO getFullUserDTOById(Integer userId) {
        // 先从缓存中获取用户信息
        // UserDTO userDTO = redisCache.get(Constant.USER_CACHE + userId, UserDTO.class);
        // if (Objects.nonNull(userDTO)) {
        //     return userDTO;
        // }
        User user = userMapper.selectByPrimaryKey(userId);
        if (Objects.isNull(user)) {
            throw new NotFoundException("未获取到用户信息");
        }
        return this.getFullUserDTOById(user);
    }

    /**
     * 获取用户信息，并设置相关额外属性
     *
     * @param user 用户信息
     * @return
     */
    // @Cacheable(key = "#p0")
    @Override
    public UserDTO getFullUserDTOById(User user) {
        // 先从缓存中获取用户信息
        // UserDTO userDTO = redisCache.get(Constant.USER_CACHE + user.getId(), UserDTO.class);
        // if (Objects.nonNull(userDTO)) {
        //     return userDTO;
        // }
        boolean disUser = UserType.isDistributor(user.getUserType());
        Integer userId = user.getId();//用户e家号
        Integer distributorId = user.getDistributorId();//经销商ID
        Integer ambassadorId = user.getAmbassadorId();//健康大使ID

        List<UserAuths> userAuthsList;
        Distributor distributor = null;
        Distributor recommend = null;
        User ambassador = null;

        try {
            // 1-异步编程，获取授权账号列表
            Callable<List<UserAuths>> callable1 = () -> userAuthsMapper.listByUserId(userId, null);
            FutureTask<List<UserAuths>> task1 = new FutureTask<>(callable1);
            FutureTask<Distributor> task2 = null;
            FutureTask<Distributor> task3 = null;
            FutureTask<User> task4 = null;

            // 2-异步编程，获取经销商信息
            if (distributorId != null) {
                Callable<Distributor> callable2 = () -> distributorMapper.getDistributorByIdForFillIntoUser(distributorId);
                task2 = new FutureTask<>(callable2);
            }
            // 3-异步编程，获取健康大使信息
            if (ambassadorId != null) {
                if (disUser) {
                    // 经销商的健康大使是他的推荐人
                    Callable<Distributor> callable3 = () -> distributorMapper.getDistributorByIdForFillIntoUser(ambassadorId);
                    task3 = new FutureTask<>(callable3);
                } else {// 其它用户的健康大使是分享给他二维码的人
                    Callable<User> callable4 = () -> userMapper.selectByIdForFillIntoUser(ambassadorId);
                    task4 = new FutureTask<>(callable4);
                }
            }


            ThreadUtil.executor.submit(task1);
            if (task2 != null) {
                ThreadUtil.executor.submit(task2);
            }
            if (task3 != null) {
                ThreadUtil.executor.submit(task3);
            }
            if (task4 != null) {
                ThreadUtil.executor.submit(task4);
            }
            userAuthsList = task1.get();
            if (task2 != null) {
                distributor = task2.get();
            }
            if (task3 != null) {
                recommend = task3.get();
            }
            if (task4 != null) {
                ambassador = task4.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            throw new NotFoundException("查询出错，请稍后重试。");
        }

        UserDTO userDTO = new UserDTO();
        user.convert(userDTO);

        // 为用户设置第三方授权账号信息，如openid等
        if (CollectionUtil.isNotEmpty(userAuthsList)) {
            for (UserAuths auths : userAuthsList) {
                if (Objects.equals(auths.getIdentityType(), IdentityTypeEnum.WECHAT_GZH.value)
                        || Objects.equals(auths.getIdentityType(), IdentityTypeEnum.H5.value)) {//健康e家微信公众号openid、H5
                    userDTO.setOpenid(auths.getIdentifierUnique());
                } else if (Objects.equals(auths.getIdentityType(), IdentityTypeEnum.HEALTH_MINI.value)) {//健康自测小程序openid
                    userDTO.setHealthyOpenid(auths.getIdentifierUnique());
                }
            }
        }
        // 为用户设置经销商信息
        if (distributor != null) {
            userDTO.setDistributorAccount(distributor.getUserName());//经销商账号
            userDTO.setDistributorNickName(distributor.getNickName());
            userDTO.setDistributorName(distributor.getRealName());//经销商姓名
            userDTO.setDistributorPhone(distributor.getPhone());//经销商手机号
            userDTO.setDistributorIdCard(distributor.getIdCard());//经销商身份证号
            userDTO.setDistributorType(distributor.getType());//经销商类型
            userDTO.setDistributorTypeName(distributor.getRoleName());
            if (distributor.getCompanyId() != null) {
                //企业版经销商企业信息
                UserCompany userCompany = userCompanyMapper.selectByPrimaryKey(distributor.getCompanyId());
                UserCompanyDTO companyDTO = new UserCompanyDTO();
                userCompany.convert(companyDTO);
                userDTO.setUserCompany(companyDTO);
            }
        }
        // 为用户设置健康大使信息
        if (disUser && recommend != null) {
            userDTO.setAmbassadorName(recommend.getRealName());
        } else if (ambassador != null) {
            userDTO.setAmbassadorName(ambassador.getRealName());//健康大使名称
            userDTO.setAmbassadorNickName(ambassador.getNickName());//健康大使昵称
            userDTO.setAmbassadorPhone(ambassador.getMobile());//健康大使手机号
            userDTO.setAmbassadorUserType(ambassador.getUserType());//健康大使用户等级
        }

        //设置用户当前登录账号的多重身份
        this.getUserIdentityName(userDTO);
        //设置当前用户的服务站名称
        this.setStationNameToUser(userDTO);
        // 将用户信息设置到缓存中
        // redisCache.set(Constant.USER_CACHE + userDTO.getId(), userDTO);
        return userDTO;
    }

    // @CachePut(key = "#p0.id")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateUser(UserDTO userDTO) {
        //查询原始账号
        User originUser = userMapper.selectByPrimaryKey(userDTO.getId());
        //这里重新设置时为了确保userType等重要信息不会因为前端传递了错误的或者过期的数据而被修改
        User update = new User();
        update.setId(userDTO.getId());
        String nickName = userDTO.getNickName();
        nickName = WxUtil.filterNickName(nickName);
        update.setNickName(StringUtil.isEmpty(nickName) ? "未设置昵称" : nickName);
        update.setRealName(userDTO.getRealName());
        update.setMobile(userDTO.getMobile());
        update.setIdCard(userDTO.getIdCard());
        update.setSex(userDTO.getSex());
        update.setProvince(userDTO.getProvince());
        update.setCity(userDTO.getCity());
        update.setRegion(userDTO.getRegion());
        update.setAddress(userDTO.getAddress());
        update.setAge(userDTO.getAge());
        update.setBirthday(userDTO.getBirthday());
        update.setEmail(userDTO.getEmail());

        int i = userMapper.updateByPrimaryKeySelective(update);
        if (i < 1) {
            throw new YimaoException("操作失败。");
        }
        //是否绑定经销商
        if (Objects.nonNull(originUser.getMid())) {
            //同步更新经销商数据
            Distributor updatedistributor = new Distributor();
            updatedistributor.setId(originUser.getMid());
            updatedistributor.setRealName(update.getRealName());
            updatedistributor.setSex(update.getSex());
            updatedistributor.setPhone(update.getMobile());
            updatedistributor.setIdCard(update.getIdCard());
            log.info("同步更新经销商数据={}", JSON.toJSONString(updatedistributor));
            distributorMapper.updateByPrimaryKeySelective(updatedistributor);
        }

        //保存变更纪录
        saveUserChangeRecord(originUser, update);
    }

    private void saveUserChangeRecord(User originUser, User updateUser) {
        Integer count = 0;

        UserChangeRecord changeRecord = new UserChangeRecord();
        changeRecord.setOrigUserId(updateUser.getId());
        changeRecord.setTime(new Date());
        changeRecord.setTerminal(3);
        changeRecord.setType(UserChangeRecordEnum.EDIT.value);
        try {
            changeRecord.setCreator(userCache.getCurrentAdminRealName());
        } catch (Exception e) {
            if (e instanceof NoLoginException) {
                log.info("未登录下，执行保存变更纪录[更新用户]");
            }
        }

        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("nickName");
        fieldNames.add("realName");
        fieldNames.add("mobile");
        fieldNames.add("idCard");
        fieldNames.add("sex");
        fieldNames.add("province");
        fieldNames.add("city");
        fieldNames.add("region");
        fieldNames.add("address");
        count = ObjectUtil.compareObject(originUser, updateUser, fieldNames);
        changeRecord.setOrigUserData(JSON.toJSONString(originUser));
        changeRecord.setDestUserData(JSON.toJSONString(updateUser));

        if (count > 0) {
            //查看变更的数据
            userChangeMapper.insert(changeRecord);
        }

    }

    @Override
    public void updateUserPart(User user) {
        int i = userMapper.updateByPrimaryKeySelective(user);
        // if (i > 0) {
        //     // 删除缓存中的用户信息
        //     redisCache.delete(Constant.USER_CACHE + user.getId());
        //     // 更新缓存中的用户信息
        //     this.getFullUserDTOById(user.getId());
        // }
    }

    /**
     * 用户信息业务处理方法
     * 根据用户授权信息中的唯一ID查找授权信息再查找用户信息
     * 没有用户信息则创建，没有授权信息则创建
     *
     * @param identityType
     * @param wxUserInfo
     * @param sharerId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public UserDTO userProcess(Integer identityType, Integer sharerId, WxUserInfo wxUserInfo, Integer origin) {
        log.info("**************传入参数：identityType=" + identityType + ",wxUserInfo:" + JsonUtil.objectToJson
                (wxUserInfo) + "***********");
        //如果小程序没有获取到unionid，则返回空的用户信息信息
        if (StringUtil.isEmpty(wxUserInfo.getUnionid())) {
            return null;
        }
        User user;
        // boolean newUser = false;
        Date nowDate = new Date();
        List<Integer> userIdList = userAuthsMapper.listUserIdByUnionid(wxUserInfo.getUnionid());
        if (CollectionUtil.isEmpty(userIdList)) {
            // newUser = true;
            user = new User();
            user.setSubscribe(true);//关注
            //先设置一个默认值
            user.setUserType(UserType.USER_4.value);
            user.setUserTypeName(UserType.USER_4.name);
            log.info("用户的昵称是======" + wxUserInfo.getNickname());
            String nickName = WxUtil.filterNickName(wxUserInfo.getNickname());
            user.setNickName(StringUtil.isEmpty(nickName) ? "未设置昵称" : nickName);
            // user.setNickName("未设置昵称");
            user.setSex(wxUserInfo.getSex());
            user.setHeadImg(this.saveWechatHeadImgToLocal(wxUserInfo.getHeadimgurl()));
            user.setAvailable(true);
            user.setCreateTime(nowDate);
            // 用户来源
            user.setOrigin(origin);
            user.setOriginTerminal(Terminal.WECHAT.value);
            //来源端：1-健康e家公众号 2-经销商APP 3-净水设备
            if (origin != null && origin == OriginEnum.DISABLE_SCREEN.value) {
                user.setOriginTerminal(Terminal.WATER_DEVICE.value);
            }
            userMapper.insertSelective(user);
            UserChangeRecordDTO dto = new UserChangeRecordDTO(user.getId(), 1, user.getUserType(), user.getMobile(),
                    "创建用户");
            amqpTemplate.convertAndSend(RabbitConstant.USER_CHANGE_QUEUE, dto);

            //创建用户授权信息
            UserAuths userAuth = new UserAuths();
            userAuth.setIdentifierUnique(wxUserInfo.getOpenid());
            userAuth.setIdentifier(wxUserInfo.getUnionid());
            userAuth.setIdentityType(identityType);
            userAuth.setState(StateEnum.BIND.value());
            userAuth.setCreateTime(nowDate);
            userAuth.setUserId(user.getId());
            userAuthsMapper.insertSelective(userAuth);
        } else {
            Integer userId = userIdList.get(0);
            UserAuths auths = userAuthsMapper.selectByOpenid(wxUserInfo.getOpenid(), identityType);
            if (auths == null) {
                auths = new UserAuths();
                auths.setIdentifierUnique(wxUserInfo.getOpenid());
                auths.setIdentifier(wxUserInfo.getUnionid());
                auths.setIdentityType(identityType);
                auths.setState(StateEnum.BIND.value());
                auths.setCreateTime(nowDate);
                auths.setUserId(userId);
                userAuthsMapper.insertSelective(auths);
            }
            user = userMapper.selectByPrimaryKey(userId);
            if (user == null) {
                log.error("创建或获取用户数据失败");
                return null;
            }
        }
        //设置用户的健康大使信息
        this.fillUserAmbassador(user, sharerId);
        // 将用户信息从缓存中删除，因为信息已经更新，this.getFullUserDTOById(user.getId())会执行更新动作
        // redisCache.delete(Constant.USER_CACHE + user.getId());
        //前面的步骤可能已有数据变更，所以要重新查询
        return this.getFullUserDTOById(user.getId());
    }

    /**
     * 将微信头像保存到本地服务器上
     */
    private String saveWechatHeadImgToLocal(String headimgurl) {
        //将微信头像保存到本地服务器存储，避免切换微信头像后，头像显示不了
        try {
            File file = ImageUtil.writeImageToDisk(headimgurl, Constant.IMAGE_TEMP_FOLDER, "headImage_template.png");
            if (file != null && file.exists()) {
                String url = SFTPUtil.upload(new FileInputStream(file), "headImages", "temp.png", null);
                return StringUtil.isNotEmpty(url) ? url : Constant.DEFAULT_HEADIMAGE;
            }
        } catch (Exception e) {
            log.error("将微信头像保存到本地服务器上出错，headimgurl={}", headimgurl);
        }
        return Constant.DEFAULT_HEADIMAGE;
    }

    /**
     * 设置用户的健康大使信息
     */
    private void fillUserAmbassador(User user, Integer sharerId) {
        //分享者ID不为空且分享者不是本人且是普通用户才会建立分享关系
        if (sharerId != null && !Objects.equals(user.getId(), sharerId) && user.getUserType() == UserType.USER_4.value) {
            Date now = new Date();
            UserDTO oldSharer = null;
            if (user.getAmbassadorId() != null) {
                oldSharer = userMapper.selectBasicUserById(user.getAmbassadorId());
            }
            UserDTO sharer = userMapper.selectBasicUserById(sharerId);
            //普通用户只能拉新，非普通用户才能拉普通用户
            if (sharer != null && sharer.getUserType() != UserType.USER_4.value) {
                user.setAmbassadorId(sharerId);
                user.setBeSharerTime(now);
                //为用户设置经销商
                if (UserType.isDistributor(sharer.getUserType())) {
                    user.setDistributorId(sharer.getMid());
                } else {
                    user.setDistributorId(sharer.getDistributorId());
                }
                //如果分享者不为普通用户，设置用户为分享用户
                if (sharer.getUserType() != UserType.USER_4.value) {
                    //当分享者不是普通用户时，将用户类型设置为分享用户
                    user.setUserType(UserType.USER_3.value);
                    user.setUserTypeName(UserType.USER_3.name);
                    user.setBindAmbassadorTime(now);
                    //将该用户的所有推广客户的用户类型也变成分享用户
                    this.changeCustomersToShareUser(user.getId(), user.getDistributorId());
                }
                userMapper.updateByPrimaryKeySelective(user);
                //向新的健康大使发送关系建立模板消息
                if (StringUtil.isNotEmpty(sharer.getOpenid())) {
                    sendMessage2Ambassador(user, sharer, 1);
                }
                if (oldSharer != null && !Objects.equals(oldSharer.getId(), sharerId) && StringUtil.isNotEmpty
                        (oldSharer.getOpenid())) {
                    //向原来的健康大使发送关系解除模板消息
                    sendMessage2Ambassador(user, oldSharer, 2);
                }
            }
            //如果升级为了分享用户，记录升级记录
            if (user.getUserType() == UserType.USER_3.value) {
                UserChangeRecordDTO dto = new UserChangeRecordDTO(user.getId(), 2, user.getUserType(), user.getMobile(), "升级为分享用户");
                amqpTemplate.convertAndSend(RabbitConstant.USER_CHANGE_QUEUE, dto);
            }
        }
    }

    /**
     * 向健康大使发送关系建立模板消息
     *
     * @param userInfo  新用户信息
     * @param oldSharer 旧用户信息
     * @param type      类型
     * @return void
     * @author hhf
     * @date 2019/4/19
     */
    private void sendMessage2Ambassador(User userInfo, UserDTO oldSharer, Integer type) {
        Map<String, Object> msgMap = new HashMap<>(8);
        msgMap.put("touser", oldSharer.getOpenid());
        msgMap.put("userId", userInfo.getId());
        msgMap.put("nickName", userInfo.getNickName());
        msgMap.put("userType", userInfo.getUserType());
        msgMap.put("type", type);
        amqpTemplate.convertAndSend(RabbitConstant.WX_TEMPLATE_MESSAGE_QUEUE, msgMap);
    }

    /**
     * 将用户的所有推广客户的用户类型变成分享用户
     * 且将用户的经销商信息设置上
     *
     * @param userId 用户ID
     */
    private void changeCustomersToShareUser(Integer userId, Integer distributorId) {
        List<Integer> ids = new ArrayList<>();
        this.findCustomersIdLoop(ids, userId);
        if (CollectionUtil.isNotEmpty(ids)) {
            Map<String, Object> queryMap = new HashMap<>(2);
            queryMap.put("ids", ids);
            queryMap.put("distributorId", distributorId);
            userMapper.changeCustomersToShareUser(queryMap);
        }
    }

    /**
     * 递归获取用户的推广客户的e家号
     *
     * @param ids
     * @param userId
     */
    private void findCustomersIdLoop(List<Integer> ids, Integer userId) {
        List<Integer> tempList = userMapper.selectUserIdByAmbassador(userId);
        if (CollectionUtil.isNotEmpty(tempList)) {
            ids.addAll(tempList);
            for (Integer id : tempList) {
                findCustomersIdLoop(ids, id);
            }
        }
    }

    @Override
    public PageVO<UserDTO> pageQueryUser(UserContidionDTO query, Integer pageNum, Integer pageSize) {
        if (query.getDistributorId() != null || StringUtil.isNotBlank(query.getDistributorAccount()) || StringUtil
                .isNotBlank(query.getDistributorProvince())) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("distributorId", query.getDistributorId());
            map.put("distributorAccount", query.getDistributorAccount());
            map.put("distributorProvince", query.getDistributorProvince());
            map.put("distributorCity", query.getDistributorCity());
            map.put("distributorRegion", query.getDistributorRegion());
            List<Integer> ids = userMapper.selectDistributorByMap(map);
            query.setDistributorIds(ids);
        }

        PageHelper.startPage(pageNum, pageSize);
        Page<UserDTO> page = userMapper.pageQueryUser(query);
        if (CollectionUtil.isNotEmpty(page.getResult())) {
            for (UserDTO userDTO : page.getResult()) {
                //是否显示解绑手机号
                Example example = new Example(UserAuths.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("userId", userDTO.getId());
                criteria.andIsNotNull("identifierUnique");//openid
                int count = userAuthsMapper.selectCountByExample(example);
                if (StringUtil.isNotBlank(userDTO.getMobile())) {
                    if (UserType.USER_7.value == userDTO.getUserType()) {
                        userDTO.setIsBind(false);
                    } else {
                        userDTO.setIsBind(true);
                    }
                } else {
                    userDTO.setIsBind(false);
                }

                if (null != userDTO.getOrigin()) {
                    userDTO.setSourceMode(OriginEnum.find(userDTO.getOrigin()).name);
                }

                //是否显示解绑微信
                if (count > 0) {
                    userDTO.setUnBindWeChat(true);
                } else {
                    userDTO.setUnBindWeChat(false);
                }

                if (null != userDTO.getAmbassadorId()) {
                    User user = userMapper.selectByPrimaryKey(userDTO.getAmbassadorId());
                    if (null != user) {
                        userDTO.setAmbassadorName(user.getRealName());
                        userDTO.setAmbassadorUserType(user.getUserType());
                    }
                }

                if (UserType.isDistributor(userDTO.getUserType())) {
                    //升级当前身份时间 取变更记录中身份为经销商 变化类型为升级的记录时间
                    UserChangeRecordDTO changeRecordDTO = userChangeMapper.queryChangeRecord(userDTO.getId(),userDTO.getDistributorId());
                    if(Objects.nonNull(changeRecordDTO)){
                        userDTO.setUpCurrentIdentityDate(changeRecordDTO.getTime());
                    }
                    //经销商身份
                    Distributor distributor = distributorMapper.selectByPrimaryKey(userDTO.getMid());
                    this.setDistributorInfo(distributor, userDTO);
                } else {
                    if (userDTO.getUserType() == UserType.USER_3.value) {
                        userDTO.setUpCurrentIdentityDate(userDTO.getBeSharerTime());
                    }
                    if (userDTO.getUserType() == UserType.USER_7.value) {
                        userDTO.setUpCurrentIdentityDate(userDTO.getBeSalesTime());
                    }
                    //普通身份
                    //设置经销商信息
                    if (Objects.nonNull(userDTO.getDistributorId())) {
                        Distributor distributor = distributorMapper.selectByPrimaryKey(userDTO.getDistributorId());
                        this.setDistributorInfo(distributor, userDTO);
                    }
                }
                //设置健康大使信息
                if (Objects.nonNull(userDTO.getAmbassadorId())) {
                    User user = userMapper.selectByPrimaryKey(userDTO.getAmbassadorId());
                    if (null != user) {
                        userDTO.setAmbassadorNumber(user.getId());
                        userDTO.setAmbassadorPhone(user.getMobile());
                    }
                } else if (Objects.nonNull(userDTO.getDistributorId())) {
                    Distributor distributor = distributorMapper.selectByPrimaryKey(userDTO.getDistributorId());
                    if (null != distributor) {
                        userDTO.setAmbassadorNumber(distributor.getUserId());
                        userDTO.setAmbassadorPhone(distributor.getPhone());
                        userDTO.setAmbassadorUserTypeName(distributor.getRoleName());
                        userDTO.setAmbassadorUserType(distributor.getRoleLevel());
                        userDTO.setAmbassadorName(distributor.getUserName());
                    }
                }
            }
        }
        return new PageVO<>(pageNum, page);
    }

    @Override
    public PageVO<UserVO> pageQueryUserToStation(UserQuery query, Integer pageNum, Integer pageSize) {
        if (query.getDistributorId() != null || StringUtil.isNotBlank(query.getDistributorAccount()) || StringUtil.isNotBlank(query.getDistributorName())
                || CollectionUtil.isNotEmpty(query.getAreas())) {
            Map<String, Object> map = new HashMap<>(7);
            map.put("distributorId", query.getDistributorId());
            map.put("distributorAccount", query.getDistributorAccount());
            map.put("distributorName", query.getDistributorName());
            map.put("areaIds", query.getAreas());
            List<Integer> ids = userMapper.selectDistributorIdByMapToStation(map);
            query.setDistributorIds(ids);
        }

        PageHelper.startPage(pageNum, pageSize);
        Page<UserVO> page = userMapper.pageQueryUserToStation(query);
        if (CollectionUtil.isNotEmpty(page.getResult())) {
            for (UserVO userVO : page.getResult()) {

                if (null != userVO.getOrigin()) {
                    userVO.setSourceMode(OriginEnum.find(userVO.getOrigin()).name);
                }

                if (null != userVO.getUserType()) {
                    userVO.setUserTypeName(UserType.getNameByType(userVO.getUserType()));
                }

                if (UserType.isDistributor(userVO.getUserType())) {
                    //升级当前身份时间
                    userVO.setUpCurrentIdentityDate(userVO.getBeDistributorTime());
                    //经销商身份
                    Distributor distributor = distributorMapper.selectByPrimaryKey(userVO.getMid());
                    this.setDistributorInfo(distributor, userVO);
                } else {
                    if (userVO.getUserType() == UserType.USER_3.value) {
                        userVO.setUpCurrentIdentityDate(userVO.getBeSharerTime());
                    }
                    if (userVO.getUserType() == UserType.USER_7.value) {
                        userVO.setUpCurrentIdentityDate(userVO.getBeSalesTime());
                    }
                    //普通身份
                    //设置经销商信息
                    if (Objects.nonNull(userVO.getDistributorId())) {
                        Distributor distributor = distributorMapper.selectByPrimaryKey(userVO.getDistributorId());
                        this.setDistributorInfo(distributor, userVO);
                    }
                }
                //设置健康大使信息
                if (Objects.nonNull(userVO.getAmbassadorId())) {
                    User user = userMapper.selectByPrimaryKey(userVO.getAmbassadorId());
                    if (null != user) {
                        userVO.setAmbassadorPhone(user.getMobile());
                    }
                } else if (Objects.nonNull(userVO.getDistributorId())) {
                    Distributor distributor = distributorMapper.selectByPrimaryKey(userVO.getDistributorId());
                    if (null != distributor) {
                        userVO.setAmbassadorPhone(distributor.getPhone());
                    }
                }
            }
        }
        return new PageVO<>(pageNum, page);
    }

    public void setDistributorInfo(Distributor distributor, UserVO userVO) {
        if (distributor != null) {
            userVO.setDistributorNumber(distributor.getUserId());
            userVO.setDistributorName(distributor.getRealName());
            userVO.setDistributorAccount(distributor.getUserName());
            userVO.setDistProvince(distributor.getProvince());
            userVO.setDistCity(distributor.getCity());
            userVO.setDistRegion(distributor.getRegion());
        }
    }

    public void setDistributorInfo(Distributor distributor, UserDTO userDTO) {
        if (distributor != null) {
            userDTO.setDistributorNumber(distributor.getUserId());
            userDTO.setDistributorName(distributor.getRealName());
            userDTO.setDistributorAccount(distributor.getUserName());
            userDTO.setDistProvince(distributor.getProvince());
            userDTO.setDistCity(distributor.getCity());
            userDTO.setDistRegion(distributor.getRegion());
        }
    }

    @Override
    public Integer unBindPhone(Integer userId) {
        //分销用户不能解绑手机号
        UserDTO user = this.getBasicUserById(userId);
        if (user == null) {
            throw new NotFoundException("未找到用户信息。");
        }
        if (UserType.USER_7.value == user.getUserType()) {
            throw new NotFoundException("分销用户不能解绑手机号");
        }
        if (StringUtil.isEmpty(user.getMobile())) {
            throw new NotFoundException("该用户未绑定手机号!");
        }

        int num = userMapper.updateMobile(userId, null, new Date());
        if (num > 0) {
            // // 删除缓存中的用户信息
            // redisCache.delete(Constant.USER_CACHE + user.getId());
            // // 更新缓存中的用户信息
            // this.getFullUserDTOById(user.getId());
            return num;
        }
        throw new YimaoException("解绑手机号失败。");
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public UserDTO updateHeadImage(Integer userId, String headImg) {
        userMapper.updateHeadImage(userId, headImg);
        // 更新缓存中的用户信息
        // redisCache.delete(Constant.USER_CACHE + userId);
        return this.getFullUserDTOById(userId);
    }

    /**
     * 经销商APP-忘记密码重置密码
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updatePwd(String userName, String password) {
        DistributorDTO distributor = distributorMapper.selectDistributorBasicInfoByUserName(userName);
        //将密码进行MD5加密
        password = MD5Util.encodeMD5(password).toUpperCase();
        //更新经销商表密码
        distributorMapper.updatePasswordById(distributor.getId(), password);
        List<UserDTO> userList = userMapper.listUserIdByMid(distributor.getId());
        if (CollectionUtil.isNotEmpty(userList)) {
            for (UserDTO user : userList) {
                //更新用户表密码
                int count = userMapper.updatePwd(user.getId(), password);
                // if (count > 0) {
                //     // 删除缓存
                //     redisCache.delete(Constant.USER_CACHE + user.getId());
                //     // 更新缓存中的用户信息
                //     this.getFullUserDTOById(user.getId());
                // }
            }
        }
    }

    @Override
    public Integer exit(Integer userId, HttpServletRequest request, HttpServletResponse response) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new YimaoException("退出登录失败");
        }
        CookieUtil.deleteCookie(request, response, AuthConstants.JWTINFO);
        user.setUpdateTime(new Date());
        user.setLastLoginTime(new Date());
        user.setOnline(false);
        return userMapper.updateByPrimaryKey(user);
    }


    @Override
    public UserDistributorDTO getUserDistributor(Integer userId) {
        log.info("**********传入参数,userId=" + userId + "**********");
        User user = userMapper.selectByPrimaryKey(userId);
        if (Objects.isNull(user)) {
            throw new NotFoundException("不存在该用户");
        }

        //查询经销商获取地址
        Distributor distributor;
        Example example = new Example(Distributor.class);
        Example.Criteria criteria = example.createCriteria();
        if (UserType.isDistributor(user.getUserType())) {
            criteria.andEqualTo("id", user.getMid());
        } else {
            criteria.andEqualTo("id", user.getDistributorId());
        }
        UserDistributorDTO userDistributorDTO = new UserDistributorDTO();
        List<Distributor> distributors = distributorMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(distributors)) {
            throw new NotFoundException("此用户的经销商信息有误");
        }
        distributor = distributors.get(0);

        Integer userType = user.getUserType();
        setUserDistributorDTO(user, distributor, userDistributorDTO);
        userDistributorDTO.setIsCompanyInfo(false);
        //判断用户身份类型
        if (UserType.isCompanyDistributor(userType)) {
            if (Objects.nonNull(distributor.getCompanyId())) {
                UserCompany userCompany = userCompanyMapper.selectByPrimaryKey(distributor.getCompanyId());
                if (Objects.nonNull(userCompany)) {
                    UserCompanyDTO companyDTO = new UserCompanyDTO();
                    userCompany.convert(companyDTO);
                    userDistributorDTO.setUserCompany(companyDTO);//企业信息
                    userDistributorDTO.setIsCompanyInfo(true);
                }
            }
        }
        log.info("userDistributorDTO:" + JsonUtil.objectToJson(userDistributorDTO));
        return userDistributorDTO;
    }


    private void setUserDistributorDTO(User user, Distributor distributor, UserDistributorDTO userDistributorDTO) {
        userDistributorDTO.setHeadImage(user.getHeadImg());
        userDistributorDTO.setAccountName(user.getUserName());
        userDistributorDTO.setRealName(user.getRealName());
        userDistributorDTO.setSex(user.getSex());
        userDistributorDTO.setPhone(user.getMobile());
        userDistributorDTO.setUserType(user.getUserType());
        userDistributorDTO.setType(distributor.getType());
        userDistributorDTO.setStationMaster(distributor.getStationMaster());
        userDistributorDTO.setAgentLevel(distributor.getAgentLevel());
//        userDistributorDTO.setWishType(user.getWishType());
        userDistributorDTO.setUserType(user.getUserType());
        userDistributorDTO.setRoleLevel(distributor.getRoleLevel());
        userDistributorDTO.setAddress(distributor.getAddress());//地址
        userDistributorDTO.setIdCard(user.getIdCard());
        userDistributorDTO.setEmail(user.getEmail());
        userDistributorDTO.setId(distributor.getId());
        userDistributorDTO.setStationMaster(distributor.getStationMaster());
    }


    /**
     * 健康e家公众号绑定手机号
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public UserDTO bindPhone(UserBindDTO userBindDTO) {
        //第一步先校验验证码，验证码如果错误，不往下进行
        String mobile = userBindDTO.getMobile();
        if (!smsService.verifyCode(mobile, Constant.COUNTRY_CODE, userBindDTO.getVerifyCode())) {
            throw new BadRequestException("验证码输入错误。");
        }
        UserDTO userDTO;
        // Integer userId = userBindDTO.getUserId();
        Integer userId = userCache.getUserId();
        log.info("绑定手机号获取到的当前登录用户e家号={}", userId);
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new BadRequestException("绑定失败，用户信息不存在。");
        }
        if (StringUtil.isNotEmpty(user.getMobile())) {
            throw new BadRequestException("您已绑定了手机。");
        }

        List<UserDTO> userList = userMapper.selectByPhoneForAppLogin(mobile);
        //这里处理APP微信授权登录绑定手机号的特殊业务逻辑（账号的识别、创建、删除等）
        //校验微信是不是同一个
        String unionid = userAuthsMapper.selectUnionidByUserId(userId);
        if (CollectionUtil.isNotEmpty(userList)) {
            Integer existsUserId = null;
            for (UserDTO dto : userList) {
                //利用Unionid比较，判断是不是同一个微信，一个账号不能同时绑定多个微信
                if (StringUtil.isNotEmpty(dto.getUnionid()) && StringUtil.isNotEmpty(unionid) && !dto.getUnionid()
                        .equalsIgnoreCase(unionid)) {
                    throw new BadRequestException("该手机号已与其它微信绑定，请更换其它手机号进行绑定。");
                }
                existsUserId = dto.getId();
            }
            //用户关系处理-如果该手机号已经有用户信息了，则删除userId用户信息
            //需要判断userId对应的用户是否有客户信息或者订单信息，
            //如果有则提示“不能绑定，该手机号已与其它账号绑定，请更换其它手机号进行绑定。”
            //如果没有则将其userAuths信息上的userId设置为existsUserId，然后删除userId对应的用户信息
            if (this.checkUserHasClientOrOrderOrIncome(userId)) {
                throw new BadRequestException("不能绑定，该手机号已绑定在e家号" + existsUserId + "上，请更换其它手机号进行绑定。");
            } else {
                userAuthsMapper.updateUserId(userId, existsUserId);
                // redisCache.delete(Constant.USER_CACHE + existsUserId);
                //将原账号废弃
                Integer userAuthsId = userAuthsMapper.selectIdByUserId(userId, IdentityTypeEnum.WECHAT_GZH.value);
                userMapper.disuse(userId, userAuthsId);
                return this.getFullUserDTOById(existsUserId);
            }
        } else {
            int origUserType = user.getUserType();
            WaterDeviceUser deviceUser = waterDeviceUserService.getByPhone(mobile);
            boolean sign = false;
            //绑定手机用户信息时要判定该用户是不是已经绑定了经销商了
            if (deviceUser != null && !UserType.isDistributor(origUserType)) {
                //当没有绑定过用户关系或者用户关系建立之前就已经有经销商时才进行数据copy，否则以翼猫健康e家数据关系为准
                sign = user.getAmbassadorId() == null || user.getBindAmbassadorTime() != null && user.getBindAmbassadorTime().after(deviceUser.getCreateTime());
                if (sign) {
                    //将水机用户部分信息复制到本地数据库中
                    this.copyWaterDeviceUserInfoToUser(user, deviceUser);
                    user.setAmbassadorId(null);
                }
            }
            user.setMobile(mobile);
            Date now = new Date();
            user.setBindPhoneTime(now);
            user.setUpdateTime(now);
            int r = userMapper.updateByPrimaryKey(user);
            if (r > 0) {
                //返回用户信息给前端做身份判定和数据同步
                if (sign) {
                    if (origUserType == UserType.USER_4.value) {
                        //如果绑定之前为普通用户，则需要将所有推广客户身份变为分享用户
                        this.changeCustomersToShareUser(user.getId(), user.getDistributorId());
                    } else {
                        this.changeCustomersDistributor(user.getId(), user.getDistributorId());
                    }
                }
                //经过上述处理，如果用户还是个普通用户
                if (user.getUserType() == UserType.USER_4.value) {
                    this.fillUserAmbassador(user, userBindDTO.getSharerId());
                }
                //1-分销用户身份判断
                this.saveIncomePermission(mobile, user);
                // redisCache.delete(Constant.USER_CACHE + user.getId());
                userDTO = this.getFullUserDTOById(user.getId());

                UserChangeRecordDTO dto = new UserChangeRecordDTO(user.getId(), 4, user.getUserType(), user.getMobile(), "绑定手机号");
                amqpTemplate.convertAndSend(RabbitConstant.USER_CHANGE_QUEUE, dto);
                return userDTO;
            } else {
                throw new BadRequestException("绑定手机号失败。");
            }
        }
    }

    private void copyWaterDeviceUserInfoToUser(User user, WaterDeviceUser deviceUser) {
        //判断经销商是不是折机版经销商
        boolean discount = distributorMapper.checkDiscountDistributor(deviceUser.getDistributorId());
        if (discount) {
            user.setDistributorId(null);
            user.setUserType(UserType.USER_4.value);//有健康大使的用户
            user.setUserTypeName(UserType.USER_4.name);//有健康大使的用户
        } else {
            user.setDistributorId(deviceUser.getDistributorId());
            user.setUserType(UserType.USER_3.value);//有健康大使的用户
            user.setUserTypeName(UserType.USER_3.name);//有健康大使的用户
            user.setBeSharerTime(new Date());
        }
        user.setRealName(deviceUser.getRealName());//水机用户真实姓名
        user.setProvince(deviceUser.getProvince());//省
        user.setCity(deviceUser.getCity());//市
        user.setRegion(deviceUser.getRegion());//区
        user.setSex(deviceUser.getSex() == null ? 1 : deviceUser.getSex());
    }

    private UserDTO getUserInfoWithJWTToken(User user, Integer system) {
        JWTInfo jwtInfo = new JWTInfo();
        jwtInfo.setId(user.getId());
        jwtInfo.setRealName(user.getRealName());
        jwtInfo.setType(system);

        //登录时删除之前的缓存
        // redisCache.delete(Constant.USER_CACHE + user.getId());

        String token = jwtHandler.createJWTToken(jwtInfo);
        UserDTO userDTO = this.getFullUserDTOById(user);
        userDTO.setToken(token);
        //设置用户当前登录账号的多重身份
        this.getUserIdentityName(userDTO);
        return userDTO;
    }

    /**
     * 获取用户当前登录账号的多重身份
     */
    private void getUserIdentityName(UserDTO user) {
        //设置用户多重身份
        UserIdentityDTO userIdentity = userMapper.selectMultiIdentityByUserId(user.getId());
        String province = userIdentity.getProvince();
        String city = userIdentity.getCity();
        String region = userIdentity.getRegion();
        List<String> identityList = new ArrayList<>();
        //设置代理商级别，空表示非代理商
        user.setAgentLevel(userIdentity.getAgentLevel());
        if (userIdentity.getFounder() != null && userIdentity.getFounder()) {
            identityList.add(UserType.CREATOR.name);
            //设置是否为创始人
            user.setFounder(true);
        }
        if (userIdentity.getAgentLevel() != null && AgentLevel.isProvinceAgent(userIdentity.getAgentLevel())) {
            identityList.add(province + "代理商");
        }
        if (userIdentity.getAgentLevel() != null && AgentLevel.isCityAgent(userIdentity.getAgentLevel())) {
            identityList.add(province + city + "代理商");
        }
        if (userIdentity.getAgentLevel() != null && AgentLevel.isRegionAgent(userIdentity.getAgentLevel())) {
            identityList.add(province + city + region + "代理商");
        }
        if (userIdentity.getStationMaster() != null && userIdentity.getStationMaster()) {
            identityList.add(province + city + region + "服务站站长");
        }
        if (userIdentity.getRoleLevel() != null && userIdentity.getRoleLevel() == DistributorRoleLevel.D_950.value) {
            identityList.add(province + city + region + UserType.DISTRIBUTOR_950.name);
        }
        if (userIdentity.getRoleLevel() != null && userIdentity.getRoleLevel() == DistributorRoleLevel.D_1000.value) {
            identityList.add(province + city + region + UserType.DISTRIBUTOR_1000.name);
        }
        if (userIdentity.getRoleLevel() != null && userIdentity.getRoleLevel() == DistributorRoleLevel.D_650.value) {
            identityList.add(province + city + region + UserType.DISTRIBUTOR_650.name);
        }
        if (userIdentity.getRoleLevel() != null && userIdentity.getRoleLevel() == DistributorRoleLevel.D_350.value) {
            identityList.add(province + city + region + UserType.DISTRIBUTOR_350.name);
        }
        if (userIdentity.getRoleLevel() != null && userIdentity.getRoleLevel() == DistributorRoleLevel.D_50.value) {
            identityList.add(province + city + region + UserType.DISTRIBUTOR_50.name);
        }
        if (userIdentity.getRoleLevel() != null && userIdentity.getRoleLevel() == DistributorRoleLevel.DISCOUNT.value) {
            identityList.add(province + city + region + UserType.DISTRIBUTOR_DISCOUNT_50.name);
        }
        if (userIdentity.getUserType() != null && userIdentity.getUserType() == UserType.USER_7.value) {
            identityList.add(UserType.USER_7.name);
        }
        if (userIdentity.getUserType() != null && userIdentity.getUserType() == UserType.USER_3.value) {
            identityList.add("普通用户");
        }
        if (userIdentity.getUserType() != null && userIdentity.getUserType() == UserType.USER_4.value) {
            identityList.add("普通用户");
        }
        user.setIdentityList(identityList);
        if (CollectionUtil.isNotEmpty(identityList)) {
            user.setIdentity(identityList.get(0));
        }
    }

    private void saveIncomePermission(String mobile, User user) {
        int beforeUserType = user.getUserType();
        //当前身份是分享用户或者普通用户，并且有经销商信息的用户才有资格升级为会员用户
        // if (beforeUserType == UserType.USER_3.value || beforeUserType == UserType.USER_4.value) {
        if (beforeUserType == UserType.USER_3.value || beforeUserType == UserType.USER_4.value && user.getDistributorId() != null) {
            WorkOrderDTO workOrderDTO = orderFeign.getUserCompletedWorkOrder(mobile);
            if (workOrderDTO != null) {
                if (user.getDistributorId() == null) {
                    user.setDistributorId(workOrderDTO.getDistributorId());
                }
                this.upgradeToSaleUser(user);
                if (beforeUserType == UserType.USER_4.value) {
                    //如果绑定之前为普通用户，则需要将所有推广客户身份变为分享用户
                    this.changeCustomersToShareUser(user.getId(), user.getDistributorId());
                } else {
                    this.changeCustomersDistributor(user.getId(), user.getDistributorId());
                }
            }
        }
    }

    /**
     * 获取当前用户的服务站名称
     */
    private void setStationNameToUser(UserDTO userDTO) {
        if (null != userDTO) {
            if (DistributorType.isDealer(userDTO.getUserType())) {
                if (userDTO.getUserType() != DistributorRoleLevel.D_1000.value) {
                    String province = userDTO.getProvince();
                    String city = userDTO.getCity();
                    String region = userDTO.getRegion();
                    boolean b = StringUtil.isNotEmpty(province) && StringUtil.isNotEmpty(city) && StringUtil.isNotEmpty(region);
                    if (b) {
                        StationDTO station = systemFeign.getStationByPRC(province, city, region, PermissionTypeEnum.PRE_SALE.value);
                        if (null != station) {
                            userDTO.setStationName(station.getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * 用户升级为分销用户
     *
     * @param user
     */
    @Override
    public void upgradeToSaleUser(User user) {
        log.info("升级会员用户，userId={}", user.getId());
        Date now = new Date();
        user.setUserType(UserType.USER_7.value);//分销用户
        user.setUserTypeName(UserType.USER_7.name);
        user.setIncomePermission(Constant.INCOME_PERMISSION);
        user.setBeSalesTime(now);//变成分销用户的时间
        user.setUpdateTime(now);
        userMapper.updateByPrimaryKeySelective(user);

        UserChangeRecordDTO dto = new UserChangeRecordDTO(user.getId(), 3, user.getUserType(), user.getMobile(),
                "成为会员用户");
        amqpTemplate.convertAndSend(RabbitConstant.USER_CHANGE_QUEUE, dto);
        //删除缓存
        // redisCache.delete(Constant.USER_CACHE + user.getId());
        //更新最新数据到缓存
        // getFullUserDTOById(user.getId());
        // 成为分销用户后-推送给自己和他的经销商
        // UserInfo sharer = this.getUserById(user.getId());
        String userOpenid = this.getWeChatOfficialAccountOpenid(user.getId());
        if (StringUtil.isNotEmpty(userOpenid)) {
            // TODO 队列实现 待测试
            amqpTemplate.convertAndSend(RabbitConstant.WX_TEMPLATE_MESSAGE_QUEUE, this.comeIncomeNotification
                    (userOpenid, user.getId(), user.getNickName(), 61));
        }

        //获取到分销用户的经销商
        UserDTO distributor = this.getMyDistributor(user);
        if (distributor != null) {
            if (distributor.getUserType() == 6) {
                distributor = this.getCompanyMainDistributor(distributor.getDistributorId());
            }
            if (distributor != null && distributor.getId() != null) {
                String openid = this.getWeChatOfficialAccountOpenid(distributor.getId());
                if (StringUtil.isNotEmpty(openid)) {
                    amqpTemplate.convertAndSend(RabbitConstant.WX_TEMPLATE_MESSAGE_QUEUE, this.comeIncomeNotification
                            (openid, user.getId(), user.getNickName(), 62));
                }
            }
        } else {
            log.info("成为分销商，e家号为：" + user.getId() + "，的用户经销商没有绑定！");
        }
    }

    /**
     * 企业版子账号需要获取主账号的净水配额进行配额不足逻辑的判断
     *
     * @param distributorId ID
     */
    private UserDTO getCompanyMainDistributor(Integer distributorId) {
        UserDTO userInfo = this.getUserByMid(distributorId);
        if (userInfo == null) {
            Distributor disUser = this.getDistributorById(distributorId);
            if (disUser != null) {
                userInfo = new UserDTO();
                this.copyDistributorToUserInfo(userInfo, disUser);
                return userInfo;
            }
        }
        // 2018-06-06 假如用户现在公众号绑定了个人版经销商，后来升级为企业版主账号，用户身份没切换的话userType会不为5
        // if (userInfo.getUserType() == 5) {
        return userInfo;
    }

    /**
     * 获取一个用户的上级经销商
     *
     * @return UserDTO
     */
    private UserDTO getMyDistributor(User user) {
        if (user == null) {
            return null;
        }
        if (UserType.isDistributor(user.getUserType())) {
            UserDTO dto = new UserDTO();
            user.convert(dto);
            return dto;
        }
        if (user.getDistributorId() == null) {
            return null;
        }
        UserDTO disUser = this.getUserByMid(user.getDistributorId());
        if (disUser == null) {
            Distributor distributor = this.getDistributorById(user.getDistributorId());
            disUser = new UserDTO();
            //经销商数据复制
            this.copyDistributorToUserInfo(disUser, distributor);
            disUser.setMobile(distributor.getPhone());
        }
        return disUser;

    }

    private UserDTO getUserByMid(Integer distributorId) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mid", distributorId);//绑定状态
        List<User> users = userMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(users)) {
            UserDTO dto = new UserDTO();
            users.get(0).convert(dto);
            return dto;
        }
        return null;
    }

    private Distributor getDistributorById(Integer mid) {
        return distributorMapper.selectByPrimaryKey(mid);
    }

    private Map<String, Object> comeIncomeNotification(String openId, Integer userId, String nickName, Integer type) {
        Map<String, Object> disMap = new HashMap<>();
        disMap.put("touser", openId);
        disMap.put("userId", userId);
        disMap.put("nickName", nickName);
        disMap.put("type", type);
        return disMap;
    }

    /**
     * 根据userId获取用户健康e家的openid
     *
     * @param userId
     * @return
     */
    private String getWeChatOfficialAccountOpenid(Integer userId) {
        Example example = new Example(UserAuths.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("state", 1);//绑定状态
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("identityType", IdentityTypeEnum.WECHAT_GZH.value);
        List<UserAuths> userAuthsList = userAuthsMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(userAuthsList)) {
            //openid
            return userAuthsList.get(0).getIdentifierUnique();
        }
        return null;

    }

    // private void copyWaterUserToUserInfo(User user, WaterUserDTO waterUser) {
    //     //userDTO.setDisUser(false);
    //     //userDTO.setWaterUser(true);//设置为水机用户
    //     //查询水机用户的经销商
    //     String distributorId = waterUser.getDistributorId();
    //     Distributor distributor = this.getDistributorByOldId(distributorId);
    //     if (StringUtil.isNotEmpty(waterUser.getChildDistributorId())) {
    //         Distributor distributorByOldId = this.getDistributorByOldId(waterUser.getChildDistributorId());
    //         //distributor = this.getDistributorById(waterUser.getChildDistributorId());
    //         if (null != distributorByOldId) {
    //             user.setDistributorId(distributorByOldId.getId());//健康大使主键
    //         }
    //     } else if (distributor != null) {
    //         //1.给水机用户设置经销商
    //         user.setDistributorId(distributor.getId());//健康大使主键
    //     }
    //     user.setUserType(3);//有健康大使的用户
    //     user.setBeSharerTime(new Date());
    //     //userDTO.setMid(waterUser.getId());//mongo数据库水机用户主键
    //     //userInfo.setUserName(waterUser.getPhone());//水机用户手机号
    //     user.setRealName(waterUser.getName());//水机用户真实姓名
    //     //if (distributor != null) {
    //     //1.给水机用户设置经销商
    //     //    userInfo.setDistributor(distributor.getId());//健康大使主键
    //     //userInfo.setDistributorType(this.getDistributorRoleConf(distributor));//角色级别：0体验版、1微创版、2个人版
    //     //}
    //     user.setProvince(waterUser.getProvince());//省
    //     user.setCity(waterUser.getCity());//市
    //     user.setRegion(waterUser.getRegion());//区
    //     String sex = waterUser.getSex();
    //     // mongoDb中获取的是字符串类型的 null
    //     if (StringUtil.isNotEmpty(sex) && NumberUtils.isDigits(sex)) {
    //         user.setSex(Integer.parseInt(sex));
    //     }
    // }


    /*@Override
    public PageVO<UserDTO> pageQueryCustomer(Integer pageNum, Integer pageSize, Integer distId, String
            province, String city, String region, String type) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserDTO> userDTOList = userMapper.selectCustomer(distId, province, city, region, type);
        PageInfo<UserDTO> pageResult = new PageInfo<>(userDTOList);

        OrderConditionDTO conditionDTO = new OrderConditionDTO();
        for (UserDTO userDTO : userDTOList) {
            //todo 调用订单服务查询该用户是否有水机订单
            conditionDTO.setUserId(userDTO.getId());
            conditionDTO.setActivityType(2);

//            orderInfoDTOList = orderFeign.orderList(conditionDTO,null,null);

        }
        PageVO<UserDTO> vo = new PageVO<>();
        vo.setResult(userDTOList);
        vo.setPages(pageResult.getPages());
        vo.setTotal(pageResult.getTotal());
        vo.setPageNum(pageNum);
        vo.setPageSize(pageSize);
        return vo;
    }*/

    /**
     * @Author lizhiqiang
     * @Date 2019/3/22
     * @Param [ids]
     */
    @Override
    public List<String> getDistributorImageById(List<Integer> ids) {
        if (!CollectionUtil.isNotEmpty(ids)) {
            throw new YimaoException("用户id为空");
        }
        Example example = new Example(User.class);
        example.createCriteria().andIn("distributorId", ids);
        List<User> userList = userMapper.selectByExample(example);
        List<String> headImageList = userList.stream().map(User::getHeadImg).collect(Collectors.toList());
        return headImageList;
    }

    @Override
    public String getQRCodeWithParam(Integer userId, Integer shareType, String shareNo, Long dateTime) {

        if (userId == null) {
            throw new BadRequestException("用户id为空");
        }
        UserDTO userDTO = userMapper.selectBasicUserById(userId);
        if (userDTO == null) {
            throw new NotFoundException("分享失败，用户不存在！");
        }
        String ticket = redisCache.get(userId + "_" + shareType + "_" + shareNo + "_" + dateTime + Constant
                .QRCODE_TICKET);

        if (StringUtil.isNotEmpty(ticket)) {
            return ticket;
        }
        try {
            String accessToken = this.getJSAPIAccessToken();
            StringBuffer url = new StringBuffer();
            url.append("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=").append(accessToken);

            com.alibaba.fastjson.JSONObject postData = new com.alibaba.fastjson.JSONObject();
            //7天，二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为30秒
            if (shareType == 1 || shareType == 2 || shareType == 4) {
                postData.put("expire_seconds", 86400);//1天
            } else {
                postData.put("expire_seconds", 2592000);//30天
            }
            //二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
            postData.put("action_name", "QR_STR_SCENE");//字符串形式

            com.alibaba.fastjson.JSONObject scene = new com.alibaba.fastjson.JSONObject();
            //场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
            if (userId == null) {
                scene.put("scene_str", "");
            } else {
                scene.put("scene_str", userId + "_" + shareType + "_" + shareNo + "_" + dateTime);
            }
            com.alibaba.fastjson.JSONObject actionInfo = new com.alibaba.fastjson.JSONObject();
            actionInfo.put("scene", scene);
            //二维码详细信息
            postData.put("action_info", actionInfo);
            //向微信服务器发送请求
            String resp = HttpUtil.postJSONData(url.toString(), postData);

            System.out.println("===获取二维码ticket的返回结果===" + resp);

            JSONObject json = new JSONObject(resp);

            if (json.has("ticket")) {
                ticket = json.getString("ticket");
                int expire_seconds = json.getInt("expire_seconds");
                redisCache.set(userId + "_" + shareType + "_" + shareNo + "_" + dateTime + Constant.QRCODE_TICKET,
                        ticket, expire_seconds - 60);
                return ticket;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("生成带参数的二维码时发生异常！");
        }
    }

    private String getJSAPIAccessToken() {
        try {
            String accessToken = redisCache.get(Constant.WX_JSAPI_ACCESS_TOKEN);
            if (StringUtil.isEmpty(accessToken)) {
                //获取授权 access_token
                StringBuffer url = new StringBuffer();
                url.append("https://api.weixin.qq.com/cgi-bin/token");
                url.append("?appid=").append(wechatProperties.getJsapi().getAppid());
                url.append("&secret=").append(wechatProperties.getJsapi().getSecret());
                url.append("&grant_type=client_credential");
                //向微信服务器发送请求
                String response = HttpUtil.executeGet(url.toString());

                System.out.println("H5获取access_token = " + response);

                JSONObject json = new JSONObject(response);

                if (json.has("errcode")) {
                    return null;
                }
                String access_token = json.getString("access_token");
                int expires_in = json.getInt("expires_in");
                redisCache.set(Constant.WX_JSAPI_ACCESS_TOKEN, access_token, expires_in - 30);

                return access_token;
            }
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("H5获取access_token时发生异常！");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public UserDTO bindDistributor(UserBindDTO userBindDTO) {
        Integer userId = userCache.getUserId();
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new BadRequestException("绑定失败，用户信息不存在");
        }
        if (UserType.isDistributor(user.getUserType())) {
            throw new BadRequestException("您已经绑定了经销商");
        }

        User existsUser = userMapper.selectUserByUserName(userBindDTO.getLoginName(), user.getId());
        if (existsUser != null) {
            List<UserAuths> userAuths = userAuthsMapper.queryUserAuthsByUserId(existsUser.getId());
            if (CollectionUtil.isNotEmpty(userAuths)) {
                throw new YimaoException("该账号已与其他微信（昵称：" + existsUser.getNickName() + "）绑定，请更换其他经销商账号进行绑定");
            }
            //如果当前用户有客户、下过单、有过收益，则提示不能绑定
            if (this.checkUserHasClientOrOrderOrIncome(userId)) {
                throw new BadRequestException("该经销商账号已与其他用户绑定，请更换其他经销商账号进行绑定");
            } else {
                Integer userAuthsId = userAuthsMapper.selectIdByUserId(userId, IdentityTypeEnum.WECHAT_GZH.value);
                //如果绑定的经销商账号已经有User数据了
                userAuthsMapper.updateUserId(userId, existsUser.getId());
                // redisCache.delete(Constant.USER_CACHE + existsUser.getId());
                //将原账号废弃
                userMapper.disuse(userId, userAuthsId);
                return this.getFullUserDTOById(existsUser.getId());
            }
        }

        Distributor query = new Distributor();
        query.setUserName(userBindDTO.getLoginName());
        Distributor distributor = distributorMapper.selectOne(query);
        if (distributor == null) {
            throw new NotFoundException("您输入的账号错误，请重新输入");
        }
        //校验密码
        String password = MD5Util.encodeMD5(userBindDTO.getPassword()).toUpperCase();
        if (!Objects.equals(password, distributor.getPassword().toUpperCase())) {
            //密码输入错误
            throw new YimaoException("您输入的账号或密码错误，请重新输入");
        }
        //代理商不能绑定，创始人不能绑定
        if (distributor.getType() == DistributorType.PROXY.value || distributor.getFounder() != null && distributor
                .getFounder()) {
            throw new YimaoException("暂不支持非经销商身份绑定");
        }
        //暂不支持体验版经销商身份绑定
        if (distributor.getRoleLevel() == DistributorRoleLevel.D_50.value) {
            throw new YimaoException("暂不支持体验版经销商身份绑定");
        }
        //暂不支持折机经销商身份绑定
        if (distributor.getRoleLevel() == DistributorRoleLevel.DISCOUNT.value) {
            throw new YimaoException("暂不支持折机经销商身份绑定");
        }
        //判断账号是否被禁用
        if (distributor.getForbidden() != null && distributor.getForbidden()) {
            throw new YimaoException("您的经销商账号已被禁用，为了不影响您的正常使用，请尽快联系客户处理");
        }
        //判断账号是否被禁用
        if (distributor.getForbiddenOrder() != null && distributor.getForbiddenOrder()) {
            throw new YimaoException("您的经销商账号已被禁用，为了不影响您的正常使用，请尽快联系客户处理");
        }

        int origUserType = user.getUserType();
        //复制经销商部门信息到用户信息上（包括在用户表里的用户类型）
        this.copyDistributorToUserInfo(user, distributor);
        if (!UserType.isDistributor(user.getUserType())) {
            throw new YimaoException("绑定失败，经销商信息有误");
        }
        Date now = new Date();
        user.setBindDistributorTime(now);
        user.setBeDistributorTime(now);
        user.setAmbassadorId(null);
        user.setIncomePermission(null);
        user.setUpdateTime(now);
        int r = userMapper.updateByPrimaryKey(user);
        if (r > 0) {
            //  将登录信息缓存到redis
            // redisCache.delete(Constant.USER_CACHE + user.getId());
            UserDTO userDTO = this.getFullUserDTOById(user.getId());
            if (origUserType == UserType.USER_4.value) {
                //如果绑定之前为普通用户，则需要将所有推广客户身份变为分享用户
                this.changeCustomersToShareUser(user.getId(), user.getMid());
            } else {
                this.changeCustomersDistributor(user.getId(), user.getMid());
            }
            //经销商身份才会赠送优惠卡
            if (UserType.isDistributor(user.getUserType())) {
                //2.优惠卡发放政策
                hraFeign.grantDiscountCard(userDTO);
            }
            UserChangeRecordDTO dto = new UserChangeRecordDTO(user.getId(), 5, user.getUserType(), user.getMobile(),
                    "绑定经销商");
            amqpTemplate.convertAndSend(RabbitConstant.USER_CHANGE_QUEUE, dto);
            return userDTO;
        } else {
            throw new YimaoException("绑定失败。");
        }
    }

    /**
     * 绑定经销商时带走所有下级用户
     *
     * @param userId
     * @param mid
     */
    private void changeCustomersDistributor(Integer userId, Integer mid) {
        List<Integer> ids = new ArrayList<>();
        this.findCustomersIdLoop(ids, userId);
        if (CollectionUtil.isNotEmpty(ids)) {
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("ids", ids);
            queryMap.put("distributor", mid);
            userMapper.changeCustomersDistributor(queryMap);
        }
    }

    private void copyDistributorToUserInfo(User user, Distributor distributor) {
        user.setMid(distributor.getId());
        user.setUserName(distributor.getUserName());//经销商登录名（手机号？）
        user.setRealName(distributor.getRealName());//经销商真实姓名
        user.setProvince(distributor.getProvince());//省
        user.setCity(distributor.getCity());//市
        user.setRegion(distributor.getRegion());//区
        user.setIdCard(distributor.getIdCard());
        user.setSex(distributor.getSex());
        user.setEmail(distributor.getEmail());

        user.setUserType(DistributorRoleLevel.getUserType(distributor.getRoleLevel()));
        user.setUserTypeName(UserType.getNameByType(user.getUserType()));

        if (DistributorRoleLevel.DISCOUNT.value == distributor.getRoleLevel()) {
            user.setUserType(null);
            user.setUserTypeName(null);
        }

        Integer recommendId = distributor.getRecommendId();
        if (recommendId != null) {
            user.setDistributorId(recommendId);
        } else {
            user.setDistributorId(null);
        }
    }

    private void copyDistributorToUserInfo(UserDTO user, Distributor distributor) {
        user.setMid(distributor.getId());
        user.setUserName(distributor.getUserName());//经销商登录名（手机号？）
        user.setRealName(distributor.getRealName());//经销商真实姓名
        user.setProvince(distributor.getProvince());//省
        user.setCity(distributor.getCity());//市
        user.setRegion(distributor.getRegion());//区
        user.setIdCard(distributor.getIdCard());
        user.setSex(distributor.getSex());
        user.setEmail(distributor.getEmail());

        user.setUserType(DistributorRoleLevel.getUserType(distributor.getRoleLevel()));
        user.setOnline(true);
        user.setAvailable(true);
        Date nowDate = new Date();
        user.setCreateTime(nowDate);
        user.setUpdateTime(nowDate);

        Integer recommendId = distributor.getRecommendId();
        if (recommendId != null) {
            user.setDistributorId(recommendId);
        } else {
            user.setDistributorId(null);
        }
    }

    private Distributor getDistributorByName(String userName) {
        Distributor query = new Distributor();
        query.setUserName(userName);
        return distributorMapper.selectOne(query);
    }

    @Override
    public Integer getUserIdByOpenid(String openid) {
        Example example = new Example(UserAuths.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("identifierUnique", openid);
        criteria.andEqualTo("identityType", IdentityTypeEnum.WECHAT_GZH.value);
        List<UserAuths> userAuthsList = userAuthsMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(userAuthsList)) {
            UserAuths userAuths = userAuthsList.get(0);
            return userAuths.getUserId();
        }
        return null;
    }


    /**
     * 获取我的推广客户列表（企业版主账号）
     *
     * @param user 用户
     * @return java.util.List<com.yimao.cloud.pojo.dto.user.UserDTO>
     * @author hhf
     * @date 2019/4/24
     */
    @Override
    public List<UserDTO> findShareCustomers(UserDTO user, boolean isMaster) {
        List<UserDTO> customers = new ArrayList<>();
        if (user.getUserType() == UserType.DISTRIBUTOR_950.value && user.getMid() != null && isMaster) {
            //企业版经销商先获取子账户列表
            customers.add(user);
            // 获取的子账号经销商可能未绑定e家号
            List<UserDTO> subDistributorList = this.getUserByDistributor(user.getMid(), DistributorRoleLevel
                    .D_1000.value);
            if (CollectionUtil.isNotEmpty(subDistributorList)) {
                // 子账号如果未获取到user表里的userType，手动设置
                // subDistributorList.forEach(o -> o.setUserType(UserType.DISTRIBUTOR_1000.value));
                customers.addAll(subDistributorList);
            }
        } else {
            List<UserDTO> customerList = this.findCustomers(user);
            if (CollectionUtil.isNotEmpty(customerList)) {
                customers.addAll(customerList);
            }
        }
        return customers;
    }


    private List<UserDTO> findCustomers(UserDTO dto) {
        return userMapper.findCustomers(dto.getId(), dto.getMid(), dto.getDistributorId(), null);

    }

    private List<UserDTO> getUserByDistributor(Integer mid, Integer type) {
        List<UserDTO> list = userMapper.getUserByDistributor(mid, type, null);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list;
    }

    /**
     * 获取我的推广客户列表
     *
     * @param userId 用户ID
     * @date 2019/4/22
     */
    @Override
    public Map<String, Object> myCustomers(Integer userId, Integer distributorId) {

        Map<String, Object> map = new HashMap<>();
        List<UserDTO> distributionUser = new ArrayList<>();
        List<UserDTO> shareUser = new ArrayList<>();
        List<UserDTO> unBindUser = new ArrayList<>();
        List<String> mobileList = new ArrayList<>();
        List<UserDTO> customers = new ArrayList<>();
        UserDTO user;
        if (userId != null) {
            user = this.getBasicUserById(userId);
            if (user != null) {
                customers = this.findShareCustomers(user, false);
                if (user.getMid() != null && UserType.isDistributor(user.getUserType())) {
                    List<WaterDeviceUser> users = waterDeviceUserService.listByDistributor(user.getMid());
                    if (CollectionUtil.isNotEmpty(users)) {
                        for (WaterDeviceUser deviceUser : users) {
                            UserDTO unbindUser = new UserDTO();
                            unbindUser.setRealName(deviceUser.getRealName());
                            unbindUser.setNickName(deviceUser.getRealName());
                            unbindUser.setMobile(deviceUser.getPhone());
                            unbindUser.setUserType(UserType.UN_BIND_USER.value);
                            customers.add(unbindUser);
                        }
                    }
                }
            }
        }
        // 企业版子账号
        if (distributorId != null) {
            Distributor distributor = getDistributorById(distributorId);
            if (distributor != null) {
                customers = this.findCustomers(null, distributorId, null);
                if (distributor.getRoleLevel() == DistributorRoleLevel.D_1000.value) {
                    List<WaterDeviceUser> users = waterDeviceUserService.listByDistributor(distributorId);
                    if (CollectionUtil.isNotEmpty(users)) {
                        for (WaterDeviceUser deviceUser : users) {
                            UserDTO unbindUser = new UserDTO();
                            unbindUser.setRealName(deviceUser.getRealName());
                            unbindUser.setNickName(deviceUser.getRealName());
                            unbindUser.setMobile(deviceUser.getPhone());
                            unbindUser.setUserType(UserType.UN_BIND_USER.value);
                            customers.add(unbindUser);
                        }
                    }
                }
            }
        }
        if (CollectionUtil.isNotEmpty(customers)) {
            for (UserDTO userInfo : customers) {
                Integer userType = userInfo.getUserType();
                if (userType == UserType.USER_7.value) {
                    distributionUser.add(userInfo);
                } else if (userType == UserType.USER_3.value || userType == UserType.USER_4.value) {
                    shareUser.add(userInfo);
                } else if (userType == UserType.UN_BIND_USER.value) {
                    unBindUser.add(userInfo);
                    if (StringUtil.isNotEmpty(userInfo.getMobile())) {
                        mobileList.add(userInfo.getMobile());
                    }
                }
            }
        }
        if (CollectionUtil.isNotEmpty(mobileList)) {
            List<User> removeList = this.findUserInfoByMobiles(mobileList);
//            ListIterator<UserDTO> it = unBindUser.listIterator();
//            while (it.hasNext()) {
//                UserDTO userInfo = it.next();
//                if (userInfo.getMobile() != null) {
//                    removeList.forEach(o -> {
//                        if (o.getMobile().equals(userInfo.getMobile())) {
//                            it.remove();
//                        }
//                    });
//                }
//            }

            Set<UserDTO> set = new HashSet<>();
            if (CollectionUtil.isNotEmpty(removeList)) {
                for (int i = 0; i < removeList.size(); i++) {
                    for (int j = 0; j < unBindUser.size(); j++) {
                        if (removeList.get(i).getMobile().equals(unBindUser.get(j).getMobile())) {
                            set.add(unBindUser.get(j));
                        }
                    }
                }
            }

            for (UserDTO userDTO : set) {
                unBindUser.remove(userDTO);
            }
        }
        distributionUser.sort(this::compareUserInfo);
        unBindUser.sort((obj1, obj2) -> {
            if (obj1 != null && obj2 != null && obj1.getRealName() != null && obj2.getRealName() != null) {
                Comparator<Object> com = Collator.getInstance(Locale.CHINA);
                return com.compare(obj1.getRealName(), obj2.getRealName());
            }
            return 1;
        });
        map.put("distributionUser", distributionUser);
        map.put("distributionCount", distributionUser.size());
        map.put("shareUser", shareUser);
        map.put("shareCount", shareUser.size());
        map.put("unBindUser", unBindUser);
        map.put("unBindCount", unBindUser.size());
        map.put("totalCount", distributionUser.size() + shareUser.size() + unBindUser.size());
        return map;
    }

    @Override
    public List<UserDTO> getUserByUserName(String username) {
        return userMapper.getUserByUserName(username);
    }

    @Override
    public List<UserDTO> findCustomers(Integer id, Integer mid, Integer distributorId) {
        return userMapper.findCustomers(id, mid, distributorId, null);
    }

    /**
     * 排序
     *
     * @author hhf
     * @date 2019/4/22
     */
    private int compareUserInfo(UserDTO obj1, UserDTO obj2) {
        if (obj1 == null && obj2 == null) {
            return 0;
        }
        if (obj1 == null) {
            return 1;
        }
        if (obj2 == null) {
            return -1;
        }
        //改为：按星级排序
        if (obj1.getStarNum() == null && obj2.getStarNum() == null) {
            Date d1 = obj1.getBindAmbassadorTime() == null ? obj1.getBindDistributorTime() :
                    obj1.getBindAmbassadorTime();
            Date d2 = obj2.getBindAmbassadorTime() == null ? obj2.getBindDistributorTime() :
                    obj2.getBindAmbassadorTime();
            if (d1 == null && d2 == null) {
                return 0;
            }
            if (d1 == null) {
                return 1;
            }
            if (d2 == null) {
                return -1;
            }
            return d2.compareTo(d1);
        }
        if (obj1.getStarNum() == null) {
            return 1;
        }
        if (obj2.getStarNum() == null) {
            return -1;
        }
        Integer l1 = obj1.getStarNum();
        Integer l2 = obj2.getStarNum();
        if (l1 != null || l2 != null) {
            if (l1 == null) {
                return 1;
            }
            if (l2 == null) {
                return -1;
            }
            return l2.compareTo(l1);
        } else {
            return 0;
        }
    }

    /**
     * 获取我的推广客户数量
     *
     * @param userId 用户ID
     * @return java.lang.Integer
     * @author hhf
     * @date 2019/4/24
     */
    @Override
    public Integer myCustomersCount(Integer userId) {
        UserDTO user = this.getBasicUserById(userId);
        if (user == null) {
            throw new NotFoundException("查询失败，用户不存在。");
        }

        List<UserDTO> distributionUser = new ArrayList<>();
        List<UserDTO> shareUser = new ArrayList<>();
        List<UserDTO> unBindUser = new ArrayList<>();
        List<String> mobileList = new ArrayList<>();
        List<UserDTO> customers = new ArrayList<>();

        customers = this.findShareCustomers(user, false);
        if (user.getMid() != null && UserType.isDistributor(user.getUserType())) {
            List<WaterDeviceUser> users = waterDeviceUserService.listByDistributor(user.getMid());
            if (CollectionUtil.isNotEmpty(users)) {
                for (WaterDeviceUser deviceUser : users) {
                    UserDTO unbindUser = new UserDTO();
                    unbindUser.setRealName(deviceUser.getRealName());
                    unbindUser.setNickName(deviceUser.getRealName());
                    unbindUser.setMobile(deviceUser.getPhone());
                    unbindUser.setUserType(UserType.UN_BIND_USER.value);
                    customers.add(unbindUser);
                }
            }
        }

        // 企业版子账号
        if (user.getDistributorId() != null) {
            Distributor distributor = getDistributorById(user.getDistributorId());
            if (distributor != null) {
                customers = this.findCustomers(null, user.getDistributorId(), null);
                if (distributor.getRoleLevel() == DistributorRoleLevel.D_1000.value) {
                    List<WaterDeviceUser> users = waterDeviceUserService.listByDistributor(user.getDistributorId());
                    if (CollectionUtil.isNotEmpty(users)) {
                        for (WaterDeviceUser deviceUser : users) {
                            UserDTO unbindUser = new UserDTO();
                            unbindUser.setRealName(deviceUser.getRealName());
                            unbindUser.setNickName(deviceUser.getRealName());
                            unbindUser.setMobile(deviceUser.getPhone());
                            unbindUser.setUserType(UserType.UN_BIND_USER.value);
                            customers.add(unbindUser);
                        }
                    }
                }
            }
        }

        if (CollectionUtil.isNotEmpty(customers)) {
            for (UserDTO userInfo : customers) {
                Integer userType = userInfo.getUserType();
                if (userType == UserType.USER_7.value) {
                    distributionUser.add(userInfo);
                } else if (userType == UserType.USER_3.value || userType == UserType.USER_4.value) {
                    shareUser.add(userInfo);
                } else if (userType == UserType.UN_BIND_USER.value) {
                    unBindUser.add(userInfo);
                    if (StringUtil.isNotEmpty(userInfo.getMobile())) {
                        mobileList.add(userInfo.getMobile());
                    }
                }
            }
        }

        if (CollectionUtil.isNotEmpty(mobileList)) {
            List<User> removeList = this.findUserInfoByMobiles(mobileList);
            Set<UserDTO> set = new HashSet<>();
            if (CollectionUtil.isNotEmpty(removeList)) {
                for (int i = 0; i < removeList.size(); i++) {
                    for (int j = 0; j < unBindUser.size(); j++) {
                        if (removeList.get(i).getMobile().equals(unBindUser.get(j).getMobile())) {
                            set.add(unBindUser.get(j));
                        }
                    }
                }
            }
            for (UserDTO userDTO : set) {
                unBindUser.remove(userDTO);
            }
        }

        return distributionUser.size() + shareUser.size() + unBindUser.size();

    }


    private List<User> findUserInfoByMobiles(List<String> mobileList) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("mobile", mobileList);
        return userMapper.selectByExample(example);
    }

    /**
     * 修改用户星级
     *
     * @param userAliasDTO 用户星级信息
     * @return void
     * @author hhf
     * @date 2019/4/24
     */
    @Override
    public void asterisk(UserAliasDTO userAliasDTO) {
        Integer userId = userCache.getUserId();
        userAliasDTO.setUserId(userId);
        User user = userMapper.selectByPrimaryKey(userAliasDTO.getClientId());
        if (user == null) {
            throw new NotFoundException("找不到要修改的用户！");
        }
        Example example = new Example(UserAlias.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userAliasDTO.getUserId());
        criteria.andEqualTo("clientId", userAliasDTO.getClientId());
        List<UserAlias> asterisks = userAliasMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(asterisks)) {
            UserAlias userAsterisk = asterisks.get(0);
            userAsterisk.setCreateTime(new Date());
            if (StringUtil.isNotEmpty(userAliasDTO.getClientName())) {
                userAsterisk.setClientName(userAliasDTO.getClientName());
            }
            if (userAliasDTO.getStarNum() != null) {
                userAsterisk.setStarNum(userAliasDTO.getStarNum());
            }
            userAliasMapper.updateByUserAlias(userAsterisk);
        } else {
            userAliasDTO.setCreateTime(new Date());
            userAliasDTO.setUpdateTime(new Date());
            UserAlias userAlias = new UserAlias(userAliasDTO);
            userAliasMapper.insertSelective(userAlias);
        }
    }

    @Override
    public List<UserDTO> getDistributorByMid(Integer mid) {
        return userMapper.getDistributorByMid(mid);
    }

    /**
     * 公众号（我的e家页面）健康大使信息获取
     *
     * @author hhf
     * @date 2019/5/14
     */
    @Override
    public UserDTO getAmbassador(Integer userId) {
        UserDTO ambassador = new UserDTO();
        UserDTO disUser = null;
        DistributorDTO distributor = null;
        UserDTO user = this.getBasicUserById(userId);
        if (user == null) {
            throw new NotFoundException("未找到用户信息！");
        }
        Integer ambassadorId = user.getAmbassadorId();
        Integer distributorId = user.getDistributorId();
        if (distributorId != null) {
            distributor = distributorService.getBasicInfoById(distributorId);
        }
        if (ambassadorId != null) {
            disUser = this.getBasicUserById(ambassadorId);
        } else {
            if (distributor != null) {
                Integer mId = distributor.getId();
                if (mId != null) {
                    disUser = this.getUserByMid(mId);
                }
                if (disUser == null) {
                    disUser = new UserDTO();
                    disUser.setId(distributor.getUserId());
                    disUser.setNickName(distributor.getRealName());
                    disUser.setMobile(distributor.getPhone());
                    disUser.setProvince(distributor.getProvince());
                    disUser.setCity(distributor.getCity());
                    disUser.setRegion(distributor.getRegion());
                }
            }
        }
        if (disUser != null) {
            ambassador.setId(disUser.getId());
            ambassador.setHeadImg(disUser.getHeadImg());
            ambassador.setNickName(disUser.getNickName());
            ambassador.setMobile(disUser.getMobile());
            String province = disUser.getProvince();
            String city = disUser.getCity();
            String region = disUser.getRegion();
            boolean b = StringUtil.isNotEmpty(province) && StringUtil.isNotEmpty(city) && StringUtil.isNotEmpty(region);
            if (b) {
                StationDTO station = systemFeign.getStationByPRC(province, city, region,PermissionTypeEnum.AFTER_SALE.value);
                if (null != station) {
                    ambassador.setStationName(station.getName());
                }
            }
        }
        return ambassador;
    }

    @Override
    public List<UserDTO> userByIds(Set<Integer> userIds) {
        return userMapper.userByIds(userIds);
    }

    @Override
    public List<Integer> getUserByUserType(Integer userType) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userType", userType);
        List<User> userList = userMapper.selectByExample(example);
        List<Integer> list = new ArrayList<>();
        for (User user : userList) {
            list.add(user.getId());
        }
        return list;
    }

    @Override
    public UserDTO getMySaleUserById(Integer id) {
        UserDTO dto = null;
        UserDTO user = this.getBasicUserById(id);
        if (user != null) {
            Integer userType = user.getUserType();
            // 自己为分销用户直接返回
            if (userType != null && userType == UserType.USER_7.value) {
                return user;
            }
            Integer ambassadorId = user.getAmbassadorId();
            if (ambassadorId != null) {
                Example example = new Example(User.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("id", ambassadorId);
                criteria.andEqualTo("userType", UserType.USER_7.value);
                List<User> userList = userMapper.selectByExample(example);
                if (CollectionUtil.isNotEmpty(userList)) {
                    dto = new UserDTO();
                    userList.get(0).convert(dto);
                }
            }
        }
        return dto;
    }

    @Override
    public List<Integer> findUserByAmbassadorId(Integer ambassadorId) {
        return userMapper.selectUserIdByAmbassador(ambassadorId);
    }

    @Override
    public List<Integer> getDistributorByUserId(Integer distributorId) {
        Map<String, Object> map = new HashMap<>();
        map.put("distributorId", distributorId);
        return userMapper.selectDistributorByMap(map);
    }

    @Override
    public UserVO stationGetUserInfo(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (null == user) {
            throw new YimaoException("该用户不存在");
        }
        UserVO userVO = BeanHelper.copyProperties(user, UserVO.class);
        Distributor queryDistributor = null;
        if (userVO.getMid() != null) {
            queryDistributor = distributorMapper.selectByPrimaryKey(user.getMid());
        } else if (user.getDistributorId() != null) {
            queryDistributor = distributorMapper.selectByPrimaryKey(user.getDistributorId());
        }
        if (queryDistributor != null) {
            userVO.setAreaId(queryDistributor.getAreaId());
        }

        if (UserType.isDistributor(userVO.getUserType())) {
            //用户身份为经销商
            userVO.setUpCurrentIdentityDate(userVO.getBeDistributorTime());
            userVO.setBindingSuperiorsDate(userVO.getBindDistributorTime());//绑定上级关系时间
            //成为经销商
            if (userVO.getUserType() == UserType.DISTRIBUTOR_1000.value) {
                //身份是经销商子账号,其健康大使是企业版主账号
                Distributor distributor = distributorMapper.selectByPrimaryKey(userVO.getMid());
                if (distributor != null) {
                    Distributor dist = distributorMapper.selectByPrimaryKey(distributor.getPid());
                    if (null != dist) {
                        userVO.setAmbassadorNumber(dist.getUserId());
                        userVO.setAmbassadorNickName(dist.getNickName());
                        userVO.setAmbassadorName(dist.getRealName() == null ? dist.getNickName() : dist.getRealName());
                        userVO.setAmbassadorUserTypeName(dist.getRoleName());
                        userVO.setAmbassadorPhone(dist.getPhone());
                    }
                }
            } else {
                if (userVO.getDistributorId() != null) {
                    Distributor distributor = distributorMapper.selectByPrimaryKey(userVO.getDistributorId());
                    this.setAmbassadorInfo(distributor, userVO);
                }
            }

            Distributor distributor = distributorMapper.selectByPrimaryKey(userVO.getMid());
            if (distributor != null) {
                //经销商信息
                userVO.setDistributorNumber(distributor.getUserId());
                userVO.setDistributorAccount(distributor.getUserName());
                userVO.setDistributorName(distributor.getRealName());
                userVO.setDistributorTypeName(distributor.getRoleName());
                userVO.setDistributorPhone(distributor.getPhone());
                userVO.setDistributorIdCard(distributor.getIdCard());
                userVO.setDistProvince(distributor.getProvince());
                userVO.setDistCity(distributor.getCity());
                userVO.setDistRegion(distributor.getRegion());
            }
        } else {
            if (userVO.getUserType() == UserType.USER_3.value) {
                userVO.setUpCurrentIdentityDate(userVO.getBindAmbassadorTime());
            }

            if (userVO.getUserType() == UserType.USER_7.value) {
                userVO.setUpCurrentIdentityDate(userVO.getBeSalesTime());
            }

            //普通用户
            if (userVO.getDistributorId() != null) {
                Distributor distributor = distributorMapper.selectByPrimaryKey(user.getDistributorId());
                if (null != distributor) {
                    userVO.setDistributorNumber(distributor.getUserId());
                    userVO.setDistributorAccount(distributor.getUserName());
                    userVO.setDistributorName(distributor.getRealName());
                    userVO.setDistributorTypeName(distributor.getRoleName());
                    userVO.setDistributorPhone(distributor.getPhone());
                    userVO.setDistributorIdCard(distributor.getIdCard());
                    userVO.setDistProvince(distributor.getProvince());
                    userVO.setDistCity(distributor.getCity());
                    userVO.setDistRegion(distributor.getRegion());
                }
            }

            if (userVO.getAmbassadorId() != null) {
                User ambassador = userMapper.selectByPrimaryKey(userVO.getAmbassadorId());
                if (null != ambassador) {
                    userVO.setAmbassadorNumber(ambassador.getId());
                    userVO.setAmbassadorNickName(ambassador.getNickName());
                    userVO.setAmbassadorName(ambassador.getRealName() == null ? ambassador.getNickName() :
                            ambassador.getRealName());
                    userVO.setAmbassadorUserType(ambassador.getUserType());
                    //健康大使身份
                    userVO.setAmbassadorUserTypeName(UserType.getNameByType(ambassador.getUserType()));
                    userVO.setAmbassadorPhone(ambassador.getMobile());
                }
            } else {
                if (userVO.getDistributorId() != null) {
                    Distributor distributor = distributorMapper.selectByPrimaryKey(user.getDistributorId());
                    this.setAmbassadorInfo(distributor, userVO);
                }
            }
        }
        return userVO;
    }

    @Override
    public UserDTO getUserInfoById(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (null == user) {
            throw new YimaoException("该用户不存在");
        }
        UserDTO userDTO = new UserDTO();
        user.convert(userDTO);
        if (UserType.isDistributor(userDTO.getUserType())) {
            //用户身份为经销商
            UserChangeRecordDTO changeRecordDTO = userChangeMapper.queryChangeRecord(userDTO.getId(),userDTO.getMid());
            if(Objects.nonNull(changeRecordDTO)){
                userDTO.setUpCurrentIdentityDate(changeRecordDTO.getTime());
            }
            userDTO.setBindingSuperiorsDate(userDTO.getBindDistributorTime());//绑定上级关系时间
            //成为经销商
            if (userDTO.getUserType() == UserType.DISTRIBUTOR_1000.value) {
                //身份是经销商子账号,其健康大使是企业版主账号
                Distributor distributor = distributorMapper.selectByPrimaryKey(userDTO.getMid());
                if (distributor != null) {
                    Distributor dist = distributorMapper.selectByPrimaryKey(distributor.getPid());
                    if (null != dist) {
                        userDTO.setAmbassadorNumber(dist.getUserId());
                        userDTO.setAmbassadorNickName(dist.getNickName());
                        userDTO.setAmbassadorName(dist.getRealName() == null ? dist.getNickName() : dist.getRealName());
                        userDTO.setAmbassadorUserTypeName(dist.getRoleName());
                        userDTO.setAmbassadorPhone(dist.getPhone());
                    }
                }
            } else {
                if (userDTO.getDistributorId() != null) {
                    Distributor distributor = distributorMapper.selectByPrimaryKey(userDTO.getDistributorId());
                    this.setAmbassadorInfo(distributor, userDTO);
                }
            }

            Distributor distributor = distributorMapper.selectByPrimaryKey(userDTO.getMid());
            if (distributor != null) {
                //经销商信息
                userDTO.setDistributorNumber(distributor.getUserId());
                userDTO.setDistributorAccount(distributor.getUserName());
                userDTO.setDistributorName(distributor.getRealName());
                userDTO.setDistributorTypeName(distributor.getRoleName());
                userDTO.setDistributorPhone(distributor.getPhone());
                userDTO.setDistributorIdCard(distributor.getIdCard());
                userDTO.setDistProvince(distributor.getProvince());
                userDTO.setDistCity(distributor.getCity());
                userDTO.setDistRegion(distributor.getRegion());
            }
        } else {
            if (userDTO.getUserType() == UserType.USER_3.value) {
                userDTO.setUpCurrentIdentityDate(userDTO.getBindAmbassadorTime());
            }

            if (userDTO.getUserType() == UserType.USER_7.value) {
                userDTO.setUpCurrentIdentityDate(userDTO.getBeSalesTime());
            }

            //普通用户
            if (userDTO.getDistributorId() != null) {
                Distributor distributor = distributorMapper.selectByPrimaryKey(user.getDistributorId());
                if (null != distributor) {
                    userDTO.setDistributorNumber(distributor.getUserId());
                    userDTO.setDistributorAccount(distributor.getUserName());
                    userDTO.setDistributorName(distributor.getRealName());
                    userDTO.setDistributorTypeName(distributor.getRoleName());
                    userDTO.setDistributorPhone(distributor.getPhone());
                    userDTO.setDistributorIdCard(distributor.getIdCard());
                    userDTO.setDistProvince(distributor.getProvince());
                    userDTO.setDistCity(distributor.getCity());
                    userDTO.setDistRegion(distributor.getRegion());
                }
            }

            if (userDTO.getAmbassadorId() != null) {
                User ambassador = userMapper.selectByPrimaryKey(userDTO.getAmbassadorId());
                if (null != ambassador) {
                    userDTO.setAmbassadorNumber(ambassador.getId());
                    userDTO.setAmbassadorNickName(ambassador.getNickName());
                    userDTO.setAmbassadorName(ambassador.getRealName() == null ? ambassador.getNickName() :
                            ambassador.getRealName());
                    userDTO.setAmbassadorUserType(ambassador.getUserType());
                    //健康大使身份
                    userDTO.setAmbassadorUserTypeName(UserType.getNameByType(ambassador.getUserType()));
                    userDTO.setAmbassadorPhone(ambassador.getMobile());
                }
            } else {
                if (userDTO.getDistributorId() != null) {
                    Distributor distributor = distributorMapper.selectByPrimaryKey(user.getDistributorId());
                    this.setAmbassadorInfo(distributor, userDTO);
                }
            }
        }
        return userDTO;
    }

    /**
     * 经销商APP-修改密码
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void modifyPassword(UserDTO user) {
        //更新用户表密码
        int num = userMapper.updatePwd(user.getId(), user.getPassword());
        if (num > 0) {
            // 删除缓存中的用户信息
            // redisCache.delete(Constant.USER_CACHE + user.getId());
            // 更新缓存中的用户信息
            // this.getFullUserDTOById(user.getId());
        }
        if (user.getMid() != null) {
            //更新经销商表密码
            distributorMapper.updatePasswordById(user.getMid(), user.getPassword());
        }
    }

    @Override
    public UserDTO getUserDtoByOpenid(Integer identityType, String identifierUnique) {
        Example example = new Example(UserAuths.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("identifierUnique", identifierUnique);
        criteria.andEqualTo("identityType", identityType);
        List<UserAuths> userAuthsList = userAuthsMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(userAuthsList)) {
            User user = userMapper.selectByPrimaryKey(userAuthsList.get(0).getUserId());
            if (user != null) {
                UserDTO dto = new UserDTO();
                user.convert(dto);
                return dto;
            }
        }
        return null;
    }

    @Override
    public Object queryMyCustomer(Integer userId, Integer type, Integer queryType, Integer subDistributorId) {
        if (queryType == null) {
            queryType = 0;
        }

        Map<String, Object> map = new HashMap<>();
        List<UserDTO> distributionUser = new ArrayList<>();
        List<UserDTO> shareUser = new ArrayList<>();

        List<UserDTO> customers = new ArrayList<>();
        UserDTO user = this.getBasicUserById(userCache.getUserId());

        if ((queryType == 1 || queryType == 2) && UserType.DISTRIBUTOR_950.value != user.getUserType()) {
            throw new YimaoException("查询失败，当前经销商不是企业版主账号");
        }
        if (queryType == 2 && subDistributorId == null) {
            throw new YimaoException("查询失败，请选择正确的子账号");
        }

        if (user != null) {
            if (UserType.isDistributor(user.getUserType())) {
                customers = userMapper.findCustomers(user.getId(), user.getMid(), null, null);
                if (user.getUserType() == UserType.DISTRIBUTOR_950.value) {
                    if (queryType == 0 || queryType == 2) {
                        //查询全部或者只查子账号的客户才有必要执行下面的逻辑
                        List<UserDTO> subDistributorList = userMapper.getUserByDistributor(user.getMid(), DistributorRoleLevel.D_1000.value, null);
                        if (CollectionUtil.isNotEmpty(subDistributorList)) {
                            customers.addAll(subDistributorList);
                        }
                    }
                }
            } else {
                customers = userMapper.findCustomers(user.getId(), null, null, null);
            }
        }

        //根据条件过滤数据
        if (queryType == 2) {
            customers.removeIf(tmpUser -> !Objects.equals(tmpUser.getDistributorId(), subDistributorId));
        }

        List<UserDTO> yesterdayAddUser = new ArrayList<>();
        List<UserDTO> currentMonthAddUser = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(customers)) {
            for (UserDTO userInfo : customers) {
                Integer userType = userInfo.getUserType();
                Date dayStartTime = DateUtil.getDayStartTime(DateUtil.addDays(new Date(), -1));
                Date dayEndTime = DateUtil.getDayEndTime(DateUtil.addDays(new Date(), -1));

                Date monthStartTime = DateUtil.getCurrentMonthBeginTime();
                Date monthEndTime = DateUtil.getCurrentMonthEndTime();
                if (userType == UserType.USER_7.value || userType == UserType.USER_3.value || userType == UserType.USER_4.value) {
                    if (userInfo.getCreateTime().getTime() > dayStartTime.getTime() && userInfo.getCreateTime().getTime() < dayEndTime.getTime()) {
                        yesterdayAddUser.add(userInfo);
                    }

                    if (userInfo.getCreateTime().getTime() > monthStartTime.getTime() && userInfo.getCreateTime().getTime() < monthEndTime.getTime()) {
                        currentMonthAddUser.add(userInfo);
                    }
                }

                if (userType == UserType.USER_7.value) {
                    distributionUser.add(userInfo);
                } else if (userType == UserType.USER_3.value || userType == UserType.USER_4.value) {
                    shareUser.add(userInfo);
                }
            }
        }

        distributionUser.sort(this::compareUserInfo);
        List<UserDTO> ordinaryUserList = new ArrayList<>();
        List<UserDTO> vipUserList = new ArrayList<>();
        if (null != type) {
            if (type == 1) {
                //昨日
                if (CollectionUtil.isNotEmpty(yesterdayAddUser)) {
                    for (UserDTO yesterdayUser : yesterdayAddUser) {
                        if (yesterdayUser.getUserType() == UserType.USER_3.value || yesterdayUser.getUserType() == UserType.USER_4.value) {
                            ordinaryUserList.add(yesterdayUser);
                        } else if (yesterdayUser.getUserType() == UserType.USER_7.value) {
                            vipUserList.add(yesterdayUser);
                        }
                    }
                }
            } else if (type == 2) {
                //本月
                if (CollectionUtil.isNotEmpty(currentMonthAddUser)) {
                    for (UserDTO currentMonthUser : currentMonthAddUser) {
                        if (currentMonthUser.getUserType() == UserType.USER_3.value || currentMonthUser.getUserType() == UserType.USER_4.value) {
                            ordinaryUserList.add(currentMonthUser);
                        } else if (currentMonthUser.getUserType() == UserType.USER_7.value) {
                            vipUserList.add(currentMonthUser);
                        }
                    }
                }
            }
        }


        map.put("ordinaryUserNum", ordinaryUserList.size());
        map.put("vipUserNum", vipUserList.size());
        map.put("ordinaryUserList", ordinaryUserList);
        map.put("vipUserList", vipUserList);

        map.put("yesterdayNum", yesterdayAddUser.size());
        map.put("currentMonthNum", currentMonthAddUser.size());
        map.put("distributionCount", distributionUser.size());
        map.put("shareCount", shareUser.size());
        map.put("totalCount", distributionUser.size() + shareUser.size());

//        map.put("yesterdayAddUser", yesterdayAddUser);
//        map.put("currentMonthAddUser", currentMonthAddUser);
        map.put("distributionUser", distributionUser);
        map.put("shareUser", shareUser);
        return map;
    }


    @Override
    public Object unBindWechat(Integer userId) {
        //解绑微信
        Example example = new Example(UserAuths.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        int count = userAuthsMapper.selectCountByExample(example);
        if (count <= 0) {
            throw new YimaoException("该用户未绑定微信");
        }
        count = userAuthsMapper.deleteByExample(example);
        if (count <= 0) {
            throw new YimaoException("微信解绑失败");
        }
        return count;
    }

    @Transactional
    @Override
    public void changeAmbassador(Integer userId, Integer ambassadorId) {
        //健康大使
        User user = userMapper.selectByPrimaryKey(ambassadorId);
        if (Objects.isNull(user)) {
            throw new YimaoException("不存在该用户");
        }

        //用户
        User toUser = userMapper.selectByPrimaryKey(userId);
        if (null == toUser) {
            throw new YimaoException("不存在该用户");
        }

        if (toUser.getAmbassadorId() == ambassadorId) {
            throw new YimaoException("该用户已存在该健康大使，无需更换");
        }

        List<Integer> ids = new ArrayList<>();
        Integer distributorId = null;
        toUser.setId(userId);
        toUser.setAmbassadorId(ambassadorId);
        toUser.setUpdateTime(new Date());
        if (UserType.isDistributor(user.getUserType())) {
            toUser.setDistributorId(user.getMid());
            distributorId = user.getMid();
        } else {
            toUser.setDistributorId(user.getDistributorId());
            distributorId = user.getDistributorId();
        }

        this.findCustomersIdLoop(ids, toUser.getId());
        if (CollectionUtil.isNotEmpty(ids)) {
            Map<String, Object> queryMap = new HashMap<>(2);
            queryMap.put("list", ids);
            queryMap.put("distributorId", distributorId);
            int count = userMapper.batchUpdateUserDistributor(queryMap);
            if (count == 0) {
                throw new YimaoException("用户下所有客户的经销商修改失败");
            }
        }

        int num = userMapper.updateUserInfoById(toUser);
        // redisCache.delete(Constant.USER_CACHE + toUser.getId());
        if (num == 0) {
            throw new YimaoException("更换用户的健康大使失败");
        }
        //信息变更记录
        UserChangeRecordDTO dto = new UserChangeRecordDTO(toUser.getId(), 12, toUser.getUserType(), toUser.getMobile
                (), "变更健康大使");
        amqpTemplate.convertAndSend(RabbitConstant.USER_CHANGE_QUEUE, dto);
    }


    @Override
    public Object getCustomerList(Integer userId, String key) {
        UserDTO user = this.getBasicUserById(userId);
        if (user == null) {
            throw new NotFoundException("查询失败，用户不存在。");
        }

        List<UserDTO> customers = new ArrayList<>();
        List<UserDTO> allDistributor = new ArrayList<>();

        if (user.getUserType() == UserType.DISTRIBUTOR_950.value) {
            allDistributor = this.queryShareCustomers(user, true, key);
            for (UserDTO userInfo1 : allDistributor) {
                List<UserDTO> tmpList = this.queryShareCustomers(userInfo1, false, key);
                customers.addAll(tmpList);
            }
        } else {
            customers = this.queryShareCustomers(user, false, key);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", customers);
        return map;
    }

    private List<UserDTO> queryShareCustomers(UserDTO user, boolean isMaster, String key) {
        List<UserDTO> customers = new ArrayList<>();
        if (user.getUserType() == UserType.DISTRIBUTOR_950.value && user.getMid() != null && isMaster) {
            customers.add(user);
            List<UserDTO> subDistributorList = userMapper.getUserByDistributor(user.getMid(), DistributorRoleLevel
                    .D_1000.value, key);
            if (CollectionUtil.isNotEmpty(subDistributorList)) {
                customers.addAll(subDistributorList);
            }
        } else {
            List<UserDTO> customerList = userMapper.findCustomers(user.getId(), user.getMid(), user.getDistributorId
                    (), key);
            if (CollectionUtil.isNotEmpty(customerList)) {
                customers.addAll(customerList);
            }
        }
        return customers;
    }

    @Override
    public List<Integer> queryUserAuthsByUnionid(String unionid) {
        return userAuthsMapper.listUserIdByUnionid(unionid);
    }

    public void setAmbassadorInfo(Distributor distributor, UserVO userVO) {
        if (distributor != null) {
            userVO.setAmbassadorNumber(distributor.getUserId());
            userVO.setAmbassadorNickName(distributor.getNickName());
            userVO.setAmbassadorName(distributor.getRealName() == null ? distributor.getNickName() : distributor
                    .getRealName());
            userVO.setAmbassadorUserType(distributor.getRoleLevel());
            userVO.setAmbassadorUserTypeName(distributor.getRoleName());
            userVO.setAmbassadorPhone(distributor.getPhone());
        }
    }


    public void setAmbassadorInfo(Distributor distributor, UserDTO userDTO) {
        if (distributor != null) {
            userDTO.setAmbassadorNumber(distributor.getUserId());
            userDTO.setAmbassadorNickName(distributor.getNickName());
            userDTO.setAmbassadorName(distributor.getRealName() == null ? distributor.getNickName() : distributor
                    .getRealName());
            userDTO.setAmbassadorUserType(distributor.getRoleLevel());
            userDTO.setAmbassadorUserTypeName(distributor.getRoleName());
            userDTO.setAmbassadorPhone(distributor.getPhone());
        }
    }

    @Override
    public Object getBusinessNewspaper(Integer id) {
        Map map = new HashMap(8);
        //企业版经销商数量
        Integer enterpriseCount = distributorMapper.selectEnterpriseDistributorNum(id);
        //个人版经销商数量
        Integer personalCount = distributorMapper.selectPersonalDistributorNum(id);
        //微创版经销商数量
        Integer minimalCount = distributorMapper.selectMinimalDistributorNum(id);

        //招商累计总人数 investmentCount
        Integer totalCount = enterpriseCount + personalCount + minimalCount;
        map.put("totalCount", totalCount);
        map.put("enterpriseCount", enterpriseCount);
        map.put("personalCount", personalCount);
        map.put("minimalCount", minimalCount);

        //销售金额
        List<ProductIncomeRecordDTO> incomeRecordDTOList = orderFeign.getProductIncomeRecord(id);
        BigDecimal salesAmount = new BigDecimal(0);
        if (CollectionUtil.isNotEmpty(incomeRecordDTOList)) {
            for (ProductIncomeRecordDTO recordDTO : incomeRecordDTOList) {
                salesAmount = salesAmount.add(recordDTO.getOrderFee());
            }
        }

        List<SalesProductDTO> saleList = orderFeign.getSaleProductListById(id);
        map.put("salesAmount", salesAmount);
        map.put("saleList", saleList);
        //续费订单数量
        Map<String, Object> resultMap = orderFeign.getRenewOrderListById(id);
        map.put("renewDTOList", resultMap.get("renewDTOList"));
        map.put("renewOrderCount", resultMap.get("count"));
        return map;
    }

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     */
    @Override
    public UserDTO appLoginByUsername(String username, String password, Integer system, Integer appType) {
        //从数据库中查询
        Distributor dist = distributorMapper.selectByUserNameForAppLogin(username);
        if (dist == null) {
            throw new BadRequestException("账号不存在");
        }
        //密码校验
        boolean boo = password.equalsIgnoreCase(dist.getPassword());
        if (!boo) {
            throw new BadRequestException("账号或密码错误，请重新输入");
        }
        if (dist.getForbidden()) {
            throw new BadRequestException("该账号已被禁用，请联系客服解决");
        }
        User user = userMapper.selectByPrimaryKey(dist.getUserId());
        if (user == null) {
            throw new NotFoundException("登录失败【code=001】。");
        }
        //更新经销商表的appType值
        updateDistributorAppType(user.getId(), appType);
        return getUserInfoWithJWTToken(user, system);
    }

    /**
     * 1、userId为空时，翼猫APP手机号登录
     * 2、userId不为空时，翼猫APP微信登录绑定手机号
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public CommResult<Map<String, Object>> appLoginByMobile(Integer userId, String mobile, Integer sharerId, Integer system, Integer appType) {
        List<UserDTO> userList = userMapper.selectByPhoneForAppLogin(mobile);
        if (userId != null) {
            //这里处理APP微信授权登录绑定手机号的特殊业务逻辑（账号的识别、创建、删除等）
            //校验微信是不是同一个
            String unionid = userAuthsMapper.selectUnionidByUserId(userId);
            if (CollectionUtil.isNotEmpty(userList)) {
                for (UserDTO dto : userList) {
                    //利用Unionid比较，判断是不是同一个微信，一个账号不能同时绑定多个微信
                    if (dto.getUnionid() != null && !dto.getUnionid().equalsIgnoreCase(unionid)) {
                        throw new BadRequestException("该手机号已与其它微信绑定，请更换其它手机号进行绑定。");
                    }
                }
                //处理特殊场景下：一个微信授权对应多个e家号的情况
                //关键业务操作-如果该手机号已经有用户信息了，则删除userId用户信息
                Integer existsUserId = userMapper.selectUserIdByMobile(mobile, userId);
                //如果该手机号已经有用户信息了，则需要进行账号合并操作
                if (existsUserId != null) {
                    //需要判断userId对应的用户是否有客户信息或者订单信息，
                    //如果有则提示“不能绑定，该手机号已与其它账号绑定，请更换其它手机号进行绑定。”
                    //如果没有则将其userAuths信息上的userId设置为existsUserId，然后删除userId对应的用户信息
                    if (this.checkUserHasClientOrOrderOrIncome(userId)) {
                        throw new BadRequestException("不能绑定，该手机号已与其它账号绑定，请更换其它手机号进行绑定。");
                    } else {
                        userAuthsMapper.updateUserId(userId, existsUserId);
                        // redisCache.delete(Constant.USER_CACHE + existsUserId);
                    }
                } else {
                    userMapper.updateMobile(userId, mobile, new Date());
                    // redisCache.delete(Constant.USER_CACHE + userId);
                }
            } else {
                userMapper.updateMobile(userId, mobile, new Date());
                // redisCache.delete(Constant.USER_CACHE + userId);
            }
            //前面的步骤有设置手机号，这里再获取一次
            userList = userMapper.selectByPhoneForAppLogin(mobile);
        }
        Map<String, Object> map = new HashMap<>(8);
        if (CollectionUtil.isNotEmpty(userList)) {
            //有用户数据，走获取账号并登录的流程
            int size = userList.size();
            if (size == 1) {
                User user = userMapper.selectByPrimaryKey(userList.get(0).getId());
                if (UserType.isDistributor(user.getUserType())) {
                    //经销商账号有效性校验
                    boolean forbidden = distributorMapper.checkForbiddenByUserName(user.getUserName());
                    if (forbidden) {
                        throw new BadRequestException("经销商账号（" + user.getUserName() + "）已被禁用，请联系客服解决");
                    }
                }
                //会员用户身份判断和升级
                this.saveIncomePermission(mobile, user);
                //设置用户的健康大使信息
                if (Objects.nonNull(sharerId)) {
                    this.fillUserAmbassador(user, sharerId);
                }
                //更新经销商表的appType值
                updateDistributorAppType(user.getId(), appType);
                map.put("type", 1);
                UserDTO userDTO = getUserInfoWithJWTToken(user, system);
                map.put("data", userDTO);
                map.put("token", userDTO.getToken());
                userDTO.setToken(null);
                return CommResult.ok(map);
            } else {
                for (UserDTO dto : userList) {
                    if (UserType.USER_4.value == dto.getUserType() || UserType.USER_3.value == dto.getUserType()) {
                        //前端要求不需要（有健康大使）字样，所以这里重新设置一下
                        dto.setUserTypeName("普通用户");
                    }
                }
                map.put("type", 2);
                map.put("data", userList);
                String key = UUIDUtil.longuuid();
                //设置一个随机字符串到缓存中，下一步选择了账号之后用此key进行验证，确保下一步接口的安全性
                redisCache.set(Constant.D_LOGIN_SETP_TWO + mobile, key, 3600L);
                map.put("key", key);
                return CommResult.ok(map);
            }
        } else {
            //无用户数据，走注册账号并登录的流程
            //检查该手机号是否购买过水机，是否有完成的工单，如果有则创建的用户为会员用户
            WorkOrderDTO workOrderDTO = orderFeign.getUserCompletedWorkOrder(mobile);
            Date nowDate = new Date();
            User user = new User();
            if (workOrderDTO != null) {
                boolean discount = distributorMapper.checkDiscountDistributor(workOrderDTO.getDistributorId());
                if (discount) {
                    user.setUserType(UserType.USER_4.value);
                    user.setUserTypeName(UserType.USER_4.name);
                } else {
                    user.setUserType(UserType.USER_7.value);
                    user.setUserTypeName(UserType.USER_7.name);
                    user.setDistributorId(workOrderDTO.getDistributorId());
                    user.setBeSalesTime(nowDate);
                }
            } else {
                user.setUserType(UserType.USER_4.value);
                user.setUserTypeName(UserType.USER_4.name);
            }
            user.setBindPhoneTime(nowDate);
            user.setNickName("未设置昵称");
            user.setHeadImg(Constant.DEFAULT_HEADIMAGE);
            user.setAvailable(true);
            user.setCreateTime(nowDate);
            user.setOrigin(OriginEnum.DISTRIBUTOR_MOBILE_REGISTER.value);
            user.setMobile(mobile);
            user.setOriginTerminal(Terminal.YIMAO_APP.value);
            userMapper.insertSelective(user);
            UserChangeRecordDTO dto = new UserChangeRecordDTO(user.getId(), 1, user.getUserType(), user.getMobile(), "创建用户");
            amqpTemplate.convertAndSend(RabbitConstant.USER_CHANGE_QUEUE, dto);

            map.put("type", 1);
            UserDTO userDTO = getUserInfoWithJWTToken(user, system);
            map.put("data", userDTO);
            map.put("token", userDTO.getToken());
            userDTO.setToken(null);
            return CommResult.ok(map);
        }
    }

    /**
     * 判断用户是否有客户或者订单数据，账号是否可以删除的依据
     */
    private boolean checkUserHasClientOrOrderOrIncome(Integer userId) {
        boolean existsClient = userMapper.existsClient(userId);
        boolean existsOrder = orderFeign.existsWithUserId(userId);
        boolean existsIncome = orderFeign.existsIncomeWithUserId(userId);
        return existsClient || existsOrder || existsIncome;
    }

    @Override
    public CommResult<UserDTO> appLoginByMobileSelectAccount(String mobile, String key, Integer userId, Integer system, Integer appType) {
        //设置一个随机字符串到缓存中，下一步选择了账号之后用此key进行验证，确保下一步接口的安全性
        String okey = redisCache.get(Constant.D_LOGIN_SETP_TWO + mobile);
        if (StringUtil.isEmpty(okey) || !Objects.equals(key, okey)) {
            throw new BadRequestException("非法登录");
        } else {
            User user = userMapper.selectByPrimaryKey(userId);
            if (user == null) {
                throw new BadRequestException("登录失败，用户不存在");
            }
            if (!Objects.equals(mobile, user.getMobile())) {
                throw new BadRequestException("登录失败，手机号错误");
            }
            if (UserType.isDistributor(user.getUserType())) {
                //经销商账号有效性校验
                boolean forbidden = distributorMapper.checkForbiddenByUserName(user.getUserName());
                if (forbidden) {
                    throw new BadRequestException("经销商账号（" + user.getUserName() + "）已被禁用，请联系客服解决");
                }
            }
            //会员用户身份判断和升级
            this.saveIncomePermission(mobile, user);
            //更新经销商表的appType值
            updateDistributorAppType(user.getId(), appType);
            return CommResult.ok(getUserInfoWithJWTToken(user, system));
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public CommResult<Map<String, Object>> appLoginByWechat(String code, Integer system, Integer appType) {
        Map<String, Object> map = new HashMap<>(8);
        WxUserInfo wxUserInfo = wechatFeign.getWxUserInfoByCode(code);
        if (wxUserInfo == null) {
            throw new BadRequestException("登录失败，未获取到授权用户信息");
        }
        List<Integer> userIdList = userAuthsMapper.listUserIdByUnionid(wxUserInfo.getUnionid());
        Date nowDate = new Date();
        User user;
        if (CollectionUtil.isEmpty(userIdList)) {
            user = new User();
            user.setUserType(UserType.USER_4.value);
            user.setUserTypeName(UserType.USER_4.name);
            String nickName = WxUtil.filterNickName(wxUserInfo.getNickname());
            user.setNickName(StringUtil.isEmpty(nickName) ? "未设置昵称" : nickName);
            // user.setNickName("未设置昵称");
            user.setSex(wxUserInfo.getSex());
            user.setHeadImg(this.saveWechatHeadImgToLocal(wxUserInfo.getHeadimgurl()));
            user.setAvailable(true);
            user.setCreateTime(nowDate);
            // 用户来源方式：1-代言卡分享 2-宣传卡分享 3-资讯分享 4-视频分享 5-商品分享 6-自主关注公众号 7-经销商APP添加
            // 8-水机屏推广二维码；11-经销商APP手机号注册；12-经销商APP微信授权注册
            user.setOrigin(12);
            //来源端：1-健康e家公众号 2-经销商APP 3-净水设备
            user.setOriginTerminal(Terminal.YIMAO_APP.value);
            userMapper.insertSelective(user);
            UserChangeRecordDTO dto = new UserChangeRecordDTO(user.getId(), 1, user.getUserType(), user.getMobile(),
                    "创建用户");
            amqpTemplate.convertAndSend(RabbitConstant.USER_CHANGE_QUEUE, dto);

            //创建用户授权信息
            UserAuths userAuth = new UserAuths();
            userAuth.setIdentifierUnique(wxUserInfo.getOpenid());
            userAuth.setIdentifier(wxUserInfo.getUnionid());
            userAuth.setIdentityType(IdentityTypeEnum.WECHAT_JXSAPP.value);
            userAuth.setState(StateEnum.BIND.value());
            userAuth.setCreateTime(nowDate);
            userAuth.setUserId(user.getId());
            userAuthsMapper.insertSelective(userAuth);
        } else {
            Integer userId = userIdList.get(0);
            UserAuths auths = userAuthsMapper.selectByOpenid(wxUserInfo.getOpenid(), IdentityTypeEnum.WECHAT_JXSAPP.value);
            if (auths == null) {
                auths = new UserAuths();
                auths.setIdentifierUnique(wxUserInfo.getOpenid());
                auths.setIdentifier(wxUserInfo.getUnionid());
                auths.setIdentityType(IdentityTypeEnum.WECHAT_JXSAPP.value);
                auths.setState(StateEnum.BIND.value());
                auths.setCreateTime(nowDate);
                auths.setUserId(userId);
                userAuthsMapper.insertSelective(auths);
            }
            user = userMapper.selectByPrimaryKey(userId);
            if (user == null) {
                log.error("创建或获取用户数据失败");
                return null;
            }
        }
        if (UserType.isDistributor(user.getUserType())) {
            //经销商账号有效性校验
            boolean forbidden = distributorMapper.checkForbiddenByUserName(user.getUserName());
            if (forbidden) {
                throw new DialogException("经销商账号（" + user.getUserName() + "）已被禁用，请联系客服解决");
            }
        }
        //更新经销商表的appType值
        updateDistributorAppType(user.getId(), appType);
        if (StringUtil.isEmpty(user.getMobile())) {
            map.put("type", 1);
            UserDTO userDTO = getUserInfoWithJWTToken(user, system);
            map.put("data", userDTO);
            map.put("token", userDTO.getToken());
            userDTO.setToken(null);
            return CommResult.ok(map);
        } else {
            return appLoginByMobile(null, user.getMobile(), null, system, appType);
        }
    }

    /**
     * 下单时如果是普通用户，需要设置健康大使，如果不是直接返回用户信息
     */
    @Override
    public UserDTO changeUserTypeIfMeetTheConditions(Integer id) {
        UserDTO user = userMapper.selectBasicUserById(id);
        //如果是普通用户，设置经销商信息
        if (Objects.equals(user.getUserType(), UserType.USER_4.value)) {
            UserIncomeAccountDTO account = userIncomeAccountService.getIncomeAccount();
            if (account == null) {
                log.error("普通用户下单获取特定经销商失败，用户e家号为：" + id);
                return null;
            }
            User updateUser = new User();
            updateUser.setId(id);
            if (account.getUserId() != null) {
                updateUser.setAmbassadorId(account.getUserId());
                updateUser.setBindAmbassadorTime(new Date());
            }
            updateUser.setDistributorId(account.getDistributorId());
            updateUser.setUserType(UserType.USER_3.value);
            updateUser.setUserTypeName(UserType.USER_3.name);
            updateUser.setBeSharerTime(new Date());
            int c = userMapper.updateByPrimaryKeySelective(updateUser);
            if (c <= 0) {
                log.error("普通用户下单完成设置特定经销商失败！userId={}", id);
                return null;
            }
            UserChangeRecordDTO dto = new UserChangeRecordDTO(id, 2, user.getUserType(), user.getMobile(), "成为分享用户");
            amqpTemplate.convertAndSend(RabbitConstant.USER_CHANGE_QUEUE, dto);
            //删除缓存的用户信息
            // redisCache.delete(Constant.USER_CACHE + id);
        }
        return getFullUserDTOById(id);
    }

    @Override
    public UserChangeRecordListDTO stationGetChangeInfoByUserId(Integer userId) {
        List<UserChangeRecordDTO> mainPointChangeRecord = new ArrayList<UserChangeRecordDTO>();
        List<UserChangeRecordDTO> otherChangeRecord = new ArrayList<UserChangeRecordDTO>();

        Example example = new Example(UserChangeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("origUserId", userId);
        List<UserChangeRecord> channgeRecordList = userChangeMapper.selectByExample(example);


        if (CollectionUtil.isNotEmpty(channgeRecordList)) {

            for (UserChangeRecord userChangeRecord : channgeRecordList) {

                Integer type = userChangeRecord.getType();

                if (UserChangeRecordEnum.CREATE_ACCOUNT_EVENT.value == type
                        ||
                        UserChangeRecordEnum.UPGRADE_SHARE_EVENT.value == type
                        ||
                        UserChangeRecordEnum.UPGRADE_SALE_EVENT.value == type
                        ||
                        UserChangeRecordEnum.FIRST_ATTENTION_EVENT.value == type
                        ||
                        UserChangeRecordEnum.CANCEL_LOGIN_EVENT.value == type
                        ||
                        UserChangeRecordEnum.CANCEL_ATTENTION_EVENT.value == type
                        ) {
                    //主要节点
                    UserChangeRecordDTO dto = new UserChangeRecordDTO();
                    userChangeRecord.convert(dto);
                    mainPointChangeRecord.add(dto);


                } else if (UserChangeRecordEnum.EDIT.value == type) {
                    //编辑
                    UserChangeRecordDTO dto = new UserChangeRecordDTO();
                    userChangeRecord.convert(dto);
                    //获取json串
                    String origJson = dto.getOrigUserData();
                    String destJson = dto.getDestUserData();
                    UserDTO origUser = JSON.parseObject(origJson, UserDTO.class);
                    UserDTO destUser = JSON.parseObject(destJson, UserDTO.class);
                    dto.setOriginUser(origUser);
                    dto.setDestUser(destUser);
                    otherChangeRecord.add(dto);

                }

            }
        }
        return new UserChangeRecordListDTO(mainPointChangeRecord, null, null, null, null, otherChangeRecord);
    }

    @Override
    public UserChangeRecordListDTO getChangeInfoByUserId(Integer userId) {
        List<UserChangeRecordDTO> mainPointChangeRecord = new ArrayList<UserChangeRecordDTO>();
        List<UserChangeRecordDTO> otherChangeRecord = new ArrayList<UserChangeRecordDTO>();

        Example example = new Example(UserChangeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("origUserId", userId);
        List<UserChangeRecord> channgeRecordList = userChangeMapper.selectByExample(example);


        if (CollectionUtil.isNotEmpty(channgeRecordList)) {

            for (UserChangeRecord userChangeRecord : channgeRecordList) {

                Integer type = userChangeRecord.getType();

                if (UserChangeRecordEnum.CREATE_ACCOUNT_EVENT.value == type
                        ||
                        UserChangeRecordEnum.UPGRADE_SHARE_EVENT.value == type
                        ||
                        UserChangeRecordEnum.UPGRADE_SALE_EVENT.value == type
                        ||
                        UserChangeRecordEnum.FIRST_ATTENTION_EVENT.value == type
                        ||
                        UserChangeRecordEnum.CANCEL_LOGIN_EVENT.value == type
                        ||
                        UserChangeRecordEnum.CANCEL_ATTENTION_EVENT.value == type
                        ) {
                    //主要节点
                    UserChangeRecordDTO dto = new UserChangeRecordDTO();
                    userChangeRecord.convert(dto);
                    mainPointChangeRecord.add(dto);


                } else if (UserChangeRecordEnum.EDIT.value == type) {
                    //编辑
                    UserChangeRecordDTO dto = new UserChangeRecordDTO();
                    userChangeRecord.convert(dto);
                    //获取json串
                    String origJson = dto.getOrigUserData();
                    String destJson = dto.getDestUserData();
                    UserDTO origUser = JSON.parseObject(origJson, UserDTO.class);
                    UserDTO destUser = JSON.parseObject(destJson, UserDTO.class);
                    dto.setOriginUser(origUser);
                    dto.setDestUser(destUser);
                    otherChangeRecord.add(dto);

                }

            }
        }
        return new UserChangeRecordListDTO(mainPointChangeRecord, null, null, null, null, otherChangeRecord);
    }


    /**
     * 更新经销商表的appType值
     *
     * @param userId  user表主键
     * @param appType 登录的手机系统：1-Android；2-ios
     */
    private void updateDistributorAppType(Integer userId, Integer appType) {
        distributorMapper.updateAppTypeByUserId(appType, userId);
    }

    @Override
    public Object queryCompanyInfoById(Integer id) {
        UserDTO userDTO = this.getBasicUserById(id);
        if (null == userDTO) {
            throw new BadRequestException("用户不存在");
        }
        Distributor distributor = null;
        UserCompany company = null;
        if (userDTO.getUserType() == UserType.DISTRIBUTOR_1000.value) {
            distributor = distributorMapper.selectByPrimaryKey(userDTO.getDistributorId());
        } else {
            distributor = distributorMapper.selectByPrimaryKey(userDTO.getMid());
        }
        if (distributor != null) {
            company = userCompanyMapper.selectByPrimaryKey(distributor.getCompanyId());
        }
        return company;
    }

    @Override
    public UserDTO getCustomerDetailInfo(Integer id) {
        UserDTO user = this.getFullUserDTOById(id);
        List<String> list = new ArrayList<>();
        List<CompanyCustomer> companyCustomers = null;
        if (null != user) {
            if (StringUtil.isNotEmpty(user.getMobile())) {
                Example example = new Example(CompanyCustomer.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("mobile", user.getMobile());
                companyCustomers = companyCustomerMapper.selectByExample(example);
                if (CollectionUtil.isEmpty(companyCustomers)) {
                    user.setCustomerType(1);
                } else {
                    user.setCustomerType(2);
                }
            } else {
                user.setCustomerType(1);
            }
        }
        return user;
    }

    @Override
    public UserGeneralSituationVO getUserGeneralSituation(Set<Integer> areas) {
        //根据areas获取经销商id集合
        List<Integer> distributorIds = distributorMapper.getDistributorIdsByAreaIds(areas);
        //获取普通用户和会员用户数量
        UserGeneralSituationVO userResult = userMapper.getGeneralUserNumAndVipUserNumByAreaIds(distributorIds);
        //获取各种经销商数量
        UserGeneralSituationVO distributorResult = distributorMapper.getAllDistributorNumByAreaIds(areas);
        //获取区县级代理商数量
        UserGeneralSituationVO agentResult = distributorMapper.getDistrictAgentNumByAreaIds(areas);

        UserGeneralSituationVO vo = new UserGeneralSituationVO();
        vo.setGeneralUserNum(userResult.getGeneralUserNum());
        vo.setVipUserNum(userResult.getVipUserNum());
        vo.setEnterpriseDistributorNum(distributorResult.getEnterpriseDistributorNum());
        vo.setExperienceDistributorNum(distributorResult.getExperienceDistributorNum());
        vo.setMicroinvasiveDistributorNum(distributorResult.getMicroinvasiveDistributorNum());
        vo.setPersonageDistributorNum(distributorResult.getPersonageDistributorNum());
        vo.setEnterpriseSonDistributorNum(distributorResult.getEnterpriseSonDistributorNum());
        vo.setDiscountDistributorNum(distributorResult.getDiscountDistributorNum());
        vo.setDistrictAgentNum(agentResult.getDistrictAgentNum());

        return vo;
    }

    /**
     * 站务系统-统计-用户统计--获取普通用户，会员用户数量（站务系统调用）
     *
     * @param query
     * @return
     */
    @Override
    public UserStatisticsDTO getUserRes(StatisticsQuery query) {
        if (CollectionUtil.isEmpty(query.getAreas())) {
            return null;
        }
        //根据地区id查询经销商id集合
        List<Integer> distributorIds = distributorService.getDistributorIdsByAreaIds(query.getAreas());
        if (CollectionUtil.isNotEmpty(distributorIds)) {
            query.setDistributorIds(distributorIds);
        }
        return userMapper.getUserSaleData(query);
    }

    @Override
    public String getOpenidByMobile(String mobile) {
        return userMapper.getOpenidByMobile(mobile);
    }

    @Override
    public UserDTO loginBySystemType(Integer userId, Integer systemType) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (Objects.nonNull(user)) {
            return getUserInfoWithJWTToken(user, systemType);
        }
        throw new YimaoException("获取用户信息失败");
    }

    @Override
    public UserStatisticsDTO getUserStatisticsInfoToStation(StatisticsQuery query) {
        if (CollectionUtil.isEmpty(query.getAreas())) {
            return null;
        }
        //根据地区id查询经销商id集合
        List<Integer> distributorIds = distributorService.getDistributorIdsByAreaIds(query.getAreas());
        if (CollectionUtil.isNotEmpty(distributorIds)) {
            query.setDistributorIds(distributorIds);
        }

        //经销商统计（按区域查询）
        List<UserStatisticsDTO> distributorRes = distributorMapper.getDistributorSaleData(query);
        //代理商统计（按区域查询）
        List<UserStatisticsDTO> agentRes = distributorMapper.getAgentSaleData(query);
        //用户统计图数据（按时间查询）
        List<UserStatisticsDTO> userPicRes = userMapper.getUserSalePicData(query);
        //经销商统计图数据（按时间查询）
        List<UserStatisticsDTO> distributorPicRes = distributorMapper.getDistributorSalePicData(query);
        //代理商统计图数据（按时间查询）
        List<UserStatisticsDTO> agentPicRes = distributorMapper.getAgentSalePicData(query);

        //信息封装
        UserStatisticsDTO result = new UserStatisticsDTO();
        result.setDistributorRes(distributorRes);
        result.setAgentRes(agentRes);
        result.setUserPicRes(userPicRes);
        result.setDistributorPicRes(distributorPicRes);
        result.setAgentPicRes(agentPicRes);
        return result;
    }
}
