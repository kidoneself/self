package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.CustomerContidionDTO;
import com.yimao.cloud.pojo.export.user.WaterDeviceUserExport;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.Distributor;
import com.yimao.cloud.user.po.WaterDeviceUser;

import java.util.List;

public interface WaterDeviceUserService {

    WaterDeviceUserDTO save(WaterDeviceUser deviceUser);

    WaterDeviceUser getByPhone(String phone);

    List<WaterDeviceUser> listByDistributor(Integer distributorId);

    PageVO<WaterDeviceUserDTO> pageQueryCustomer(Integer pageNum, Integer pageSize, CustomerContidionDTO query);

    WaterDeviceUserDTO getDeviceUserDTOInfo(Integer id);

    void updateDeviceUserInfo(WaterDeviceUserDTO deviceUserDTO);

   /* List<WaterDeviceUserExport> customersList(CustomerContidionDTO query);*/

	int updateDeviceUserDistributorId(Integer oldDistributorId, Distributor newDistributor);

}
