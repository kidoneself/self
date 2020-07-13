package com.yimao.cloud.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.*;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.feign.WaterFeign;
import com.yimao.cloud.order.mapper.*;
import com.yimao.cloud.order.po.*;
import com.yimao.cloud.order.service.MoveWaterDeviceOrderService;
import com.yimao.cloud.pojo.dto.order.MoveWaterDeviceOrderDTO;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.EngineerServiceAreaDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDevicePlaceChangeRecordDTO;
import com.yimao.cloud.pojo.query.order.MoveWaterDeviceOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.MoveWaterDeviceOrderVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MoveWaterDeviceOrderServiceImpl implements MoveWaterDeviceOrderService {

    @Resource
    private MoveWaterDeviceOrderMapper moveWaterDeviceOrderMapper;
    @Resource
    private MoveWaterDeviceOrderHangUpLogMapper moveWaterDeviceOrderHangUpLogMapper;
    @Resource
    private UserFeign userFeign;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private WaterFeign waterFeign;
    @Resource
    private UserCache userCache;
    @Resource
    private ServiceEngineerChangeRecordMapper serviceEngineerChangeRecordMapper;
    @Resource
    private WorkRepairOrderMapper workRepairOrderMapper;
    @Resource
    private WorkOrderBackMapper workOrderBackMapper;

    @Override
    public List<MoveWaterDeviceOrderVO> getWaitDisposeList(Integer engineerId, Boolean sort, Integer serviceType, Double longitude, Double latitude) {
        List<MoveWaterDeviceOrderDTO> list = moveWaterDeviceOrderMapper.getWaitDisposeListByEngineerId(engineerId, serviceType);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        List<MoveWaterDeviceOrderVO> vos = new ArrayList<>();
        for (MoveWaterDeviceOrderDTO moveWaterDeviceOrderDTO : list) {
            MoveWaterDeviceOrderVO vo = new MoveWaterDeviceOrderVO();
            MoveWaterDeviceOrderVOInfoPackage(vo, moveWaterDeviceOrderDTO);
            if (moveWaterDeviceOrderDTO.getServiceType().equals("移出拆机")) {
                vo.setServiceAddress(moveWaterDeviceOrderDTO.getOrigProvince() + moveWaterDeviceOrderDTO.getOrigCity() + moveWaterDeviceOrderDTO.getOrigRegion() + moveWaterDeviceOrderDTO.getOrigAddress());
                vo.setLatitude(moveWaterDeviceOrderDTO.getOrigLatitude());
                vo.setLongitude(moveWaterDeviceOrderDTO.getOrigLongitude());
                vo.setServiceStartTime(moveWaterDeviceOrderDTO.getStartDismantleTime());
                vo.setServiceEndTime(moveWaterDeviceOrderDTO.getEndDismantleTime());
                vo.setDisplayServiceTime(moveWaterDeviceOrderDTO.getDisplayDismantleTime());
                vo.setMayDismantleServiceButton(true); //拆机服务设为可点
            } else if (moveWaterDeviceOrderDTO.getServiceType().equals("移入安装")) {
                vo.setServiceAddress(moveWaterDeviceOrderDTO.getDestProvince() + moveWaterDeviceOrderDTO.getDestCity() + moveWaterDeviceOrderDTO.getDestRegion() + moveWaterDeviceOrderDTO.getDestAddress());
                vo.setLatitude(moveWaterDeviceOrderDTO.getDestLatitude());
                vo.setLongitude(moveWaterDeviceOrderDTO.getDestLongitude());
                vo.setServiceStartTime(moveWaterDeviceOrderDTO.getStartInstallTime());
                vo.setServiceEndTime(moveWaterDeviceOrderDTO.getStartInstallTime());
                vo.setDisplayServiceTime(moveWaterDeviceOrderDTO.getDisplayInstallTime());
                if (moveWaterDeviceOrderDTO.getStatus() == MoveWaterDeviceOrderStatusEnum.WAIT_INSTALL.value) {
                    vo.setMayInstallServiceButton(true); //移入安装设为可点
                } else {
                    vo.setMayWaitDismantleServiceButton(true); //等待拆机服务
                }
            } else {
                throw new YimaoException("数据查询异常！");
            }
            Double distance = null;
            if (vo.getLatitude() != null && vo.getLongitude() != null) {
                //计算安装工所在地到服务地址的距离
                distance = GetLatAndLngUtil.getDistance(longitude, latitude, vo.getLongitude(), vo.getLatitude());
            } else {
                throw new YimaoException("服务地址获取异常。");
            }
            vo.setDistance(distance);
            vos.add(vo);
        }
        sort(sort, vos);
        return vos;
    }

    private void MoveWaterDeviceOrderVOInfoPackage(MoveWaterDeviceOrderVO vo, MoveWaterDeviceOrderDTO moveWaterDeviceOrderDTO) {
        vo.setId(moveWaterDeviceOrderDTO.getId());
        vo.setDeviceUserName(moveWaterDeviceOrderDTO.getDeviceUserName());
        vo.setDeviceUserPhone(moveWaterDeviceOrderDTO.getDeviceUserPhone());
        vo.setDeviceModel(moveWaterDeviceOrderDTO.getDeviceModel());
        vo.setServiceType(moveWaterDeviceOrderDTO.getServiceType());
        vo.setDistributorName(moveWaterDeviceOrderDTO.getDistributorName());
        vo.setDistributorPhone(moveWaterDeviceOrderDTO.getDistributorPhone());
        vo.setCreateTime(moveWaterDeviceOrderDTO.getCreateTime());
    }

    @Override
    public List<MoveWaterDeviceOrderVO> getDisposeList(Integer engineerId, Boolean sort, Double longitude, Double latitude) {
        List<MoveWaterDeviceOrderDTO> list = moveWaterDeviceOrderMapper.getDisposeListByEngineerId(engineerId);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        List<MoveWaterDeviceOrderVO> vos = new ArrayList<>();
        for (MoveWaterDeviceOrderDTO moveWaterDeviceOrderDTO : list) {
            MoveWaterDeviceOrderVO vo = new MoveWaterDeviceOrderVO();
            MoveWaterDeviceOrderVOInfoPackage(vo, moveWaterDeviceOrderDTO);
            if (moveWaterDeviceOrderDTO.getServiceType().equals("移出拆机")) {
                vo.setServiceAddress(moveWaterDeviceOrderDTO.getOrigProvince() + moveWaterDeviceOrderDTO.getOrigCity() + moveWaterDeviceOrderDTO.getOrigRegion() + moveWaterDeviceOrderDTO.getOrigAddress());
                vo.setLatitude(moveWaterDeviceOrderDTO.getOrigLatitude());
                vo.setLongitude(moveWaterDeviceOrderDTO.getOrigLongitude());
                vo.setServiceStartTime(moveWaterDeviceOrderDTO.getStartDismantleTime());
                vo.setServiceEndTime(moveWaterDeviceOrderDTO.getEndDismantleTime());
                vo.setDisplayServiceTime(moveWaterDeviceOrderDTO.getDisplayDismantleTime());
                vo.setMayContinueDismantleServiceButton(true); //继续拆机服务设为可点
            } else if (moveWaterDeviceOrderDTO.getServiceType().equals("移入安装")) {
                vo.setServiceAddress(moveWaterDeviceOrderDTO.getDestProvince() + moveWaterDeviceOrderDTO.getDestCity() + moveWaterDeviceOrderDTO.getDestRegion() + moveWaterDeviceOrderDTO.getDestAddress());
                vo.setLatitude(moveWaterDeviceOrderDTO.getDestLatitude());
                vo.setLongitude(moveWaterDeviceOrderDTO.getDestLongitude());
                vo.setServiceStartTime(moveWaterDeviceOrderDTO.getStartInstallTime());
                vo.setServiceEndTime(moveWaterDeviceOrderDTO.getStartInstallTime());
                vo.setDisplayServiceTime(moveWaterDeviceOrderDTO.getDisplayInstallTime());
                if (moveWaterDeviceOrderDTO.getInstallEngineerId().intValue() == engineerId) {
                    vo.setMayContinueInstallServiceButton(true); //继续移入安装按钮可点
                } else {
                    vo.setMayWaitInstallServiceButton(true); //等待移入按钮可点
                }
            } else {
                throw new YimaoException("数据查询异常！");
            }
            Double distance = null;
            if (vo.getLatitude() != null && vo.getLongitude() != null) {
                //计算安装工所在地到服务地址的距离
                distance = GetLatAndLngUtil.getDistance(longitude, latitude, vo.getLongitude(), vo.getLatitude());
            } else {
                throw new YimaoException("服务地址获取异常。");
            }
            vo.setDistance(distance);
            vos.add(vo);
        }
        sort(sort, vos);
        return vos;
    }

    private void sort(Boolean sort, List<MoveWaterDeviceOrderVO> vos) {
        if (sort != null && sort) {
            //根据服务时间排序
            Collections.sort(vos, new Comparator<MoveWaterDeviceOrderVO>() {
                @Override
                public int compare(MoveWaterDeviceOrderVO o1, MoveWaterDeviceOrderVO o2) {
                    return o1.getServiceStartTime().getTime() >= o2.getServiceStartTime().getTime() ? 1 : -1;
                }
            });
        } else {
            //根据距离排序
            Collections.sort(vos, new Comparator<MoveWaterDeviceOrderVO>() {
                @Override
                public int compare(MoveWaterDeviceOrderVO o1, MoveWaterDeviceOrderVO o2) {
                    return o1.getDistance() >= o2.getDistance() ? 1 : -1;
                }
            });
        }
    }

    @Override
    public List<MoveWaterDeviceOrderVO> getPendingList(Integer engineerId, Boolean sort, Double longitude, Double latitude) {
        List<MoveWaterDeviceOrderDTO> list = moveWaterDeviceOrderMapper.getPendingListByEngineerId(engineerId);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        List<MoveWaterDeviceOrderVO> vos = new ArrayList<>();
        for (MoveWaterDeviceOrderDTO moveWaterDeviceOrderDTO : list) {
            MoveWaterDeviceOrderVO vo = new MoveWaterDeviceOrderVO();
            MoveWaterDeviceOrderVOInfoPackage(vo, moveWaterDeviceOrderDTO);
            if (moveWaterDeviceOrderDTO.getServiceType().equals("移出拆机")) {
                vo.setServiceAddress(moveWaterDeviceOrderDTO.getOrigProvince() + moveWaterDeviceOrderDTO.getOrigCity() + moveWaterDeviceOrderDTO.getOrigRegion() + moveWaterDeviceOrderDTO.getOrigAddress());
                vo.setLatitude(moveWaterDeviceOrderDTO.getOrigLatitude());
                vo.setLongitude(moveWaterDeviceOrderDTO.getOrigLongitude());
                vo.setServiceStartTime(moveWaterDeviceOrderDTO.getStartDismantleTime());
                vo.setServiceEndTime(moveWaterDeviceOrderDTO.getEndDismantleTime());
                vo.setDisplayServiceTime(moveWaterDeviceOrderDTO.getDisplayDismantleTime());
                vo.setHangUpCause(moveWaterDeviceOrderDTO.getDismantleHangUpCause());
                vo.setMayDismantleServiceButton(true); //拆机服务设为可点
            } else if (moveWaterDeviceOrderDTO.getServiceType().equals("移入安装")) {
                vo.setServiceAddress(moveWaterDeviceOrderDTO.getDestProvince() + moveWaterDeviceOrderDTO.getDestCity() + moveWaterDeviceOrderDTO.getDestRegion() + moveWaterDeviceOrderDTO.getDestAddress());
                vo.setLatitude(moveWaterDeviceOrderDTO.getDestLatitude());
                vo.setLongitude(moveWaterDeviceOrderDTO.getDestLongitude());
                vo.setServiceStartTime(moveWaterDeviceOrderDTO.getStartInstallTime());
                vo.setServiceEndTime(moveWaterDeviceOrderDTO.getStartInstallTime());
                vo.setDisplayServiceTime(moveWaterDeviceOrderDTO.getDisplayInstallTime());
                vo.setHangUpCause(moveWaterDeviceOrderDTO.getInstallHangUpCause());
                if (moveWaterDeviceOrderDTO.getStatus() == MoveWaterDeviceOrderStatusEnum.WAIT_INSTALL.value) {
                    vo.setMayInstallServiceButton(true); //移入安装设为可点
                } else {
                    vo.setMayWaitDismantleServiceButton(true); //等待拆机服务
                }
            } else {
                throw new YimaoException("数据查询异常！");
            }
            Double distance = null;
            if (vo.getLatitude() != null && vo.getLongitude() != null) {
                //计算安装工所在地到服务地址的距离
                distance = GetLatAndLngUtil.getDistance(longitude, latitude, vo.getLongitude(), vo.getLatitude());
            } else {
                throw new YimaoException("服务地址获取异常。");
            }
            vo.setDistance(distance);
            vos.add(vo);
        }
        sort(sort, vos);
        return vos;
    }

    @Override
    public PageVO<MoveWaterDeviceOrderVO> getCompleteList(Integer engineerId, Boolean sort, Integer pageNum, Integer pageSize) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<MoveWaterDeviceOrderVO> page = moveWaterDeviceOrderMapper.getCompleteListByEngineerId(engineerId, sort);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public MoveWaterDeviceOrderVO waitInstall(String id) {
        MoveWaterDeviceOrder moveWaterDeviceOrder = moveWaterDeviceOrderMapper.selectByPrimaryKey(id);
        if (moveWaterDeviceOrder == null) {
            throw new BadRequestException("移机工单不存在！");
        }
        MoveWaterDeviceOrderVO vo = new MoveWaterDeviceOrderVO();
        vo.setInstallEngineerName(moveWaterDeviceOrder.getInstallEngineerName());
        vo.setInstallEngineerPhone(moveWaterDeviceOrder.getInstallEngineerPhone());
        vo.setInstallEngineerStationName(moveWaterDeviceOrder.getInstallEngineerStationName());
        vo.setServiceStartTime(moveWaterDeviceOrder.getStartInstallTime());
        vo.setServiceEndTime(moveWaterDeviceOrder.getEndInstallTime());
        vo.setDisplayServiceTime(getDisplayServiceTime(moveWaterDeviceOrder.getStartInstallTime(), moveWaterDeviceOrder.getEndInstallTime()));
        return vo;
    }

    @Override
    public MoveWaterDeviceOrderVO waitDismantle(String id) {
        MoveWaterDeviceOrder moveWaterDeviceOrder = moveWaterDeviceOrderMapper.selectByPrimaryKey(id);
        if (moveWaterDeviceOrder == null) {
            throw new BadRequestException("移机工单不存在！");
        }
        MoveWaterDeviceOrderVO vo = new MoveWaterDeviceOrderVO();
        vo.setDismantleEngineerName(moveWaterDeviceOrder.getDismantleEngineerName());
        vo.setDismantleEngineerPhone(moveWaterDeviceOrder.getDismantleEngineerPhone());
        vo.setDismantleEngineerStationName(moveWaterDeviceOrder.getDismantleEngineerStationName());
        vo.setServiceStartTime(moveWaterDeviceOrder.getStartDismantleTime());
        vo.setServiceEndTime(moveWaterDeviceOrder.getEndDismantleTime());
        vo.setDisplayServiceTime(getDisplayServiceTime(moveWaterDeviceOrder.getStartDismantleTime(),moveWaterDeviceOrder.getEndDismantleTime()));
        return vo;
    }

    private String getDisplayServiceTime(Date startTime, Date endTime) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日 HH点");
        String startTimeStr = sf.format(startTime);
        SimpleDateFormat sf2 = new SimpleDateFormat("HH点");
        String endTimeStr = sf2.format(endTime);
        return startTimeStr + " -- " + endTimeStr;
    }

    @Override
    public MoveWaterDeviceOrderVO continueDismantle(String id) {
        MoveWaterDeviceOrder moveWaterDeviceOrder = moveWaterDeviceOrderMapper.selectByPrimaryKey(id);
        if (moveWaterDeviceOrder == null) {
            throw new BadRequestException("移机工单不存在！");
        }
        if (moveWaterDeviceOrder.getStatus() != MoveWaterDeviceOrderStatusEnum.DISMANTLE.value) {
            throw new BadRequestException("移机状态异常，不允许进行继续拆机操作！");
        }
        //回显数据
        return this.getDismantleMoveWaterDeviceOrderVO(moveWaterDeviceOrder);
    }

    @Override
    public MoveWaterDeviceOrderVO dismantle(String id) {
        MoveWaterDeviceOrder moveWaterDeviceOrder = moveWaterDeviceOrderMapper.selectByPrimaryKey(id);
        if (moveWaterDeviceOrder == null) {
            throw new BadRequestException("移机工单不存在！");
        }
        if (moveWaterDeviceOrder.getStatus() != MoveWaterDeviceOrderStatusEnum.WAIT_DISMANTLE.value) {
            throw new BadRequestException("移机状态异常，不允许进行拆机操作！");
        }

        //===================================== 回显数据 =====================================================
        MoveWaterDeviceOrderVO vo = this.getDismantleMoveWaterDeviceOrderVO(moveWaterDeviceOrder);

        //===================================== 修改订单状态为拆机中 ===========================================
        MoveWaterDeviceOrder update = new MoveWaterDeviceOrder();
        update.setId(id);
        update.setDismantleHangUpStatus(0); //将挂单状态改为不挂单
        update.setStatus(MoveWaterDeviceOrderStatusEnum.DISMANTLE.value);
        update.setRealStartDismantleTime(new Date());
        moveWaterDeviceOrderMapper.updateByPrimaryKeySelective(update);
        return vo;
    }

    /**
     * 拆机服务中页面数据
     *
     * @param moveWaterDeviceOrder
     * @return
     */
    private MoveWaterDeviceOrderVO getDismantleMoveWaterDeviceOrderVO(MoveWaterDeviceOrder moveWaterDeviceOrder) {
        //查询设备信息
        WaterDeviceDTO waterDevice = this.getWaterDeviceByDeviceId(moveWaterDeviceOrder.getDeviceId());
        MoveWaterDeviceOrderVO vo = new MoveWaterDeviceOrderVO();
        vo.setId(moveWaterDeviceOrder.getId());
        vo.setDismantleEngineerName(moveWaterDeviceOrder.getDismantleEngineerName());
        vo.setDismantleEngineerPhone(moveWaterDeviceOrder.getDismantleEngineerPhone());
        vo.setDismantleEngineerStationName(moveWaterDeviceOrder.getDismantleEngineerStationName());
        vo.setDeviceUserName(moveWaterDeviceOrder.getDeviceUserName());
        vo.setDeviceUserPhone(moveWaterDeviceOrder.getDeviceUserPhone());
        vo.setDeviceModel(moveWaterDeviceOrder.getDeviceModel());
        vo.setSn(moveWaterDeviceOrder.getSn());
        vo.setLogisticsCode(waterDevice.getLogisticsCode());
        vo.setIccid(waterDevice.getIccid());
        vo.setCurrentCostName(waterDevice.getCurrentCostName());
        vo.setMoney(waterDevice.getMoney());
        vo.setServiceAddress(moveWaterDeviceOrder.getOrigProvince() + moveWaterDeviceOrder.getOrigCity() + moveWaterDeviceOrder.getOrigRegion() + moveWaterDeviceOrder.getOrigAddress());
        vo.setDistributorName(moveWaterDeviceOrder.getDistributorName());
        vo.setDistributorPhone(moveWaterDeviceOrder.getDistributorPhone());
        return vo;
    }

    /**
     * 根据设备id查询设备信息
     *
     * @param deviceId
     * @return
     */
    private WaterDeviceDTO getWaterDeviceByDeviceId(Integer deviceId) {
        WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceById(deviceId);
        if (waterDevice == null) {
            throw new YimaoException("设备信息不存在！");
        }
        return waterDevice;
    }

    private StationDTO getEngineerStation(Integer engineerId) {
        //查询安装工信息
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (engineer == null) {
            throw new YimaoException("安装工信息不存在！");
        }
        //查询安装工所属门店
        StationDTO station = systemFeign.getStationById(engineer.getStationId());
        if (station == null) {
            throw new YimaoException("安装工所属门店不存在！");
        }
        return station;
    }

    @Override
    public Map<String, Integer> completeDismantle(String id, Integer engineerId) {
        MoveWaterDeviceOrder moveWaterDeviceOrder = moveWaterDeviceOrderMapper.selectByPrimaryKey(id);
        if (moveWaterDeviceOrder == null) {
            throw new BadRequestException("移机工单不存在！");
        }
        if (moveWaterDeviceOrder.getStatus() != MoveWaterDeviceOrderStatusEnum.DISMANTLE.value) {
            throw new BadRequestException("移机状态异常，不允许进行完成拆机操作！");
        }
        //将移机工单状态改为待移机
        MoveWaterDeviceOrder update = new MoveWaterDeviceOrder();
        update.setId(id);
        update.setRealEndDismantleTime(new Date());
        update.setStatus(MoveWaterDeviceOrderStatusEnum.WAIT_INSTALL.value);
        moveWaterDeviceOrderMapper.updateByPrimaryKeySelective(update);

        //返回给客户端做业务判断 1-弹出继续移机服务的对话框 2-跳转至处理中
        Map<String, Integer> result = new HashMap<>();
        //判断拆机移机是否同一个人
        if (moveWaterDeviceOrder.getInstallEngineerId().intValue() == engineerId) {
            //是同一个人
            result.put("type", 1);
        } else {
            //不是同一个人
            result.put("type", 2);
        }
        return result;
    }

    @Override
    public MoveWaterDeviceOrderVO install(String id) {
        MoveWaterDeviceOrder moveWaterDeviceOrder = moveWaterDeviceOrderMapper.selectByPrimaryKey(id);
        if (moveWaterDeviceOrder == null) {
            throw new BadRequestException("移机工单不存在！");
        }
        if (moveWaterDeviceOrder.getStatus() != MoveWaterDeviceOrderStatusEnum.WAIT_INSTALL.value) {
            throw new BadRequestException("移机状态异常，不允许进行移入安装操作！");
        }
        //===================================== 回显数据 =====================================================
        MoveWaterDeviceOrderVO vo = this.getInstallMoveWaterDeviceOrderVO(moveWaterDeviceOrder);

        //===================================== 修改订单状态为拆机中 ===========================================
        MoveWaterDeviceOrder update = new MoveWaterDeviceOrder();
        update.setId(id);
        update.setInstallHangUpStatus(0);
        update.setStatus(MoveWaterDeviceOrderStatusEnum.INSTALL.value);
        update.setRealStartInstallTime(new Date());
        moveWaterDeviceOrderMapper.updateByPrimaryKeySelective(update);
        return vo;
    }

    /**
     * 移入安装中页面数据
     *
     * @param moveWaterDeviceOrder
     * @return
     */
    private MoveWaterDeviceOrderVO getInstallMoveWaterDeviceOrderVO(MoveWaterDeviceOrder moveWaterDeviceOrder) {
        //查询设备信息
        WaterDeviceDTO waterDevice = this.getWaterDeviceByDeviceId(moveWaterDeviceOrder.getDeviceId());
        MoveWaterDeviceOrderVO vo = new MoveWaterDeviceOrderVO();
        vo.setId(moveWaterDeviceOrder.getId());
        vo.setInstallEngineerName(moveWaterDeviceOrder.getInstallEngineerName());
        vo.setInstallEngineerPhone(moveWaterDeviceOrder.getInstallEngineerPhone());
        vo.setInstallEngineerStationName(moveWaterDeviceOrder.getInstallEngineerStationName());
        vo.setDeviceUserName(moveWaterDeviceOrder.getDeviceUserName());
        vo.setDeviceUserPhone(moveWaterDeviceOrder.getDeviceUserPhone());
        vo.setDeviceModel(moveWaterDeviceOrder.getDeviceModel());
        vo.setSn(moveWaterDeviceOrder.getSn());
        vo.setLogisticsCode(waterDevice.getLogisticsCode());
        vo.setIccid(waterDevice.getIccid());
        vo.setCurrentCostName(waterDevice.getCurrentCostName());
        vo.setMoney(waterDevice.getMoney());
        vo.setOrigAddress(moveWaterDeviceOrder.getOrigProvince() + moveWaterDeviceOrder.getOrigCity() + moveWaterDeviceOrder.getOrigRegion() + moveWaterDeviceOrder.getOrigAddress());
        vo.setDestAddress(moveWaterDeviceOrder.getDestProvince() + moveWaterDeviceOrder.getDestCity() + moveWaterDeviceOrder.getDestRegion() + moveWaterDeviceOrder.getDestAddress());
        vo.setDistributorName(moveWaterDeviceOrder.getDistributorName());
        vo.setDistributorPhone(moveWaterDeviceOrder.getDistributorPhone());
        return vo;
    }

    @Override
    public MoveWaterDeviceOrderVO continueInstall(String id) {
        MoveWaterDeviceOrder moveWaterDeviceOrder = moveWaterDeviceOrderMapper.selectByPrimaryKey(id);
        if (moveWaterDeviceOrder == null) {
            throw new BadRequestException("移机工单不存在！");
        }
        if (moveWaterDeviceOrder.getStatus() != MoveWaterDeviceOrderStatusEnum.INSTALL.value) {
            throw new BadRequestException("移机状态异常，不允许进行继续拆机操作！");
        }
        //回显数据
        return this.getInstallMoveWaterDeviceOrderVO(moveWaterDeviceOrder);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void complete(String id) {
        MoveWaterDeviceOrder moveWaterDeviceOrder = moveWaterDeviceOrderMapper.selectByPrimaryKey(id);
        if (moveWaterDeviceOrder == null) {
            throw new BadRequestException("移机工单不存在！");
        }
        if (moveWaterDeviceOrder.getStatus() != MoveWaterDeviceOrderStatusEnum.INSTALL.value) {
            throw new BadRequestException("移机状态异常，不允许进行提交完成操作！");
        }
        //将移机工单状态改为待移机
        MoveWaterDeviceOrder update = new MoveWaterDeviceOrder();
        update.setId(id);
        update.setCompleteTime(new Date());
        update.setStatus(MoveWaterDeviceOrderStatusEnum.COMPLETED.value);
        moveWaterDeviceOrderMapper.updateByPrimaryKeySelective(update);

        //============================ 设备信息更变 ==============================
        WaterDeviceDTO waterDevice = waterFeign.getBySnCode(moveWaterDeviceOrder.getSn());
        if (waterDevice == null) {
            throw new YimaoException("设备信息不存在！");
        }
        //移入地安装工信息
        EngineerDTO engineer = userFeign.getEngineerById(moveWaterDeviceOrder.getInstallEngineerId());
        WaterDeviceDTO updateWaterDevice = new WaterDeviceDTO();
        updateWaterDevice.setId(waterDevice.getId());
        updateWaterDevice.setEngineerId(moveWaterDeviceOrder.getInstallEngineerId());
        updateWaterDevice.setEngineerName(moveWaterDeviceOrder.getInstallEngineerName());
        updateWaterDevice.setEngineerPhone(moveWaterDeviceOrder.getInstallEngineerPhone());
        updateWaterDevice.setProvince(moveWaterDeviceOrder.getDestProvince());
        updateWaterDevice.setCity(moveWaterDeviceOrder.getDestCity());
        updateWaterDevice.setRegion(moveWaterDeviceOrder.getDestRegion());
        updateWaterDevice.setAddress(moveWaterDeviceOrder.getDestAddress());
        updateWaterDevice.setOldCustomerId(engineer.getOldId() == null ? "" : engineer.getOldId());
        waterFeign.updateDevice(updateWaterDevice);

        //========================= 设备地址更变记录 =================================
        WaterDevicePlaceChangeRecordDTO waterDevicePlaceChangeRecord = new WaterDevicePlaceChangeRecordDTO();
        waterDevicePlaceChangeRecord.setSn(moveWaterDeviceOrder.getSn());
        waterDevicePlaceChangeRecord.setOldPlace(waterDevice.getProvince() + waterDevice.getCity() + waterDevice.getRegion() + waterDevice.getAddress());
        waterDevicePlaceChangeRecord.setNewPlace(moveWaterDeviceOrder.getDestProvince() + moveWaterDeviceOrder.getDestCity() + moveWaterDeviceOrder.getDestRegion() + moveWaterDeviceOrder.getDestAddress());
        waterDevicePlaceChangeRecord.setCreateTime(new Date());
        waterDevicePlaceChangeRecord.setType(WaterDevicePlaceChangeRecordTypeEnum.MOVE_WATER_DEVICE.value);
        waterFeign.saveWaterDevicePlaceChangeRecord(waterDevicePlaceChangeRecord);
    }

    @Override
    public MoveWaterDeviceOrderVO appGetMoveWaterDeviceDetails(String id) {
        MoveWaterDeviceOrder moveWaterDeviceOrder = moveWaterDeviceOrderMapper.selectByPrimaryKey(id);
        if (moveWaterDeviceOrder == null) {
            throw new BadRequestException("移机工单不存在！");
        }
        if (moveWaterDeviceOrder.getStatus() != MoveWaterDeviceOrderStatusEnum.COMPLETED.value) {
            throw new BadRequestException("移机状态异常，不允许进行查看工单详情操作！");
        }
        //查询设备信息
        WaterDeviceDTO waterDevice = waterFeign.getBySnCode(moveWaterDeviceOrder.getSn());
        if (waterDevice == null) {
            throw new YimaoException("设备信息不存在！");
        }
        MoveWaterDeviceOrderVO vo = new MoveWaterDeviceOrderVO();
        vo.setId(moveWaterDeviceOrder.getId());
        vo.setDismantleEngineerName(moveWaterDeviceOrder.getDismantleEngineerName());
        vo.setDismantleEngineerPhone(moveWaterDeviceOrder.getDismantleEngineerPhone());
        vo.setDismantleEngineerStationName(moveWaterDeviceOrder.getDismantleEngineerStationName());
        vo.setInstallEngineerName(moveWaterDeviceOrder.getInstallEngineerName());
        vo.setInstallEngineerPhone(moveWaterDeviceOrder.getInstallEngineerPhone());
        vo.setInstallEngineerStationName(moveWaterDeviceOrder.getInstallEngineerStationName());
        vo.setDeviceUserName(moveWaterDeviceOrder.getDeviceUserName());
        vo.setDeviceUserPhone(moveWaterDeviceOrder.getDeviceUserPhone());
        vo.setDeviceModel(moveWaterDeviceOrder.getDeviceModel());
        vo.setSn(moveWaterDeviceOrder.getSn());
        vo.setLogisticsCode(waterDevice.getLogisticsCode());
        vo.setIccid(waterDevice.getIccid());
        vo.setCurrentCostName(waterDevice.getCurrentCostName());
        vo.setMoney(waterDevice.getMoney());
        vo.setOrigAddress(moveWaterDeviceOrder.getOrigProvince() + moveWaterDeviceOrder.getOrigCity() + moveWaterDeviceOrder.getOrigRegion() + moveWaterDeviceOrder.getOrigAddress());
        vo.setDestAddress(moveWaterDeviceOrder.getDestProvince() + moveWaterDeviceOrder.getDestCity() + moveWaterDeviceOrder.getDestRegion() + moveWaterDeviceOrder.getDestAddress());
        vo.setDistributorName(moveWaterDeviceOrder.getDistributorName());
        vo.setDistributorPhone(moveWaterDeviceOrder.getDistributorPhone());
        vo.setCompleteTime(moveWaterDeviceOrder.getCompleteTime());
        //查询是否有拆机挂单记录
        MoveWaterDeviceOrderHangUpLog queryDismantleHangUpLog = new MoveWaterDeviceOrderHangUpLog();
        queryDismantleHangUpLog.setMoveWaterDeviceOrderId(id);
        queryDismantleHangUpLog.setType(1);
        MoveWaterDeviceOrderHangUpLog dismantleHangUpLog = moveWaterDeviceOrderHangUpLogMapper.selectOne(queryDismantleHangUpLog);
        if (dismantleHangUpLog != null) {
            //有过拆机挂单记录
            vo.setDismantleHangUpCause(dismantleHangUpLog.getCause());
            vo.setStartDismantleTime(dismantleHangUpLog.getDestStartTime());
            vo.setEndDismantleTime(dismantleHangUpLog.getDestEndTime());
            vo.setDisplayDismantleTime(getDisplayServiceTime(dismantleHangUpLog.getDestStartTime(),dismantleHangUpLog.getDestEndTime()));
        }
        //查询是否有装机挂单记录
        MoveWaterDeviceOrderHangUpLog queryInstallHangUpLog = new MoveWaterDeviceOrderHangUpLog();
        queryInstallHangUpLog.setMoveWaterDeviceOrderId(id);
        queryInstallHangUpLog.setType(2);
        MoveWaterDeviceOrderHangUpLog installHangUpLog = moveWaterDeviceOrderHangUpLogMapper.selectOne(queryInstallHangUpLog);
        if (installHangUpLog != null) {
            //有过移入挂单记录
            vo.setInstallHangUpCause(installHangUpLog.getCause());
            vo.setStartInstallTime(installHangUpLog.getDestStartTime());
            vo.setEndInstallTime(installHangUpLog.getDestEndTime());
            vo.setDisplayInstallTime(getDisplayServiceTime(installHangUpLog.getDestStartTime(),installHangUpLog.getDestEndTime()));
        }

        return vo;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void hangUp(String id, Integer type, String cause, Date startTime, Date endTime, Integer engineerId) {
        if (type == null) {
            throw new BadRequestException("请指明拆机挂单或是移入挂单。");
        }
        MoveWaterDeviceOrder moveWaterDeviceOrder = moveWaterDeviceOrderMapper.selectByPrimaryKey(id);
        if (moveWaterDeviceOrder == null) {
            throw new BadRequestException("移机工单不存在！");
        }
        MoveWaterDeviceOrderHangUpLog query = new MoveWaterDeviceOrderHangUpLog();
        query.setMoveWaterDeviceOrderId(id);
        query.setType(type);
        MoveWaterDeviceOrderHangUpLog existMoveWaterDeviceOrderHangUpLog = moveWaterDeviceOrderHangUpLogMapper.selectOne(query);
        if (existMoveWaterDeviceOrderHangUpLog != null) {
            throw new BadRequestException("该移机工单已进行过一次" + (type == 1 ? "拆机" : "移入") + "挂单操作，不能再次进行该操作！");
        }

        //挂单记录
        MoveWaterDeviceOrderHangUpLog moveWaterDeviceOrderHangUpLog = new MoveWaterDeviceOrderHangUpLog();
        MoveWaterDeviceOrder update = new MoveWaterDeviceOrder();
        update.setId(id);
        if (type == 1) {
            //拆机挂单
            if (moveWaterDeviceOrder.getDismantleEngineerId().intValue() != engineerId) {
                throw new BadRequestException("操作人并非拆机安装工，不予操作！");
            }
            if (moveWaterDeviceOrder.getStatus() != MoveWaterDeviceOrderStatusEnum.WAIT_DISMANTLE.value) {
                throw new BadRequestException("移机工单状态异常，不允许进行拆机挂单操作！");
            }
            if (moveWaterDeviceOrder.getDismantleHangUpStatus() == 1) {
                throw new YimaoException("同一个移机工单无法进行两次拆机挂单！");
            }
            update.setDismantleHangUpStatus(1);//拆机挂单状态设为已挂单
            update.setDismantleHangUpCause(cause);
            update.setStartDismantleTime(startTime);
            update.setEndDismantleTime(endTime);
            moveWaterDeviceOrderHangUpLog.setType(1);//拆机挂单
            moveWaterDeviceOrderHangUpLog.setOrigStartTime(moveWaterDeviceOrder.getStartDismantleTime());
            moveWaterDeviceOrderHangUpLog.setOrigEndTime(moveWaterDeviceOrder.getEndDismantleTime());
            moveWaterDeviceOrderHangUpLog.setEngineerId(engineerId);
            moveWaterDeviceOrderHangUpLog.setEngineerName(moveWaterDeviceOrder.getDismantleEngineerName());
        } else if (type == 2) {
            if (moveWaterDeviceOrder.getInstallEngineerId().intValue() != engineerId) {
                throw new BadRequestException("操作人并非移入安装工，不予操作！");
            }
            //移入挂单
            if (moveWaterDeviceOrder.getStatus() == MoveWaterDeviceOrderStatusEnum.INSTALL.value || moveWaterDeviceOrder.getStatus() == MoveWaterDeviceOrderStatusEnum.COMPLETED.value) {
                throw new BadRequestException("移机工单状态异常，不允许进行移入挂单操作！");
            }
            if (moveWaterDeviceOrder.getInstallHangUpStatus() == 1) {
                throw new YimaoException("同一个移机工单无法进行两次移入挂单！");
            }
            update.setInstallHangUpStatus(1);//移入挂单状态设为已挂单
            update.setInstallHangUpCause(cause);
            update.setStartInstallTime(startTime);
            update.setEndInstallTime(endTime);
            moveWaterDeviceOrderHangUpLog.setType(2);//移入挂单
            moveWaterDeviceOrderHangUpLog.setOrigStartTime(moveWaterDeviceOrder.getStartInstallTime());
            moveWaterDeviceOrderHangUpLog.setOrigEndTime(moveWaterDeviceOrder.getEndInstallTime());
            moveWaterDeviceOrderHangUpLog.setEngineerId(engineerId);
            moveWaterDeviceOrderHangUpLog.setEngineerName(moveWaterDeviceOrder.getDismantleEngineerName());
        } else {
            throw new BadRequestException("type传参有误！");
        }
        moveWaterDeviceOrderMapper.updateByPrimaryKeySelective(update);
        //保存挂单记录
        moveWaterDeviceOrderHangUpLog.setMoveWaterDeviceOrderId(id);
        moveWaterDeviceOrderHangUpLog.setOperationTime(new Date());
        moveWaterDeviceOrderHangUpLog.setDestStartTime(startTime);
        moveWaterDeviceOrderHangUpLog.setDestEndTime(endTime);
        moveWaterDeviceOrderHangUpLogMapper.insertSelective(moveWaterDeviceOrderHangUpLog);
    }

    @Override
    public void save(MoveWaterDeviceOrderDTO dto) {
        //必传参数校验
        this.checkParam(dto);
        //校验设备是否有未完成的工单
        this.unSolvedOrderCheck(dto.getDeviceId());
        if (dto.getDismantleEngineerId() != null) {
            //拆机服务人员服务站信息
            StationDTO dismantleEngineerStation = getEngineerStation(dto.getDismantleEngineerId());
            dto.setDismantleEngineerStationId(dismantleEngineerStation.getId());
            dto.setDismantleEngineerStationName(dismantleEngineerStation.getName());
        }
        //装机服务人员服务站信息
        StationDTO installEngineerStation = getEngineerStation(dto.getInstallEngineerId());
        dto.setInstallEngineerStationId(installEngineerStation.getId());
        dto.setInstallEngineerStationName(installEngineerStation.getName());
        //计算拆机地址经纬度
        Map<String, Double> map;
        try {
            map = BaiduMapUtil.getLngAndLatByAddress(dto.getOrigProvince() + dto.getOrigCity() + dto.getOrigRegion() + dto.getOrigAddress());
        } catch (Exception e) {
            throw new YimaoException("获取地址经纬度失败");
        }
        if (Objects.isNull(map)) {
            throw new BadRequestException("拆机地址经纬度定位失败！");
        }
        dto.setOrigLatitude(map.get("lat"));
        dto.setOrigLongitude(map.get("lng"));
        //计算装机地址经纬度
        try {
            map = BaiduMapUtil.getLngAndLatByAddress(dto.getDestProvince() + dto.getDestCity() + dto.getDestRegion() + dto.getDestAddress());
        } catch (Exception e) {
            throw new YimaoException("获取地址经纬度失败");
        }
        if (Objects.isNull(map)) {
            throw new BadRequestException("装机地址经纬度定位失败！");
        }
        dto.setDestLatitude(map.get("lat"));
        dto.setDestLongitude(map.get("lng"));
        dto.setId(UUIDUtil.getMoveWaterDeviceOrderId());
        dto.setCreateTime(new Date());
        int count = moveWaterDeviceOrderMapper.insertSelective(new MoveWaterDeviceOrder(dto));
        if (count != 1) {
            throw new YimaoException("移机工单创建失败。");
        }
    }

    //校验创建移机工单是否存在其他未完成工单
    private void unSolvedOrderCheck(Integer deviceId) {
        //校验设备对应的工单是否完成
        Example workOrderExample = new Example(WorkOrder.class);
        Example.Criteria workOrderCriteria = workOrderExample.createCriteria();
        workOrderCriteria.andEqualTo("deviceId", deviceId);
        workOrderCriteria.andNotEqualTo("status", WorkOrderNewStatusEnum.COMPLETED.value);
        int workOrderCount = moveWaterDeviceOrderMapper.selectCountByExample(workOrderExample);

        if (workOrderCount > 0) {
            throw new YimaoException("设备对应的安装工单还未完成。");
        }

        //校验设备是否有未完成的移机工单
        Example moveWaterOrderExample = new Example(MoveWaterDeviceOrder.class);
        Example.Criteria moveWaterCriteria = moveWaterOrderExample.createCriteria();
        moveWaterCriteria.andEqualTo("deviceId", deviceId);
        moveWaterCriteria.andNotEqualTo("status", MoveWaterDeviceOrderStatusEnum.COMPLETED.value);
        int moveWaterOrderCount = moveWaterDeviceOrderMapper.selectCountByExample(moveWaterOrderExample);

        if (moveWaterOrderCount > 0) {
            throw new YimaoException("设备存在其它未完成的移机工单。");
        }

        //校验设备是否有未完成的维修工单
        Example repairOrderExample = new Example(WorkRepairOrder.class);
        Example.Criteria repairOrderCriteria = repairOrderExample.createCriteria();
        repairOrderCriteria.andEqualTo("deviceId", deviceId);
        repairOrderCriteria.andNotEqualTo("status", RepairOrderStatus.SOLVED.value);
        int sameRepairOrderCount = workRepairOrderMapper.selectCountByExample(repairOrderExample);

        if (sameRepairOrderCount > 0) {
            throw new YimaoException("设备存在未完成的维修工单。");
        }

        //校验设备是否有未完成的退机工单
        Example workOrderBackExample = new Example(WorkOrderBack.class);
        Example.Criteria workOrderBackCriteria = workOrderBackExample.createCriteria();
        workOrderBackCriteria.andEqualTo("deviceId", deviceId);
        workOrderBackCriteria.andNotEqualTo("status", WorkOrderBackStatusEnum.COMPLETED.value);
        int workOrderBackCount = workOrderBackMapper.selectCountByExample(workOrderBackExample);

        if (workOrderBackCount > 0) {
            throw new YimaoException("设备存在未完成的退机工单。");
        }

    }

    @Override
    public Map<String, Integer> getMoveWaterDeviceCount(Integer engineerId) {
        Map<String, Integer> map = new HashMap<>();
        //待处理
        List<MoveWaterDeviceOrderDTO> pendingList = moveWaterDeviceOrderMapper.getWaitDisposeListByEngineerId(engineerId, null);
        map.put("pendingWaterDeviceCount", pendingList.size());
        //处理中
        List<MoveWaterDeviceOrderDTO> disposeList = moveWaterDeviceOrderMapper.getDisposeListByEngineerId(engineerId);
        map.put("disposeWaterDeviceCount", disposeList.size());
        //挂单
        List<MoveWaterDeviceOrderDTO> lodgeList = moveWaterDeviceOrderMapper.getPendingListByEngineerId(engineerId);
        map.put("lodgeWaterDeviceCount", lodgeList.size());
        //已完成
        Page<MoveWaterDeviceOrderVO> completeList = moveWaterDeviceOrderMapper.getCompleteListByEngineerId(engineerId, null);
        map.put("completeWaterDeviceCount", completeList.size());
        return map;
    }


    @Override
    public Integer getMoveModelTotalCount(Integer engineerId) {
        //待处理
        int waitCount = moveWaterDeviceOrderMapper.getWaitDisposeListByEngineerId(engineerId, null).size();
        //处理中
        int disposeCount = moveWaterDeviceOrderMapper.getDisposeListByEngineerId(engineerId).size();
        //挂单
        int pendingCount = moveWaterDeviceOrderMapper.getPendingListByEngineerId(engineerId).size();
        return waitCount + disposeCount + pendingCount;
    }

    @Override
    public List<RenewDTO> getMoveWaterDeviceList(String completeTime, Integer engineerId, Integer timeType) {
        return moveWaterDeviceOrderMapper.getMoveWaterDeviceList(completeTime, engineerId, timeType);
    }


    @Override
    public PageVO<MoveWaterDeviceOrderDTO> moveWaterOrderPage(Integer pageNum, Integer pageSize, MoveWaterDeviceOrderQuery query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<MoveWaterDeviceOrderDTO> page = moveWaterDeviceOrderMapper.getPage(query);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public MoveWaterDeviceOrderDTO getMoveWaterDeviceOrderById(String id) {
        MoveWaterDeviceOrder po = moveWaterDeviceOrderMapper.selectByPrimaryKey(id);
        if (po == null) {
            throw new NotFoundException("未找到该条工单数据");
        }
        MoveWaterDeviceOrderDTO dto = new MoveWaterDeviceOrderDTO();
        po.convert(dto);
        WaterDeviceDTO waterDevice = waterFeign.getWaterDeviceById(dto.getDeviceId());
        if (waterDevice == null) {
            throw new YimaoException("设备信息不存在！");
        }
        dto.setWaterDeviceDTO(waterDevice);
        if (Objects.equals(dto.getOrigProvince(), dto.getDestProvince())
                && Objects.equals(dto.getOrigCity(), dto.getDestCity())
                && Objects.equals(dto.getOrigRegion(), dto.getDestRegion())) {
            dto.setIsTransRegional(false);//未跨区
        } else {
            dto.setIsTransRegional(true);//跨区
        }
        return dto;
    }

    private void checkParam(MoveWaterDeviceOrderDTO dto) {
        if (dto.getDeviceId() == null) {
            throw new BadRequestException("设备id不能为空。");
        }
        if (StringUtil.isEmpty(dto.getSn())) {
            throw new BadRequestException("请输入sn码。");
        }
        if (StringUtil.isEmpty(dto.getDeviceModel())) {
            throw new BadRequestException("设备型号不能为空。");
        }
        if (StringUtil.isEmpty(dto.getDeviceScope())) {
            throw new BadRequestException("设备分类不能为空。");
        }
        if (dto.getDeviceUserId() == null) {
            throw new BadRequestException("客户id不能为空。");
        }
        if (StringUtil.isEmpty(dto.getDeviceUserName())) {
            throw new BadRequestException("客户姓名不能为空。");
        }
        if (StringUtil.isEmpty(dto.getDeviceUserPhone())) {
            throw new BadRequestException("客户联系方式不能为空。");
        }
        if (StringUtil.isEmpty(dto.getOrigProvince())) {
            throw new BadRequestException("设备原地址省不能为空。");
        }
        if (StringUtil.isEmpty(dto.getOrigCity())) {
            throw new BadRequestException("设备原地址市不能为空。");
        }
        if (StringUtil.isEmpty(dto.getOrigRegion())) {
            throw new BadRequestException("设备原地址区不能为空。");
        }
        if (StringUtil.isEmpty(dto.getOrigAddress())) {
            throw new BadRequestException("设备原地址不能为空。");
        }
        if (dto.getDistributorId() == null) {
            throw new BadRequestException("经销商id不能为空。");
        }
        if (StringUtil.isEmpty(dto.getDistributorName())) {
            throw new BadRequestException("经销商姓名不能为空。");
        }
        if (StringUtil.isEmpty(dto.getDistributorPhone())) {
            throw new BadRequestException("经销商手机号不能为空。");
        }
        if (dto.getIsClientDismantle() == null) {
            throw new BadRequestException("请指明拆机方式。");
        }
        if (!dto.getIsClientDismantle()) {
            //客户非自主拆机，需安排安装工拆机
            if (dto.getDismantleEngineerId() == null) {
                throw new BadRequestException("拆机安装工id不能为空。");
            }
            if (StringUtil.isEmpty(dto.getDismantleEngineerName())) {
                throw new BadRequestException("拆机安装工姓名不能为空。");
            }
            if (StringUtil.isEmpty(dto.getDismantleEngineerPhone())) {
                throw new BadRequestException("拆机安装工联系方式不能为空。");
            }
            if (dto.getStartDismantleTime() == null && dto.getEndDismantleTime() == null) {
                throw new BadRequestException("请选择拆机服务时间。");
            }
            if (!DateUtil.checkIsSameDate(dto.getStartDismantleTime(), dto.getEndDismantleTime())) {
                throw new BadRequestException("拆机服务开始时间与结束时间必须在同一天。");
            }
            //移机工单状态直接设为待拆机
            dto.setStatus(MoveWaterDeviceOrderStatusEnum.WAIT_DISMANTLE.value);
        } else {
            //移机工单状态设为待移入
            dto.setStatus(MoveWaterDeviceOrderStatusEnum.INSTALL.value);
        }
        if (StringUtil.isEmpty(dto.getDestProvince())) {
            throw new BadRequestException("设备移入地址省不能为空。");
        }
        if (StringUtil.isEmpty(dto.getDestCity())) {
            throw new BadRequestException("设备移入地址市不能为空。");
        }
        if (StringUtil.isEmpty(dto.getDestRegion())) {
            throw new BadRequestException("设备移入地址区不能为空。");
        }
        if (StringUtil.isEmpty(dto.getDestAddress())) {
            throw new BadRequestException("设备移入地址不能为空。");
        }
        if (dto.getInstallEngineerId() == null) {
            throw new BadRequestException("装机安装工id不能为空。");
        }
        if (StringUtil.isEmpty(dto.getInstallEngineerName())) {
            throw new BadRequestException("装机安装工姓名不能为空。");
        }
        if (StringUtil.isEmpty(dto.getInstallEngineerPhone())) {
            throw new BadRequestException("装机安装工联系方式不能为空。");
        }
        if (dto.getStartInstallTime() == null && dto.getEndInstallTime() == null) {
            throw new BadRequestException("请选择移入服务时间。");
        }
        if (!DateUtil.checkIsSameDate(dto.getStartInstallTime(), dto.getEndInstallTime())) {
            throw new BadRequestException("装机服务开始时间与结束时间必须在同一天。");
        }
        if (dto.getSource() == null) {
            throw new BadRequestException("请指明工单来源方式。");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void changeEngineer(String id, Integer engineerId, List<Integer> engineerIds, Integer type, Integer source, String operator) {
        if (type == null) {
            throw new BadRequestException("请指明是更换拆机人员或是安装人员。");
        }
        MoveWaterDeviceOrder moveWaterDeviceOrder = moveWaterDeviceOrderMapper.selectByPrimaryKey(id);
        if (moveWaterDeviceOrder == null) {
            throw new BadRequestException("移机工单不存在！");
        }
        Date now = new Date();
        //更换记录
        ServiceEngineerChangeRecord serviceEngineerChangeRecord = new ServiceEngineerChangeRecord();
        serviceEngineerChangeRecord.setWorkOrderNo(id);

        boolean isServiceArea = false;

        MoveWaterDeviceOrder update = new MoveWaterDeviceOrder();
        update.setId(id);
        if (type == 1) { //更换拆机服务人员
            if (moveWaterDeviceOrder.getStatus() != MoveWaterDeviceOrderStatusEnum.WAIT_DISMANTLE.value) {
                throw new BadRequestException("当前移机工单已被处理，不能更换拆机服务人员！");
            }
            if (!engineerIds.contains(moveWaterDeviceOrder.getDismantleEngineerId())) {
                //站务系统管理员没有操作该移机工单更换拆机服务人员的权限
                throw new YimaoException("您没有操作该移机工单更换拆机服务人员的权限！");
            }
            //查询原拆机安装工
            EngineerDTO origDismantleEngineer = userFeign.getEngineerById(moveWaterDeviceOrder.getDismantleEngineerId());
            if (origDismantleEngineer == null) {
                throw new YimaoException("原拆机安装工信息不存在！");
            }
            EngineerDTO destDismantleEngineer = userFeign.getEngineerById(engineerId);
            if (destDismantleEngineer == null) {
                throw new YimaoException("替代的安装工信息不存在！");
            }

            for (EngineerServiceAreaDTO serviceArea : destDismantleEngineer.getServiceAreaList()) {
                String province = serviceArea.getProvince();
                String city = serviceArea.getCity();
                String region = serviceArea.getRegion();
                if (moveWaterDeviceOrder.getOrigProvince().equals(province) && moveWaterDeviceOrder.getOrigCity().equals(city) && moveWaterDeviceOrder.getOrigRegion().equals(region)) {
                    isServiceArea = true;
                    break;
                }
            }
            if (!isServiceArea) {
                throw new YimaoException("更换安装工服务区域不匹配");
            }
            update.setDismantleEngineerId(engineerId);
            update.setDismantleEngineerName(destDismantleEngineer.getRealName());
            update.setDismantleEngineerPhone(destDismantleEngineer.getPhone());

            serviceEngineerChangeRecord.setWorkOrderType(ServiceEngineerChangeWorkOrderTypeEnum.MOVE_WATER_DEVICE_WORK_ORDER_DISMANTLE.value);
            serviceEngineerChangeRecord.setOrigEngineerId(origDismantleEngineer.getId());
            serviceEngineerChangeRecord.setOrigEngineerName(origDismantleEngineer.getRealName());
            serviceEngineerChangeRecord.setDestEngineerId(destDismantleEngineer.getId());
            serviceEngineerChangeRecord.setDestEngineerName(destDismantleEngineer.getRealName());
        } else if (type == 2) {
            if (moveWaterDeviceOrder.getStatus() == MoveWaterDeviceOrderStatusEnum.INSTALL.value || moveWaterDeviceOrder.getStatus() == MoveWaterDeviceOrderStatusEnum.COMPLETED.value) {
                throw new BadRequestException("当前移机工单已被移入安装工处理，不能更换移入服务人员！");
            }
            if (!engineerIds.contains(moveWaterDeviceOrder.getInstallEngineerId())) {
                //站务系统管理员没有操作该移机工单更换拆机服务人员的权限
                throw new YimaoException("您没有操作该移机工单更换拆机服务人员的权限！");
            }
            //查询原装机安装工
            EngineerDTO origInstallEngineer = userFeign.getEngineerById(moveWaterDeviceOrder.getInstallEngineerId());
            if (origInstallEngineer == null) {
                throw new YimaoException("原移入安装工信息不存在！");
            }
            EngineerDTO destInstallEngineer = userFeign.getEngineerById(engineerId);
            if (destInstallEngineer == null) {
                throw new YimaoException("替代的安装工信息不存在！");
            }
            for (EngineerServiceAreaDTO serviceArea : destInstallEngineer.getServiceAreaList()) {
                String province = serviceArea.getProvince();
                String city = serviceArea.getCity();
                String region = serviceArea.getRegion();
                if (moveWaterDeviceOrder.getDestProvince().equals(province) && moveWaterDeviceOrder.getDestCity().equals(city) && moveWaterDeviceOrder.getDestRegion().equals(region)) {
                    isServiceArea = true;
                    break;
                }
            }
            if (!isServiceArea) {
                throw new YimaoException("更换安装工服务区域不匹配");
            }
            update.setDismantleEngineerId(engineerId);
            update.setDismantleEngineerName(destInstallEngineer.getRealName());
            update.setDismantleEngineerPhone(destInstallEngineer.getPhone());

            serviceEngineerChangeRecord.setWorkOrderType(ServiceEngineerChangeWorkOrderTypeEnum.MOVE_WATER_DEVICE_WORK_ORDER_INSTALL.value);
            serviceEngineerChangeRecord.setOrigEngineerId(origInstallEngineer.getId());
            serviceEngineerChangeRecord.setOrigEngineerName(origInstallEngineer.getRealName());
            serviceEngineerChangeRecord.setDestEngineerId(destInstallEngineer.getId());
            serviceEngineerChangeRecord.setDestEngineerName(destInstallEngineer.getRealName());
        } else {
            throw new BadRequestException("type传参有误！");
        }
        moveWaterDeviceOrderMapper.updateByPrimaryKey(update);
        // 服务人员更换记录
        serviceEngineerChangeRecord.setSource(source);
        serviceEngineerChangeRecord.setOperator(operator);
        serviceEngineerChangeRecord.setTime(now);
        serviceEngineerChangeRecordMapper.insertSelective(serviceEngineerChangeRecord);
    }
}
