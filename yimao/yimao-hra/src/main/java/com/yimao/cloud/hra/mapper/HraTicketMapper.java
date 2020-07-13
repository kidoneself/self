package com.yimao.cloud.hra.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.hra.po.HraTicket;
import com.yimao.cloud.pojo.dto.hra.HraAllotTicketDTO;
import com.yimao.cloud.pojo.dto.hra.HraAllotTicketExportDTO;
import com.yimao.cloud.pojo.dto.hra.HraExportPhysicalDTO;
import com.yimao.cloud.pojo.dto.hra.HraExportQuery;
import com.yimao.cloud.pojo.dto.hra.HraExportReservationDTO;
import com.yimao.cloud.pojo.dto.hra.HraPhysicalDTO;
import com.yimao.cloud.pojo.dto.hra.HraQueryDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketPastDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketResultDTO;
import com.yimao.cloud.pojo.dto.station.HraStatisticsDTO;
import com.yimao.cloud.pojo.query.station.HraSpecialQuery;
import com.yimao.cloud.pojo.query.station.HraTicketQuery;
import com.yimao.cloud.pojo.query.hra.HraPhysicalQuery;
import com.yimao.cloud.pojo.vo.hra.HraCardVO;
import com.yimao.cloud.pojo.vo.station.StationHraCardVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2017/11/29.
 */
public interface HraTicketMapper extends Mapper<HraTicket> {

    void insertBatch(List<HraTicket> ticketList);

    List<HraTicketDTO> findTicketListByUserId(Integer userId);

    /**
     * 根据状态和体检卡号查询
     *
     * @param ticketNo
     * @return
     */
    HraTicketDTO findByTicketNoAndStatus(@Param("ticketNo") String ticketNo);

    //根据用户id和体检卡查询体检卡和体检人
    List<HraTicket> findTicketByUserIdAndTicketNo(@Param("ticketNo") String ticketNo, @Param("userId") Integer userId);

    Page<HraTicketDTO> reportTicket(@Param("startNum") Integer startNum,
                                    @Param("pageSize") Integer pageSize,
                                    @Param("userId") Integer userId);

    //待使用和已使用评估券数量
    Integer getNoUsedCount(@Param(value = "userId") Integer userId, @Param(value = "ticketStatus") Integer ticketStatus);

    //已赠出评估券数量
    Integer getSendCount(@Param(value = "userId") Integer userId);

    Page<HraTicket> findHraTicketByList(@Param("tickectNoList") List<String> tickectNoList);

    Page<HraTicketDTO> getHraTicketByUser(@Param(value = "userId") Integer userId);

//    List<HraTicket> reserveTicketByPayList(@Param("userId") Integer userId);
//
//    List<HraTicket> reserveTicketByNoPayList(@Param("userId") Integer userId);

    Integer selectFreeCardCount(@Param(value = "userId") Integer userId, @Param(value = "ticketStatus") Integer ticketStatus);

    Integer selectGiveCardCountByUser(@Param(value = "userId") Integer userId);

    /**
     * 已赠出的优惠卡
     *
     * @param userId     用户ID
     * @param ticketType 类型
     * @return page
     * @author liuhao@yimaokeji.com
     */
    Page<HraTicketDTO> selectGiveCardByUser(@Param(value = "userId") Integer userId,
                                            @Param(value = "ticketType") String ticketType);

    //根据卡号，获取体检卡
    HraTicketDTO findTicketByTicketNo(@Param(value = "ticketNo") String ticketNo);

    /**
     * @param userId
     * @param ticketType
     * @param orderId
     * @param ticketStatus
     * @return
     */
    Page<HraTicketDTO> selectFreeCardByUser(@Param(value = "userId") Integer userId,
                                            @Param(value = "ticketType") String ticketType,
                                            @Param(value = "orderId") Long orderId,
                                            @Param(value = "ticketStatus") Integer ticketStatus);

    //根据体检卡号,查询该卡支付时间
//    Date getPayTimeByTicketNo(@Param("ticketNo") String ticketNo);

    /**
     * 查询用户符合赠送条件的卡的数量
     *
     * @param id         用户id
     * @param ticketType 体检卡类型Y/M/F
     * @return
     */
    Integer getCanBeSendCardCount(@Param("id") Integer id,
                                  @Param("ticketStatus") Integer ticketStatus,
                                  @Param("ticketType") String ticketType);

    HraTicketDTO getTicketByTicketNo(@Param("ticketNo") String ticketNo);

    HraTicketDTO getTicketByUserIdAndTicketNo(Map<String, Object> map);

    /**
     * 查询用户符合赠送条件的卡的数量
     *
     * @param id         用户id
     * @param ticketType 体检卡类型Y/M/F
     * @return
     */
    List<HraTicketDTO> getCanBeSendCardList(@Param("id") Integer id,
                                            @Param("ticketStatus") Integer ticketStatus,
                                            @Param("ticketType") String ticketType);


    int updateBatchHraTicketStatus(@Param("handselTime") Date handselTime,
                                   @Param("handselStatus") int handselStatus,
                                   @Param("ticketNoList") List ticketNoList,
                                   @Param("handselFlag") Long handselFlag);


