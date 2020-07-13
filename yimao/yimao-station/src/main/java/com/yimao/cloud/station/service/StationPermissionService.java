package com.yimao.cloud.station.service;

import com.yimao.cloud.pojo.dto.station.StationPermissionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.station.po.StationPermission;

import java.util.List;
import java.util.Set;

/**
 * 站务管理权限管理业务层
 *
 * @author Liu Long Jie
 * @date 2019/12/25.
 */
public interface StationPermissionService {
    void save(StationPermission permission);

    void delete(Integer id);

    void update(StationPermission permission);

    PageVO<StationPermissionDTO> page(Integer pageNum, Integer pageSize, Integer menuId);

    void batchSave(List<StationPermissionDTO> dtoList);

	boolean existPermission(String method, String url);

    Set<Integer> getPermissionIdsByRoleId(Integer roleId);
}
