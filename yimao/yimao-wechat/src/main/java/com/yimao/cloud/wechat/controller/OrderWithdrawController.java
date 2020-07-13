package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.pojo.dto.order.WithdrawQueryDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawSubDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@Api(tags = "OrderWithdrawController")
public class OrderWithdrawController {

    @Resource
    private OrderFeign orderFeign;

    /**
     * 提现记录列表
     *
     * @param pageNum          分页页数
     * @param pageSize         分页大小
     * @param withdrawQueryDTO 查询信息
     * @author hhf
     * @date 2019/4/23
     */
    @PostMapping(value = "/withdraw/record/{pageNum}/{pageSize}")
    @ApiOperation(value = "提现记录列表", notes = "提现记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "withdrawQueryDTO", value = "提现查询信息", required = true, dataType = "WithdrawQueryDTO", paramType = "body")
    })
    public PageVO<WithdrawSubDTO> withdrawRecordList(@PathVariable(value = "pageNum") Integer pageNum,
                                                     @PathVariable(value = "pageSize") Integer pageSize,
                                                     @RequestBody WithdrawQueryDTO withdrawQueryDTO) {
        return orderFeign.withdrawRecordList(pageNum, pageSize, withdrawQueryDTO);
    }
}
