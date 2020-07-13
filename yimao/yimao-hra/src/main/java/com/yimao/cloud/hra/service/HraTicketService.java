package com.yimao.cloud.hra.service;


import com.yimao.cloud.hra.po.HraTicket;
import com.yimao.cloud.pojo.dto.hra.*;
import com.yimao.cloud.pojo.dto.station.HraStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.query.station.HraSpecialQuery;
import com.yimao.cloud.pojo.query.station.HraTicketQuery;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.hra.HraCardVO;
import com.yimao.cloud.pojo.vo.station.StationHraCardVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2017/12/1.
 */
public interface HraTicketService {

    HraTicket findHraTicketByTicketNo(String ticketNo);

    /**
     * @param userId 用户ID
     * @Description: 查询评估报告列表
     * @author ycl
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.hra.HraTicketDTO>
     * @Create: 2019/4/22 11:59
     */
    List<HraTicketDTO> findTicketListByUserId(Integer userId);

    /**
     * @param ticketNo
     * @Description: 查询体检卡
     * @author ycl
     * @Return: com.yimao.cloud.pojo.dto.hra.HraTicketDTO
     * @Create: 2019/4/22 13:49
     */
    HraTicketDTO findByTicketNoAndStatus(String ticketNo);

    HraTicketDTO findTicketByUserIdAndTicketNo(String ticketNo, Integer userId);

    PageVO<HraTicketDTO> reportTicket(Integer pageNum, Integer pageSize, Integer userId);

    List<HraTicketDTO> ticketByCardId(String cardId);

    HraTicketDTO createReserveTicket(ReserveDTO reserveDTO, Integer userId);

    PageVO<HraTicketDTO> getHraTicketByUser(Integer pageNum, Integer pageSize, Integer userId);

    PageVO<HraTicketDTO> reserveTicketList(Integer pageNum, Integer pageSize, Integer userId);

    Map<String, Object> listFreeCard(Integer pageNum, Integer pageSize, Long orderId, Integer userId, Integer ticketStatus);

    Map<String, Object> getLifecyleByTicket(String ticketNo, Integer userId);

    /**
     * 获取可赠送出去的优惠卡数量
     *
     * @param userId       用户Id
     * @param ticketStatus 体检卡状态
     * @param ticketType   体检卡型号
     * @return int
     * @author liuhao@yimaokeji.com
     */
    Integer getCanBeSendCardCount(Integer userId, Integer ticketStatus, String ticketType);

    Integer cancelReserve(Integer ticketId, Integer userId);

    HraTicketDTO getTicketByTicketNo(String ticketNo);

    PageVO<HraTicketResultDTO> listTicket(String beginTime, String endTime, Integer userSource, String mobile, String ticketNo, String province, String city, String region, String name, Integer flag, Integer pageNum, Integer pageSize, Integer reserveStatus, Integer hasUpload, Integer userId, String ticketType);

    //体检卡列表
    PageVO<HraPhysicalDTO> physical(HraQueryDTO query, Integer pageNum, Integer pageSize);


    /**
     * @Description: 通过评估卡id 查询评估劵
     * @author ycl
     * @param: cardId
     * @Return: java.util.List<com.yimao.cloud.hra.po.HraTicket>
     * @Create: 2019/2/19 15:53
     */
    List<HraTicket> findByCardId(String cardId);

    /**
     * @Description: F卡管理-分配F卡
     * @author ycl
     * @param: stationId
     * @Return: java.util.List<com.yimao.cloud.hra.po.HraCard>
     * @Create: 2019/2/19 15:54
     */
    List<HraCardDTO> allot(HraFCardDTO hraFCardDTO);

    /**
     * F卡管理
     */
    PageVO<HraCardVO> allotTicket(HraQueryDTO query, Integer pageNum, Integer pageSize);

    void updateTicket(HraTicketDTO hraTicketDTO);

    //F卡管理-是否禁用功能
    void updateStatus(HraAllotTicketDTO hraAllotTicketDTO);

    /**
     * 发放卡前，生成优惠卡发放记录
     *
     * @param user     用户
     * @param count    数量
     * @param remark   备注
     * @param giveType 1-经销商固定配额，2-经销商业绩发放，3-用户业绩，5-手动发放卡，7-分销用户业绩卡 8-兑换码兑换
     */
    void generateHraRecord(UserDTO user, Integer count, String remark, Integer giveType,Integer styleType);

    /**
     * 发放卡
     *
     * @param userDTO   用户
     * @param productId 产品信息
     * @param giveType  发卡类型
     * @return 卡号
     */
    String assignCard(UserDTO userDTO, Integer productId, Integer giveType);

