package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.system.AreaInfoDTO;
import com.yimao.cloud.system.po.Area;

import java.util.List;
import java.util.Map;

public interface AreaService {

    /**
     * 根据区域ID查询区域
     *
     * @param id 区域ID
     */
    Area getById(Integer id);

    /**
     * 根据区域ID获取省市区名称
     *
     * @param id 区域ID
     */
    Map<String, String> getPCRNamesById(Integer id);

    /**
     * 根据名称查询省市区ID集合
     *
     * @param name 名称
     * @return
     */
    List<Integer> getAreaIdsByName(String name);

    /**
     * 查询所有省
     *
     * @return
     */
    List<AreaDTO> listProvince();

    /**
     * 查询所有下级区域
     *
     * @param pid 上级ID
     * @return
     */
    List<AreaDTO> listSubArea(Integer pid);

    /**
     * 根据区域ID获取省市区名称信息
     *
     * @param id 区域ID
     */
    AreaInfoDTO getAreaInfoById(Integer id);

    /**
     * 获取地区
     *
     * @return list
     */
    List<AreaDTO> areaList();

    Integer getRegionIdByPCR(String province, String city, String region);
}
