package com.yimao.cloud.hra.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.BaseTimeUnit;
import com.yimao.cloud.base.enums.HraGiveTypeEnum;
import com.yimao.cloud.base.enums.HraHandselStatusEnum;
import com.yimao.cloud.base.enums.HraLifeCycleEnum;
import com.yimao.cloud.base.enums.HraTicketStatusEnum;
import com.yimao.cloud.base.enums.HraType;
import com.yimao.cloud.base.enums.OrderFrom;
import com.yimao.cloud.base.enums.Terminal;
import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.base.enums.VirtualEffectiveType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.hra.feign.OrderFeign;
import com.yimao.cloud.hra.feign.ProductFeign;
import com.yimao.cloud.hra.feign.SystemFeign;
import com.yimao.cloud.hra.feign.UserFeign;
import com.yimao.cloud.hra.mapper.DiscountCardRecordMapper;
import com.yimao.cloud.hra.mapper.HraCardMapper;
import com.yimao.cloud.hra.mapper.HraCustomerMapper;
import com.yimao.cloud.hra.mapper.HraTicketLifeCycleMapper;
import com.yimao.cloud.hra.mapper.HraTicketMapper;
import com.yimao.cloud.hra.po.DiscountCardRecord;
import com.yimao.cloud.hra.po.DiscountCardSetting;
import com.yimao.cloud.hra.po.HraCard;
import com.yimao.cloud.hra.po.HraCustomer;
import com.yimao.cloud.hra.po.HraTicket;
import com.yimao.cloud.hra.po.HraTicketLifeCycle;
import com.yimao.cloud.hra.service.DiscountCardRecordService;
import com.yimao.cloud.hra.service.DiscountCardSettingService;
import com.yimao.cloud.hra.service.HraCustomerService;
import com.yimao.cloud.hra.service.HraTicketService;
import com.yimao.cloud.pojo.dto.hra.HraAllotTicketDTO;
import com.yimao.cloud.pojo.dto.hra.HraCardDTO;
import com.yimao.cloud.pojo.dto.hra.HraCustomerDTO;
import com.yimao.cloud.pojo.dto.hra.HraFCardDTO;
import com.yimao.cloud.pojo.dto.hra.HraPhysicalDTO;
import com.yimao.cloud.pojo.dto.hra.HraQueryDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketLifeCycleDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketResultDTO;
import com.yimao.cloud.pojo.dto.hra.ReserveDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.product.VirtualProductConfigDTO;
import com.yimao.cloud.pojo.dto.station.HraStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.query.station.HraSpecialQuery;
import com.yimao.cloud.pojo.query.station.HraTicketQuery;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.hra.HraCardVO;
import com.yimao.cloud.pojo.vo.station.StationHraCardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Zhang Bo
 * @date 2017/12/1.
 */
@Service
@Slf4j
public class HraTicketServiceImpl implements HraTicketService {

    private Lock lock = new ReentrantLock();
    @Resource
    private DomainProperties domainProperties;
    @Resource
    private HraCardMapper hraCardMapper;
    @Resource
    private HraTicketMapper hraTicketMapper;
    @Resource
    private HraCustomerMapper hraCustomerMapper;
    @Resource
    private HraTicketLifeCycleMapper hraTicketLifeCycleMapper;
    @Resource
    private HraCustomerService hraCustomerService;
    @Resource
    private DiscountCardSettingService discountCardSettingService;
    @Resource
    private DiscountCardRecordService discountCardRecordService;
    @Resource
    private DiscountCardRecordMapper discountCardRecordMapper;
    @Resource
    private MailSender mailSender;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private RedisCache redisCache;


