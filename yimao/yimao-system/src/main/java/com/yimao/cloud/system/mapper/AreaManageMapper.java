package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.AreaManageDTO;
import com.yimao.cloud.system.po.AreaManage;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Lizhqiang
 * @date 2019-08-19
 */

public interface AreaManageMapper extends Mapper<AreaManage> {


    Page<AreaManage> page(@Param("id") Integer id, @Param("level") Integer level, @Param("pid") Integer pid);

	List<AreaManageDTO> getAreaManagerList();
}
