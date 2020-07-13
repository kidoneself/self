package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordExportDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public interface WaterDeviceFilterChangeRecordService {

    /***
     * 功能描述:创建滤芯更换记录(pad客户端提交)
     *
     * @param: [dto]
     * @auther: liu yi
     * @date: 2019/5/15 10:58
     * @return: void
     */
    void createByClient(String filterNames,Integer deviceGroup,Integer source,String maintenanceWorkOrderId) ;

    /***
     * 功能描述:创建滤芯更换记录
     *
     * @param: [dto]
     * @auther: liu yi
     * @date: 2019/6/25 11:29
     * @return: void
     */
    void save(WaterDeviceFilterChangeRecordDTO dto);

    /***
     * 功能描述:根据id获取详情
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/5/15 10:57
     * @return:
     */
    WaterDeviceFilterChangeRecordDTO getFilterChangeRecordById(Integer id);

    /***
     * 功能描述:获取多个滤芯记录
     *
     * @param: [ids]
     * @auther: liu yi
     * @date: 2019/6/15 11:58
     * @return: java.util.List<com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO>
     */
    List<WaterDeviceFilterChangeRecordDTO> getFilterChangeRecordListByIds(Integer[] ids);

    /***
     * 功能描述:分页查询
     *
     * @param: [queryDTO, pageNum, pageSize]
     * @auther: liu yi
     * @date: 2019/5/15 10:57
     * @return:
     */
    PageVO<WaterDeviceFilterChangeRecordDTO> pageList(WaterDeviceFilterChangeRecordQueryDTO queryDTO, Integer pageNum, Integer pageSize);

    /***
     * 功能描述:根据SN和时间查询维护工单记录
     *
     * @param: [deviceSncode, materielDetailName]
     * @auther: liu yi
     * @date: 2019/5/16 15:50
     * @return:
     */
    List<WaterDeviceFilterChangeRecordDTO> getWaterDeviceFilterChangeRecordBySN(String deviceSncode, Date createTime, Integer source);

    /***
     * 功能描述:批量生效记录
     *
     * @param: [ids, effective]
     * @auther: liu yi
     * @date: 2019/5/16 16:46
     * @return: void
     */
    void forbiddenWaterDeviceFilterChangeRecord(Integer[] ids, Integer effective);

    void batchSave(List<WaterDeviceFilterChangeRecordDTO> list);
}
