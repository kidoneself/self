package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.user.CustomerContidionDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import com.yimao.cloud.pojo.export.user.WaterDeviceUserExport;
import com.yimao.cloud.user.po.WaterDeviceUser;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface WaterDeviceUserMapper extends Mapper<WaterDeviceUser> {
    boolean existsWithOldId(@Param("oldId") String id);

    Page<WaterDeviceUserDTO> deviceUserList(CustomerContidionDTO query);

    /*List<WaterDeviceUserExport> customersList(CustomerContidionDTO query);*/
    Page<WaterDeviceUserExport> customersList(CustomerContidionDTO query);

    List<WaterDeviceUserDTO> datamove();

    WaterDeviceUser selectRealNameAndPhoneById(Integer id);
}