    Integer getTicketNum(@Param("userId") Integer userId);

    List<HraTicket> selectTickets(@Param(value = "userId") Integer userId);

    Page<HraTicketResultDTO> listTicket(@Param("beginTime") String beginTime,
                                        @Param("endTime") String endTime,
                                        @Param("userSource") Integer userSource,
                                        @Param("mobile") String mobile,
                                        @Param("ticketNo") String ticketNo,
                                        @Param("province") String province,
                                        @Param("city") String city,
                                        @Param("region") String region,
                                        @Param("name") String name,
                                        @Param("flag") Integer flag,
                                        @Param("reserveStatus") Integer reserveStatus,
                                        @Param("hasUpload") Integer hasUpload,
                                        @Param("userId") Integer userId,
                                        @Param("ticketType") String ticketType,
                                        @Param("stationIds") List<Integer> stationIds);

    /**
     * @Description:F评估卡分配列表
     * @author ycl
     */
    Page<HraCardVO> listTicketBy(HraQueryDTO query);

    /**
     * @param isExpire
     * @param minSurplus
     * @param state
     * @param beginTime
     * @param endTime
     * @param ticketNo
     * @Description:评估卡分配列表
     * @author ycl
     * @Return: com.github.pagehelper.Page<com.yimao.cloud.hra.po.HraTicket>
     * @Create: 2019/1/12 16:37
     */
    Page<HraAllotTicketDTO> allotTicket(@Param("ids") List<Integer> ids,
                                        @Param("isExpire") String isExpire,
                                        @Param("minSurplus") Integer minSurplus,
                                        @Param("maxSurplus") Integer maxSurplus,
                                        @Param("state") Integer state,
                                        @Param("beginTime") String beginTime,
                                        @Param("endTime") String endTime,
                                        @Param("ticketNo") String ticketNo,
                                        @Param("date") Date date);

    /**
     * @Description: 体检卡管理
     * @author ycl
     * @param: province 省
     * @Return: com.github.pagehelper.Page<com.yimao.cloud.pojo.dto.hra.HraTicketResultDTO>
     * @Create: 2019/2/19 9:57
     */
    Page<HraPhysicalDTO> physical(HraQueryDTO query);

    /**
     * 根据评估卡查询多张评估劵
     *
     * @param cardId
     * @return list
     * @author liuhao@yimaokeji.com
     */
    List<HraTicketDTO> ticketByCardId(@Param("cardId") String cardId);

    /**
     * 可预约的卡
     *
     * @param userId 用户id
     * @return page
     */
    Page<HraTicketDTO> reserveTicketList(@Param("userId") Integer userId);

    /**
     * 单个修改体检卡的所有权
     *
     * @param sharerId         分享用户
     * @param userId           用户id
     * @param type             类型
     * @param cardIdOrTicketNo 体检卡或者体检劵
     * @param receiveTime      时间
     * @return
     */
    int changeTicketOwner(@Param("sharerId") Integer sharerId,
                          @Param("userId") Integer userId,
                          @Param("type") int type,
                          @Param("cardIdOrTicketNo") String cardIdOrTicketNo,
                          @Param("receiveTime") Date receiveTime);

    /**
     * 根据体检劵号 获取订单Id
     *
     * @param ticketNo 体检劵号
     * @return 订单号
     */
    Long getOrderIdByTicketNo(@Param("ticketNo") String ticketNo);


    int changeTicketHandselStatus(@Param(value = "userId") Integer userId,
                                  @Param(value = "cardIdOrTicketNo") String cardIdOrTicketNo,
                                  @Param(value = "type") Integer type,
                                  @Param(value = "handselTime") Date handselTime,
                                  @Param(value = "handselFlag") Long handselFlag);


    List<HraTicketDTO> listTicketForPay(@Param(value = "userId") Integer id, @Param(value = "count") Integer count);

    /*List<HraExportReservationDTO> listTicketExport(@Param("beginTime") String beginTime,
                                                   @Param("endTime") String endTime,
                                                   @Param("userSource") Integer userSource,
                                                   @Param("mobile") String mobile,
                                                   @Param("ticketNo") String ticketNo,
                                                   @Param("province") String province,
                                                   @Param("city") String city,
                                                   @Param("region") String region,
                                                   @Param("name") String name,
                                                   @Param("flag") Integer flag,
                                                   @Param("reserveStatus") Integer reserveStatus,
                                                   @Param("hasUpload") Integer hasUpload,
                                                   @Param("userId") Integer userId,
                                                   @Param("ticketType") String ticketType,
                                                   @Param("stationIds") List<Integer> stationIds);*/

    Page<HraExportReservationDTO> listTicketExport(HraExportQuery query);