    @Override
    public HraTicket findHraTicketByTicketNo(String ticketNo) {
        Example example = new Example(HraTicket.class);
        example.createCriteria().andEqualTo("ticketNo", ticketNo);
        List<HraTicket> list = hraTicketMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<HraTicketDTO> findTicketListByUserId(Integer userId) {
        return hraTicketMapper.findTicketListByUserId(userId);
    }

    @Override
    public HraTicketDTO findByTicketNoAndStatus(String ticketNo) {
        log.debug("********传入体检卡号,ticketNo=" + ticketNo + "*********");
        HraTicketDTO hraTicket = hraTicketMapper.findByTicketNoAndStatus(ticketNo);
        if (hraTicket != null) {
            return hraTicket;
        }
        throw new NotFoundException("hraTicket为空");
    }

    @Override
    public HraTicketDTO findTicketByUserIdAndTicketNo(String ticketNo, Integer userId) {
        List<HraTicket> ticketList = hraTicketMapper.findTicketByUserIdAndTicketNo(ticketNo, userId);
        HraTicketDTO ticketDTO = new HraTicketDTO();
        if (CollectionUtil.isNotEmpty(ticketList)) {
            HraTicket hraTicket = ticketList.get(0);
            hraTicket.convert(ticketDTO);
            HraCustomerDTO customerDTO = hraCustomerService.findHraCustomer(hraTicket.getCustomerId());
            if (customerDTO != null) {
                ticketDTO.setHraCustomer(customerDTO);
            }
            return ticketDTO;
        }
        throw new NotFoundException("ticketList为空！");
    }

    @Override
    public PageVO<HraTicketDTO> reportTicket(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        Page<HraTicketDTO> page = hraTicketMapper.reportTicket(pageNum, pageSize, userId);
        if (CollectionUtil.isNotEmpty(page)) {
            throw new NotFoundException("page为空！");
        }
        return new PageVO<>(pageNum, page);
    }

    @Override
    public List<HraTicketDTO> ticketByCardId(String cardId) {
        List<HraTicketDTO> page = hraTicketMapper.ticketByCardId(cardId);
        if (CollectionUtil.isNotEmpty(page)) {
            for (HraTicketDTO ticket : page) {
                ticket.setHandsel(true);
                if (Objects.equals(HraTicketStatusEnum.HRA_USE.value, ticket.getTicketStatus()) || Objects.equals(HraTicketStatusEnum.HRA_STOP.value, ticket.getTicketStatus()) || Objects.equals(HraTicketStatusEnum.HRA_EXPIRE.value, ticket.getTicketStatus())) {
                    //评估卡状态为【已使用】【已禁用】【已过期】时不可被赠送
                    ticket.setHandsel(false);
                }
                if (ticket.getHandselStatus() != null && ticket.getHandselStatus() == 1) {
                    //已赠送中的评估卡不可被赠送
                    ticket.setHandsel(false);
                }
                if (ticket.getCustomerId() != null) {
                    //已预约的评估卡不可被赠送
                    ticket.setHandsel(false);
                }
            }
        }
        return page;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public HraTicketDTO createReserveTicket(ReserveDTO reserveDTO, Integer userId) {
        //判断评估号是否存在(或已使用或已过期)
        HraTicket ticket = new HraTicket();
        ticket.setTicketNo(reserveDTO.getTicketNo());
        ticket.setUserId(userId);
        ticket.setDeleteStatus(false);
        HraTicket tick = hraTicketMapper.selectOne(ticket);

        if (tick == null) {
            log.error("预约失败,找不到该体检卡");
            throw new NotFoundException("找不到该体检卡!");
        }
        //状态：1未使用，2已用，3禁用，4过期 5-预约过期
        if (Objects.equals(HraTicketStatusEnum.HRA_PAY.value, tick.getTicketStatus()) || Objects.equals(HraTicketStatusEnum.HRA_STOP.value, tick.getTicketStatus()) || Objects.equals(HraTicketStatusEnum.HRA_RESERVE.value, tick.getTicketStatus())) {
            if (Objects.equals(HraType.F.value, tick.getTicketType())) {
                log.error("预约失败,服务站卡不能预约");
                throw new NotFoundException("预约失败,服务站卡不能预约!");
            }
            //如果是赠送中，不能预约
            if (tick.getHandselStatus() != null && Objects.equals(HraHandselStatusEnum.HRA_SEND.value, tick.getHandselStatus())) {
                log.error("预约失败,该卡处于赠送中");
                throw new NotFoundException("预约失败,该卡处于赠送中!");
            }
        } else {
            log.error("已使用或者已过期的卡不能预约");
            throw new NotFoundException("已使用或者已过期的卡不能预约!");
        }
        if (StringUtil.isNotEmpty(reserveDTO.getSex())) {
            if ("1".equals(reserveDTO.getSex())) {
                reserveDTO.setSex("男");
            } else {
                reserveDTO.setSex("女");
            }
        }
        HraCustomer customer = new HraCustomer();
        HraTicketDTO hraTicketDTO;
        HraCustomerDTO customerDTO;
        //已过期卡预约
        if (tick.getCustomerId() != null && tick.getReserveTime().getTime() < (new Date()).getTime()) {
            HraCustomerDTO hc = hraCustomerService.findHraCustomer(tick.getCustomerId());
            hc.setUsername(reserveDTO.getUserName());
            hc.setIdcard(reserveDTO.getIdCard());
            hc.setPhone(reserveDTO.getPhone());
            hc.setHeight(reserveDTO.getHeight());
            hc.setWeight(reserveDTO.getWeight());
            hc.setBirthdate(reserveDTO.getBirthDate());
            hc.setSex(reserveDTO.getSex());
            customer = new HraCustomer(hc);
            customerDTO = hraCustomerService.update(customer);
            hraTicketDTO = updateHraTicket(customerDTO.getId(), reserveDTO, userId, null);
            if (hraTicketDTO != null) {
                hraTicketDTO.setHraCustomer(customerDTO);
                return hraTicketDTO;
            }
            //正常卡预约
        } else if (tick.getCustomerId() == null && tick.getReserveTime() == null) {
            customer.setUsername(reserveDTO.getUserName());
            customer.setIdcard(reserveDTO.getIdCard());
            customer.setPhone(reserveDTO.getPhone());
            customer.setHeight(reserveDTO.getHeight());
            customer.setWeight(reserveDTO.getWeight());
            customer.setBirthdate(reserveDTO.getBirthDate());
            customer.setSex(reserveDTO.getSex());
            customerDTO = hraCustomerService.save(customer);
            hraTicketDTO = updateHraTicket(customerDTO.getId(), reserveDTO, userId, null);
            if (hraTicketDTO != null) {
                hraTicketDTO.setHraCustomer(customerDTO);
                return hraTicketDTO;
            }
        }
        return null;
    }

    private HraTicketDTO updateHraTicket(Integer customerId, ReserveDTO reserveDTO, Integer userId, Integer status) {
        //根据评估劵号查询到评估劵对象
        HraTicketDTO ticketDTO = new HraTicketDTO();
        ticketDTO.setTicketNo(reserveDTO.getTicketNo());
        HraTicket hraTicket = new HraTicket(ticketDTO);
        HraTicket tick = hraTicketMapper.selectOne(hraTicket);
        if (tick != null) {
            tick.setUserId(userId);
            tick.setCustomerId(customerId);
            tick.setStationId(reserveDTO.getStationId());
            tick.setReserveStationId(reserveDTO.getStationId());
            tick.setReserveTime(DateUtil.transferStringToDate(reserveDTO.getReserveTime(), "yyyy-MM-dd HH:mm"));
            tick.setReserveFrom(reserveDTO.getReserveFrom());
//                if(status!=null){
//                    tick.setTicketStatus(status);
//                }
            hraTicketMapper.updateByPrimaryKey(tick);
            tick.convert(ticketDTO);
            return ticketDTO;
        }
        throw new NotFoundException("tick为空!");
    }

    @Override
    public PageVO<HraTicketDTO> getHraTicketByUser(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        Page<HraTicketDTO> hraPage = hraTicketMapper.getHraTicketByUser(userId);
        if (CollectionUtil.isNotEmpty(hraPage)) {
            HraCustomerDTO hraCustomerDTO;
            StationDTO stationDTO = null;
            for (HraTicketDTO ticket : hraPage) {
                //如果预约时间已过期
                if (!Objects.equals(HraTicketStatusEnum.HRA_USE.value, ticket.getTicketStatus()) && ticket.getReserveTime() != null && DateUtil.getDayEndTime(ticket.getReserveTime()).getTime() < (new Date()).getTime()) {
                    HraTicket hraTicket = new HraTicket(ticket);
                    ticket.setTicketStatus(HraTicketStatusEnum.HRA_RESERVE.value); //逻辑设置预约已过期,供前端判断
                    hraTicketMapper.updateByPrimaryKeySelective(hraTicket);
                }

                if (ticket.getCustomerId() != null) {
                    hraCustomerDTO = hraCustomerService.findHraCustomer(ticket.getCustomerId());
                    ticket.setHraCustomer(hraCustomerDTO);
                    if (ticket.getReserveStationId() != null) {
                        stationDTO = systemFeign.getStationById(ticket.getReserveStationId()); //查询预约的服务站
                    }
                    if (stationDTO == null) {
                        stationDTO = systemFeign.getStationById(ticket.getStationId());  //实际体检的服务站
                    }
                }
                ticket.setStation(stationDTO);
            }
        }
        return new PageVO<>(pageNum, hraPage);
    }


    @Override
    public PageVO<HraTicketDTO> reserveTicketList(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        Page<HraTicketDTO> hraPage = hraTicketMapper.reserveTicketList(userId);
        return new PageVO<>(pageNum, hraPage);
    }

    @Override
    public Map<String, Object> listFreeCard(Integer pageNum, Integer pageSize, Long orderId, Integer userId, Integer ticketStatus) {
        Map<String, Object> objectMap = new HashMap<>(8);
        objectMap.put("total", 0); //总数
        objectMap.put("nPay", hraTicketMapper.selectFreeCardCount(userId, HraTicketStatusEnum.HRA_STOP.value));   //未付款-未赠送
        objectMap.put("yPay", hraTicketMapper.selectFreeCardCount(userId, HraTicketStatusEnum.HRA_PAY.value));   //已付款
        objectMap.put("yUse", hraTicketMapper.selectFreeCardCount(userId, HraTicketStatusEnum.HRA_USE.value));   //已使用
        objectMap.put("yPast", hraTicketMapper.selectFreeCardCount(userId, HraTicketStatusEnum.HRA_EXPIRE.value));  //已过期
        objectMap.put("yGive", hraTicketMapper.selectGiveCardCountByUser(userId));  //已赠送

        Page<HraTicketDTO> page;
        PageHelper.startPage(pageNum, pageSize);
        if (ticketStatus != null && Objects.equals(ticketStatus, HraTicketStatusEnum.HRA_RESERVE.value)) {
            page = hraTicketMapper.selectGiveCardByUser(userId, "M");
            if (CollectionUtil.isNotEmpty(page.getResult())) {
                HraTicketDTO hraTicket;
                for (HraTicketDTO ticket : page.getResult()) {
                    //解决：字段长度不一样，索引失效。 速度慢的问题
                    hraTicket = hraTicketMapper.findTicketByTicketNo(ticket.getTicketNo());
                    if (hraTicket != null) {
                        ticket.setTicketPrice(hraTicket.getTicketPrice());
                        ticket.setValidBeginTime(hraTicket.getValidBeginTime());
                        ticket.setValidEndTime(hraTicket.getValidEndTime());
                        ticket.setTicketStyle(hraTicket.getTicketStyle());
                        ticket.setTicketContent(hraTicket.getTicketContent());
                        ticket.setTicketStatus(hraTicket.getTicketStatus());
                        ticket.setHandselStatus(hraTicket.getHandselStatus());
                        ticket.setImage(hraTicket.getImage());
                        ticket.setImageUsed(hraTicket.getImageUsed());
                    }
                }
            }
        } else {
            page = hraTicketMapper.selectFreeCardByUser(userId, "M", orderId, ticketStatus);
        }
        PageVO<HraTicketDTO> basePage = new PageVO<>(pageNum, page);
        objectMap.put("ticketList", basePage);

        if (CollectionUtil.isEmpty(basePage.getResult())) {
            return objectMap;
        }

        // ticketStatus  1-未使用，2-已使用，3-待付款，4-已过期 5-已赠出
        for (HraTicketDTO ticket : basePage.getResult()) {
            ticket.setHandsel(true);
            if (Objects.equals(ticketStatus, HraTicketStatusEnum.HRA_USE.value) || Objects.equals(ticketStatus, HraTicketStatusEnum.HRA_EXPIRE.value)) {
                //评估卡状态为【已使用】【已过期】时不可被赠送
                ticket.setHandsel(false);
            }
            if (ticket.getHandselStatus() != null && ticket.getHandselStatus() == 1) {
                //已赠送中的评估卡不可被赠送
                ticket.setHandsel(false);
            }
            if (ticket.getCustomerId() != null) {
                //已预约的评估卡不可被赠送
                ticket.setHandsel(false);
            }
            //已赠出的卡对于自己来说不可被赠送。对于其他人是可以赠送的
            if (ticketStatus != null && Objects.equals(ticketStatus, HraTicketStatusEnum.HRA_RESERVE.value)) {
                ticket.setHandsel(false);
            }

            //从订单入口进，判断卡是否可赠送
            if (orderId != null) {
                int count = hraTicketLifeCycleMapper.hasGiveCard(ticket.getTicketNo(), userId);
                if (count > 0) {
                    ticket.setHandsel(false);
                    ticket.setTicketStatus(5);
                }
            }
        }
        objectMap.put("total", basePage.getTotal());
        return objectMap;
    }

    @Override
    public Map<String, Object> getLifecyleByTicket(String ticketNo, Integer userId) {
        // 查询卡是否存在,同时也为了获取卡的创建时间和卡的当前状态(后面有用)
        Example example1 = new Example(HraTicket.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("ticketNo", ticketNo);
        List<HraTicket> hraTickets = hraTicketMapper.selectByExample(example1);
        if (CollectionUtil.isEmpty(hraTickets)) {
            return null;
        }

        Date payTime = null;
        //根据体检卡号，获取订单号
        Long orderId = hraTicketMapper.getOrderIdByTicketNo(ticketNo);
        if (Objects.nonNull(orderId)) {
            OrderSubDTO subDTO = orderFeign.findBasicOrderInfoById(orderId);
            if (subDTO != null) {
                payTime = subDTO.getPayTime();
            }
        }
        //体检卡使用时间
        Date useTime = hraTickets.get(0).getUseTime();

        //根据体检卡号获取该体检卡的生命周期列表
        Example example = new Example(HraTicketLifeCycle.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("ticketNo", "%" + ticketNo + "%");
        criteria.andIsNotNull("destUserId");
        example.setOrderByClause("receive_time ASC");
        List<HraTicketLifeCycle> lifeCycleList = hraTicketLifeCycleMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(lifeCycleList)) {
            throw new NotFoundException("lifeCycleList为空!");            //如果该体检卡未发生赠送行为,直接返回null
        }

        List<HraTicketLifeCycleDTO> hraTicketLifeCycleDtoList = new ArrayList<>();
        int size = lifeCycleList.size();
        boolean flag = false;
        boolean insertFlag = true;
        for (int i = 0; i < size; i++) {
            HraTicketLifeCycle h = lifeCycleList.get(i);
            Integer origUserId = h.getOrigUserId();

            if (Objects.equals(userId, origUserId) || flag) {       //循环遍历判断赠送人是否是当前用户,如果是,则从这条记录开始记录
                flag = true;
                HraTicketLifeCycleDTO tmp = this.getHraTicketLifeCycleDTO(origUserId);
                if (i != 0) {
                    HraTicketLifeCycle pre = lifeCycleList.get(i - 1);
                    Date time = pre.getReceiveTime();
                    tmp.setTime(time);
                    tmp.setStatus(HraLifeCycleEnum.HRA_LIFE_RECEiVER.value);
                    /*插入支付记录 insertFlag 保证只进入一次(体检卡只能支付一次)*/
                    if (payTime != null && payTime.before(time) && insertFlag) {
                        HraTicketLifeCycleDTO preResult = hraTicketLifeCycleDtoList.get(hraTicketLifeCycleDtoList.size() - 1);
                        HraTicketLifeCycleDTO insert = new HraTicketLifeCycleDTO();
                        insert.setId(preResult.getId());
                        insert.setNickName(preResult.getNickName());
                        insert.setHeadImg(preResult.getHeadImg());
                        insert.setTime(payTime);
                        insert.setStatus(HraLifeCycleEnum.HRA_LIFE_PAY.value);
                        hraTicketLifeCycleDtoList.add(insert);
                        insertFlag = false;
                    }
                } else {
                    //如果该卡是当前用户最先拥有的,则获得时间为卡的创建时间
                    tmp.setTime(hraTickets.get(0).getCreateTime());
                    tmp.setStatus(HraLifeCycleEnum.HRA_LIFE_RECEiVER.value);
                }
                hraTicketLifeCycleDtoList.add(tmp);
            }
        }
        if (CollectionUtil.isEmpty(hraTicketLifeCycleDtoList)) {
            return null;
        }

        //设置最后一个领取人的领取卡的时间
        HraTicketLifeCycle last = lifeCycleList.get(size - 1);
        HraTicketLifeCycleDTO lastDest = this.getHraTicketLifeCycleDTO(last.getDestUserId());
        if (lastDest != null) {
            lastDest.setTime(last.getReceiveTime());
            lastDest.setStatus(HraLifeCycleEnum.HRA_LIFE_RECEiVER.value);
            hraTicketLifeCycleDtoList.add(lastDest);
        }

        if (payTime != null && lastDest != null && payTime.after(lastDest.getTime())) {
            HraTicketLifeCycleDTO preResult = hraTicketLifeCycleDtoList.get(hraTicketLifeCycleDtoList.size() - 1);
            HraTicketLifeCycleDTO insert = new HraTicketLifeCycleDTO();
            insert.setId(preResult.getId());
            insert.setNickName(preResult.getNickName());
            insert.setHeadImg(preResult.getHeadImg());
            insert.setTime(payTime);
            insert.setStatus(HraLifeCycleEnum.HRA_LIFE_PAY.value);
            hraTicketLifeCycleDtoList.add(insert);
        }

        /*插入使用记录*/
        if (useTime != null) {
            HraTicketLifeCycleDTO preResult = hraTicketLifeCycleDtoList.get(hraTicketLifeCycleDtoList.size() - 1);
            HraTicketLifeCycleDTO insert = new HraTicketLifeCycleDTO();
            insert.setId(preResult.getId());
            insert.setNickName(preResult.getNickName());
            insert.setHeadImg(preResult.getHeadImg());
            insert.setTime(useTime);
            insert.setStatus(HraLifeCycleEnum.HRA_LIFE_USER.value);
            hraTicketLifeCycleDtoList.add(insert);
        }

        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("resultList", hraTicketLifeCycleDtoList);
        resultMap.put("ticketStatus", hraTickets.get(0).getTicketStatus());
        resultMap.put("isPay", payTime == null ? "false" : "true");

        return resultMap;
    }


    @Override
    public Integer getCanBeSendCardCount(Integer id, Integer ticketStatus, String ticketType) {
        return hraTicketMapper.getCanBeSendCardCount(id, ticketStatus, ticketType);
    }

    @Override
    public Integer cancelReserve(Integer ticketId, Integer userId) {
        //确保这张卡，就是登录用户自己的卡
        Example example = new Example(HraTicket.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", ticketId);
        criteria.andEqualTo("userId", userId);
        HraTicket ticket = hraTicketMapper.selectOneByExample(example);
        if (ticket == null) {
            log.error("取消预约失败，评估卡不存在");
            throw new YimaoException("取消预约失败,评估卡不存在！");
        }
        Integer ticketStatus = ticket.getTicketStatus();
        if (ticketStatus == null || Objects.equals(ticketStatus, HraTicketStatusEnum.HRA_USE.value) || ticket.getDeviceId() != null) {
            log.error("取消预约失败，已使用的评估卡不能取消");
            throw new YimaoException("取消预约失败,已使用的评估卡不能取消！");
        }

        Integer customerId = ticket.getCustomerId();

        if (Objects.equals(ticketStatus, HraTicketStatusEnum.HRA_PAY.value) || Objects.equals(ticketStatus, HraTicketStatusEnum.HRA_STOP.value)) {
            ticket.setReserveStationId(null);
            ticket.setStationId(null);
            ticket.setReserveFrom(null);
            ticket.setReserveTime(null);
            ticket.setCustomerId(null);
            int count = hraTicketMapper.updateByPrimaryKey(ticket);
            if (count > 0) {
                try {
                    hraCustomerService.deleteHraCustomer(customerId);
                } catch (Exception e) {
                    log.error("取消预约成功，删除客户信息失败，评估卡号：" + ticket.getTicketNo() + ",客户id：" + customerId);
                }
                return 1;
            }
        }
        log.error("取消预约失败!");
        throw new YimaoException("取消预约失败！");
    }

    @Override
    public HraTicketDTO getTicketByTicketNo(String ticketNo) {
        HraTicketDTO hraTicket = hraTicketMapper.getTicketByTicketNo(ticketNo);
        if (hraTicket == null) {
            return null;
        }
        return hraTicket;
    }

    @Override
    public PageVO<HraTicketResultDTO> listTicket(String beginTime, String endTime, Integer userSource, String mobile, String ticketNo, String province, String city, String region, String name, Integer flag, Integer pageNum, Integer pageSize, Integer reserveStatus, Integer hasUpload, Integer userId, String ticketType) {

        List<Integer> stationIds = this.getStationByArea(province, city, region);
        PageHelper.startPage(pageNum, pageSize);
        Page<HraTicketResultDTO> ticketPage = hraTicketMapper.listTicket(beginTime, endTime, userSource, mobile, ticketNo, province, city, region, name, flag, reserveStatus, hasUpload, userId, ticketType, stationIds);
        if (CollectionUtil.isNotEmpty(ticketPage)) {
            for (HraTicketResultDTO dto : ticketPage) {
                Integer stationId = dto.getStationId();
                if (stationId != null) {
                    StationDTO station = systemFeign.getStationById(stationId);
                    if (null != station) {
                        dto.setStationProvince(station.getProvince());
                        dto.setStationCity(station.getCity());
                        dto.setStationRegion(station.getRegion());
                        dto.setStationName(station.getName());
                    }
                }
                Date reserveTime = dto.getReserveTime();
                if (reserveTime != null) {
                    Date date = DateUtil.getBeforeEndDay();
                    if (date.after(reserveTime)) {
                        dto.setReserveStatus("已过期");
                    } else {
                        dto.setReserveStatus("已预约");
                    }
                }
            }
        }
        return new PageVO<>(pageNum, ticketPage);
    }


    @Override
    public PageVO<HraPhysicalDTO> physical(HraQueryDTO query, Integer pageNum, Integer pageSize) {
        List<Integer> stationIds = systemFeign.findStationIdsByPCR(query.getProvince(), query.getCity(), query.getRegion(), query.getStationName());
        query.setStationIds(stationIds);
        if (Objects.nonNull(query.getUserType())) {
            List<Integer> ids = userFeign.getUserByUserType(query.getUserType());
            query.setIds(ids);
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<HraPhysicalDTO> ticketPage = hraTicketMapper.physical(query);
        Integer validDays = 0;
        List<HraPhysicalDTO> physicalDTOS = ticketPage.getResult();
        if (CollectionUtil.isNotEmpty(physicalDTOS)) {
            for (HraPhysicalDTO hraPhysicalDTO : physicalDTOS) {
                if (null != hraPhysicalDTO.getCurrentUserId()) {
                    UserDTO userDTO = userFeign.getUserById(hraPhysicalDTO.getCurrentUserId());
                    if(Objects.nonNull(userDTO)){
                        hraPhysicalDTO.setUserType(userDTO.getUserType());
                    }
                }

                //有效期天数
                if (hraPhysicalDTO.getCreateTime() != null && hraPhysicalDTO.getValidEndTime() != null) {
                    validDays = DateUtil.betweenDays(hraPhysicalDTO.getCreateTime(), hraPhysicalDTO.getValidEndTime());
                    hraPhysicalDTO.setValidDays(validDays);
                }

                //服务站名称
                if (Objects.nonNull(hraPhysicalDTO.getStationId())) {
                    StationDTO station = systemFeign.getStationById(hraPhysicalDTO.getStationId());
                    if (Objects.nonNull(station)) {
                        hraPhysicalDTO.setStationName(station.getName());
                    }
                }

                //查询并设置HraCard信息
                String cardId = hraPhysicalDTO.getCardId();
                if (cardId != null) {
                    HraCard hraCard = hraCardMapper.selectByPrimaryKey(cardId);
                    if (hraCard != null) {
                        hraPhysicalDTO.setCreateUserId(hraCard.getUserId());
                        hraPhysicalDTO.setOrderFrom(hraCard.getOrderFrom());
                        hraPhysicalDTO.setOrderId(hraCard.getOrderId());
                    }
                }
                //查询并设置体检人身份证号码
                Integer customerId = hraPhysicalDTO.getCustomerId();
                if (customerId != null) {
                    HraCustomer hraCustomer = hraCustomerMapper.selectByPrimaryKey(customerId);
                    if (hraCustomer != null) {
                        hraPhysicalDTO.setIdCard(hraCustomer.getIdcard());
                    }
                }
            }
        }
        return new PageVO<>(pageNum, ticketPage);
    }

    /**
     * @Description: 只能分配M卡
     * @author ycl
     * @param: userId
     * @Return: void
     * @Create: 2019/2/26 15:10
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public String assignCard(UserDTO userDTO, Integer productId, Integer giveType) {
        log.info("=================================================================================================================");
        log.info("传入参数:userId=" + userDTO.getId() + ",productId=" + productId);
        log.info("=================================================================================================================");
        try {
            ProductDTO productDTO = productFeign.findMCardProductList();
            if (productDTO == null) {
                log.error("体检卡分配失败,产品已下架");
                throw new YimaoException("体检卡分配失败");
            }
            //查询类目
            ProductCategoryDTO productCategoryDTO = productFeign.getProductCategory(productDTO.getCategoryId());
            if (productCategoryDTO == null || !Objects.equals(HraType.M.value, productCategoryDTO.getName())) {
                log.error("获取产品类目信息失败");
                throw new YimaoException("体检卡分配失败");
            }
            //获取体检卡配置
            VirtualProductConfigDTO virtualProductConfig = productDTO.getVirtualProductConfig();
            if (virtualProductConfig == null) {
                log.error("获取体检卡配置信息失败");
                throw new YimaoException("获取体检卡配置信息失败");
            }
            //有效期
            Map<String, Date> validTime = this.validTime(virtualProductConfig);
            Date data = new Date();
            StringBuffer buffer = new StringBuffer();
            Integer userId = userDTO.getId();
            List<DiscountCardRecord> recordList = discountCardRecordService.listNotReceivedRecords(userId, giveType);
            if (CollectionUtil.isNotEmpty(recordList)) {
                for (DiscountCardRecord record : recordList) {
                    List<HraCard> cardList = new ArrayList<>();
                    List<HraTicket> ticketList = new ArrayList<>();
                    for (int i = 0; i < record.getGiveCount(); i++) {
                        HraCard card = new HraCard();
                        card.setId(UUIDUtil.buildHraCardNo(productCategoryDTO.getName()));
                        card.setOrderId(null);
                        card.setUserId(userId);
                        card.setOrderFrom(OrderFrom.SYSTEM.value);
                        card.setCardType(productCategoryDTO.getName());
                        card.setCardPrice(productDTO.getPrice());
                        card.setProductId(productDTO.getId());
                        card.setDeleteStatus(false);
                        card.setValidTime(validTime.get("endTime"));
                        card.setRemark(productCategoryDTO.getName());
                        card.setCreateTime(data);
                        card.setUpdateTime(data);
                        cardList.add(card);

                        // 如果一张体检卡对应多张体检劵
                        HraTicket hraTicket = new HraTicket();
                        hraTicket.setTicketNo(UUIDUtil.buildHraTicketNo(productCategoryDTO.getName()));
                        hraTicket.setUserId(userId);
                        hraTicket.setReserveFrom(5);//后台管理系统
                        hraTicket.setTicketType(card.getCardType());
                        hraTicket.setTicketStatus(HraTicketStatusEnum.HRA_STOP.value);
                        hraTicket.setTicketPrice(productDTO.getPrice());
                        // 有效期 生效类型：1-无限期；2-指定时间内可用；3-自定义时间段
                        hraTicket.setValidBeginTime(validTime.get("beginTime"));
                        hraTicket.setValidEndTime(validTime.get("endTime"));
                        //使用次数
                        Integer useCount = virtualProductConfig.getUsageCount();
                        hraTicket.setTotal(useCount != null ? useCount : 1);
                        hraTicket.setUseCount(useCount != null ? useCount : 1);
                        //优惠卡样式
                        hraTicket.setTicketStyle(record.getTicketStyle());
                        hraTicket.setTicketContent(record.getWatermark());
                        hraTicket.setImage(record.getImage());
                        hraTicket.setImageUsed(record.getImageUsed());
                        hraTicket.setCardId(card.getId());
                        hraTicket.setDeleteStatus(false);
                        hraTicket.setCreateTime(data);
                        hraTicket.setUpdateTime(data);
                        ticketList.add(hraTicket);

                        //针对，兑换码兑换体检劵的情况
                        if (giveType != null && Objects.equals(HraGiveTypeEnum.HRA_EXCHANGE.value, giveType)) {
                            buffer.append(hraTicket.getTicketNo());
                        }
                    }
                    hraCardMapper.insertBatch(cardList);
                    hraTicketMapper.insertBatch(ticketList);

                    //发放状态设置
                    record.setReceived(true);
                    record.setReceivedTime(data);
                    discountCardRecordMapper.updateByPrimaryKeySelective(record);
                }
            }
            return buffer.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("分配M卡出错！");
        }
    }

    @Override
    public List<HraTicket> findByCardId(String cardId) {
        Example example = new Example(HraTicket.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cardId", cardId);
        return hraTicketMapper.selectByExample(example);
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public List<HraCardDTO> allot(HraFCardDTO hraFCardDTO) {
        Integer cardCount = hraFCardDTO.getCardCount();
        if (Objects.isNull(cardCount) || cardCount < 1) {
            log.error("体检卡分配失败,生成数量必须大于1");
            throw new YimaoException("体检卡分配失败,生成数量必须大于1");
        }
        ProductDTO product = productFeign.getProductById(hraFCardDTO.getProductId());
        if (product == null) {
            log.error("体检卡分配失败,产品不能为空");
            throw new YimaoException("体检卡分配失败,产品不能为空");
        }
        VirtualProductConfigDTO virtualProductConfig = product.getVirtualProductConfig();
        if (virtualProductConfig == null) {
            log.error("体检卡分配失败,配置信息不能为空");
            throw new YimaoException("体检卡分配失败,配置信息不能为空");
        }

        //有效期设置
        Map<String, Date> validTime = this.validTime(virtualProductConfig);

        log.info("productDTO.getCategoryId()====" + product.getCategoryId());
        ProductCategoryDTO productCategory = productFeign.getProductCategory(product.getCategoryId());
        if (Objects.isNull(productCategory) || !Objects.equals(HraType.F.value, productCategory.getName())) {
            log.error("体检卡分配失败,只能分配F卡");
            throw new YimaoException("未查询到商品分类");
        }

        HraCard card;
        HraCardDTO cardDTO;
        List<HraTicketDTO> ticketList;
        Date nowDate = new Date();
        List<HraCardDTO> hraCardDTOList = new ArrayList<>();
        for (int i = 0; i < cardCount; i++) {
            card = new HraCard();
            //卡号
            card.setId(UUIDUtil.buildHraCardNo(productCategory.getName().trim()));
            card.setOrderId(null);
            card.setUserId(null);
            card.setOrderFrom(OrderFrom.SYSTEM.value);
            card.setCreateTime(nowDate);
            card.setUpdateTime(nowDate);
            card.setCardPrice(new BigDecimal(0));
            card.setProductId(hraFCardDTO.getProductId());
            //生成评估券的数量
            card.setTicketNum(virtualProductConfig.getCount() != null ? virtualProductConfig.getCount() : 1);
            card.setCardType(productCategory.getName().trim());
            card.setRemark(productCategory.getName());
            //有效期
            card.setValidTime(validTime.get("endTime"));
            card.setDeleteStatus(false);
            hraCardMapper.insert(card);
            ticketList = this.createHraTicket(card, productCategory, product, hraFCardDTO, virtualProductConfig, validTime);
            cardDTO = new HraCardDTO();
            cardDTO.setTicketList(ticketList);
            card.convert(cardDTO);
            hraCardDTOList.add(cardDTO);
        }
        return hraCardDTOList;
    }

    private List<HraTicketDTO> createHraTicket(HraCard card, ProductCategoryDTO productCategory, ProductDTO product, HraFCardDTO hraFCardDTO, VirtualProductConfigDTO virtualProductConfig, Map<String, Date> validTime) {
        List<HraTicket> ticketList = new ArrayList<>();
        List<HraTicketDTO> ticketDTOS = new ArrayList<>();
        Integer num = card.getTicketNum();

        log.info("card.getTicketNum()==" + card.getTicketNum() + ",card.getId()==" + card.getId());
        HraTicketDTO ticketDTO;
        for (int i = 0; i < num; i++) {
            HraTicket hraTicket = new HraTicket();
            //评估券号
            hraTicket.setTicketNo(UUIDUtil.buildHraTicketNo(productCategory.getName().trim()));
            hraTicket.setTicketPrice(card.getCardPrice().divide(new BigDecimal(num), 2, BigDecimal.ROUND_HALF_UP));
            //1-未使用，2-已使用，3-已禁用，4-已过期
            if (hraFCardDTO.getDisabled() == 1) {
                hraTicket.setTicketStatus(HraTicketStatusEnum.HRA_STOP.value);
            } else {
                hraTicket.setTicketStatus(HraTicketStatusEnum.HRA_PAY.value);
            }

            hraTicket.setSelfStation(false);
            if (hraFCardDTO.getDisabled() != null && hraFCardDTO.getDisabled() == 1) {
                hraTicket.setSelfStation(true);
            }

            hraTicket.setTicketType(productCategory.getName().trim());
            hraTicket.setCardId(card.getId());
            hraTicket.setReserveFrom(5);//后台管理系统
            hraTicket.setCreateTime(new Date());
            //后台分配的操作者
            hraTicket.setCreator(card.getCreator());
            //生成评估卡时，如果存在服务站为后台生成
            if (hraFCardDTO.getStationStoreId() != null) {
                StationDTO station = systemFeign.getStationById(hraFCardDTO.getStationStoreId());
                if (station != null) {
                    hraTicket.setStationId(station.getId());
                    hraTicket.setStationName(station.getName());
                    hraTicket.setStationProvince(station.getProvince());
                    hraTicket.setStationCity(station.getCity());
                    hraTicket.setStationRegion(station.getRegion());
                }
                hraTicket.setReserveFrom(HraTicketStatusEnum.HRA_RESERVE.value);
            }
            //可用次数
            Integer userCount = virtualProductConfig.getUsageCount();
            hraTicket.setTotal(userCount != null ? userCount : 1);
            hraTicket.setUseCount(userCount != null ? userCount : 1);
            //只能在本服务中使用
            if (hraFCardDTO.getSelfStation() != null && hraFCardDTO.getSelfStation() == 1) {
                hraTicket.setSelfStation(true);
            } else {
                hraTicket.setSelfStation(false);
            }

            hraTicket.setValidBeginTime(validTime.get("beginTime"));
            hraTicket.setValidEndTime(validTime.get("endTime"));
            hraTicket.setDeleteStatus(false);
            ticketList.add(hraTicket);
            ticketDTO = new HraTicketDTO();
            hraTicket.convert(ticketDTO);
            ticketDTOS.add(ticketDTO);
        }

        //批量插入
        hraTicketMapper.insertBatch(ticketList);
        return ticketDTOS;
    }

    /**
     * F卡列表
     */
    @Override
    public PageVO<HraCardVO> allotTicket(HraQueryDTO query, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<HraCardVO> allotTicketDTOS = hraTicketMapper.listTicketBy(query);
        return new PageVO<>(pageNum, allotTicketDTOS);
    }

    @Override
    public void updateTicket(HraTicketDTO hraTicketDTO) {
        Date updateVaildTime = hraTicketDTO.getValidEndTime();//修改后的时间
        HraTicket ticket = hraTicketMapper.selectByPrimaryKey(hraTicketDTO.getId());
        if (Objects.nonNull(ticket)) {
            if (Objects.equals(ticket.getTicketStatus(), HraTicketStatusEnum.HRA_USE.value)) {
                throw new BadRequestException("修改失败,不能修改已使用的卡");
            }
            String ticketType = ticket.getTicketType();//型号
            if (StringUtil.isEmpty(ticketType) && updateVaildTime == null) {
                throw new BadRequestException("修改失败,有效期不能为空");
            }
            //如果修改是M卡
            if (Objects.equals(HraType.M.value, ticketType)) {
                HraCard card = hraCardMapper.selectByPrimaryKey(ticket.getCardId());
                if (Objects.nonNull(card) && Objects.nonNull(card.getOrderId())) {
                    //此卡已支付
                    OrderSubDTO orderSubDTO = orderFeign.findBasicOrderInfoById(card.getOrderId());
                    if (Objects.nonNull(orderSubDTO) && orderSubDTO.getPay()) {
                        //大于当前时间-延长有效期    小于当前时间-过期
                        if (updateVaildTime.getTime() >= (new Date()).getTime()) {
                            if (ticket.getTicketStatus() == HraTicketStatusEnum.HRA_EXPIRE.value) {
                                ticket.setTicketStatus(HraTicketStatusEnum.HRA_PAY.value);
                            }
                        } else {
                            if (ticket.getTicketStatus() == HraTicketStatusEnum.HRA_PAY.value) {
                                ticket.setTicketStatus(HraTicketStatusEnum.HRA_EXPIRE.value);
                            }
                        }
                    }
                } else {
                    //此卡未支付
                    if (updateVaildTime.getTime() >= (new Date()).getTime()) {
                        if (ticket.getTicketStatus() == HraTicketStatusEnum.HRA_EXPIRE.value) {
                            ticket.setTicketStatus(HraTicketStatusEnum.HRA_STOP.value);
                        }
                    } else {
                        if (ticket.getTicketStatus() == HraTicketStatusEnum.HRA_STOP.value) {
                            ticket.setTicketStatus(HraTicketStatusEnum.HRA_EXPIRE.value);
                        }
                    }
                }
            } else if (Objects.equals(HraType.Y.value, ticketType) || Objects.equals(HraType.F.value, ticketType)) {
                //大于当前时间-延长有效期    小于当前时间-过期
                if (updateVaildTime.getTime() >= (new Date()).getTime()) {
                    if (ticket.getTicketStatus() == HraTicketStatusEnum.HRA_EXPIRE.value) {
                        ticket.setTicketStatus(HraTicketStatusEnum.HRA_PAY.value);
                    }
                } else {
                    if (ticket.getTicketStatus() == HraTicketStatusEnum.HRA_PAY.value || ticket.getTicketStatus() == HraTicketStatusEnum.HRA_STOP.value) {
                        ticket.setTicketStatus(HraTicketStatusEnum.HRA_EXPIRE.value);
                    }
                }
            }
        }
        ticket.setUpdateTime(new Date());
        ticket.setValidEndTime(hraTicketDTO.getValidEndTime());
        int count = hraTicketMapper.updateByPrimaryKey(ticket);
        if (count < 1) {
            throw new YimaoException("体检卡编辑失败");
        }
    }

    @Override
    public void updateStatus(HraAllotTicketDTO hraAllotTicketDTO) {
        HraTicket ticket = hraTicketMapper.selectByPrimaryKey(hraAllotTicketDTO.getId());
        if (Objects.isNull(ticket)) {
            throw new YimaoException("此卡不存在");
        }
        if (ticket.getTicketStatus() == HraTicketStatusEnum.HRA_USE.value) {
            throw new YimaoException("已使用的卡不能修改。");
        }
        if (ticket.getTicketStatus() == HraTicketStatusEnum.HRA_EXPIRE.value) {
            throw new YimaoException("已过期的卡不能修改。");
        }

        HraTicket update = new HraTicket();
        update.setId(ticket.getId());

        if (ticket.getTicketStatus() == HraTicketStatusEnum.HRA_PAY.value) {
            update.setTicketStatus(HraTicketStatusEnum.HRA_STOP.value);
        } else if (ticket.getTicketStatus() == HraTicketStatusEnum.HRA_STOP.value) {
            update.setTicketStatus(HraTicketStatusEnum.HRA_PAY.value);
        } else {
            update.setTicketStatus(HraTicketStatusEnum.HRA_STOP.value);
        }

        int i = hraTicketMapper.updateByPrimaryKeySelective(update);
        if (i < 1) {
            throw new YimaoException("修改失败");
        }
    }


    /**
     * 发放卡前，生成优惠卡发放记录
     *
     * @param user      用户
     * @param count     数量
     * @param remark    备注
     * @param giveType  1-经销商固定配额，2-经销商业绩发放，3-用户业绩，5-手动发放卡，7-分销用户业绩卡 8-兑换码兑换
     * @param styleType 样式类型
     */
    @Override
    public void generateHraRecord(UserDTO user, Integer count, String remark, Integer giveType, Integer styleType) {
        if (StringUtil.isEmpty(remark)) {
            remark = "手动申请优惠卡";
        }
        DiscountCardRecord record = new DiscountCardRecord();
        record.setUserId(user.getId());
        //该用户是否是经销商
        if (UserType.isDistributor(user.getUserType())) {
            record.setGiveName(user.getUserName());
        }

        //用户发放的卡，和经销商的样式一样
        DiscountCardSetting setting;
        if (styleType == null) {
            setting = discountCardSettingService.getGiveCount(user.getUserType(), user.getCompanyName());
        } else {
            setting = discountCardSettingService.getGiveCount(styleType, user.getCompanyName());
        }
        record.setTicketStyle(setting.getTicketStyle());
        record.setWatermark(setting.getWatermark());
        record.setImage(setting.getImage());
        record.setImageUsed(setting.getImageUsed());
        //发放类型
        record.setGiveType(giveType);
        record.setGiveCount(count);
        record.setTotalCount(count);
        record.setRemark(remark);
        discountCardRecordService.saveRecord(record);
    }


    @Override
    public List<HraTicket> getHraTicketListByTicketNoList(List<String> list, Long dateTime) throws YimaoException {
        try {
            Example example = new Example(HraTicket.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("ticketNo", list);
            criteria.andEqualTo("handselFlag", dateTime);
            return hraTicketMapper.selectByExample(example);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("HraTicketApiImpl.getHraTicketListByTicketNoList error : ", e);
            throw new YimaoException(e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public boolean changeTicketOwner(Integer sharerId, Integer userId, String cardIdOrTicketNo, int type, Long handselFlag) {
        try {
            //同步处理
            lock.lock();
            Example example = new Example(HraTicket.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("ticketStatus", HraTicketStatusEnum.HRA_PAY.value);
            criteria.andEqualTo("handselStatus", HraHandselStatusEnum.HRA_SEND.value);
            //必须是未使用的卡才能赠送
            //必须是赠送中状态
            if (type == 1) {
                criteria.andEqualTo("cardId", cardIdOrTicketNo);
            } else {
                criteria.andEqualTo("ticketNo", cardIdOrTicketNo);
            }
            Example.Criteria criteria2 = example.createCriteria();
            criteria2.andEqualTo("ticketStatus", HraTicketStatusEnum.HRA_STOP.value);
            criteria2.andEqualTo("handselStatus", HraHandselStatusEnum.HRA_SEND.value);
            //必须是未使用的卡才能赠送
            //必须是赠送中状态
            if (type == 1) {
                criteria2.andEqualTo("cardId", cardIdOrTicketNo);
            } else {
                criteria2.andEqualTo("ticketNo", cardIdOrTicketNo);
            }
            example.or(criteria2);
            List<HraTicket> list = hraTicketMapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(list)) {
                for (HraTicket ht : list) {
                    if (ht.getHandselStatus() != null && Objects.equals(ht.getHandselStatus(), HraHandselStatusEnum.HRA_SEND.value)) {
                        hraCardMapper.updateCardUpdateTime(type, cardIdOrTicketNo);
                        Date receiveTime = new Date();
                        //修改体检卡归属关系
                        int count = hraTicketMapper.changeTicketOwner(sharerId, userId, type, cardIdOrTicketNo, receiveTime);
                        if (count > 0) {
                            //保存领取记录
                            boolean flag = saveReceiveRecord(userId, receiveTime, handselFlag);
                            if (!flag) {
                                throw new YimaoException("生成评估卡卷的接收生命周期失败,事务回滚");
                            }
                            return true;
                        }
                        return false;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("HraTicketApi.changeTicketOwner error : ", e);
            throw new YimaoException(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public String saveTicketNoRedis(Integer userId, Integer sendCount, Integer ticketStatus, String ticketType) {
        try {
            log.info("【传入参数】,ticketStatus={}", ticketStatus);
            PageHelper.startPage(1, sendCount);
            List<HraTicketDTO> hraTicketList = hraTicketMapper.getCanBeSendCardList(userId, ticketStatus, ticketType);
            List<String> ticketNoList = new ArrayList<>();
            for (HraTicketDTO h : hraTicketList) {
                ticketNoList.add(h.getTicketNo());
            }

            String key = UUIDUtil.longuuid32();
            boolean hasKey = redisCache.hasKey(Constant.TICKETNO + key);
            if (hasKey) {
                key = UUIDUtil.longuuid32();
            }
            redisCache.setCacheList(Constant.TICKETNO + key, ticketNoList, String.class, Constant.SEND_TICKET_EXPIRED_TIME);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("HraTicketApiImpl.saveTicketNo2Redis error : ", e);
            throw new YimaoException(e.getMessage(), e);
        }
    }

    /**
     * 修改优惠卡赠送状态
     *
     * @param userId      用户id
     * @param shareNOList 优惠卡集合
     * @param dateTime    时间戳
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void updateTicketStatusAndInsertSendRecord(Integer userId, String shareNOList, Long dateTime) {
        List<String> ticketNoList = redisCache.getCacheList(Constant.TICKETNO + shareNOList, String.class);
        Example example = new Example(HraTicket.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("ticketNo", ticketNoList);
        List<HraTicket> hraTicketList = hraTicketMapper.selectByExample(example);
        Date handselTime = new Date();
        for (HraTicket h : hraTicketList) {
            HraTicketLifeCycle ticketLifeCycle = new HraTicketLifeCycle();
            ticketLifeCycle.setCardId(h.getCardId());
            ticketLifeCycle.setTicketNo(h.getTicketNo());
            ticketLifeCycle.setOrigUserId(userId);
            ticketLifeCycle.setHandselTime(handselTime);
            ticketLifeCycle.setHandselFlag(dateTime);
            ticketLifeCycle.setExpiredFlag(0);
            hraTicketLifeCycleMapper.insert(ticketLifeCycle);
        }
        hraTicketMapper.updateBatchHraTicketStatus(handselTime, HraHandselStatusEnum.HRA_SEND.value, ticketNoList, dateTime);
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public boolean changeTicketHandselStatus(Integer userId, String cardIdOrTicketNo, int type, Long dateTime) {
        Date nowDate = new Date();
        int count = hraTicketMapper.changeTicketHandselStatus(userId, cardIdOrTicketNo, type, nowDate, dateTime);
        if (count > 0) {
            System.out.println("=========================================================");
            System.out.println(userId + "--" + cardIdOrTicketNo + "--" + type + "--" + nowDate + "--" + dateTime);
            System.out.println("=========================================================");
            boolean flag = saveHandselRecord(userId, cardIdOrTicketNo, type, nowDate, dateTime);
            if (!flag) {
                throw new YimaoException("生成评估卡劵的赠送生命周期失败,事务回滚");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<HraTicketDTO> listTicketForPay(Integer userId, Integer count) {
        return hraTicketMapper.listTicketForPay(userId, count);
    }

    /**
     * 分配Y卡
     *
     * @param orderId
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void createHraCardAndTicket(Long orderId) {

        OrderSubDTO order = orderFeign.findBasicOrderInfoById(orderId);
        if (order == null || order.getPay() == null || !order.getPay()) {
            log.error("体检卡分配失败，订单尚未支付。");
            return;
        }

        ProductDTO product = productFeign.getProductById(order.getProductId());
        if (product == null) {
            log.error("体检卡分配失败，未获取到产品信息。");
            throw new YimaoException("体检卡分配失败，未获取到产品信息。");
        }
        VirtualProductConfigDTO virtualProductConfig = product.getVirtualProductConfig();
        if (virtualProductConfig == null) {
            log.error("体检卡分配失败，未获取到虚拟产品配置信息。");
            throw new YimaoException("体检卡分配失败，未获取到虚拟产品配置信息。");
        }

        //有效期设置
        Map<String, Date> validTime = this.validTime(virtualProductConfig);

        HraCard card;
        Date nowDate = new Date();
        Integer userId = order.getUserId();
        for (int i = 0; i < order.getCount(); i++) {
            card = new HraCard();
            //卡号
            card.setId(UUIDUtil.buildHraCardNo(HraType.Y.value));
            card.setCardType(HraType.Y.value);
            card.setUserId(userId);
            card.setOrderId(orderId);
            card.setMainOrderId(order.getMainOrderId());
            card.setProductId(order.getProductId());
            card.setOrderFrom(order.getTerminal());
            card.setOrderFromName(Terminal.find(order.getTerminal()) != null ? Terminal.find(order.getTerminal()).name : null);
            //有效期
            card.setValidTime(validTime.get("endTime"));
            card.setCardPrice(product.getPrice());
            //生成评估券的数量
            card.setTicketNum(virtualProductConfig.getCount() != null ? virtualProductConfig.getCount() : 1);
            card.setCreateTime(nowDate);
            card.setDeleteStatus(false);
            hraCardMapper.insert(card);
            for (int j = 0; j < card.getTicketNum(); j++) {
                HraTicket hraTicket = new HraTicket();
                //评估券号
                hraTicket.setTicketNo(UUIDUtil.buildHraTicketNo(HraType.Y.value));
                hraTicket.setTicketPrice(card.getCardPrice().divide(new BigDecimal(card.getTicketNum()), 2, BigDecimal.ROUND_HALF_UP));
                //1-未使用，2-已使用，3-已禁用，4-已过期
                hraTicket.setTicketStatus(HraTicketStatusEnum.HRA_PAY.value);
                hraTicket.setTicketType(HraType.Y.value);
                hraTicket.setCreateTime(nowDate);
                hraTicket.setValidBeginTime(validTime.get("beginTime"));
                hraTicket.setValidEndTime(validTime.get("endTime"));
                hraTicket.setCardId(card.getId());
                hraTicket.setUserId(userId);
                hraTicket.setDeleteStatus(false);
                hraTicket.setSelfStation(false);
                //价格 = 总价格 / 生成张数
                hraTicket.setCreateTime(new Date());
                //可用次数
                Integer usageCount = virtualProductConfig.getUsageCount();
                //总可用次数
                hraTicket.setTotal(usageCount != null ? usageCount : 1);
                //剩余可用次数
                hraTicket.setUseCount(usageCount != null ? usageCount : 1);
                hraTicketMapper.insert(hraTicket);
            }
        }

    }

    /*  */

    // /**
    //  * 预约列表导出
    //  *
    //  * @param beginTime
    //  * @param endTime
    //  * @param userSource
    //  * @param mobile
    //  * @param ticketNo
    //  * @param province
    //  * @param city
    //  * @param region
    //  * @param name
    //  * @param reserveStatus
    //  * @param userId
    //  * @return
    //  */
    // @Override
    // public List<HraExportReservationDTO> exportReservation(String beginTime, String endTime, Integer userSource, String mobile, String ticketNo, String province, String city, String region, String name, Integer reserveStatus, Integer userId) {
    //     //获取服务站ids
    //     List<Integer> stationIds = this.getStationByArea(province, city, region);
    //     List<HraExportReservationDTO> result = hraTicketMapper.exportReservation(beginTime, endTime, userSource, mobile, ticketNo, province, city, region, name, 2, reserveStatus, null, userId, null, stationIds);
    //     if (CollectionUtil.isNotEmpty(result)) {
    //         for (HraExportReservationDTO dto : result) {
    //             setHraTicketStationMessage(dto);
    //             String reserveTime = dto.getReserveTime();
    //             if (reserveTime != null) {
    //                 Date toDate = DateUtil.transferStringToDate(reserveTime);
    //                 Date date = DateUtil.getBeforeEndDay();
    //                 if (date.after(toDate)) {
    //                     dto.setReserveStatus("已过期");
    //                 } else {
    //                     dto.setReserveStatus("已预约");
    //                 }
    //             }
    //         }
    //     } else {
    //         throw new NotFoundException("没有数据");
    //     }
    //     return result;
    // }

    private List<Integer> getStationByArea(String province, String city, String region) {
        if (StringUtil.isNotEmpty(province) || StringUtil.isNotEmpty(city) || StringUtil.isNotEmpty(region)) {
            //服务站ids
            return systemFeign.findStationIdsByPCR(province, city, region, null);
        }
        return null;
    }

    /**
     * @param beginTime
     * @param endTime
     * @param userSource
     * @param mobile
     * @param ticketNo
     * @param province
     * @param city
     * @param region
     * @param name
     * @param reserveStatus
     * @param userId
     * @param ticketType
     * @return
     */
    /*@Override
    public List<HraExportReservationDTO> getTicketInfoToExport(String beginTime, String endTime, Integer userSource, String mobile, String ticketNo, String province, String city, String region, String name, Integer reserveStatus, Integer userId, String ticketType) {
        //服务站ids
        List<Integer> stationIds = this.getStationByArea(province, city, region);
        List<HraExportReservationDTO> result = hraTicketMapper.listTicketExport(beginTime, endTime, userSource, mobile, ticketNo, province, city, region, name, 1, reserveStatus, null, userId, ticketType, stationIds);
        if (CollectionUtil.isNotEmpty(result)) {
            for (HraExportReservationDTO dto : result) {
                this.setHraTicketStationMessage(dto);
            }
        } else {
            throw new NotFoundException("没有数据");
        }
        return result;

    }*/
    @Override
    public Boolean changeTicketOwnerBatch(Integer sharerId, Integer userId, List<String> handselList, Date date, Long handselFlag) {
        try {
            hraTicketMapper.changeTicketOwnerBatch(sharerId, userId, handselList, date);
            hraTicketLifeCycleMapper.updateHraTicketLifeCycleToBeReceive(sharerId, userId, handselList, date, handselFlag, 1);    //将体检卡的状态更新为被领取
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("HraTicketServiceImpl.changeTicketOwner error : ", e);
            throw new YimaoException(e.getMessage(), e);
        }
    }


    @Override
    public List<HraTicket> listHandselExpiredHraTicket() {
        return hraTicketMapper.listHandselExpiredHraTicket();
    }

    @Override
    public void updateHraTicketExpired(Integer id) {
        hraTicketMapper.updateHraTicketExpired(id);
    }

    @Override
    public void updateHraTicketHandselStatusToNull(Integer id) {
        hraTicketMapper.updateHraTicketHandselStatusToNull(id);
    }

    @Override
    public List<HraTicket> listExpiredHraTicket() {
        return hraTicketMapper.listExpiredHraTicket();
    }

    /**
     * 评估卡导出
     *
     * @param province
     * @param city
     * @param region
     * @param stationName
     * @param currentUserId
     * @param ticketNo
     * @param userType
     * @param minSurplus
     * @param maxSurplus
     * @param beginTime
     * @param endTime
     * @param cardType
     * @return
     */
   /* @Override
    public List<HraExportPhysicalDTO> exportPhysical(String province, String city, String region, String stationName, Integer currentUserId, String ticketNo, Integer userType, Integer minSurplus, Integer maxSurplus, Integer ticketStatus, Date beginTime, Date endTime, String cardType) {
        List<Integer> stationIds = systemFeign.findStationIdsByPCR(province, city, region, stationName);
        List<Integer> ids = null;
        if (Objects.nonNull(userType)) {
            ids = userFeign.getUserByUserType(userType);
        }
        List<HraExportPhysicalDTO> exportPhysicalList = hraTicketMapper.exportPhysical(ids, stationIds, currentUserId, ticketNo, userType, minSurplus, maxSurplus, ticketStatus, beginTime, endTime, cardType);
        Iterator<HraExportPhysicalDTO> iterator = exportPhysicalList.iterator();
        while (iterator.hasNext()) {

            HraExportPhysicalDTO next = iterator.next();
            if (next.getOrderId() != null) {
                OrderSubDTO basicOrderInfoById = orderFeign.findBasicOrderInfoById(next.getOrderId());//payStatus   payTime//支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败
                switch (basicOrderInfoById.getPayStatus()) {
                    case 1:
                        next.setPay("未支付");
                        break;
                    case 2:
                        next.setPay("待审核");
                        break;
                    case 3:
                        next.setPay("支付成功");
                        break;
                    case 4:
                        next.setPay("支付失败");
                        break;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String payTime = sdf.format(basicOrderInfoById.getPayTime());
                next.setPayTime(payTime);
            }
        }
        return exportPhysicalList;
    }*/

    /**
     * F卡导出
     *
     * @param province
     * @param city
     * @param region
     * @param stationName
     * @param isExpire
     * @param minSurplus
     * @param maxSurplus
     * @param state
     * @param beginTime
     * @param endTime
     * @param ticketNo
     * @return
     */
    /*@Override
    public List<HraAllotTicketExportDTO> exportSpecialTicket(String province, String city, String region, String
            stationName, String isExpire, Integer minSurplus, Integer maxSurplus, Integer state, String beginTime, String
                                                                     endTime, String ticketNo) {
        List<HraAllotTicketExportDTO> resultList = hraTicketMapper.hraAllotTicketExport(province, city, region, stationName, isExpire, minSurplus, maxSurplus, state, beginTime, endTime, ticketNo, new Date());
        return resultList;
    }*/

    @Override
    public PageVO<StationHraCardVO> stationAllotTicket(Integer pageNum, Integer pageSize, HraSpecialQuery query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<StationHraCardVO> vos = hraTicketMapper.listTicketByQuery(query);
        return new PageVO<>(pageNum, vos);
    }

    /**
     * 订单支付后将优惠卡变为已支付状态
     *
     * @param orderId 订单号
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void doRefer(Long orderId, String ticketNo, Integer terminal) {
        try {
            //如果M卡订单没有支付，设置为隐藏。支付后设置为可见
            if (StringUtil.isNotEmpty(ticketNo)) {
                HraTicket ticket = hraTicketMapper.selectIdAndCardIdByTicketNo(ticketNo);
                if (ticket != null) {
                    HraTicket updateTicket = new HraTicket();
                    updateTicket.setId(ticket.getId());
                    //50元优惠卡，用户付款后将评估卡状态从[已禁用]变为[未使用]
                    updateTicket.setTicketStatus(HraTicketStatusEnum.HRA_PAY.value);
                    hraTicketMapper.updateByPrimaryKeySelective(updateTicket);
                    HraCard updateCard = new HraCard();
                    updateCard.setId(ticket.getCardId());
                    //将订单号设置到评估卡上供后续服务收益分配使用
                    updateCard.setOrderId(orderId);
                    updateCard.setOrderFrom(terminal);
                    hraCardMapper.updateByPrimaryKeySelective(updateCard);
                } else {
                    log.error("===优惠卡支付后变更卡状态失败，没有查询到体检卡===orderId=" + orderId);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String subject = "优惠卡支付后变更卡状态出错===" + domainProperties.getApi();
            String content = "优惠卡支付后变更卡状态出错。orderId=" + orderId + "\n" + sw.toString();
            mailSender.send(null, subject, content);
            throw new YimaoException(e.getMessage(), e);
        }
    }


    /**
     * 有效期
     *
     * @param virtualProductConfig 配置对象
     * @return map
     */
    private Map<String, Date> validTime(VirtualProductConfigDTO virtualProductConfig) {
        Integer effectiveType = virtualProductConfig.getEffectiveType();
        Map<String, Date> validMap = new HashMap<>(8);
        if (VirtualEffectiveType.ASSIGN.value == effectiveType) {
            Integer usefulType = virtualProductConfig.getUsefulType();
            Integer usefulNum = virtualProductConfig.getUsefulNum();
            Date beginTime = DateUtil.getCurrentDayBeginTime();
            Date endTime = null;
            if (usefulType == BaseTimeUnit.YEAR.value) {
                endTime = DateUtil.yearAfter(DateUtil.getDayEndTime(new Date()), usefulNum);
            } else if (usefulType == BaseTimeUnit.MONTH.value) {
                endTime = DateUtil.monthAfter(DateUtil.getDayEndTime(new Date()), usefulNum);
            } else if (usefulType == BaseTimeUnit.DAY.value) {
                endTime = DateUtil.dayAfter(DateUtil.getDayEndTime(new Date()), usefulNum);
            }
            validMap.put("beginTime", beginTime);
            validMap.put("endTime", new Date(endTime.getTime() - 1000));
        } else if (VirtualEffectiveType.BETWEEN.value == effectiveType) {
            validMap.put("beginTime", virtualProductConfig.getEffectiveStartTime());
            validMap.put("endTime", virtualProductConfig.getEffectiveEndTime());
        } else {
            validMap.put("beginTime", null);
            validMap.put("endTime", null);
        }
        return validMap;
    }

    /**
     * 保存卡领取记录
     *
     * @param destId      领取人
     * @param receiveTime 领取时间
     * @param handselFlag 标志
     * @return bool
     */
    private boolean saveReceiveRecord(Integer destId, Date receiveTime, Long handselFlag) {
        Example example = new Example(HraTicketLifeCycle.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("handselFlag", handselFlag);
        List<HraTicketLifeCycle> ticketLifeCycles = hraTicketLifeCycleMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(ticketLifeCycles)) {
            return false;
        }
        HraTicketLifeCycle ticketLifeCycle = ticketLifeCycles.get(0);
        ticketLifeCycle.setDestUserId(destId);
        ticketLifeCycle.setReceiveTime(receiveTime);
        ticketLifeCycle.setExpiredFlag(1);
        int count = hraTicketLifeCycleMapper.updateByPrimaryKeySelective(ticketLifeCycle);
        if (count < 0) {
            return false;
        }
        return true;
    }


    /**
     * 获取体检卡生命周期中的用户
     *
     * @param origUserId
     * @return
     */
    private HraTicketLifeCycleDTO getHraTicketLifeCycleDTO(Integer origUserId) {
        UserDTO userDTO = userFeign.getUserById(origUserId);
        if (userDTO == null) {
            throw new YimaoException("获取不到用户信息！");
        }
        HraTicketLifeCycleDTO tmp = new HraTicketLifeCycleDTO();
        tmp.setId(userDTO.getId());
        tmp.setHeadImg(userDTO.getHeadImg());
        tmp.setNickName(userDTO.getNickName());
        return tmp;
    }

    private boolean saveHandselRecord(Integer userId, String shareNo, Integer shareType, Date nowDate, long dateTime) {
        int count = 0;
        List<HraTicket> hraTickets = null;
        HraTicketLifeCycle ticketLifeCycle = new HraTicketLifeCycle();
        ticketLifeCycle.setOrigUserId(userId);
        ticketLifeCycle.setHandselTime(nowDate);
        ticketLifeCycle.setHandselFlag(dateTime);
        ticketLifeCycle.setExpiredFlag(0);

        Example example = new Example(HraTicket.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        if (shareType == 1) {
            criteria.andEqualTo("cardId", shareNo);
            hraTickets = hraTicketMapper.selectByExample(example);
            if (CollectionUtil.isEmpty(hraTickets)) {
                return false;
            }
            StringBuffer sb = new StringBuffer();
            for (HraTicket hraTicket : hraTickets) {
                sb.append(hraTicket.getTicketNo()).append(",");
            }
            sb.setLength(sb.length() - 1);
            String ticketNo = sb.toString();
            ticketLifeCycle.setCardId(shareNo);
            ticketLifeCycle.setTicketNo(ticketNo);
            count = hraTicketLifeCycleMapper.insert(ticketLifeCycle);
        }

        if (shareType == 2 || shareType == 3) {
            criteria.andEqualTo("ticketNo", shareNo);
            hraTickets = hraTicketMapper.selectByExample(example);
            if (CollectionUtil.isEmpty(hraTickets)) {
                return false;
            }
            ticketLifeCycle.setCardId(hraTickets.get(0).getCardId());
            ticketLifeCycle.setTicketNo(shareNo);
            count = hraTicketLifeCycleMapper.insert(ticketLifeCycle);
        }
        return count > 0;
    }

    /**
     * 站务系统查询体检列表
     */
	@Override
	public PageVO<HraTicketResultDTO> getStationHraTicketUsedList(Integer pageNum, Integer pageSize,HraTicketQuery query) {
		
        PageHelper.startPage(pageNum, pageSize);
        Page<HraTicketResultDTO> ticketPage = hraTicketMapper.getStationHraTicketUsedList(query);
        if (CollectionUtil.isNotEmpty(ticketPage)) {
            for (HraTicketResultDTO dto : ticketPage) {
         	
                Date reserveTime = dto.getReserveTime();
                if (reserveTime != null) {
                    Date date = DateUtil.getBeforeEndDay();
                    if (date.after(reserveTime)) {
                        dto.setReserveStatus("已过期");
                    } else {
                        dto.setReserveStatus("已预约");
                    }
                }
            }
        }
        return new PageVO<>(pageNum, ticketPage);
	}

	@Override
	public PageVO<HraTicketResultDTO> getStationHraTicketReservationList(Integer pageNum, Integer pageSize,
			HraTicketQuery query) {
		
		   PageHelper.startPage(pageNum, pageSize);
	        Page<HraTicketResultDTO> ticketPage = hraTicketMapper.getStationHraTicketReservationList(query);
	        if (CollectionUtil.isNotEmpty(ticketPage)) {
	            for (HraTicketResultDTO dto : ticketPage) {
	            	
	            	
	                Date reserveTime = dto.getReserveTime();
	                if (reserveTime != null) {
	                    Date date = DateUtil.getBeforeEndDay();
	                    if (date.after(reserveTime)) {
	                        dto.setReserveStatus("已过期");
	                    } else {
	                        dto.setReserveStatus("已预约");
	                    }
	                }
	            }
	        }
	        return new PageVO<>(pageNum, ticketPage);
	}

    /* 站务系统-控制台-待办事项(昨日已评估数，今日待评估，总评估数)
    * @param areas
    * @param distributorIds 
    * @return
    */
	public StationScheduleDTO getStationHraNum(List<Integer> stations) {
		
		int totalAssessNum = hraTicketMapper.getTotalAssessNum(stations);
    	
    	int yesterdayAssessNum = hraTicketMapper.getYesterdayAssessNum(stations);
    	
    	int todayNeedAssessNum = hraTicketMapper.getTodayNeedAssessNum(stations);
    	
    	StationScheduleDTO res=new StationScheduleDTO();
    	res.setTotalFinishAssess(totalAssessNum);
    	res.setYesterdayFinishAssess(yesterdayAssessNum);
    	res.setTodayNeedAssess(todayNeedAssessNum);
    	
    	return res;
    	
	}

	/**
	 * 站务系统-统计评估统计表格数据（体检用户和公众号预约用户）
	 */
	public List<HraStatisticsDTO> getStationHraData(HraTicketQuery hraQuery) {
		//预约数据
		List<HraStatisticsDTO> ReservationRes = hraTicketMapper.getStationHraReserveData(hraQuery);
		//评估数据
		List<HraStatisticsDTO> usedRes = hraTicketMapper.getStationHraUsedData(hraQuery);
		
		Set<Integer> stations = hraQuery.getStations();
		
		List<HraStatisticsDTO> res=new ArrayList();
		
		for (Integer stationId : stations) {
			HraStatisticsDTO dto=new HraStatisticsDTO();
			dto.setStationId(stationId);
	
			if(CollectionUtil.isNotEmpty(ReservationRes)) {
				for (HraStatisticsDTO reservationDto : ReservationRes) {
					if(reservationDto.getStationId() !=null && stationId.equals(reservationDto.getStationId())) {
						dto.setReserveNum(reservationDto.getReserveNum());
					}
				}
			}else {
				dto.setReserveNum(0);
			}
			
			if(Objects.isNull(dto.getReserveNum())) {
				dto.setReserveNum(0);
			}
					
			if(CollectionUtil.isNotEmpty(usedRes)) {
				for (HraStatisticsDTO usedDto : usedRes) {
					if(usedDto.getStationId() != null && stationId .equals(usedDto.getStationId())) {
						dto.setUsedNum(usedDto.getUsedNum());
					}
				}
			}else {
				dto.setUsedNum(0);
			}
			
			if(Objects.isNull(dto.getUsedNum())) {
				dto.setUsedNum(0);
			}
			
			res.add(dto);
		}
		return res;
	}

	@Override
	public HraStatisticsDTO getStationHraPicData(HraTicketQuery hraQuery) {
		HraStatisticsDTO res=new HraStatisticsDTO();
		
		//预约图表数据
		List<HraStatisticsDTO> reservationPicRes = hraTicketMapper.getStationHraReservePicData(hraQuery);
		//评估图表数据
		List<HraStatisticsDTO> usedPicRes = hraTicketMapper.getStationHraUsedPicData(hraQuery);
		
		res.setReservationPicRes(reservationPicRes);
		res.setUsedPicRes(reservationPicRes);
		
		return res;
	}
	
	
}
