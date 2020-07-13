package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.water.SimCardAccountDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.SimCardAccount;
import com.yimao.cloud.water.service.SimCardAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：SIM运营商分配的权限账号
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@RestController
public class SimCardAccountController {

    @Resource
    private SimCardAccountService simCardAccountService;

    /**
     * 创建SIM运营商分配的权限账号
     *
     * @param dto SIM运营商分配的权限账号
     */
    @PostMapping(value = "/simcard/account")
    public void save(@RequestBody SimCardAccountDTO dto) {
        SimCardAccount simCardAccount = new SimCardAccount(dto);
        simCardAccountService.save(simCardAccount);
    }

    /**
     * 修改SIM运营商分配的权限账号
     *
     * @param dto SIM运营商分配的权限账号
     */
    @PutMapping(value = "/simcard/account")
    public void update(@RequestBody SimCardAccountDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            SimCardAccount simCardAccount = new SimCardAccount(dto);
            simCardAccountService.update(simCardAccount);
        }
    }

    /**
     * 查询SIM运营商分配的权限账号（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    @GetMapping(value = "/simcard/account/{pageNum}/{pageSize}")
    public PageVO<SimCardAccountDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return simCardAccountService.page(pageNum, pageSize);
    }

    /**
     * 获取所有SIM运营商
     */
    @GetMapping(value = "/simcard/account/all")
    public List<SimCardAccountDTO> list() {
        return simCardAccountService.list();
    }

}