   /* Page<HraTicketExportDTO> listTicketExport(@Param("beginTime") String beginTime,
                                              @Param("endTime") String endTime,
                                              @Param("userSource") Integer userSource,
                                              @Param("mobile") String mobile,
                                              @Param("ticketNo") String ticketNo,
                                              @Param("province") String province,
                                              @Param("city") String city,
                                              @Param("region") String region,
                                              @Param("name") String name,
                                              @Param("flag") Integer flag,
                                              @Param("reserveStatus") Integer reserveStatus,
                                              @Param("hasUpload") Integer hasUpload,
                                              @Param("userId") Integer userId,
                                              @Param("ticketType") String ticketType,
                                              @Param("stationIds") List<Integer> stationIds);*/


    /**
     * 查询赠送时间超过24小时体检劵
     *
     * @return list
     */
    List<HraTicket> listHandselExpiredHraTicket();

    /**
     * 将评估卡劵修改为过期状态且赠送状态设置为null
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
     * 体检卡七天过期的所属用户ID集合
     *
     * @param ticketType
     * @return
     */
    List<HraTicketPastDTO> ticketPastUserId(@Param("ticketType") String ticketType);

    /**
     * 预约明天体检的所属用户ID集合
     *
     * @return
     */
    List<HraTicketPastDTO> ticketReservePastUserId();

    /**
     * 查询已过期的评估卡
     *
     * @return
     */
    List<Integer> findHasExpired();

    List<HraTicket> listExpiredHraTicket();

    /*List<HraExportPhysicalDTO> exportPhysical(@Param("ids") List<Integer> ids,
                                              @Param("stationIds") List<Integer> stationIds,
                                              @Param("currentUserId") Integer currentUserId,
                                              @Param("ticketNo") String ticketNo,
                                              @Param("userType") Integer userType,
                                              @Param("minSurplus") Integer minSurplus,
                                              @Param("maxSurplus") Integer maxSurplus,
                                              @Param("ticketStatus") Integer ticketStatus,
                                              @Param("beginTime") Date beginTime,
                                              @Param("endTime") Date endTime,
                                              @Param("cardType") String cardType);*/

    Page<HraExportPhysicalDTO> exportPhysical(HraQueryDTO hraQuery);

    // List<HraExportReservationDTO> exportReservation(@Param("beginTime") String beginTime,
    //                                                 @Param("endTime") String endTime,
    //                                                 @Param("userSource") Integer userSource,
    //                                                 @Param("mobile") String mobile,
    //                                                 @Param("ticketNo") String ticketNo,
    //                                                 @Param("province") String province,
    //                                                 @Param("city") String city,
    //                                                 @Param("region") String region,
    //                                                 @Param("name") String name,
    //                                                 @Param("flag") Integer flag,
    //                                                 @Param("reserveStatus") Integer reserveStatus,
    //                                                 @Param("hasUpload") Integer hasUpload,
    //                                                 @Param("userId") Integer userId,
    //                                                 @Param("ticketType") String ticketType,
    //                                                 @Param("stationIds") List<Integer> stationIds
    // );
    
//     Page<HraExportReservationDTO> exportReservationPage(@Param("beginTime") String beginTime,
//             @Param("endTime") String endTime,
//             @Param("userSource") Integer userSource,
//             @Param("mobile") String mobile,
//             @Param("ticketNo") String ticketNo,
//             @Param("province") String province,
//             @Param("city") String city,
//             @Param("region") String region,
//             @Param("name") String name,
//             @Param("flag") Integer flag,
//             @Param("reserveStatus") Integer reserveStatus,
//             @Param("hasUpload") Integer hasUpload,
//             @Param("userId") Integer userId,
//             @Param("ticketType") String ticketType,
//             @Param("stationIds") List<Integer> stationIds
// );

    void changeTicketOwnerBatch(@Param("sharerId") Integer sharerId,
                                @Param("userId") Integer userId,
                                @Param("handselList") List<String> handselList,
                                @Param("receiveTime") Date receiveTime);


    /*List<HraAllotTicketExportDTO> hraAllotTicketExport(String province, String city, String region, String stationName, String isExpire, Integer minSurplus, Integer maxSurplus, Integer state, String beginTime, String endTime, String ticketNo, Date date);*/

    Page<HraAllotTicketExportDTO> hraAllotTicketExport(HraQueryDTO hraQuery);

    HraTicket selectIdAndCardIdByTicketNo(@Param("ticketNo") String ticketNo);

    Page<StationHraCardVO> listTicketByQuery(HraSpecialQuery query);

	Page<HraTicketResultDTO> getStationHraTicketUsedList(HraTicketQuery query);

	Page<HraTicketResultDTO> getStationHraTicketReservationList(HraTicketQuery query);

	int getTotalAssessNum(@Param("stations") List<Integer> stations);

	int getYesterdayAssessNum(@Param("stations") List<Integer> stations);

	int getTodayNeedAssessNum(@Param("stations") List<Integer> stations);

	List<HraStatisticsDTO> getStationHraReserveData(HraTicketQuery hraQuery);

	List<HraStatisticsDTO> getStationHraUsedData(HraTicketQuery hraQuery);

	List<HraStatisticsDTO> getStationHraReservePicData(HraTicketQuery hraQuery);

	List<HraStatisticsDTO> getStationHraUsedPicData(HraTicketQuery hraQuery);
}
