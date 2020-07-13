package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.AreaTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.ExcelUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.out.DeviceQuery;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.water.ConditionalAdvertisingDTO;
import com.yimao.cloud.pojo.dto.water.EffectStatisticsDTO;
import com.yimao.cloud.pojo.dto.water.EffectStatisticsDayAppDTO;
import com.yimao.cloud.pojo.dto.water.EffectStatisticsForAppDTO;
import com.yimao.cloud.pojo.dto.water.ServiceStationWaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.VersionDetailStatisticsDTO;
import com.yimao.cloud.pojo.dto.water.VersionStatisticsDTO;
import com.yimao.cloud.pojo.query.water.WaterDeviceQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import com.yimao.cloud.water.enums.DeviceTypeEnum;
import com.yimao.cloud.water.enums.PlatformEnum;
import com.yimao.cloud.water.enums.ProductModelEnum;
import com.yimao.cloud.water.enums.ScreenLocationEnum;
import com.yimao.cloud.water.handler.SystemFeignHandler;
import com.yimao.cloud.water.mapper.EffectStatisticsMapper;
import com.yimao.cloud.water.mapper.MaterialsMapper;
import com.yimao.cloud.water.mapper.PlatformMapper;
import com.yimao.cloud.water.mapper.ServiceStationWaterDeviceMapper;
import com.yimao.cloud.water.mapper.VersionDetailStatisticsMapper;
import com.yimao.cloud.water.mapper.VersionStatisticsMapper;
import com.yimao.cloud.water.po.EffectStatistics;
import com.yimao.cloud.water.po.Materials;
import com.yimao.cloud.water.po.Platform;
import com.yimao.cloud.water.po.ServiceStationWaterDevice;
import com.yimao.cloud.water.po.VersionDetailStatistics;
import com.yimao.cloud.water.po.VersionStatistics;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.service.ConditionalAdvertisingService;
import com.yimao.cloud.water.service.ServiceStationWaterDeviceService;
import com.yimao.cloud.water.service.StatisticsService;
import com.yimao.cloud.water.service.WaterDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：数据统计
 */
