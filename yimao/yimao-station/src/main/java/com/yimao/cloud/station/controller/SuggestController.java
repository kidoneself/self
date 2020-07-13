package com.yimao.cloud.station.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.SuggestStatusEnum;
import com.yimao.cloud.base.enums.Terminal;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.station.StationAdminCacheDTO;
import com.yimao.cloud.pojo.dto.system.SuggestDTO;
import com.yimao.cloud.pojo.query.system.SuggestQuery;
import com.yimao.cloud.station.feign.SystemFeign;
import com.yimao.cloud.station.mapper.StationRoleMapper;
import com.yimao.cloud.station.po.StationRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@Slf4j
@Api(tags = "SuggestController")
public class SuggestController {

    @Resource
    private UserCache userCache;
    @Resource
    private StationRoleMapper stationRoleMapper;
    @Resource
    private SystemFeign systemFeign;


    /**
     * 站务系统--内容--提交建议反馈
     *
     * @param dto
     */
    @PostMapping("/station/suggest/submit")
    @ApiOperation(value = "提交建议反馈")
    @ApiImplicitParam(name = "dto", value = "反馈信息", required = true, dataType = "SuggestDTO", paramType = "body")
    public void submitSuggest(@RequestBody SuggestDTO dto) {
        //必传参数校验
        checkParam(dto);
        StationAdminCacheDTO userInfo = userCache.getStationUserInfo();
        StationRole role = stationRoleMapper.selectByPrimaryKey(userInfo.getRoleId());
        dto.setRoleName(role.getRoleName());
        dto.setRoleId(userInfo.getRoleId());
        dto.setUserId(userCache.getUserId());
        dto.setName(userInfo.getRealName());
        dto.setTerminal(Terminal.STATION.value);
        dto.setStatus(SuggestStatusEnum.NO_REPLY.value);
        dto.setTime(new Date());
        systemFeign.submitSuggest(dto);
    }

    private void checkParam(SuggestDTO dto) {

        if (dto.getStationId() == null) {
            throw new BadRequestException("请选择服务中心!");
        }

        if (dto.getSuggestType() == null) {
            throw new BadRequestException("请选择建议类型!");
        }

        if (StringUtil.isEmpty(dto.getContent())) {
            throw new BadRequestException("反馈内容不能为空!");
        }
    }

    /**
     * 内容--建议反馈--建议列表查询
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @return
     */
    @RequestMapping(value = "/suggest/{pageNum}/{pageSize}", method = RequestMethod.POST)
    @ApiOperation(value = "建议列表查询", notes = "建议列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "SuggestQuery", paramType = "body")
    })
    public Object page(@PathVariable Integer pageNum,
                       @PathVariable Integer pageSize,
                       @RequestBody SuggestQuery query) {
        query.setUserId(userCache.getUserId());
        query.setTerminal(Terminal.STATION.value);
        return systemFeign.pageSuggest(pageNum, pageSize, query);
    }

    /**
     * 建议类型筛选条件下拉框
     */
    @RequestMapping(value = "/suggestType/list", method = RequestMethod.GET)
    @ApiOperation(value = "建议类型筛选条件下拉框", notes = "建议类型筛选条件下拉框")
    public Object listSuggestType() {

        return systemFeign.listSuggestType(Terminal.STATION.value);
    }

 
}
