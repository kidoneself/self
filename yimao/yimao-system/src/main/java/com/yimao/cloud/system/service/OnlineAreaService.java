package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.OnlineArea;

public interface OnlineAreaService {

    /**
     * 新增上线地区
     *
     * @param record 上线地区
     */
    void save(OnlineArea record);

    /**
     * 服务站是否升级新流程校验
     *
     * @param province 省
     * @param city     市
     * @param region   区
     * @param level    区域等级：1-省；2-省市；3-省市区
     */
    OnlineArea getOnlineAreaByPCR(String province, String city, String region, Integer level);

    /**
     * 查询列表
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    PageVO<OnlineArea> onlineAreaList(String province, String city, String region, Integer pageNum, Integer pageSize);

    /**
     * 设置地区上线
     *
     * @param id
     */
    void setAreaOnline(Integer id);

    /**
     * 根据id删除
     *
     * @param id
     */
    void deleteOnlineAreaById(Integer id);
}
