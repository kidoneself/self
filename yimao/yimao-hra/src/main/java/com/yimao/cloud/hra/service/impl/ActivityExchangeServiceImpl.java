package com.yimao.cloud.hra.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.HraGiveTypeEnum;
import com.yimao.cloud.base.enums.TimeUnitEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.hra.feign.SystemFeign;
import com.yimao.cloud.hra.feign.UserFeign;
import com.yimao.cloud.hra.mapper.ActivityExchangeMapper;
import com.yimao.cloud.hra.mapper.ActivityExchangeSettingMapper;
import com.yimao.cloud.hra.mapper.HraExchangeRecordMapper;
import com.yimao.cloud.hra.po.ActivityExchange;
import com.yimao.cloud.hra.po.HraExchangeRecord;
import com.yimao.cloud.hra.po.HraExchangeSetting;
import com.yimao.cloud.hra.service.ActivityExchangeService;
import com.yimao.cloud.hra.service.HraTicketService;
import com.yimao.cloud.pojo.dto.hra.ActivityExchangeDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * @author Lizhqiang
 * @date 2019/4/10
 */
@Slf4j
@Service
public class ActivityExchangeServiceImpl implements ActivityExchangeService {
    @Resource
    private ActivityExchangeMapper activityExchangeMapper;

    @Resource
    private HraExchangeRecordMapper hraExchangeRecordMapper;

    @Resource
    private UserCache userCache;

    @Resource
    private HraTicketService hraTicketService;

    @Resource
    private ActivityExchangeSettingMapper activityExchangeSettingMapper;

    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserFeign userFeign;

    /**
     * 分页查询兑换码
     *
     * @param side
     * @param channel
     * @param exchangeCode
     * @param batchNumber
     * @param exchangeStatus
     * @param ticketNo
     * @param ticketStatus
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<ActivityExchangeDTO> page(Integer side, Integer channel, String exchangeCode, String batchNumber, String exchangeStatus, String ticketNo, Integer ticketStatus, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ActivityExchangeDTO> page = activityExchangeMapper.getActivityExchangePage(side, channel, exchangeCode, batchNumber, exchangeStatus, ticketNo, ticketStatus);
        return new PageVO<>(pageNum, page);
    }


    /**
     * 生成兑换码
     *
     * @param count
     * @param beginTime
     * @param endTime
     * @param side
     * @param channel
     * @param channelName
     */
    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public void saveHraExchange(Integer count, Date beginTime, Date endTime, Integer side, Integer channel, String channelName) {

        List<ActivityExchange> exchangeList = new ArrayList<>();
        ActivityExchange activityExchange;
        String code;
        String batchNumber = this.existsBatchNumber(channelName);

        for (int i = 0; i < count; i++) {
            activityExchange = new ActivityExchange();
            activityExchange.setBeginTime(beginTime);
            activityExchange.setEndTime(endTime);
            activityExchange.setCreator(userCache.getCurrentAdminRealName());
            activityExchange.setBatchNumber(batchNumber);
            activityExchange.setNum(1);
            //兑换状态 1-未兑换 2-兑换成功  3-兑换失败 4-活动过期 5-兑换禁止
            activityExchange.setExchangeStatus(1);
            //端
            activityExchange.setSide(side);
            //渠道
            activityExchange.setChannel(channel);
            activityExchange.setCreateTime(new Date());
            code = UUIDUtil.buildExchange(6);
            //兑换码不能重复
            if (this.existsExchangeCode(code)) {
                log.info("重复兑换码：" + code);
                --i;
                continue;
            }
            activityExchange.setExchangeCode(code);
            exchangeList.add(activityExchange);
        }
        activityExchangeMapper.insertBatch(exchangeList);
    }

