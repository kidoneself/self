package com.yimao.cloud.order.service.impl;

import com.yimao.cloud.base.enums.AgentLevel;
import com.yimao.cloud.base.enums.DistributorRoleLevel;
import com.yimao.cloud.base.enums.DistributorType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.mapper.DealerLeaderBoardMapper;
import com.yimao.cloud.order.mapper.StationLeaderBoardMapper;
import com.yimao.cloud.order.po.DealerSales;
import com.yimao.cloud.order.po.StationSales;
import com.yimao.cloud.order.service.LeaderBoardService;
import com.yimao.cloud.pojo.dto.order.DealerSalesDTO;
import com.yimao.cloud.pojo.dto.order.StationSalesDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Lizhqiang
 * @date 2019/2/25
 */
@Service
@Slf4j
public class LeaderBoardServiceImpl implements LeaderBoardService {

    @Resource
    private DealerLeaderBoardMapper dealerLeaderBoardMapper;

    @Resource
    private StationLeaderBoardMapper stationLeaderBoardMapper;


    @Resource
    private SystemFeign systemFeign;

    @Resource
    private UserFeign userFeign;


    /**
     * 我的业绩头部
     *
     * @param
     * @param
     * @return
     */
    @Override
    public DealerSalesDTO dealerNationalRanking(DistributorDTO distributorDTO) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Integer batchId = Integer.parseInt(sdf.format(calendar.getTime()));
        DealerSales dealerSales;
        //角色等级为1000时，为企业版子账号
        Integer pid = distributorDTO.getPid();
        Integer roleLevel = distributorDTO.getRoleLevel();
        if (roleLevel == DistributorRoleLevel.D_1000.value) {
            //为企业版经销商子账号--只展示排名，不展示业绩  主账号的全国排名，主账号排名提升，top排名不带业绩
            dealerSales = dealerLeaderBoardMapper.subDealerNationalRanking(pid, batchId);
        } else {
            //等级不为1000时可能是企业版经销商主账号和其他类型经销商 --或者代理商，这两者区分展示
            if (distributorDTO.getType() == DistributorType.PROXY.value) {
                // 代理商（省市区县级代理商）----展示全国排名第一的经销商业绩--不展示文案
                dealerSales = dealerLeaderBoardMapper.agentNationalRanking();
            } else {
                //为其他经销商，全部展示
                dealerSales = dealerLeaderBoardMapper.dealerNationalRanking(distributorDTO.getId(), batchId);
            }
        }
        DealerSalesDTO dto = new DealerSalesDTO();
        if (dealerSales != null) {
            dealerSales.setBatchId(batchId);
            dealerSales.convert(dto);
            dto.setBatchIdStart(Integer.valueOf(sdf.format(geLastWeekMonday(new Date()))));
            dto.setRealName(distributorDTO.getRealName());
            dto.setHeadImage(distributorDTO.getHeadImage());
        } else {
            dealerSales = new DealerSales();
            dealerSales.setBatchId(batchId);
            dealerSales.convert(dto);
            dto.setBatchIdStart(Integer.valueOf(sdf.format(geLastWeekMonday(new Date()))));
            dto.setRealName(distributorDTO.getRealName());
            dto.setHeadImage(distributorDTO.getHeadImage());
        }
        return dto;
    }

    /**
     * 经销商：全国top10
     *
     * @return
     */
    @Override
    public List<DealerSalesDTO> getTopDealerNationalRanking(Integer dealerId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Integer batchId = Integer.parseInt(sdf.format(calendar.getTime()));
        List<DealerSalesDTO> topList = dealerLeaderBoardMapper.getTopDealerNationalRanking(batchId);
        //获取用户头像集合
        List<Integer> ids = topList.stream().map(DealerSalesDTO::getDealerId).collect(Collectors.toList());
        if (ids.size() == 0) {
            throw new BadRequestException("用户id集合为空");
        }
        List<String> distributorImageById = userFeign.getDistributorImageById(ids);
        for (int j = 0; j < topList.size(); j++) {
            DealerSalesDTO dealerSalesDTO = topList.get(j);
            dealerSalesDTO.setHeadImage(distributorImageById.get(j));
            //获取是否在点赞库中存在，存在给1，不存在给0
            Integer dealerSalesId = dealerSalesDTO.getId();
            Integer hasPraised = dealerLeaderBoardMapper.getHasPraised(dealerSalesId, dealerId);
            if (hasPraised == 1) {
                //点赞
                dealerSalesDTO.setHasPraised(1);
            } else {
                //未点赞
                dealerSalesDTO.setHasPraised(0);
            }
        }
        if (topList == null) {
            throw new YimaoException("为找到相关数据");
        }
        return topList;
    }

    /**
     * 点赞经销商
     *
     * @param dealerId
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void rankingDealerLink(Integer dealerId, Integer rankingId) {
        //更新点赞数
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Integer batchId = Integer.parseInt(sdf.format(calendar.getTime()));
        //先去中间表查询是否点赞了，获取到了点赞记录则做取消点赞
        Integer id = dealerLeaderBoardMapper.selectLinkRecord(dealerId, rankingId);
        if (id != null) {
            //则有点赞记录，此次为取消点赞
            DealerSales queryDb = dealerLeaderBoardMapper.selectByPrimaryKey(rankingId);
            Integer linkCount = queryDb.getLinkCount();
            linkCount = linkCount - 1;
            queryDb.setLinkCount(linkCount);
            int i = dealerLeaderBoardMapper.updateByPrimaryKeySelective(queryDb);
            if (i < 1) {
                throw new YimaoException("取消点赞失败");
            }
            Integer insert = dealerLeaderBoardMapper.deleteLinkRecord(id);
            if (insert < 1) {
                throw new YimaoException("取消点赞失败");
            }
        } else {
            DealerSales queryDb = dealerLeaderBoardMapper.selectByPrimaryKey(rankingId);
            Integer linkCount = queryDb.getLinkCount();
            linkCount = linkCount + 1;
            queryDb.setLinkCount(linkCount);
            int i = dealerLeaderBoardMapper.updateByPrimaryKeySelective(queryDb);
            if (i < 1) {
                throw new YimaoException("点赞失败");
            }
            Integer insert = dealerLeaderBoardMapper.insertLinkId(dealerId, rankingId);
            if (insert < 1) {
                throw new YimaoException("点赞记录写入失败");
            }
        }
    }


    /**
     * 服务站：我的全国排名(头部)
     *
     * @param distributorDTO
     * @return
     */
    @Override
    public StationSalesDTO stationNationalRanking(DistributorDTO distributorDTO) {
        Integer type = distributorDTO.getType();
        Integer agentLevel = distributorDTO.getAgentLevel();
        Integer pid = distributorDTO.getPid();
        Integer roleLevel = distributorDTO.getRoleLevel();
        //判断条件
        boolean eOne = Objects.equals(type, DistributorType.BOTH.value);
        boolean eTwo = Objects.equals(agentLevel, AgentLevel.AGENT_P.value);
        boolean eThree = Objects.equals(agentLevel, AgentLevel.AGENT_C.value);
        boolean eFour = Objects.equals(agentLevel, AgentLevel.AGENT_R.value);
        boolean eFive = Objects.equals(agentLevel, AgentLevel.AGENT_PC.value);
        boolean eSix = Objects.equals(agentLevel, AgentLevel.AGENT_PR.value);
        boolean eSeven = Objects.equals(agentLevel, AgentLevel.AGENT_CR.value);
        boolean eEight = Objects.equals(agentLevel, AgentLevel.AGENT_PCR.value);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        //System.out.println("本批次: " + sdf.format(calendar2.getTime()));
        Integer batchId = Integer.parseInt(sdf.format(calendar2.getTime()));

        StationSales stationSales = null;
        if (roleLevel == DistributorRoleLevel.D_1000.value) {
            //企业版经销商子账号--主账号所属服务站，不展示业绩；
            stationSales = stationLeaderBoardMapper.getSubMyStationRanking(distributorDTO.getRegion(), batchId);
        } else if (type == DistributorType.DEALER.value) {
            //单纯经销商（除企业版经销商子账号外）显示经销商所属服务站
            stationSales = stationLeaderBoardMapper.getDealerMyStationRanking(distributorDTO.getRegion(), batchId);
        } else if (eOne) {
            //既是经销商不管是什么代理商--显示所属服务站
            stationSales = stationLeaderBoardMapper.getDealerMyStationRanking(distributorDTO.getRegion(), batchId);
        } else if (eTwo || eThree || eFive) {
            //没有区的代理都显示全国第一
            stationSales = stationLeaderBoardMapper.getFirstStationRanking(batchId);
        } else if (eEight || eSeven || eSix || eFour) {
            //只要有区代的都显示区代
            stationSales = stationLeaderBoardMapper.getDealerMyStationRanking(distributorDTO.getRegion(), batchId);
        }
        StationSalesDTO dto = new StationSalesDTO();
        stationSales.convert(dto);
        return dto;
    }

    /**
     * 服务站排行榜TOP10
     *
     * @return
     */
    @Override
    public List<StationSalesDTO> getTopStationNationalRanking(Integer dealerId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Integer oldBatchId = Integer.parseInt(sdf.format(calendar.getTime()));
        List<StationSalesDTO> dtoList = stationLeaderBoardMapper.getStationNationalRankingLastWeek(oldBatchId);
        for (int j = 0; j < dtoList.size(); j++) {
            StationSalesDTO stationSalesDTO = dtoList.get(j);
            //获取是否在点赞库中存在，存在给1，不存在给0
            //StationDTO stationByPRC = systemFeign.getStationByPRC(stationSalesDTO.getProvince(), stationSalesDTO.getCity(), stationSalesDTO.getRegion());
            StationDTO stationById = systemFeign.getStationById(stationSalesDTO.getStationId());
            stationSalesDTO.setStationName(stationById.getName());
            Integer hasPraised = stationLeaderBoardMapper.getHasPraised(stationSalesDTO.getId(), dealerId);
            if (hasPraised == 1) {
                //点赞
                stationSalesDTO.setHasPraised(1);
            } else {
                //未点赞
                stationSalesDTO.setHasPraised(0);
            }

            StationSalesDTO dto = stationLeaderBoardMapper.getLinkCount(oldBatchId, j + 1);
            stationSalesDTO.setLinkCount(dto.getLinkCount());
            stationSalesDTO.setSalesAccount(dto.getSalesAccount());
            stationSalesDTO.setSort(dto.getSort());
            stationSalesDTO.setId(dto.getId());
        }
        return dtoList;
    }

    /**
     * 服务站点赞
     *
     * @param
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void rankingStationLink(Integer dealerId, Integer rankingId) {
        //查找是否已经点赞过
        Integer id = stationLeaderBoardMapper.selectLinkRecord(dealerId, rankingId);
        if (id != null) {
            StationSales stationSales = stationLeaderBoardMapper.selectById(rankingId);
            Integer linkCount = stationSales.getLinkCount();
            linkCount = linkCount - 1;
            stationSales.setLinkCount(linkCount);
            int i = stationLeaderBoardMapper.updateByPrimaryKeySelective(stationSales);
            if (i < 1) {
                throw new YimaoException("取消点赞失败");
            }
            Integer insert = stationLeaderBoardMapper.deleteLinkRecord(id);
            if (insert < 1) {
                throw new YimaoException("点赞记录删除失败");
            }
        } else {
            StationSales stationSales = stationLeaderBoardMapper.selectById(rankingId);
            Integer linkCount = stationSales.getLinkCount();
            linkCount = linkCount + 1;
            stationSales.setLinkCount(linkCount);
            int i = stationLeaderBoardMapper.updateByPrimaryKeySelective(stationSales);
            if (i < 1) {
                throw new YimaoException("点赞失败");
            }
            Integer dealerSalesId = stationSales.getId();
            Integer insert = stationLeaderBoardMapper.insertLinkId(dealerId, dealerSalesId);
            if (insert < 1) {
                throw new YimaoException("点赞记录写入失败");
            }
        }
    }

    public Date geLastWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }

    public Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        System.out.println(dayWeek);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }
}
