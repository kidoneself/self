package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.AreaTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.water.ServiceStationWaterDeviceDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.enums.ProductModelEnum;
import com.yimao.cloud.water.handler.SystemFeignHandler;
import com.yimao.cloud.water.mapper.ConditionalAdvertisingMapper;
import com.yimao.cloud.water.mapper.ServiceStationWaterDeviceMapper;
import com.yimao.cloud.water.po.ServiceStationWaterDevice;
import com.yimao.cloud.water.service.AdslotService;
import com.yimao.cloud.water.service.ServiceStationWaterDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


@Service
@Slf4j
public class ServiceStationWaterDeviceServiceImpl implements ServiceStationWaterDeviceService {

    @Resource
    private ServiceStationWaterDeviceMapper mapper;
    // @Resource
    // private OutFeign outFeign;
    @Resource
    private SystemFeignHandler systemFeignHandler;
    @Resource
    private ConditionalAdvertisingMapper conditionalAdvertisingMapper;
    @Resource
    private AdslotService adslotService;


    // /**
    //  * 同步服务站设备数据，到本地数据库
    //  */
    // @Override
    // public void save() {
    //     List<ServiceDeviceDTO> list = outFeign.serviceDevicesList();
    //     for (ServiceDeviceDTO dto : list) {
    //         ServiceStationWaterDevice station = new ServiceStationWaterDevice();
    //         BeanUtils.copyProperties(dto, station);
    //         String substring = dto.getSncode().substring(4, 9);
    //         station.setDeviceModel(substring);
    //         if (Objects.equals(dto.getOnline(), "是")) {
    //             station.setOnline(true);
    //         } else if (Objects.equals(dto.getOnline(), "否")) {
    //             station.setOnline(false);
    //         }
    //         station.setSnCode(dto.getSncode());
    //         station.setLastOnlineTime(dto.getLastonlineTime());
    //         station.setPlace(dto.getPlace());
    //         if (StringUtil.isNotEmpty(station.getLatitude()) && StringUtil.isNotEmpty(station.getLongitude())){
    //             Map<String, String> map = getAddressByBaidu(station.getLatitude() + "," + station.getLongitude());
    //             String province = map.get("province");
    //             String city = map.get("city");
    //             String district = map.get("district");
    //             if (StringUtils.equals(province,station.getProvince()) && StringUtils.equals(city,station.getCity()) &&StringUtils.equals(district,station.getRegion()) ){
    //                 String place = map.get("place");
    //                 station.setAddress(place);
    //             }
    //         }
    //         mapper.insert(station);
    //     }
    //
    // }


    // /**
    //  * 通过经纬度查询具体地址详情
    //  * @param lat
    //  * @return
    //  */
    // public static Map<String,String> getAddressByBaidu(String lat) {
    //     Map<String,String> map =new HashMap<>();
    //     //   v1zmMwtcKfmm9vEOfcCm7kUFlokBGRFp
    //     String url = "http://api.map.baidu.com/geocoder/v2/?output=json&latest_admin=1&ak=ClHs9DC7WUoMSNrsz5hKHuEfqCh5W4U6&location=" + lat;
    //     String get = null;
    //     try {
    //         get = HttpUtil.executeGet(url);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     System.out.println("============" + get);
    //     JSONObject data = JSONObject.parseObject(get);
    //     int status = (int) data.get("status");
    //     if (Objects.equals(status, 0)) {
    //         JSONObject result = (JSONObject) data.get("result");
    //         JSONObject addressComponent = (JSONObject) result.get("addressComponent");
    //         String province = (String) addressComponent.get("province");
    //         String city = (String) addressComponent.get("city");
    //         String district = (String) addressComponent.get("district");
    //
    //         String formatted_address = (String) result.get("formatted_address");
    //         String sematic_description = (String) result.get("sematic_description");
    //         String place = formatted_address + sematic_description;
    //         map.put("province",province);
    //         map.put("city",city);
    //         map.put("district",district);
    //         map.put("place",place);
    //     }
    //     return map;
    // }


    /**
     * 根据条件查询服务站设备数据总和
     *
     * @param areaList       省市区名称集合
     * @param models         设备型号集合
     * @param connectionType 网络连接类型
     * @return
     */
    public int count(List<Map<String, String>> areaList, List<String> models, Integer connectionType) {
        return mapper.selectCountByCondition(areaList, models, connectionType);
    }

