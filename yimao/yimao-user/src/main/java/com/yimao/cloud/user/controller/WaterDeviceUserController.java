package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.pojo.dto.user.CustomerContidionDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.mapper.WaterDeviceUserMapper;
import com.yimao.cloud.user.po.WaterDeviceUser;
import com.yimao.cloud.user.service.WaterDeviceUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 水机设备用户信息
 *
 * @author Zhang Bo
 * @date 2017/12/15.
 */
@RestController
public class WaterDeviceUserController {

    @Resource
    private WaterDeviceUserService waterDeviceUserService;
    @Resource
    private WaterDeviceUserMapper waterDeviceUserMapper;

    /**
     * 创建水机用户
     */
    @PostMapping(value = "/waterdeviceuser")
    public WaterDeviceUserDTO save(@RequestBody WaterDeviceUserDTO dto) {
        WaterDeviceUser deviceUser = new WaterDeviceUser(dto);
        return waterDeviceUserService.save(deviceUser);
    }

    /**
     * 根据手机号获取水机设备用户信息
     *
     * @param phone 手机号
     */
    @GetMapping(value = "/waterdeviceuser/phone")
    public WaterDeviceUserDTO getByPhone(@RequestParam String phone) {
        WaterDeviceUser deviceUser = waterDeviceUserService.getByPhone(phone);
        if (deviceUser == null) {
            return null;
        }
        WaterDeviceUserDTO dto = new WaterDeviceUserDTO();
        deviceUser.convert(dto);
        return dto;
    }

    /**
     * @Author ycl
     * @Description 业务管理系统-查询客户列表
     * @Date 14:24 2019/8/14
     * @Param
     **/
    @PostMapping("/waterdeviceuser/{pageNum}/{pageSize}")
    public PageVO<WaterDeviceUserDTO> pageQueryCustomer(@PathVariable(value = "pageNum") Integer pageNum,
                                                        @PathVariable(value = "pageSize") Integer pageSize,
                                                        @RequestBody(required = false) CustomerContidionDTO query) {
        PageVO<WaterDeviceUserDTO> page = waterDeviceUserService.pageQueryCustomer(pageNum, pageSize, query);
        if (page == null) {
            throw new NotFoundException("未找到客户信息。");
        }
        return page;
    }

    @GetMapping("/waterdeviceuser/{id}")
    public WaterDeviceUserDTO getDeviceUserDTOInfo(@PathVariable(value = "id") Integer id) {
        return waterDeviceUserService.getDeviceUserDTOInfo(id);
    }


    //编辑
    @PatchMapping(value = "waterdeviceuser")
    public void updateDeviceUserInfo(@RequestBody WaterDeviceUserDTO deviceUserDTO) {
        waterDeviceUserService.updateDeviceUserInfo(deviceUserDTO);
    }


    /*//客户列表导出
    @PostMapping(value = "/waterdeviceuser/export")
    public List<WaterDeviceUserExport> customersExport(@RequestBody CustomerContidionDTO query) {
        return waterDeviceUserService.customersList(query);
    }*/

    /**
     * 数据迁移用（业务不准调用）
     */
    @GetMapping("/waterdeviceuser/datamove")
    public Object datamove() {
        return waterDeviceUserMapper.datamove();
    }

    /**
     * 数据迁移用（业务不准调用）
     */
    @GetMapping("/waterdeviceuser/getByOldId")
    public WaterDeviceUserDTO getByOldId(@RequestParam(value = "oldId") String oldId) {
        WaterDeviceUser query = new WaterDeviceUser();
        query.setOldId(oldId);
        WaterDeviceUser deviceUser = waterDeviceUserMapper.selectOne(query);
        if (deviceUser != null) {
            WaterDeviceUserDTO dto = new WaterDeviceUserDTO();
            deviceUser.convert(dto);
            return dto;
        }
        return null;
    }

}
