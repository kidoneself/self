package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import com.yimao.cloud.pojo.dto.system.TransferAreaInfoDTO;
import com.yimao.cloud.pojo.dto.user.EngineerChangeRecordDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.EngineerExportDTO;
import com.yimao.cloud.pojo.dto.user.EngineerServiceAreaDTO;
import com.yimao.cloud.pojo.query.station.StationEngineerQuery;
import com.yimao.cloud.pojo.query.user.EngineerQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.EngineerVO;
import com.yimao.cloud.user.po.Engineer;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EngineerService {

    /**
     * 安装工登录
     *
     * @param userName 用户名
     * @param password 密码
     * @param appType  登录的手机系统：1-Android；2-ios
     */
    EngineerDTO login(String userName, String password, Integer appType);

    /**
     * 创建安装工
     *
     * @param engineer 安装工信息
     */
    void save(Engineer engineer);

    /**
     * 根据安装工ID获取安装工信息
     *
     * @param id 安装工ID
     */
    Engineer getById(Integer id);

    /**
     * 根据安装工ID获取安装工信息（消息推送时只需获取很少的几个字段）
     *
     * @param id 安装工ID
     */
    EngineerDTO getBasicInfoByIdForMsgPushInfo(Integer id);

    /**
     * 根据安装工用户名获取安装工信息
     *
     * @param userName 用户名
     */
    Engineer getByUserName(String userName, String oldId);

    /**
     * 根据安装工手机号获取安装工信息
     *
     * @param userName 用户名
     */
    Engineer getByUserPhone(String phone);

    /**
     * 更新安装工信息
     *
     * @param engineer 安装工信息
     */
    void update(Engineer engineer);

    /**
     * @param
     * @return void
     * @description 安装工改密码
     * @author Liu Yi
     * @date 2019/9/30 14:46
     */
    void updatePassword(Engineer engineer);

    /**
     * 禁用/启用安装工账号
     *
     * @param engineer 安装工
     */
    void forbidden(Engineer engineer);

    /**
     * 解绑（安装工账号和手机ICCID的绑定）
     *
     * @param engineer 安装工
     * @param type
     */
    void unbind(Engineer engineer, Integer type);

    /**
     * 绑定ICCID
     */
    void binding(Engineer engineer, String iccid);

    /**
     * 检查iccid是否已经存在
     *
     * @param iccid SIM卡卡号
     */
    Boolean checkEngineerIccid(String iccid);

    /**
     * 根据省市区查询安装工
     *
     * @param province city
     * @param city     市
     * @param region   区
     */
    List<EngineerDTO> getEngineerByArea(String province, String city, String region);

    /**
     * 根据省市区查询安装工数量（包含已经禁用的）
     */
    int countEngineerByArea(Integer areaId);

    /**
     * 校验安装工是否存在
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    boolean checkEngineerExistsByPCR(Integer areaId);

    /**
     * 分页查询安装工
     *
     * @param pageNum  当前页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    PageVO<EngineerDTO> page(Integer pageNum, Integer pageSize, EngineerQuery query);

    /**
     * 获取安装工修改记录
     *
     * @param engineerId 安装工ID
     */
    List<EngineerChangeRecordDTO> getEngineerChangeRecords(Integer engineerId);

    /**
     * 根据老的安装工ID获取安装工信息
     *
     * @param oldId 老的安装工ID
     */
    Engineer getEngineerByOldId(String oldId);

    /**
     * 分页查询安装工（站务系统调用）
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    PageVO<EngineerVO> pageEngineerInfoToStation(Integer pageNum, Integer pageSize, StationEngineerQuery query);

    List<Integer> getEngineerIdsByAreaIds(Set<Integer> areaIds);

    /**
     * 站务系统--订单--安装工单--安装工程师筛选条件
     *
     * @param stationIds
     * @return
     */
    List<EngineerVO> getEngineerListByStationIds(Set<Integer> stationIds);

    /***
     * 获取服务站下所有未禁用的安装工信息
     * @param engineerId
     * @return
     */
	List<EngineerDTO> getEngineerListByEngineerId(Integer engineerId);

    /***
     * 根据服务站id查询该服务站下的所有安装工
     */
    List<EngineerDTO> getEngineerListByStationId(Integer stationId);

    /***
	 * 安装工转让,需要将 未完成的工单、续费单、订单、收益、水机
	 * @param oldId
	 * @param newId
	 */
	void transferEngineer(Integer oldId, Integer newId);

    /**
     * 将指定服务区域下的安装工转给指定服务站门店
     *
     * @param transferAreaInfo 转让区域及承包方信息
     */
    void transferEngineerToNewStationByServiceArea(TransferAreaInfoDTO transferAreaInfo);

    /**
     * 将原服务于指定服务区域的安装工对该区域的服务权限删除，给指定安装工新增对该地区的服务权限
     *
     * @param transferAreaInfo 转让区域及承包方信息
     */
    void engineerUpdateServiceArea(TransferAreaInfoDTO transferAreaInfo);

    List<EngineerDTO> getEngineerListByArea(Integer areaId);

    void updateEngineerServiceArea(List<StationServiceAreaDTO> serviceAreaList, Integer stationId);

    List<EngineerServiceAreaDTO> getEngineerServiceArea(Integer id);

    void updateHeadImg(EngineerDTO dto);

    void updateStationName(Map<String,Object> stationInfo);
}