    /**
     * 兑换
     *
     * @param exchangeCode
     * @param exchangeFrom
     * @param channel
     * @param ip
     * @return
     */
    @Override
    public String exChangeTicketByCode(String exchangeCode, Integer exchangeFrom, String channel, String ip) {
        Integer userId = userCache.getUserId();
        UserDTO user = userFeign.getUserById(userId);
        //兑换限制
        List<HraExchangeSetting> hraExchangeSettingList = activityExchangeSettingMapper.selectAll();
        HraExchangeSetting hraExchangeSetting = hraExchangeSettingList.get(0);
        String bTime = null;
        String eTime = null;
        String exchangeMsg = "";
        Integer times = 5;
        if (hraExchangeSetting != null) {
            times = hraExchangeSetting.getTimes();
            if (Objects.equals(hraExchangeSetting.getLimitType(), TimeUnitEnum.DAY.value)) {
                exchangeMsg = "抱歉，每位用户每天限兑换" + times + "次，您已超出限制！";
                bTime = DateUtil.getDayStartTime("yyyy-MM-dd HH:mm:ss");
                eTime = DateUtil.getDayEndTime("yyyy-MM-dd HH:mm:ss");
                //周
            } else if (Objects.equals(hraExchangeSetting.getLimitType(), TimeUnitEnum.WEEK.value)) {
                exchangeMsg = "抱歉，每位用户每周限兑换" + times + "次，您已超出限制！";
                bTime = DateUtil.getSunDayBeginTime();
                eTime = DateUtil.getSunDayEndTime();
                //月
            } else if (Objects.equals(hraExchangeSetting.getLimitType(), TimeUnitEnum.MONTH.value)) {
                exchangeMsg = "抱歉，每位用户每月限兑换" + times + "次，您已超出限制！";
                bTime = DateUtil.getCurrentMonthBeginTime("yyyy-MM-dd HH:mm:ss");
                eTime = DateUtil.getCurrentMonthEndTime(bTime, "yyyy-MM-dd HH:mm:ss");
            }
        }

        //记录hra兑换记录
        HraExchangeRecord hraExchangeRecord = this.buildHraExchangeRecord(exchangeCode, userId, ip, channel);

        //每个用户每天只能失败兑换5次(防止重复兑换)
        //根据用户id，当天时间，获取失败次数
        int failureNum = hraExchangeRecordMapper.failureCount(userId, channel);
        if (failureNum > 5) {
            throw new BadRequestException("您今天的失败次数超过五次了，请明天再兑换！");
        }

        Example example = new Example(ActivityExchange.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("exchangeCode", exchangeCode);
        List<ActivityExchange> activityExchanges = activityExchangeMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(activityExchanges)) {
            ActivityExchange activityExchange = activityExchanges.get(0);
            //兑换成功次数限制
            Integer successNum = hraExchangeRecordMapper.successCount(userId, bTime, eTime, activityExchange.getChannel() + "");
            if (times != 0 && successNum >= times) {
                throw new YimaoException(exchangeMsg);
            }

            if (activityExchange.getSide() != null && !Objects.equals(exchangeFrom, activityExchange.getSide())) {
                List<String> list = new ArrayList<>();
                list.add(activityExchange.getSide() + "");
                log.info("端不一样，不能兑换！");
                String sideOrChannel = systemFeign.findSideOrChannel("side", list, "公众号");
                throw new YimaoException("抱歉，请前往" + sideOrChannel + "进行兑换~");
            }
            Integer hraChannel = activityExchange.getChannel();
            //兑换状态 1-未兑换 2-兑换成功  3-兑换失败 4-活动过期 5-兑换禁止
            Integer status = activityExchange.getExchangeStatus();
            Date date = new Date();
            //兑换活动开启时间
            Date beginTime = activityExchange.getBeginTime();
            //兑换活动截止时间
            Date endTime = activityExchange.getEndTime();

            //如果渠道不一样，不能兑换  例如： 公众号里面的京东兑换入口，不能用天猫的兑换码兑换
            if (StringUtil.isNotEmpty(channel)) {
                List<String> list = StringUtil.spiltListByStr(channel);
                if (CollectionUtil.isNotEmpty(list) && !list.contains(hraChannel + "")) {
                    log.info("渠道不一样，不能兑换");
                    String sideOrChannel = systemFeign.findSideOrChannel("ACTIVITY_CHANNELL", list, "京东(JD)");
                    throw new YimaoException("抱歉，此处只能兑换" + sideOrChannel + "处领取的兑换码~");
                }
            }
            //新：加个无限期
            if (beginTime != null && endTime != null) {
                if (date.getTime() <= beginTime.getTime() || date.getTime() >= endTime.getTime()) {
                    if (date.getTime() <= beginTime.getTime()) {
                        throw new YimaoException("此兑换码需在 " + DateUtil.transferDateToString(beginTime, "yyyy-MM-dd HH:mm:ss") + "后,方可兑换!");
                    } else {
                        activityExchange.setExchangeStatus(4);
                        activityExchangeMapper.updateByPrimaryKey(activityExchange);
                        throw new YimaoException("兑换码已过期");
                    }
                }
            }

            if (status == 1) {
                JWTInfo jwtInfo = userCache.getJWTInfo();
                if (jwtInfo == null) {
                    throw new NotFoundException("获取不到用户信息");
                }

                activityExchange.setExchangeFrom(exchangeFrom);
                activityExchange.setExchangeTime(new Date());
                activityExchange.setUserId(userId);
                //生成优惠卡记录
                hraTicketService.generateHraRecord(user, 1, "通过兑换码兑换", HraGiveTypeEnum.HRA_EXCHANGE.value, hraChannel);
                //发放优惠卡 注：activityExchange.getChannel() 字段表示生成什么样式的卡
                String ticketNo = hraTicketService.assignCard(user, null, HraGiveTypeEnum.HRA_EXCHANGE.value);
                if (StringUtil.isEmpty(ticketNo)) {
                    log.info("兑换失败,发放优惠卡失败！");
                    throw new YimaoException("兑换失败~");
                }
                activityExchange.setExchangeStatus(2);
                activityExchange.setTicketNo(ticketNo);
                int count = activityExchangeMapper.updateByPrimaryKey(activityExchange);
                if (count > 0) {
                    if (hraExchangeRecord != null) {
                        hraExchangeRecord.setChannel(hraChannel + "");
                        hraExchangeRecord.setExchangeStatus(1);
                        hraExchangeRecordMapper.updateByPrimaryKey(hraExchangeRecord);
                    }
                    return ticketNo;
                }
                throw new YimaoException("兑换失败~");
            } else if (status == 2) {
                throw new YimaoException("抱歉，该兑换码已被使用，请重新输入~");
            } else if (status == 4) {
                throw new YimaoException("抱歉，该兑换码已过期~");
            } else if (status == 5) {
                throw new YimaoException("抱歉，此兑换码无法兑换~");
            }
        }
        throw new YimaoException("抱歉，兑换码不存在，请输入正确的兑换码哦~");
    }

