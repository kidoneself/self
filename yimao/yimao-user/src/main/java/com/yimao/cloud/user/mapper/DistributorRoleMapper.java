package com.yimao.cloud.user.mapper;

import com.yimao.cloud.pojo.dto.user.DistributorRoleDTO;
import com.yimao.cloud.user.po.DistributorRole;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Lizhqiang
 * @date 2018/12/18
 */
public interface DistributorRoleMapper extends Mapper<DistributorRole> {

    List<DistributorRoleDTO> listAll();

}
