package com.yimao.cloud.system.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.HttpUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.out.ServicesiteDTO;
import com.yimao.cloud.system.mapper.StationCompanyMapper;
import com.yimao.cloud.system.mapper.StationCompanyServiceAreaMapper;
import com.yimao.cloud.system.mapper.StationCompanyStationMapper;
import com.yimao.cloud.system.mapper.StationMapper;
import com.yimao.cloud.system.mapper.StationServiceAreaMapper;
import com.yimao.cloud.system.po.Station;
import com.yimao.cloud.system.po.StationCompany;
import com.yimao.cloud.system.po.StationCompanyServiceArea;
import com.yimao.cloud.system.po.StationCompanyStation;
import com.yimao.cloud.system.po.StationServiceArea;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2019/06/01
 */
@Component
@Slf4j
public class SyncStationProcessor {

    @Resource
    private StationMapper stationMapper;
    @Resource
    private StationServiceAreaMapper stationServiceAreaMapper;
    @Resource
    private StationCompanyMapper stationCompanyMapper;
    @Resource
    private StationCompanyServiceAreaMapper stationCompanyServiceAreaMapper;
    @Resource
    private StationCompanyStationMapper stationCompanyStationMapper;

    @RabbitListener(queues = RabbitConstant.SYNC_STATION)
    @RabbitHandler
    public void processor(Set<ServicesiteDTO> list) {
        try {
            //获取已经同步到mysql的服务站ID
            // List<String> oldIdList = stationMapper.listOldId();
            // List<String> mongoIdList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(list)) {
                for (ServicesiteDTO dto : list) {
                    String oldId = dto.getId();
                    // mongoIdList.add(oldId);
                    // Station query = new Station();
                    // query.setOldId(oldId);
                    Station station = new Station();
                    // if (station == null) {
                    //     station = new Station();
                    // }
                    //服务站名称
                    station.setName(dto.getName());
                    station.setProvince(dto.getProvince());
                    station.setCity(dto.getCity());
                    station.setRegion(dto.getRegion());
                    station.setAddress(dto.getAddress());
                    //门店类型：1-加盟店，2-连锁店，3-旗舰店；
                    station.setType(1);
                    station.setOnline(1);
                    station.setOnlineTime(dto.getCreateTime());
                    station.setContact(dto.getPerson());
                    station.setContactPhone(dto.getPhone());
                    station.setMasterName(dto.getMasterName());
                    station.setMasterPhone(dto.getMasterPhone());
                    station.setMasterIdCard(dto.getIdentityNumber());
                    station.setCompanyName(dto.getCompanyName());
                    station.setOldId(oldId);
                    if (station.getId() == null) {
                        station.setCreateTime(dto.getCreateTime());
                        station.setCreator("SYNC");
                    }
                    // station.setUpdateTime(new Date());
                    //把统一社会信用代码临时存放在updater字段上
                    station.setUpdater(dto.getCreditCode());
                    // String mongoAddress = StringUtil.isEmpty(dto.getAddress()) ? "" : dto.getAddress().trim();
                    // String mysqlAddress = StringUtil.isEmpty(station.getAddress()) ? "" : station.getAddress().trim();
                    // if (!Objects.equals(mongoAddress, mysqlAddress)) {
                    //     //设置经纬度
                    //     this.fillLngAndLat(station);
                    // }
                    // if (station.getId() == null) {
                    int i = stationMapper.insert(station);
                    if (i <= 0) {
                        log.error("插入Station失败。" + station.getName());
                    }
                    // } else {
                    //     stationMapper.updateByPrimaryKey(station);
                    // }


                    StationServiceArea ssa = new StationServiceArea();
                    ssa.setStationId(station.getId());
                    ssa.setStationName(station.getName());
                    ssa.setProvince(station.getProvince());
                    ssa.setCity(station.getCity());
                    ssa.setRegion(station.getRegion());
                    i = stationServiceAreaMapper.insert(ssa);
                    if (i <= 0) {
                        log.error("插入StationServiceArea失败。" + station.getName());
                    }


                    StationCompany company = new StationCompany();
                    company.setName(dto.getCompanyName());
                    company.setCreditCode(dto.getCreditCode());
                    company.setType(1);
                    company.setProvince(dto.getProvince());
                    company.setCity(dto.getCity());
                    company.setRegion(dto.getRegion());
                    company.setAddress(dto.getAddress());
                    company.setContact(dto.getPerson());
                    company.setContactPhone(dto.getPhone());
                    company.setLegalPerson(dto.getLegalPerson());
                    company.setOnline(true);
                    company.setOnlineTime(dto.getCreateTime());
                    i = stationCompanyMapper.insert(company);
                    if (i <= 0) {
                        log.error("插入StationCompany失败。" + station.getName());
                    }

                    StationCompanyServiceArea scsa = new StationCompanyServiceArea();
                    scsa.setStationCompanyId(company.getId());
                    scsa.setStationCompanyName(company.getName());
                    scsa.setProvince(company.getProvince());
                    scsa.setCity(company.getCity());
                    scsa.setRegion(company.getRegion());
                    i = stationCompanyServiceAreaMapper.insert(scsa);
                    if (i <= 0) {
                        log.error("插入StationCompanyServiceArea失败。" + station.getName());
                    }

                    StationCompanyStation scs = new StationCompanyStation();
                    scs.setStationId(station.getId());
                    scs.setStationCompanyId(company.getId());
                    i = stationCompanyStationMapper.insert(scs);
                    if (i <= 0) {
                        log.error("插入StationCompanyStation失败。" + station.getName());
                    }

                }
                // //如果本地的数据已经不存在了则删除
                // if (CollectionUtil.isNotEmpty(oldIdList)) {
                //     for (String id : oldIdList) {
                //         if (StringUtil.isNotEmpty(id) && !mongoIdList.contains(id)) {
                //             stationMapper.deleteByOldId(id);
                //         }
                //     }
                // }
            }
        } catch (Exception e) {
            log.error("出错了。");
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 填充服务站的经纬度信息
     *
     * @param station
     */
    private void fillLngAndLat(Station station) {
        String address = station.getAddress();
        if (StringUtil.isNotEmpty(address)) {
            try {
                String url = "http://restapi.amap.com/v3/geocode/geo?key=c053e166c378b139766060f7d4bce6eb&address=" + URLEncoder.encode(address, "UTF-8");
                String resp = HttpUtil.executeGet(url);
                log.info(resp);
                if (StringUtil.isNotEmpty(resp)) {
                    JSONObject respJson = new JSONObject(resp);
                    JSONArray array = respJson.getJSONArray("geocodes");
                    if (array != null) {
                        JSONObject geocode = array.getJSONObject(0);
                        if (geocode != null) {
                            String location = geocode.getString("location");
                            if (StringUtil.isNotEmpty(location)) {
                                String[] split = location.split(",");
                                if (StringUtil.isNotEmpty(split[0])) {
                                    station.setLongitude(Double.parseDouble(split[0]));
                                }
                                if (StringUtil.isNotEmpty(split[1])) {
                                    station.setLatitude(Double.parseDouble(split[1]));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                ;
            }
        }
    }

}