@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    @Resource
    private EffectStatisticsMapper effectStatisticsMapper;
    @Resource
    private PlatformMapper platformMapper;
    @Resource
    private VersionStatisticsMapper versionStatisticsMapper;
    @Resource
    private VersionDetailStatisticsMapper versionDetailStatisticsMapper;
    @Resource
    private SystemFeignHandler systemFeignHandler;
    @Resource
    private ConditionalAdvertisingService conditionalAdvertisingService;
    @Resource
    private MaterialsMapper materialsMapper;
    @Resource
    private ServiceStationWaterDeviceService serviceStationWaterDeviceService;
    @Resource
    private ServiceStationWaterDeviceMapper serviceStationWaterDeviceMapper;
    @Resource
    private WaterDeviceService waterDeviceService;

    /**
     * 查询效果统计--每日展示详情
     *
     * @param materielId
     * @param advertisingId
     * @param adslotId
     * @param time
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<EffectStatisticsDTO> queryEffectDetail(Integer materielId, Integer advertisingId, String adslotId, Date time, String snCode,
                                                         Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<EffectStatisticsDTO> page = effectStatisticsMapper.queryEffectDetail(materielId, advertisingId, adslotId, time, snCode);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 查询效果统计--展示详情
     *
     * @param materielId
     * @param advertisingId
     * @param adslotId
     * @param beginTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<EffectStatisticsDTO> queryDetail(Integer materielId, Integer advertisingId, String adslotId, Date beginTime,
                                                   Date endTime, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<EffectStatisticsDTO> page = effectStatisticsMapper.queryDetail(materielId, advertisingId, adslotId, beginTime, endTime);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 查询效果统计列表
     *
     * @param platform
     * @param screenLocation
     * @param materielName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<EffectStatisticsDTO> queryEffectListByCondition(Integer platform, Integer screenLocation, String materielName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<EffectStatisticsDTO> page = effectStatisticsMapper.queryEffectListByCondition(platform, screenLocation, materielName);
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        for (EffectStatisticsDTO dto : page) {
            //根据投放配置ID查询投放详情
            ConditionalAdvertisingDTO advertising = conditionalAdvertisingService.selectAdById(dto.getAdvertisingId());
            if (Objects.nonNull(advertising)) {
                dto.setEffect(advertising);
                // 根据物料id查询物料详情
                if (Objects.equals(advertising.getPlatform(), PlatformEnum.YIMAO.value)) {
                    Materials materials = materialsMapper.selectByPrimaryKey(Integer.valueOf(advertising.getAdslotId()));
                    if (Objects.nonNull(materials)) {
                        dto.setMaterielName(materials.getName());
                        dto.setAdvertisers(materials.getAdvertisers());
                        dto.setMaterielId(materials.getId());
                    }
                }
            }

            //计算到达率
            if (Objects.nonNull(dto.getDeviceSum()) && Objects.nonNull(dto.getSnCodeSum())) {
                String percentage = numberFormat.format((float) dto.getSnCodeSum() / (float) dto.getDeviceSum() * 100) + "%";
                dto.setPercentage(percentage);
            } else {
                dto.setPercentage("0");
            }
        }
        return new PageVO<>(pageNum, page);
    }


    @Override
    public PageVO<EffectStatisticsDTO> queryEffectListBySn(String snCode, Date beginTime, Date endTime, Integer pageNum, Integer pageSize) {
        if (StringUtils.isBlank(snCode)) {
            throw new BadRequestException("设备编码不能为空");
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<EffectStatisticsDTO> page = effectStatisticsMapper.queryEffectListBySn(snCode, beginTime, endTime);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public EffectStatisticsDTO queryEffectCountBySn(String snCode, Date beginTime, Date endTime) {
        if (StringUtils.isBlank(snCode)) {
            throw new BadRequestException("设备编码不能为空");
        }
        EffectStatisticsDTO dto = effectStatisticsMapper.queryEffectCountBySn(snCode, beginTime, endTime);

        if (Objects.nonNull(dto) && Objects.nonNull(dto.getDeviceGroup())) {
            if (Objects.equals(dto.getDeviceGroup(), DeviceTypeEnum.USER.value)) {
                // 查询用户组设备
                // WaterDevice waterDevice = waterDeviceService.getBySnCode(snCode);
                //根据sn查询用户组设备详情
                // DeviceDTO device = outFeign.getDeviceBySnCode(snCode);
                WaterDevice device = waterDeviceService.getBySnCode(snCode);
                if (!Objects.nonNull(device)) {
                    throw new YimaoException("用户组不存该设备！");
                }
                dto.setModle(device.getDeviceModel());
            } else if (Objects.equals(dto.getDeviceGroup(), DeviceTypeEnum.STATION.value)) {
                //服务站设备
                ServiceStationWaterDeviceDTO stationDevice = serviceStationWaterDeviceService.selectBySn(snCode);
                if (!Objects.nonNull(stationDevice)) {
                    throw new YimaoException("服务站组不存该设备！");
                }
                dto.setModle(stationDevice.getDeviceModel());
            }
        }

        return dto;
    }

    /**
     * 保存效果统计数据
     *
     * @param list
     */
    @Override
    public void saveEffect(List<EffectStatisticsForAppDTO> list) {
        List<EffectStatistics> effList = new ArrayList<>();
        for (EffectStatisticsForAppDTO dto : list) {
            if (StringUtil.isEmpty(dto.getSnCode())) {
                throw new BadRequestException("设备编码不能为空");
            }
            if (StringUtil.isEmpty(dto.getAdslotId())) {
                throw new BadRequestException("广告位编码不能为空");
            }
            if (Objects.isNull(dto.getScreenLocation())) {
                throw new BadRequestException("屏幕位置不能为空");
            }
            if (Objects.isNull(dto.getAdvertisingId())) {
                throw new BadRequestException("投放配置ID不能为空");
            }
            if (Objects.isNull(dto.getBeginTime())) {
                throw new BadRequestException("投放开始时间不能为空");
            }
            if (Objects.isNull(dto.getEndTime())) {
                throw new BadRequestException("投放结束时间不能为空");
            }
            if (Objects.isNull(dto.getPlatform())) {
                throw new BadRequestException("广告平台不能为空");
            }
            if (Objects.isNull(dto.getDeviceGroup())) {
                throw new BadRequestException("设备组不能为空");
            }

            List<EffectStatisticsDayAppDTO> dayAppDTOList = dto.getDayAppDTOList();
            if (CollectionUtil.isNotEmpty(dayAppDTOList)) {
                for (EffectStatisticsDayAppDTO dayAppDTO : dayAppDTOList) {
                    EffectStatistics eff = new EffectStatistics(dto);
                    eff.setCreateTime(new Date());
                    eff.setClicks(dayAppDTO.getClicks());
                    eff.setPlayAmount(dayAppDTO.getPlayAmount());
                    eff.setPlayTime(dayAppDTO.getPlayTime());
                    effList.add(eff);
                }
            }
        }

        effectStatisticsMapper.insertBatch(effList);
    }

    /**
     * 查询效果统计--通过投放配置ID查询详情
     *
     * @param advertisingId
     * @return
     */
    @Override
    public EffectStatisticsDTO queryEffectDetailByAdvertising(Integer advertisingId) {
        EffectStatisticsDTO dto = effectStatisticsMapper.queryEffectDetailByAdvertising(advertisingId);
        if (Objects.nonNull(dto)) {
            //根据投放配置ID查询投放详情
            ConditionalAdvertisingDTO advertising = conditionalAdvertisingService.selectAdById(dto.getAdvertisingId());
            if (Objects.nonNull(advertising)) {
                dto.setEffect(advertising);
                // 根据物料id查询物料详情
                if (Objects.equals(advertising.getPlatform(), PlatformEnum.YIMAO.value)) {
                    Materials materials = materialsMapper.selectByPrimaryKey(Integer.valueOf(advertising.getAdslotId()));
                    if (Objects.nonNull(materials)) {
                        dto.setMaterielName(materials.getName());
                        dto.setAdvertisers(materials.getAdvertisers());
                        dto.setMaterielId(materials.getId());
                    }
                }
            }
        }
        return dto;
    }

    /**
     * 导出效果统计列表
     *
     * @param platform
     * @param screenLocation
     * @param materielName
     * @param response
     */
    @Override
    public void exportEffect(Integer platform, Integer screenLocation, String materielName, HttpServletResponse
            response) {

        List<EffectStatisticsDTO> list = effectStatisticsMapper.queryEffectListByCondition(platform, screenLocation, materielName);
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<Platform> platformList = platformMapper.selectAll();
        for (EffectStatisticsDTO dto : list) {
            if (Objects.nonNull(dto.getScreenLocation())) {
                if (Objects.equals(dto.getScreenLocation(), ScreenLocationEnum.ONE.value)) {
                    dto.setScreenLocationName(ScreenLocationEnum.ONE.name);
                } else if (Objects.equals(dto.getScreenLocation(), ScreenLocationEnum.TWO.value)) {
                    dto.setScreenLocationName(ScreenLocationEnum.TWO.name);
                }
            }
            if (Objects.nonNull(dto.getBeginTime()) && Objects.nonNull(dto.getEndTime())) {
                dto.setTimeFormat(format.format(dto.getBeginTime()) + " - " + format.format(dto.getEndTime()));
            }

            //根据投放配置ID查询投放详情
            ConditionalAdvertisingDTO advertising = conditionalAdvertisingService.selectAdById(dto.getAdvertisingId());
            if (Objects.nonNull(advertising)) {
                dto.setEffect(advertising);
                // 根据物料id查询物料详情
                if (Objects.equals(advertising.getPlatform(), PlatformEnum.YIMAO.value)) {
                    Materials materials = materialsMapper.selectByPrimaryKey(Integer.valueOf(advertising.getAdslotId()));
                    if (Objects.nonNull(materials)) {
                        dto.setMaterielName(materials.getName());
                        dto.setAdvertisers(materials.getAdvertisers());
                        dto.setMaterielId(materials.getId());
                    }
                }
            }
            //计算到达率
            if (Objects.nonNull(dto.getDeviceSum()) && Objects.nonNull(dto.getSnCodeSum())) {
                String percentage = numberFormat.format((float) dto.getSnCodeSum() / (float) dto.getDeviceSum() * 100) + "%";
                dto.setPercentage(percentage);
            } else {
                dto.setPercentage("0");
            }
            //翻译第三方平台
            if (CollectionUtil.isNotEmpty(platformList)) {
                for (Platform pla : platformList) {
                    if (Objects.equals(pla.getId(), dto.getPlatform())) {
                        dto.setPlatformName(pla.getName());
                        break;
                    }
                }
            }
        }

        String header = "效果统计";
        String[] beanPropertys = new String[]{"platformName", "materielId", "screenLocationName", "adslotId", "materielName", "advertisers",
                "timeFormat", "deviceSum", "snCodeSum", "percentage", "clicksSum", "playAmountSum"};
        String[] titles = new String[]{"平台名称", "物料标识", "投放位置", "广告位", "文件名", "广告主", "广告播放时段",
                "设备投放数量", "广告到达数量", "投放到达率", "用户点击数", "广告播放(次)"};
        ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanPropertys, response);
    }


    /**
     * 导出效果统计--展示详情
     *
     * @param materielId
     * @param advertisingId
     * @param adslotId
     * @param beginTime
     * @param endTime
     * @param response
     */
    @Override
    public void exportEffectDetail(Integer materielId, Integer advertisingId, String adslotId, Date beginTime, Date
            endTime, HttpServletResponse response) {

        List<EffectStatisticsDTO> list = effectStatisticsMapper.queryDetail(materielId, advertisingId, adslotId, beginTime, endTime);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (EffectStatisticsDTO dto : list) {
            if (Objects.nonNull(dto.getPlayTime())) {
                dto.setTimeFormat(format.format(dto.getPlayTime()));
            }
        }
        String header = "效果统计展示详情";
        String[] beanPropertys = new String[]{"timeFormat", "deviceDayAmount", "playDayAmount", "clicksDayCount"};
        String[] titles = new String[]{"投放日期", "展示设备数量", "广告展示量(次)", "累计用户点击数"};
        ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanPropertys, response);

    }


    /**
     * 导出效果统计--每日展示详情
     *
     * @param materielId
     * @param advertisingId
     * @param adslotId
     * @param time
     * @param snCode
     * @param response
     */
    @Override
    public void exportEffectDetailByDay(Integer materielId, Integer advertisingId, String adslotId, Date
            time, String snCode, HttpServletResponse response) {

        List<EffectStatisticsDTO> list = effectStatisticsMapper.queryEffectDetail(materielId, advertisingId, adslotId, time, snCode);
        String header = "效果统计每日展示详情";
        String[] beanPropertys = new String[]{"snCode", "playAmount", "clicks"};
        String[] titles = new String[]{"SN码", "单日展示量(次)", "单日用户点击数"};
        ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanPropertys, response);
    }

    /**
     * 效果统计---昨日数据(区分第三方和自有)
     *
     * @return
     */
    @Override
    public Map<String, Object> queryEffectCountByYesterday() {
        Map<String, Object> map = new HashMap<>();
        //查询自有数据
        EffectStatisticsDTO dto = effectStatisticsMapper.queryYesterdayCount(1);
        //查询第三方数据
        EffectStatisticsDTO thirdDto = effectStatisticsMapper.queryYesterdayCount(2);
        map.put("own", dto);
        map.put("third", thirdDto);
        return map;
    }

    /**
     * 版本统计
     *
     * @return
     */
    @Override
    public List<VersionStatisticsDTO> queryVersionList() {
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        Example example = new Example(VersionStatistics.class);
        example.setOrderByClause("version desc");
        //根据版本号进行排序查询版本列表
        List<VersionStatistics> list = versionStatisticsMapper.selectByExample(example);
        List<VersionStatisticsDTO> dtoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            for (VersionStatistics ver : list) {
                VersionStatisticsDTO dto = new VersionStatisticsDTO();
                ver.convert(dto);
                //计算到达率
                if (Objects.nonNull(ver.getDeviceCount()) && Objects.nonNull(ver.getSuccessCount())) {
                    String percentage = numberFormat.format((float) ver.getSuccessCount() / (float) ver.getDeviceCount() * 100) + "%";
                    dto.setPercentage(percentage);
                } else {
                    dto.setPercentage("0%");
                }
                dtoList.add(dto);
            }
        }
        return dtoList;
    }


    /**
     * 创建版本统计相关数据
     *
     * @param dto
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void saveVersion(VersionDetailStatisticsDTO dto) {
        VersionDetailStatistics ver = new VersionDetailStatistics(dto);
        if (StringUtils.isEmpty(ver.getVersion())) {
            throw new BadRequestException("请添加版本号。");
        }
        if (Objects.isNull(ver.getUpdateVersionTime())) {
            throw new BadRequestException("请添加版本发布时间。");
        }
        if (StringUtils.isEmpty(ver.getSnCode())) {
            throw new BadRequestException("请添加设备编码。");
        }
        if (Objects.isNull(ver.getDeviceGroup())) {
            throw new BadRequestException("设备组不能为空");
        }
        //更新或者新增数据的时候需要判断是否是新版本
        Example ex = new Example(VersionStatistics.class);
        Example.Criteria cri = ex.createCriteria();
        cri.andEqualTo("version", ver.getVersion());
        int detail = versionDetailStatisticsMapper.selectCountByExample(ex);
        if (Objects.equals(detail, 0)) {
            //表明当前数据是新版本
            Example example = new Example(VersionStatistics.class);
            example.setOrderByClause("version desc");
            //根据版本号进行排序查询版本列表
            List<VersionStatistics> list = versionStatisticsMapper.selectByExample(example);
            //根据当前时间查询设备数量
            DeviceQuery deviceQuery = new DeviceQuery();
            deviceQuery.setCreateTime(new Date());
            // Long device = outFeign.countDevice(deviceQuery);
            int device = waterDeviceService.countDevice(deviceQuery);
            // 查询服务站设备总数
            int stationCount = serviceStationWaterDeviceMapper.selectCount(new ServiceStationWaterDevice());
            int count = device + stationCount;
            if (CollectionUtil.isNotEmpty(list)) {
                //只更新数组中的第一个
                VersionStatistics verbs = list.get(0);
                //查询当前时间已经更新的设备数量
                Example example1 = new Example(VersionDetailStatistics.class);
                Example.Criteria criteria = example1.createCriteria();
                criteria.andLessThanOrEqualTo("updateVersionTime", new Date());
                int i = versionDetailStatisticsMapper.selectCountByExample(example1);

                //更新上个版本设备成功更新总数量
                VersionStatistics versionStatistics = new VersionStatistics();
                versionStatistics.setId(verbs.getId());
                versionStatistics.setDeviceCount(count);
                versionStatistics.setSuccessCount(i);
                versionStatisticsMapper.updateByPrimaryKeySelective(versionStatistics);

                //插入一条版本总表记录
                VersionStatistics version = new VersionStatistics();
                version.setDeviceCount(count);
                // 默认更新版本成功数量为1
                version.setSuccessCount(1);
                version.setVersion(ver.getVersion());
                versionStatisticsMapper.insertSelective(version);
            } else {
                //当版本总表没有数据时，插入一条版本总表记录
                VersionStatistics version = new VersionStatistics();
                version.setDeviceCount(count);
                // 默认更新版本成功数量为1
                version.setSuccessCount(1);
                version.setVersion(ver.getVersion());
                versionStatisticsMapper.insertSelective(version);
            }
        }
        Example example = new Example(VersionDetailStatistics.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("snCode", ver.getSnCode());
        VersionDetailStatistics detailStatistics = versionDetailStatisticsMapper.selectOneByExample(example);
        //存在就更新，没有就新增
        if (Objects.nonNull(detailStatistics)) {
            //设备存在,就更新版本号
            VersionDetailStatistics version = new VersionDetailStatistics();
            version.setId(detailStatistics.getId());
            version.setVersion(ver.getVersion());
            version.setUpdateVersionTime(ver.getUpdateVersionTime());
            version.setConsumeFlowType(ver.getConsumeFlowType());
            versionDetailStatisticsMapper.updateByPrimaryKeySelective(version);
        } else {
            versionDetailStatisticsMapper.insertSelective(ver);
        }
    }


    /**
     * 描述：查询所有设备的版本信息
     *
     * @param areaId
     * @param model
     * @param version
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<WaterDeviceVO> queryVersionDetailList(Integer areaId, String model, String version, Integer deviceGroup, String snCode,
                                                    Integer pageNum, Integer pageSize) {
        PageVO<WaterDeviceVO> devicePage = new PageVO<>();
        if (Objects.equals(deviceGroup, DeviceTypeEnum.USER.value)) {
            //查询用户组设备数据
            devicePage = queryDevice(areaId, model, pageNum, pageSize, snCode);
            if (CollectionUtil.isNotEmpty(devicePage.getResult())) {
                for (WaterDeviceVO vo : devicePage.getResult()) {
                    //给设备数据绑定版本信息
                    setVersion(vo, version);
                    vo.setDeviceGroup(DeviceTypeEnum.USER.name);
                }
            }
        } else if (Objects.equals(deviceGroup, DeviceTypeEnum.STATION.value)) {
            //分页查询服务站设备数据
            PageVO<ServiceStationWaterDeviceDTO> page = serviceStationWaterDeviceService.queryListByCondition(areaId, model, null, null,
                    null, snCode, null, null, pageNum, pageSize);
            List<ServiceStationWaterDeviceDTO> result = page.getResult();
            List<WaterDeviceVO> deviceDTOList = new ArrayList<>();
            for (ServiceStationWaterDeviceDTO deviceDTO : result) {
                WaterDeviceVO vo = new WaterDeviceVO();
                deviceDTO.convert(vo);
                //给设备数据绑定版本信息
                setVersion(vo, version);
                vo.setDeviceGroup(DeviceTypeEnum.STATION.name);
                deviceDTOList.add(vo);
            }
            devicePage.setTotal(page.getTotal());
            devicePage.setResult(deviceDTOList);
            devicePage.setPages(page.getPages());
            devicePage.setPageNum(pageNum);
            devicePage.setPageSize(pageSize);
        }
        return devicePage;
    }


    /**
     * 给设备数据绑定版本信息
     *
     * @param vo
     * @param version
     * @return
     */
    public void setVersion(WaterDeviceVO vo, String version) {
        //根据sn码查询版本信息
        VersionDetailStatistics ver = new VersionDetailStatistics();
        ver.setSnCode(vo.getSnCode());
        VersionDetailStatistics statistics = versionDetailStatisticsMapper.selectOne(ver);
        if (Objects.nonNull(statistics)) {
            vo.setAppVersion(statistics.getVersion());//水机版本号
            vo.setConsumeFlowType(statistics.getConsumeFlowType());//消耗流量类型
            vo.setUpdateVersionTime(statistics.getUpdateVersionTime());//设备更新版本时间
            if (StringUtils.equals(version, statistics.getVersion())) {
                vo.setIsUpdate("是");//是否更新
            } else {
                vo.setIsUpdate("否");
            }
        } else {
            vo.setIsUpdate("否");
        }
    }

    /**
     * 根据条件查询设备分页列表
     *
     * @param areaId
     * @param model
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageVO<WaterDeviceVO> queryDevice(Integer areaId, String model, Integer pageNum, Integer pageSize, String snCode) {
        WaterDeviceQuery query = new WaterDeviceQuery();
        //省市区
        if (Objects.nonNull(areaId)) {
            AreaDTO area = systemFeignHandler.getAreaById(areaId);
            if (Objects.nonNull(area)) {
                if (area.getLevel() == AreaTypeEnum.PROVINCE.value) {
                    query.setProvince(area.getName());
                } else if (area.getLevel() == AreaTypeEnum.CITY.value) {
                    AreaDTO province = systemFeignHandler.getAreaById(area.getPid());
                    query.setProvince(province.getName());
                    query.setCity(area.getName());
                } else if (area.getLevel() == AreaTypeEnum.REGION.value) {
                    AreaDTO city = systemFeignHandler.getAreaById(area.getPid());
                    AreaDTO province = systemFeignHandler.getAreaById(city.getPid());
                    query.setProvince(province.getName());
                    query.setCity(city.getName());
                    query.setRegion(area.getName());
                }
            }
        }
        //水机型号
        if (StringUtil.isNoneBlank(model)) {
            if (!model.equalsIgnoreCase(ProductModelEnum.MODEL_1601T.value)
                    && !model.equalsIgnoreCase(ProductModelEnum.MODEL_1602T.value)
                    && !model.equalsIgnoreCase(ProductModelEnum.MODEL_1603T.value)
                    && !model.equalsIgnoreCase(ProductModelEnum.MODEL_1601L.value)) {
                throw new BadRequestException("错误的型号。");
            }
            query.setDeviceModel(model);
        }

        //SN
        if (StringUtil.isNotBlank(snCode)) {
            query.setSn(snCode);
        }
        // PageVO<DeviceDTO> devicePage = outFeign.pageDevice(pageNum, pageSize, query);
        PageVO<WaterDeviceVO> wdvo = waterDeviceService.pageWaterDevice(pageNum, pageSize, query);
        return wdvo;
    }
}
