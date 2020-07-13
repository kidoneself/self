package com.yimao.cloud.station.service;

import java.util.List;

import com.yimao.cloud.pojo.dto.station.StationAdminDTO;
import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import com.yimao.cloud.pojo.query.station.StationAdminQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.StationAdminLoginVO;
import com.yimao.cloud.pojo.vo.station.StationCompanyVO;
import com.yimao.cloud.station.po.StationAdmin;

import javax.servlet.http.HttpServletRequest;

public interface StationAdminService {

    void save(StationAdminDTO stationAdmin);

    StationAdminLoginVO login(String userName, String password, HttpServletRequest request);

    void createStationAdminCache(Integer id);

    void update(StationAdminDTO stationAdmin);
	
	void updateStationAdminPermissionCache(Integer adminId, Integer stationCompanyId, Integer roleId);
	
	void updateStationAdminAreasCache(StationAdmin stationAdmin);
	
	void updateStationAdminAreasCache(Integer stationId,List<StationServiceAreaDTO> areas);
	
	void updateStationAdminInfoCache(StationAdmin origStationAdmin,StationAdmin updateStationAdmin);
	
	void updateStationAdminPermissionCache(Integer roleId);
	
	void updateStationAdminPermissionCache(List<StationAdmin> adminList);

    PageVO<StationAdminDTO> page(Integer pageNum, Integer pageSize, StationAdminQuery query);

	void delete(Integer id);

	void forbidden(Integer id);

	List<StationCompanyVO> getAllStationCompany();

	void changePassword(String originPassword, String changePassword);

	StationAdminLoginVO phoneMessageLogin(String phone, Integer code, HttpServletRequest request);

	boolean checkStationAdminByPhone(String phone);

}