    /**
     * 根据sn查询服务站设置详情
     *
     * @param snCode
     * @return
     */
    @Override
    public ServiceStationWaterDeviceDTO selectBySn(String snCode) {
        Example example = new Example(ServiceStationWaterDevice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("snCode", snCode);
        ServiceStationWaterDevice device = mapper.selectOneByExample(example);
        ServiceStationWaterDeviceDTO dto = new ServiceStationWaterDeviceDTO();
        if(Objects.nonNull(device)){
            device.convert(dto);
        }
        return dto;
    }


    /**
     * 分页查询服务站设备数据
     *
     * @param areaId
     * @param model
     * @param online
     * @param connectionType
     * @param keyWord
     * @param snCode
     * @param beginTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<ServiceStationWaterDeviceDTO> queryListByCondition(Integer areaId, String model, Boolean online, Integer connectionType,
                                                                     String keyWord, String snCode, Date beginTime, Date endTime,
                                                                     Integer pageNum, Integer pageSize) {
        Example example = new Example(ServiceStationWaterDevice.class);
        Example.Criteria criteria = example.createCriteria();
        if (Objects.nonNull(areaId)) {
            //根据区域id查询省市区名称
            AreaDTO area = systemFeignHandler.getAreaById(areaId);
            if (Objects.nonNull(area)) {
                if (area.getLevel() == AreaTypeEnum.PROVINCE.value) {
                    criteria.andEqualTo("province", area.getName());
                } else if (area.getLevel() == AreaTypeEnum.CITY.value) {
                    AreaDTO province = systemFeignHandler.getAreaById(area.getPid());
                    criteria.andEqualTo("province", province.getName());
                    criteria.andEqualTo("city", area.getName());
                } else if (area.getLevel() == AreaTypeEnum.REGION.value) {
                    AreaDTO city = systemFeignHandler.getAreaById(area.getPid());
                    AreaDTO province = systemFeignHandler.getAreaById(city.getPid());
                    criteria.andEqualTo("province", province.getName());
                    criteria.andEqualTo("city", city.getName());
                    criteria.andEqualTo("region", area.getName());
                }
            }
        }
        if (StringUtil.isNotEmpty(model)) {
            //水机型号
            String[] modelList = model.split(",");
            List<String> modelListTemp = new ArrayList<>();
            modelListTemp.add(ProductModelEnum.MODEL_1601T.value);
            modelListTemp.add(ProductModelEnum.MODEL_1602T.value);
            modelListTemp.add(ProductModelEnum.MODEL_1603T.value);
            modelListTemp.add(ProductModelEnum.MODEL_1601L.value);
            for (String models : modelList) {
                if (!modelListTemp.contains(models.toUpperCase())) {
                    throw new BadRequestException("请选择正确的水机型号。");
                }
            }
            criteria.andIn("deviceModel", Arrays.asList(modelList));
        }
        if (Objects.nonNull(online)) {
            //是否在线
            criteria.andEqualTo("online", online);
        }
        if (Objects.nonNull(connectionType)) {
            //联网状态
            criteria.andEqualTo("connectionType", connectionType);
        }
        if (StringUtil.isNotEmpty(keyWord)) {
            //关键词模糊搜索
            criteria.andLike("address", "%"+keyWord+"%");
        }
        if (StringUtil.isNotBlank(snCode)) {
            //设备sn
            criteria.andEqualTo("snCode", snCode);
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ServiceStationWaterDevice> deviceList = (Page<ServiceStationWaterDevice>) mapper.selectByExample(example);
        PageVO<ServiceStationWaterDeviceDTO> pageVO = new PageVO<>(pageNum, deviceList, ServiceStationWaterDevice.class, ServiceStationWaterDeviceDTO.class);
        List<ServiceStationWaterDeviceDTO> result = pageVO.getResult();
        if (CollectionUtil.isNotEmpty(result) && Objects.nonNull(beginTime) && Objects.nonNull(endTime)) {
            for (ServiceStationWaterDeviceDTO dto : result) {
                //根据名称查询省市区ID集合
                List<Integer> areaIds = systemFeignHandler.getAreaIdsByName(dto.getRegion());
                Set<String> modelSet = new HashSet<>();
                modelSet.add(dto.getDeviceModel());
                //根据条件查询可以用广告位
                Set<String> adslotIdList = conditionalAdvertisingMapper.selectAdslotId(areaIds, modelSet, connectionType,
                        beginTime, endTime);
                //查询剩余广告位
                Set<String> adslotNameList = adslotService.selectAdslotName(adslotIdList);
                dto.setAdslotStock(adslotNameList);
            }
        }
        return pageVO;
    }
}
