package com.yimao.cloud.hra.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.HraGiveTypeEnum;
import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.hra.feign.OrderFeign;
import com.yimao.cloud.hra.feign.UserFeign;
import com.yimao.cloud.hra.mapper.HraCardMapper;
import com.yimao.cloud.hra.mapper.HraDeviceMapper;
import com.yimao.cloud.hra.mapper.HraTicketMapper;
import com.yimao.cloud.hra.po.*;
import com.yimao.cloud.hra.service.*;
import com.yimao.cloud.pojo.dto.hra.HraCardDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceOnlineDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.user.UserDistributorDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Zhang Bo
 * @date 2017/12/1.
 */
@Service
@Slf4j
public class HraCardServiceImpl implements HraCardService {

    @Resource
    private HraCardMapper hraCardMapper;
    @Resource
    private HraTicketMapper hraTicketMapper;
    @Resource
    private HraDeviceMapper hraDeviceMapper;
    @Resource
    private HraTicketService hraTicketService;
    @Resource
    private TicketLifeCycleService ticketLifeCycleService;
    @Resource
    private DiscountCardRecordService discountCardRecordService;
    @Resource
    private DiscountCardSettingService discountCardSettingService;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserFeign userFeign;


    @Override
    public PageVO<HraCardDTO> getHraCardByUser(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        Page<HraCardDTO> page = hraCardMapper.selectCardAndTicket(userId);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public PageVO<HraCardDTO> listCardByUserId(Integer pageNum, Integer pageSize, Integer userId, Long orderId) {
        //1.获取评估卡
        PageHelper.startPage(pageNum, pageSize);
        Page<HraCardDTO> page = hraCardMapper.selectCardByUser(userId, orderId);
        if (CollectionUtil.isNotEmpty(page.getResult())) {
            List<HraTicket> ticketList;

            for (HraCardDTO hraCard : page.getResult()) {
                Example example = new Example(HraTicket.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("cardId", hraCard.getId());
                criteria.andEqualTo("userId", userId);
                criteria.andEqualTo("deleteStatus", 0);
                ticketList = hraTicketMapper.selectByExample(example);
                if (CollectionUtil.isNotEmpty(ticketList)) {
                    hraCard.setTotalCount(ticketList.size());
                    int useCount = 0;
                    boolean handsel = false;
                    Integer handselStatus = null;
                    for (HraTicket ticket : ticketList) {
                        if (ticket.getTicketStatus() != null) {
                            if (ticket.getTicketStatus() == 2) {
                                useCount++;
                            }
                            //设置【评估卡】赠送状态
                            if (ticket.getHandselStatus() != null && ticket.getHandselStatus() == 1) {
                                handselStatus = ticket.getHandselStatus();
                            }
                            //设置【评估卡】是否可赠送
                            Boolean flag = ticket.getTicketStatus() == 1 && (ticket.getHandselStatus() == null || ticket.getHandselStatus() == 2) && ticket.getCustomerId() == null;
                            if (flag) {
                                handsel = true;
                            }
                        }
                    }
                    hraCard.setUseCount(useCount);
                    hraCard.setHandsel(handsel);
                    hraCard.setHandselStatus(handselStatus);
                } else {
                    hraCard.setTotalCount(0);
                    hraCard.setUseCount(0);
                }
            }


            return new PageVO<>(pageNum, page);
        }
        return new PageVO<>();
    }

    @Override
    public Map<String, Object> listFreeCounpons(String state, Integer userId, Integer pageSize, Integer pageNum) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("noUsedNum", hraTicketMapper.getNoUsedCount(userId, 1));//待使用
        objectMap.put("usedNum", hraTicketMapper.getNoUsedCount(userId, 2));//已使用
        objectMap.put("sendedNum", hraTicketMapper.getSendCount(userId));//已赠出
        objectMap.put("overdueNum", hraTicketMapper.getNoUsedCount(userId, 4));//已过期
        List<HraTicketLifeCycle> lifeCycleList;
        List<String> tickectNoList = new ArrayList<>();

        if (state.equals("3")) {
            lifeCycleList = ticketLifeCycleService.getTicketLifeCycle(userId);
            if (CollectionUtil.isNotEmpty(lifeCycleList)) {
                for (HraTicketLifeCycle lifeCycle : lifeCycleList) {
                    log.info("lifeCycle.getTicketNo()=" + lifeCycle.getTicketNo());
                    tickectNoList.add(lifeCycle.getTicketNo());
                }
                PageHelper.startPage(pageNum, pageSize);
                Page<HraTicket> page = hraTicketMapper.findHraTicketByList(tickectNoList);
                log.info("page.getTotal()====" + page.getTotal());
                objectMap.put("ticketList", page);
            } else {
                log.info("************获取lifeCycleList为空************");
                objectMap.put("200", "overdue");
            }

            return objectMap;

        } else {
            Example example = new Example(HraTicket.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId", userId);
            criteria.andEqualTo("ticketStatus", Integer.parseInt(state));
            criteria.andEqualTo("ticketType", "Y");
            criteria.andEqualTo("deleteStatus", 0);
            example.orderBy("createTime").asc().orderBy("ticketStatus").asc();
            PageHelper.startPage(pageNum, pageSize);
            Page<HraTicket> page = (Page<HraTicket>) hraTicketMapper.selectByExample(example);
            log.info("page.getTotal()=" + page.getTotal());
            objectMap.put("ticketList", page);
        }
        return objectMap;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public int deleteCard(String cardId) {
        //判断卡是否存在
        HraCard hraCard = new HraCard();
        hraCard.setId(cardId);
        hraCard.setDeleteStatus(false);
        HraCard hc = hraCardMapper.selectOne(hraCard);
        if (hc != null) {
            //根据评估卡查询下面的评估劵，如果都已使用 ，可以删除，否则提示不可删
            Example example = new Example(HraTicket.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("cardId", cardId);
            criteria.andEqualTo("deleteStatus", false);
            List<HraTicket> tickets = hraTicketMapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(tickets)) {
                for (HraTicket tick : tickets) {
                    if (tick.getTicketStatus() != 2) {
                        return -1;
                    }
                }
            }
            hraCard.setDeleteStatus(true);
            return hraCardMapper.updateByPrimaryKeySelective(hc);
        }
        return 0;
    }

    /**
     * 保存发卡记录
     *
     * @param user     用户id
     * @param count    数量
     * @param remark   备注
     * @param giveType 类型
     */
    private void saveCardRecord(UserDTO user, int count, int totalCount, String remark, Integer giveType) {
        DiscountCardRecord record = new DiscountCardRecord();
        record.setUserId(user.getId());

        UserDistributorDTO distributor;
        DiscountCardSetting setting;
        Integer userType = user.getUserType();
        if (UserType.isDistributor(user.getUserType())) {
            record.setGiveName(user.getUserName());
        } else {
            //用户卡的样式是跟他的经销商的样式一样的
            record.setGiveName(user.getMobile());
            distributor = userFeign.getUserDistributor(user.getId());
            if (distributor != null) {
                userType = distributor.getUserType();
            }
        }
        setting = discountCardSettingService.getGiveCount(userType, user.getCompanyName());
        record.setTicketStyle(setting.getTicketStyle());
        record.setWatermark(setting.getWatermark());
        record.setImage(setting.getImage());
        record.setImageUsed(setting.getImageUsed());
        //发放类型
        record.setGiveType(giveType);
        record.setGiveCount(count);
        record.setTotalCount(totalCount);
        record.setRemark(remark);
        discountCardRecordService.saveRecord(record);
    }


    /**
     * 分销商身份判定、优惠卡政策执行
     *
     * @param userDTO 本地用户信息
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void freeCardHandsel(UserDTO userDTO) {
        if (UserType.isDistributor(userDTO.getUserType())) {//经销商
            //根据经销商Id获取经销商的oldMid
            this.countAndHandselFreeCard(userDTO);
        } else {
            String mobile = userDTO.getMobile();
            if (StringUtil.isEmpty(mobile)) {
                return;
            }
            //2.优惠卡发放政策
            this.countAndHandselFreeCard(userDTO);//用户
            //3.分销用户5张卡发放，修改时间：2018-07-10
            this.saleCardHandsel(userDTO);
        }
    }


    /**
     * 优惠卡赠送政策
     *
     * @param userDTO 本地MYSQL库用户信息
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void countAndHandselFreeCard(UserDTO userDTO) {
        //累计水机、健康产品已完成的工单数(即发卡数)
        int totalCount = orderFeign.countCompletedOrderFromDate(userDTO.getMid(), userDTO.getMobile(), userDTO.getUserType());
        //当前获得的优惠卡总数
        if (totalCount == 0) {
            return;
        }

        DiscountCardRecord record = discountCardRecordService.getHistoryRecord(userDTO);
        int giveCount = 0;
        if (record == null) {
            giveCount = totalCount;
        } else {
            //前一次计算的优惠卡总数
            int oldTotalCount = record.getTotalCount();
            if (oldTotalCount < totalCount) {
                //本次需要赠送的数量
                giveCount = totalCount - oldTotalCount;
            }
        }
        int giveType;
        String remark;
        if (giveCount > 0) {
            //经销商
            if (UserType.isDistributor(userDTO.getUserType())) {
                //经销商业绩优惠卡
                giveType = HraGiveTypeEnum.DIS_SALE.value;
                remark = HraGiveTypeEnum.DIS_SALE.name;
            } else {
                //用户业绩优惠卡
                giveType = HraGiveTypeEnum.USER_SALE.value;
                //用户卡的样式是跟他的经销商的样式一样的
                remark = HraGiveTypeEnum.USER_SALE.name;
            }
            //保存发卡记录
            this.saveCardRecord(userDTO, giveCount, totalCount, remark, giveType);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void saveHraDevice(HraDeviceOnlineDTO hraDeviceOnlineDTO) {
        HraDevice hraDevice = new HraDevice();
        hraDevice.setDeviceId(hraDeviceOnlineDTO.getDeviceId().toUpperCase());
        hraDevice.setDeviceType(hraDeviceOnlineDTO.getDeviceType());//设备型号
        hraDevice.setCreateTime(new Date());
        hraDevice.setStationId(hraDeviceOnlineDTO.getStationId());
        hraDevice.setStationName(hraDeviceOnlineDTO.getStationName());
        hraDevice.setStationAddress(hraDeviceOnlineDTO.getStationAddress());
        hraDevice.setStationTel(hraDeviceOnlineDTO.getStationTel());
        hraDevice.setDeviceStatus(2);//锁定
        int num = hraDeviceMapper.insertSelective(hraDevice);
        if (num < 1) {
            throw new YimaoException("设备上线失败");
        }
    }

    @Override
    public HraTicketDTO getHraTicketByOrderId(Long id) {
        Example example = new Example(HraCard.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", id);
        List<HraCard> hraCardList = hraCardMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(hraCardList)) {
            log.error("***************根据订单号未查询到体检卡***************");
            return null;
        }

        HraCard card = hraCardList.get(0);
        Example hraExample = new Example(HraTicket.class);
        Example.Criteria hraCriteria = hraExample.createCriteria();
        hraCriteria.andEqualTo("cardId", card.getId());
        List<HraTicket> hraTickets = hraTicketMapper.selectByExample(hraExample);
        HraTicketDTO ticketDTO = new HraTicketDTO();
        if (CollectionUtil.isEmpty(hraTickets)) {
            log.error("************查询HraTicket为空************");
            return null;
        }

        HraTicket hraTicket = hraTickets.get(0);
        hraTicket.convert(ticketDTO);
        return ticketDTO;
    }

    @Override
    public List<HraTicketDTO> cardDetailByOrderId(Long orderId) {
        //1.获取评估卡号列表
        Example example = new Example(HraCard.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("deleteStatus", 0);
        List<HraCard> ticketNoList = hraCardMapper.selectByExample(example);
        List<HraTicketDTO> ticketList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(ticketNoList)) {
            for (HraCard hc : ticketNoList) {
                //2.根据体检卡号获取多张体检卷
                List<HraTicketDTO> dtoList = hraTicketService.ticketByCardId(hc.getId());
                if (CollectionUtil.isNotEmpty(dtoList)) {
                    ticketList.addAll(dtoList);
                }
            }
        }
        if (CollectionUtil.isEmpty(ticketList)) {
            throw new YimaoException("此体检卡被送出去了哦。");
        }
        return ticketList;
    }


    /**
     * 分销用户5张卡发放，修改时间：2018-07-10
     *
     * @param userDTO
     */
    private void saleCardHandsel(UserDTO userDTO) {
        //获取分销用户的分销业绩卡记录。
        //每分销出去一台水机获得一张，总共五张(可配置)，业务修改时间：2018-07-10
        if (Objects.equals(UserType.USER_7.value, userDTO.getUserType())) {
            //分销出去的数量
            int saleCount = orderFeign.countSaleOrder(userDTO.getId());
            if (saleCount > 0) {
                //已经发放的分销业绩卡
                int saleRecordCount = discountCardRecordService.countVipUserQuotaRecord(userDTO);
                //一共可以发放多少张分销业绩卡
                DiscountCardSetting setting = discountCardSettingService.getGiveCount(UserType.USER_7.value, null);
                //符合数量条件才进行发放
                if (saleCount > saleRecordCount && saleRecordCount < setting.getGiveCount()) {

                    //计算需要发放的卡数量，上限为setting.getGiveCount()
                    int giveCount;
                    if (saleCount >= setting.getGiveCount()) {
                        giveCount = setting.getGiveCount() - saleRecordCount;
                    } else {
                        giveCount = saleCount - saleRecordCount;
                    }
                    if (giveCount > 0) {
                        DiscountCardRecord newRecord = new DiscountCardRecord();
                        newRecord.setUserId(userDTO.getId());
                        newRecord.setGiveName(userDTO.getMobile());
                        newRecord.setGiveType(HraGiveTypeEnum.USER_QUOTA.value);//分销业绩卡
                        newRecord.setRemark(HraGiveTypeEnum.USER_QUOTA.name);
                        newRecord.setGiveCount(giveCount);
                        newRecord.setTotalCount(giveCount + saleRecordCount);
                        //用户卡的样式是跟他的经销商的样式一样的
                        UserDistributorDTO userDistributor = userFeign.getUserDistributor(userDTO.getId());
                        Integer disUserType = UserType.USER_7.value;
                        if (Objects.nonNull(userDistributor) && Objects.nonNull(userDistributor.getRoleLevel())) {
                            //50体验版、350微创版、650个人版、950企业版
                            switch (userDistributor.getRoleLevel()) {
                                case 50:
                                    disUserType = UserType.DISTRIBUTOR_50.value;
                                    break;
                                case 350:
                                    disUserType = UserType.DISTRIBUTOR_350.value;
                                    break;
                                case 650:
                                    disUserType = UserType.DISTRIBUTOR_650.value;
                                    break;
                                case 950:
                                    disUserType = UserType.DISTRIBUTOR_950.value;
                                    break;
                                case 1000:
                                    disUserType = UserType.DISTRIBUTOR_1000.value;
                                    break;
                                default:
                                    disUserType = UserType.USER_7.value;
                                    break;
                            }
                        }
                        DiscountCardSetting disUserSetting = discountCardSettingService.getGiveCount(disUserType, Objects.isNull(userDistributor.getUserCompany()) ? null : userDistributor.getUserCompany().getCompanyName());
                        newRecord.setTicketStyle(disUserSetting.getTicketStyle());
                        newRecord.setWatermark(disUserSetting.getWatermark());
                        newRecord.setImage(disUserSetting.getImage());
                        newRecord.setImageUsed(disUserSetting.getImageUsed());
                        discountCardRecordService.saveRecord(newRecord);
                    }
                }
            }
        }
    }

}
