package com.yimao.cloud.station.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.BeanHelper;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.station.StationAdminAreasCacheDTO;
import com.yimao.cloud.pojo.dto.station.StationAdminDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyServiceAreaDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import com.yimao.cloud.pojo.query.station.StationAdminQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.StationVO;
import com.yimao.cloud.station.constant.StationConstant;
import com.yimao.cloud.station.feign.SystemFeign;
import com.yimao.cloud.station.service.StationAdminService;
import com.yimao.cloud.station.service.StationRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 服务站员工
 *
 * @author yaoweijun
 */
@RestController
@Api(tags = "StationAdminController")
@Slf4j
public class StationAdminController {
    @Resource
    private UserCache userCache;

    @Resource
    private StationAdminService stationAdminService;

    @Resource
    private SystemFeign systemFeign;

    @Resource
    private StationRoleService stationRoleService;

    /**
     * 系统-员工管理-新增员工
     */

    @PostMapping(value = "/admin")
    @ApiOperation(value = "创建用户")
    @ApiImplicitParam(name = "stationAdmin", value = "创建员工", required = true, dataType = "StationAdminDTO", paramType = "body")
    public void save(@RequestBody StationAdminDTO stationAdmin) {

        log.info("admin={}", JSON.toJSONString(stationAdmin));

        stationAdminService.save(stationAdmin);

    }

    /**
     * 系统-员工管理- 编辑员工
     *
     * @param stationAdmin
     */
    @PutMapping(value = "/admin")
    @ApiOperation(value = "编辑用户")
    @ApiImplicitParam(name = "stationAdmin", value = "创建员工", required = true, dataType = "StationAdminDTO", paramType = "body")
    public void update(@RequestBody StationAdminDTO stationAdmin) {
        log.info("admin={}", JSON.toJSONString(stationAdmin));
        stationAdminService.update(stationAdmin);
    }