    /**
     * 根据体检劵号获取体检卡列表
     *
     * @param list     体检劵集合
     * @param dateTime 时间戳
     * @return
     */
    List<HraTicket> getHraTicketListByTicketNoList(List<String> list, Long dateTime);


    /**
     * 改变体检劵归属关系
     *
     * @param sharerId         分享用户
     * @param userId           用户
     * @param cardIdOrTicketNo 体检卡或者劵
     * @param type             类型
     * @param handselFlag      标志
     * @return bool
     */
    boolean changeTicketOwner(Integer sharerId, Integer userId, String cardIdOrTicketNo, int type, Long handselFlag);

    /**
     * 改变体检劵归属关系（批量）
     *
     * @param sharerId    分享用户
     * @param userId      用户
     * @param handselList 体检卡或者劵
     * @param date        时间
     * @param handselFlag 标志
     * @return
     */
    Boolean changeTicketOwnerBatch(Integer sharerId, Integer userId, List<String> handselList, Date date, Long handselFlag);

    /**
     * M卡支付回调(修改M卡信息)
     *
     * @param orderId 订单号
     * @author liuhao@yimaokeji.com
     */
    void doRefer(Long orderId, String ticketNo, Integer terminal);

    /**
     * 把要赠送出去的卡保存到redis中
     *
     * @param userId       用户Id
     * @param sendCount    赠送数量
     * @param ticketStatus 赠送状态 1-待使用 3-未支付
     * @param ticketType   卡类型 (M,Y)
     * @return String
     * @author liuhao@yimaokeji.com
     */
    String saveTicketNoRedis(Integer userId, Integer sendCount, Integer ticketStatus, String ticketType);


    /**
     * 更新要赠送的卡的状态为赠送中,并往体检卡周期表中插入数据
     *
     * @param userId      用户id
     * @param shareNOList 卡集合
     * @param dateTime    时间戳
     */
    void updateTicketStatusAndInsertSendRecord(Integer userId, String shareNOList, Long dateTime);

    /**
     * 赠送前修改评估卡状态为赠送中
     *
     * @param userId   用户Id
     * @param shareNo  赠送卡
     * @param type     类型
     * @param dateTime 时间戳
     * @return bool
     */
    boolean changeTicketHandselStatus(Integer userId, String shareNo, int type, Long dateTime);

    /**
     * @param id
     * @param num
     * @Description: 查询该用户下未支付的优惠卡
     */
    List<HraTicketDTO> listTicketForPay(Integer userId, Integer count);


    void createHraCardAndTicket(Long orderId);


    /**
     * 查询赠送时间超过24小时体检劵
     *
     * @return
     */
    List<HraTicket> listHandselExpiredHraTicket();

    /**
     * 将评估卡劵修改为过期状态
     *
     * @param id
     */
    void updateHraTicketExpired(Integer id);

    /**
     * 将赠送状态修改为未赠送状态
     *
     * @param id
     */
    void updateHraTicketHandselStatusToNull(Integer id);

    /**
     * @return
     */
    List<HraTicket> listExpiredHraTicket();

    PageVO<StationHraCardVO> stationAllotTicket(Integer pageNum, Integer pageSize, HraSpecialQuery query);

	PageVO<HraTicketResultDTO> getStationHraTicketUsedList(Integer pageNum, Integer pageSize, HraTicketQuery query);

	PageVO<HraTicketResultDTO> getStationHraTicketReservationList(Integer pageNum, Integer pageSize,
			HraTicketQuery query);

	StationScheduleDTO getStationHraNum(List<Integer> stations);

	List<HraStatisticsDTO> getStationHraData(HraTicketQuery hraQuery);

	HraStatisticsDTO getStationHraPicData(HraTicketQuery hraQuery);

    /*==================================================导出=======================================================*/
    /*==================================================导出=======================================================*/
    /*==================================================导出=======================================================*/
    /*List<HraExportPhysicalDTO> exportPhysical(String province, String city, String region, String stationName, Integer currentUserId, String ticketNo, Integer userType, Integer minSurplus, Integer maxSurplus, Integer ticketStatus, Date beginTime, Date endTime, String cardType);*/

    /*List<HraAllotTicketExportDTO> exportSpecialTicket(String province, String city, String region, String stationName, String isExpire, Integer minSurplus, Integer maxSurplus, Integer state, String beginTime, String endTime, String ticketNo);*/

    // List<HraExportReservationDTO> exportReservation(String beginTime, String endTime, Integer userSource, String mobile, String ticketNo, String province, String city, String region, String name, Integer reserveStatus, Integer userId);

    /*List<HraExportReservationDTO> getTicketInfoToExport(String beginTime, String endTime, Integer userSource, String mobile, String ticketNo, String province, String city, String region, String name, Integer reserveStatus, Integer userId, String ticketType);*/

}