    @Override
    public void exChangeSet(Integer terminal, Integer limitType, Integer times) {
        activityExchangeMapper.exChangeSet(terminal, limitType, times);
    }


    /**
     * 兑换码不能重复
     *
     * @param code 兑换码
     * @return
     */
    private Boolean existsExchangeCode(String code) {
        Example example = new Example(ActivityExchange.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("exchangeCode", code);
        List<ActivityExchange> hraExchanges = activityExchangeMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(hraExchanges)) {
            return false;
        }
        return true;
    }

    /**
     * 批次码不能重复
     *
     * @param channelName 渠道
     * @return
     */
    private String existsBatchNumber(String channelName) {
        String batchNumber = UUIDUtil.buildBatchNumber(channelName, 4);

        Example example = new Example(ActivityExchange.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("batchNumber", batchNumber);
        List<ActivityExchange> hraExchanges = activityExchangeMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(hraExchanges)) {
            log.info("重复批次号：" + batchNumber);
            return existsBatchNumber(channelName);
        }
        return batchNumber;
    }

    private HraExchangeRecord buildHraExchangeRecord(String exchangeCode, Integer userId, String ip, String channel) {
        HraExchangeRecord hraExchangeRecord = new HraExchangeRecord();
        hraExchangeRecord.setExchangeCode(exchangeCode);
        hraExchangeRecord.setUserId(userId);
        hraExchangeRecord.setChannel(channel);
        hraExchangeRecord.setExchangeTime(new Date());
        hraExchangeRecord.setIp(ip);
        hraExchangeRecord.setNum(1);
        hraExchangeRecord.setExchangeStatus(2);
        hraExchangeRecordMapper.insert(hraExchangeRecord);
        return hraExchangeRecord;
    }
}
