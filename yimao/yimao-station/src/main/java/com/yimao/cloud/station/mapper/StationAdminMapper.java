package com.yimao.cloud.station.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.station.StationAdminDTO;
import com.yimao.cloud.pojo.query.station.StationAdminQuery;
import com.yimao.cloud.pojo.vo.station.StationCompanyVO;
import com.yimao.cloud.station.po.StationAdmin;

import tk.mybatis.mapper.common.Mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StationAdminMapper extends Mapper<StationAdmin>{

    /**
     * 查询用户名数量（员工新增编辑使用）
     *
     * @param userName
     * @param stationCompanyId
     * @param adminId          员工编辑则需要剔除本身id
     * @return
     */
    int selectUserNameCount(@Param("userName") String userName, @Param("id") Integer adminId);

    /**
     * 根据用户名和服务站公司id查询用户（登录使用，包含可用不可用用户）
     *
     * @param userName
     * @param stationCompanyId
     * @return
     */
    StationAdmin selectStationAdmin(@Param("userName") String userName);

    /**
     * 根据条件查询员工数量，这里指查询可用用户
     *
     * @param stationAdmin
     * @return
     */
    int selectAdminCount(StationAdmin stationAdmin);

    /**
     * 根据条件查询员工列表，这里指查询可用用户
     *
     * @param admin
     * @return
     */
    List<StationAdmin> selectByQuery(StationAdmin admin);

    /**
     * 根据条件查询服务站员工列表（分页）
     *
     * @param query 查询条件
     * @return
     */
    Page<StationAdminDTO> listAdminByQuery(StationAdminQuery query);

	List<StationCompanyVO> getAllStationCompany();

	/**
	 * 查询用户中手机号码数量
	 * @param phone
	 * @param adminId
	 * @return
	 */
	int selectPhoneCount(@Param("phone")String phone, @Param("id")Integer adminId);
	/**
	 * 根据手机号码查询用户（登录查询）
	 * @param phone
	 * @return
	 */
	StationAdmin selectStationAdminByPhone(@Param("phone")String phone);

	/**
	 * 根据权限集合查询所有绑定权限的用户
	 * @param roleIds
	 * @return
	 */
	List<StationAdmin> selectByRoleList(@Param("roleIds")List<Integer> roleIds);
}