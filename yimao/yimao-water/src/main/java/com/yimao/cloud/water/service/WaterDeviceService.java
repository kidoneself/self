package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.out.DeviceQuery;
import com.yimao.cloud.pojo.dto.station.RenewStatisticsDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceCompleteDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceOverviewDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceReplaceRecordDTO;
import com.yimao.cloud.pojo.query.station.StationWaterDeviceQuery;
import com.yimao.cloud.pojo.query.water.WaterDeviceQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.DeviceGeneralSituationVO;
import com.yimao.cloud.pojo.vo.station.StationWaterDeviceVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import com.yimao.cloud.water.po.WaterDevice;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WaterDeviceService {

    /**
     * 创建水机设备信息
     *
     * @param device 水机设备信息
     */
    WaterDeviceDTO save(WaterDevice device);

    /**
     * 根据ID获取水机设备信息
     *
     * @param id 设备ID
     */
    WaterDevice getById(Integer id);

    /**
     * 根据SN码获取水机设备信息
     *
     * @param sn SN码
     */
    WaterDevice getBySnCode(String sn);

    /**
     * 根据SN码获取水机设备基本信息
     *
     * @param sn SN码
     */
    WaterDevice getBasicInfoBySnCode(String sn);

    /**
     * 根据水机设备iccid获取设备信息
     *
     * @param iccid 设备sim卡号
     */
    WaterDevice getByIccid(String iccid);

    /**
     * 更新
     *
     * @param waterDevice 水机设备
     */
    void update(WaterDevice waterDevice);

    /**
     * 根据条件分页获取设备信息
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    PageVO<WaterDeviceDTO> page(Integer pageNum, Integer pageSize, WaterDeviceQuery query);

    /**
     * 根据sim获取水机设备信息
     *
     * @param sim
     */
    WaterDevice getBySIM(String sim);

    /**
     * 检查sn是否存在
     */
    Boolean checkSnExists(Integer id, String sn);

    /**
     * 检查sim是否存在
     */
    Boolean checkIccidExists(Integer id, String iccid);

    /**
     * 更新水机设备信息
     *
     * @param dto
     */
    void update(WaterDeviceDTO dto);

    /**
     * 设备概况：按在线离线统计分类
     */
    Map<String, Integer> classificationByOnline();

    /**
     * 设备概况：按设备地区统计分类
     */
    Map<String, Integer> classificationByArea();

    /**
     * 设备概况：按设产品型号统计分类
     */
    Map<String, Integer> classificationByModel();

    /**
     * 设备概况：按激活日期统计分类
     *
     * @param startTime 开始日期
     * @param endTime   结束日期
     */
    Map<String, Integer> classificationByTrend(Date startTime, Date endTime);

    /**
     * 根据ID获取水机设备信息（详情）
     *
     * @param id 设备ID
     */
    WaterDeviceVO getDetailById(Integer id);

    /**
     * 修改水机计费方式
     *
     * @param id        设备ID
     * @param newCostId 新的计费方式ID
     */
    void changeCost(Integer id, Integer newCostId);

    /**
     * 激活SIM卡
     *
     * @param id 设备ID
     */
    void activatingSimCard(Integer id, String iccid);

    /**
     * 停用SIM卡
     *
     * @param id 设备ID
     */
    void deactivatedSimCard(Integer id);

    /**
     * 水机设备列表-解除绑定
     *
     * @param id 设备ID
     */
    void unbundling(Integer id);

    /**
     * 水机设备列表-恢复满额
     *
     * @param id 设备ID
     */
    void restoreFullAmount(Integer id);

    /**
     * 水机设备列表-修改设备信息
     *
     * @param id           设备ID
     * @param oldSn        旧SN码
     * @param newSn        新SN码
     * @param oldIccid     旧iccid
     * @param newIccid     新iccid
     * @param oldBatchCode 旧批次码
     * @param newBatchCode 新批次码
     */
    void update(Integer id, String oldSn, String newSn, String oldIccid, String newIccid, String oldBatchCode, String newBatchCode, String address);

    /**
     * 水机设备列表-更换设备
     *
     * @param deviceId     设备ID
     * @param oldSn        旧SN码
     * @param newSn        新SN码
     * @param oldIccid     旧iccid
     * @param newIccid     新iccid
     * @param oldBatchCode 旧批次码
     * @param newBatchCode 新批次码
     */
    void replace(Integer deviceId, String oldSn, String newSn, String oldIccid, String newIccid, String oldBatchCode, String newBatchCode);

    /**
     * 水机设备列表-续费
     *
     * @param id        设备ID
     * @param costId    新的计费方式ID
     * @param filePaths 附件文件路径
     */
    void renew(Integer id, Integer costId, Integer payType, String filePaths, MultipartFile[] files);

    /**
     * 根据水机设备ID删除设备
     *
     * @param id 设备ID
     */
    void delete(Integer id);

    Map getWaterDeviceGrowthTrend();


    /**
     * 设备概况统计
     *
     * @return UserOverviewDTO
     * @author hhf
     * @date 2019/3/19
     */
    WaterDeviceOverviewDTO waterDeviceOverview();

    /**
     * 根据条件查询设备数量
     *
     * @param query 条件
     */
    int countDevice(DeviceQuery query);

    PageVO<WaterDeviceVO> pageWaterDevice(Integer pageNum, Integer pageSize, WaterDeviceQuery query);

    void renewProcessor(WaterDeviceDTO dto);

    void updateForPadSyncData(WaterDevice update);

    void checkDeviceRenewTypeAndPutMir(WaterDevice device, BigDecimal money, WorkOrderDTO workOrder);

    /**
     * 安装工app查询设备列表
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @param beginTime
     * @param endTime
     * @return
     */
    PageVO<WaterDeviceDTO> pageDeviceForEngineerApp(Integer pageNum, Integer pageSize, Integer engineerId, String search);

    /**
     * 根据条件分页获取设备信息（站务系统调用）
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    PageVO<StationWaterDeviceVO> stationPageWaterDeviceInfo(Integer pageNum, Integer pageSize, StationWaterDeviceQuery query);

    /**
     * 查询续费数据
     *
     * @param waterDeviceQuery
     * @return
     */
    RenewStatisticsDTO getWaterDeviceRenewData(StationWaterDeviceQuery waterDeviceQuery);

    /**
     * 站务系统-统计-续费统计-图表数据（新安装+应续费）
     * 这里应续费指代当前为止还未续费的水机
     *
     * @param waterDeviceQuery
     * @return
     */
    List<RenewStatisticsDTO> getWaterDeviceRenewPicData(StationWaterDeviceQuery waterDeviceQuery);

    /**
     * 站务系统查询总设备数
     *
     * @param engineerIds
     * @return
     */
    Integer getDeviceTotalNum(List<Integer> engineerIds);


    /**
     * 站务系统查询昨日新增设备数
     *
     * @param engineerIds
     * @return
     */
    Integer getYesterdayNewInstallNum(List<Integer> engineerIds);

    DeviceGeneralSituationVO getStationWaterDeviceGeneralChart(List<Integer> engineerIds);

    List<SalesStatsDTO> getDeviceRenewPropList(List<Integer> ids);

    void updateWaterDeviceForEngineer(List<WaterDeviceDTO> wdds);

    PageVO<WaterDeviceReplaceRecordDTO> getWaterDeviceReplaceBySn(String sn, Integer pageNum, Integer pageSize);

    PageVO<WaterDeviceCompleteDTO> getCompleteWorkOrderList(Integer pageNum, Integer pageSize, String key, Integer engineerId);


}