    /**
     * 系统-员工管理-查询员工列表
     *
     * @return
     */
    @PostMapping(value = "/admins/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StationAdminQuery", paramType = "body")
    })
    public PageVO<StationAdminDTO> page(@PathVariable Integer pageNum,
                                        @PathVariable Integer pageSize,
                                        @RequestBody StationAdminQuery query) {
        return stationAdminService.page(pageNum, pageSize, query);
    }


    /**
     * 系统-员工管理-删除员工
     *
     * @return
     */
    @DeleteMapping(value = "/admin/{id}")
    @ApiOperation(value = "删除用户")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    public void delete(@PathVariable Integer id) {

        stationAdminService.delete(id);
    }

    /**
     * 系统-员工管理- 禁用/启用账号
     *
     * @param id 管理员ID
     */
    @PatchMapping(value = "/admin/{id}")
    @ApiOperation(value = "禁用账号")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    public void forbidden(@PathVariable Integer id) {
        stationAdminService.forbidden(id);
    }


    /**
     * 修改密码
     *
     * @param originPassword
     * @param changePassword
     */
    @PostMapping(value = "/admin/changePassword")
    @ApiOperation(value = "修改密码")
    public void changePassword(String originPassword, String changePassword) {

        if (StringUtils.isBlank(originPassword)) {
            throw new BadRequestException("原密码不能为空");
        }

        if (StringUtils.isBlank(changePassword)) {
            throw new BadRequestException("修改密码不能为空");
        }

        stationAdminService.changePassword(originPassword, changePassword);
    }


    /**
     * system调用（用于服务站服务区域变更）
     *
     * @param obj
     */
    @PostMapping(value = "/admins/stationAreaChange/{stationId}")
    public void stationAreaChange(@PathVariable("stationId") Integer stationId, @RequestBody List<StationServiceAreaDTO> areas) {

        log.info("system修改区域服务站id={}", stationId);

        log.info("system修改服务站区域集合={}", JSON.toJSONString(areas));

        stationAdminService.updateStationAdminAreasCache(stationId, areas);
    }


    /**
     * 系统-员工管理-新增/编辑-根据服务站公司选服务区域使用,展示门店区域列表及用户是否绑定标识
     * 1.
     * 2.
     *
     * @param stationCompanyId为编辑或新增的公司id
     * @param adminId 为编辑的员工id 新增则为空
     * @return
     */
    @GetMapping(value = "/admin/stationAndServiceArea")
    @ApiOperation(value = "根据服务站公司id获取服务站门店和服务站服务区域")
    public List<StationVO> getStationAndServiceArea(Integer stationCompanyId, Integer adminId) {
        //true 则为普通用户编辑或新增旗下公司用户  false则为管理员编辑其他用户
        boolean flag = true;
        //查询调用该接口员工所属的服务站公司id
        Integer cacheStationCompanyId = userCache.getJWTInfo().getStationCompanyId();

       
        //判断是否是超级管理员
        if (cacheStationCompanyId.equals(StationConstant.SUPERCOMPANYID)) {
            //判断是否为管理员编辑超管角色
            if (cacheStationCompanyId.equals(stationCompanyId)) {//stationCompanyId为0
                // 查询所有服务站及其所属区域
                List<StationDTO> stationDTOS = systemFeign.getAllStation();

                return BeanHelper.copyWithCollection(stationDTOS, StationVO.class);

            } else {//(stationCompanyId不存在0，所以当不为0的时候必定为编辑或新增其他用户)
                //管理员编辑其他公司用户
                cacheStationCompanyId = stationCompanyId;

                flag = false;
            }

        } else {
            if (!cacheStationCompanyId.equals(stationCompanyId)) {
                throw new YimaoException("服务站公司与用户所在服务站公司不匹配。");
            }
        }
        
        //查询调用该接口员工所属的服务门店
        Set<Integer> stations = userCache.getStationUserAreas(0,null);
        //查询调用该接口员工所属的服务区域
        Set<Integer> areas = userCache.getStationUserAreas(1,PermissionTypeEnum.ALL.value);

        //针对被编辑用户是否绑定该服务站id与区域id集合
        Set<Integer> adminHaveStations = new HashSet();
        Set<Integer> adminHaveAreas = new HashSet();
        //查询被编辑用户绑定区域（新增则不需要，以上集合size为0）
        if (Objects.nonNull(adminId)) {
        	
            StationAdminAreasCacheDTO stationAdminAreasCacheDTO = stationRoleService.findStationsAndAreasByAdminId(adminId);
            adminHaveStations.addAll(stationAdminAreasCacheDTO.getStationIds());
            adminHaveAreas.addAll(stationAdminAreasCacheDTO.getPreAreaIds());
            adminHaveAreas.addAll(stationAdminAreasCacheDTO.getAfterAreaIds());
        }


        //查询服务站的服务区域
        StationCompanyDTO stationCompany = systemFeign.getStationCompanyById(cacheStationCompanyId);
        log.info("查询stationCompany={}", JSON.toJSONString(stationCompany));
        if (Objects.isNull(stationCompany)) {
            throw new YimaoException("该服务站公司不存在");
        }

        List<StationDTO> stationDTOS = systemFeign.getStationAndServiceArea(cacheStationCompanyId);
        log.info("查询stationDTOS={}", JSON.toJSONString(stationDTOS));
        if (CollectionUtil.isEmpty(stationDTOS)) {
            throw new YimaoException("该服务站公司未发展服务站门店");
        }

        //需要根据服务站公司的服务区域剔除服务站公司下门店服务区域的非服务站公司的服务区域
        List<StationCompanyServiceAreaDTO> companyAreas = stationCompany.getServiceAreas();
        List<Integer> companyAreaIds = new ArrayList<Integer>();
        //设置区域id
        for (StationCompanyServiceAreaDTO stationCompanyServiceAreaDTO : companyAreas) {
            companyAreaIds.add(stationCompanyServiceAreaDTO.getAreaId());
        }

        if (flag) {

            Iterator<StationDTO> stationIterator = stationDTOS.iterator();
            while (stationIterator.hasNext()) {
                StationDTO station = stationIterator.next();
                Integer stationId = station.getId();
                //判断调用该接口员工所绑定服务站缓存是否有该服务站，没有删除
                if (!stations.contains(stationId)) {
                    stationIterator.remove();
                }

            }
        }
        log.info("筛选后stationDTOS={}", JSON.toJSONString(stationDTOS));
        log.info("companyAreaIds={}", JSON.toJSONString(companyAreaIds));
        for (StationDTO station : stationDTOS) {
            Integer stationId = station.getId();
            if (adminHaveStations.contains(stationId)) {
                station.setStationHave(true);
            }

            //获取服务站对应的区域
            List<StationServiceAreaDTO> stationAreas = station.getServiceAreas();

            if (CollectionUtil.isNotEmpty(stationAreas)) {
                Iterator<StationServiceAreaDTO> iterator = stationAreas.iterator();
                //遍历区域
                while (iterator.hasNext()) {
                    StationServiceAreaDTO stationArea = iterator.next();

                    Integer AreaId = stationArea.getAreaId();
                    //判断服务站公司服务区域都否有该区域，没有删除
                    if (!companyAreaIds.contains(AreaId)) {
                        iterator.remove();
                        continue;
                    }

                    //判断调用该接口员工所绑定服务区域缓存是否有该地区，没有删除
                    if (flag && !areas.contains(AreaId)) {
                        iterator.remove();
                        continue;
                    }

                    //判断被编辑用户是否拥有该区域
                    if (adminHaveAreas.contains(AreaId)) {
                        stationArea.setAreaHave(true);
                    }
                }

            }

        }

        return BeanHelper.copyWithCollection(stationDTOS, StationVO.class);
    }


}
