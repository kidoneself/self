package com.yimao.cloud.hra.service;

import com.yimao.cloud.hra.po.HraDevice;
import com.yimao.cloud.pojo.dto.hra.HraDeviceDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceExportDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2018/10/31.
 */
public interface HraDeviceService {

    /**
     * @param pageNum
     * @param pageSize
     * @param query
     * @Description:查询HRA设备列表
     * @author ycl
     * @Return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.hra.HraDeviceDTO>
     * @Create: 2019/1/12 18:04
     */
    PageVO<HraDeviceDTO> queryStationOnline(Integer pageNum, Integer pageSize, HraDeviceQuery query);

    /**
     * @param stationId
     * @Description:服务站ID查询HRA设备
     * @author ycl
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.hra.HraDeviceDTO>
     * @Create: 2019/1/26 17:19
     */
    List<HraDeviceDTO> getHraDeviceByStationId(Integer stationId);


    void deleteDevice(Integer id);


    HraDevice findHraDeviceById(Integer id);

    void updateDevice(HraDevice hraDevice);

    void batchDelete(Integer[] ids);

    /**
     * 查询HRA设备信息
     *
     * @param deviceId HRA设备ID
     */
    HraDevice getHraDeviceByDeviceId(String deviceId);

    /**
     * 保存设备上线
     *
     * @param dto dto
     * @return dot
     */
    Boolean saveDeviceOnline(HraDeviceDTO dto);

    List<HraDeviceExportDTO> exportDevice(HraDeviceQuery hraDeviceQuery);

    boolean getDeviceStatus(Integer stationId);

    List<Integer> getHraStationIds();
}
