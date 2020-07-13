package com.yimao.cloud.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.user.CustomerContidionDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.mapper.DistributorMapper;
import com.yimao.cloud.user.mapper.WaterDeviceUserMapper;
import com.yimao.cloud.user.po.Distributor;
import com.yimao.cloud.user.po.WaterDeviceUser;
import com.yimao.cloud.user.service.WaterDeviceUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * 描述：水机设备用户信息
 *
 * @Author Zhang Bo
 * @Date 2019/8/14
 */
@Service
@Slf4j
public class WaterDeviceUserServiceImpl implements WaterDeviceUserService {

    @Resource
    private WaterDeviceUserMapper waterDeviceUserMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private DistributorMapper distributorMapper;

    @Override
    public WaterDeviceUserDTO save(WaterDeviceUser deviceUser) {
        WaterDeviceUser query = new WaterDeviceUser();
        query.setPhone(deviceUser.getPhone());
        query.setRealName(deviceUser.getRealName());//逻辑变更:根据手机号和姓名查询水机用户是否已存在
        List<WaterDeviceUser> list = waterDeviceUserMapper.select(query);
        if (CollectionUtil.isNotEmpty(list)) {
            WaterDeviceUser dbRecord = list.get(list.size() - 1);
            deviceUser.setId(dbRecord.getId());
            deviceUser.setOldId(dbRecord.getOldId());
            deviceUser.setCreateTime(dbRecord.getCreateTime());
            deviceUser.setUpdateTime(new Date());
            waterDeviceUserMapper.updateByPrimaryKeySelective(deviceUser);
        } else {
            waterDeviceUserMapper.insert(deviceUser);
            //设置oldId
            WaterDeviceUser update = new WaterDeviceUser();
            update.setId(deviceUser.getId());
            update.setOldId(String.valueOf(deviceUser.getId()));
            waterDeviceUserMapper.updateByPrimaryKeySelective(update);
            deviceUser.setOldId(String.valueOf(deviceUser.getId()));
        }
        WaterDeviceUserDTO dto = new WaterDeviceUserDTO();
        deviceUser.convert(dto);
        return dto;
    }

    @Override
    public WaterDeviceUser getByPhone(String phone) {
        Example example = new Example(WaterDeviceUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone", phone);
        example.orderBy("createTime").desc();
        List<WaterDeviceUser> list = waterDeviceUserMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<WaterDeviceUser> listByDistributor(Integer distributorId) {
        WaterDeviceUser query = new WaterDeviceUser();
        query.setDistributorId(distributorId);
        return waterDeviceUserMapper.select(query);
    }


    @Override
    public PageVO<WaterDeviceUserDTO> pageQueryCustomer(Integer pageNum, Integer pageSize, CustomerContidionDTO query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceUserDTO> deviceUserDTOList = waterDeviceUserMapper.deviceUserList(query);
        return new PageVO<>(pageNum, deviceUserDTOList);
    }

    @Override
    public WaterDeviceUserDTO getDeviceUserDTOInfo(Integer id) {
        WaterDeviceUser waterDeviceUser = waterDeviceUserMapper.selectByPrimaryKey(id);
        if (null != waterDeviceUser) {
            WaterDeviceUserDTO dto = new WaterDeviceUserDTO();
            waterDeviceUser.convert(dto);
            if (waterDeviceUser.getDistributorId() != null) {
                Distributor distributor = distributorMapper.selectByPrimaryKey(waterDeviceUser.getDistributorId());
                if (distributor != null) {
                    dto.setDistributorUserId(distributor.getUserId());
                }
            }
            return dto;
        }
        return null;
    }

    // @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateDeviceUserInfo(WaterDeviceUserDTO deviceUserDTO) {
        if (deviceUserDTO.getId() == null) {
            throw new BadRequestException("参数错误");
        }
        WaterDeviceUser record = waterDeviceUserMapper.selectRealNameAndPhoneById(deviceUserDTO.getId());
        if (record == null) {
            throw new BadRequestException("操作对象不存在");
        }
        String realName = record.getRealName();
        String phone = record.getPhone();
        WaterDeviceUser deviceUser = new WaterDeviceUser();
        deviceUser.setId(deviceUserDTO.getId());
        deviceUser.setRealName(deviceUserDTO.getRealName());
        deviceUser.setSex(deviceUserDTO.getSex());
        deviceUser.setPhone(deviceUserDTO.getPhone());
        deviceUser.setIdCard(deviceUserDTO.getIdCard());
        deviceUser.setDegree(deviceUserDTO.getDegree());
        deviceUser.setEmail(deviceUserDTO.getEmail());
        int num = waterDeviceUserMapper.updateByPrimaryKeySelective(deviceUser);
        if (num == 0) {
            throw new YimaoException("编辑水机客户信息失败");
        }
        if (!Objects.equals(realName, deviceUserDTO.getRealName()) || !Objects.equals(phone, deviceUserDTO.getPhone())) {
            Map<String, Object> map = new HashMap<>();
            map.put("deviceUserId", deviceUserDTO.getId());
            map.put("deviceUserName", deviceUserDTO.getRealName());
            map.put("deviceUserPhone", deviceUserDTO.getPhone());
            rabbitTemplate.convertAndSend(RabbitConstant.CHANGE_DEVICE_USER, map);
        }
    }

    /*@Override
    public List<WaterDeviceUserExport> customersList(CustomerContidionDTO query) {
        return waterDeviceUserMapper.customersList(query);
    }*/

    /**
     * 转让经销商更新水机用户绑定的经销商
     */
    @Override
    public int updateDeviceUserDistributorId(Integer oldDistributorId, Distributor newDistributor) {
        Example u = new Example(WaterDeviceUser.class);
        Example.Criteria uCriteria = u.createCriteria();
        uCriteria.andEqualTo("distributorId", oldDistributorId);
        WaterDeviceUser waterDeviceUser = new WaterDeviceUser();
        waterDeviceUser.setDistributorId(newDistributor.getId());
        waterDeviceUser.setOldDistributorId(newDistributor.getId().toString());
        waterDeviceUser.setDistributorAccount(newDistributor.getUserName());
        waterDeviceUser.setDistributorName(newDistributor.getRealName());
        waterDeviceUser.setRoleLevel(newDistributor.getRoleLevel());
        waterDeviceUser.setRoleName(newDistributor.getRoleName());
        waterDeviceUser.setDistributorPhone(newDistributor.getPhone());
        waterDeviceUser.setDistributorIdCard(newDistributor.getIdCard());
        waterDeviceUser.setDistributorProvince(newDistributor.getProvince());
        waterDeviceUser.setDistributorCity(newDistributor.getCity());
        waterDeviceUser.setDistributorRegion(newDistributor.getRegion());
        waterDeviceUser.setDistributorAddress(newDistributor.getAddress());
        return waterDeviceUserMapper.updateByExampleSelective(waterDeviceUser, u);
    }

}
