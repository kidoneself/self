package com.yimao.cloud.station.mapper;

import com.yimao.cloud.station.po.StationRoleMenu;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StationRoleMenuMapper extends Mapper<StationRoleMenu> {
    void batchInsert(@Param("list") List<StationRoleMenu> roleMenuList);
}