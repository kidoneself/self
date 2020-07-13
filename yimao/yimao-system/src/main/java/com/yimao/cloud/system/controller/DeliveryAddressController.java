package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.order.DeliveryAddressDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Lizhqiang
 * @date 2019/3/27
 */
@Slf4j
@RestController
@Api(tags = "DeliveryAddressController")
public class DeliveryAddressController {


    @Resource
    private OrderFeign orderFeign;

    @GetMapping("/address/{pageNum}/{pageSize}")
    @ApiOperation(value = "地址分页", notes = "地址分页")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "页码", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "pageSize", value = "页数", paramType = "path", dataType = "Long")})
    public Object addressPage(@PathVariable("pageNum") Integer pageNum,
                              @PathVariable("pageSize") Integer pageSize) {
        PageVO<DeliveryAddressDTO> page = orderFeign.addressPage(pageNum, pageSize);
        return ResponseEntity.ok(page);
    }


    /**
     * 新增地址
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/delivery/address")
    @ApiOperation(notes = "新增地址", value = "新增地址")
    @ApiImplicitParam(name = "dto", value = "新增地址", dataType = "DeliveryAddressDTO", paramType = "body")
    public Object addDeliveryAddress(@RequestBody DeliveryAddressDTO dto) {
        orderFeign.addDeliveryAddress(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 设置为默认收货地址
     *
     * @param id
     * @return
     */
    @PatchMapping(value = "/delivery/{id}")
    @ApiOperation(notes = "设置为默认发货地址", value = "设置为默认发货地址")
    @ApiImplicitParam(name = "id", value = "地址id", dataType = "Long", paramType = "path")
    public Object delivery(@PathVariable() Integer id) {

        orderFeign.delivery(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 设置为默认退货地址
     *
     * @param id
     * @return
     */
    @PatchMapping(value = "/refund/{id}")
    @ApiOperation(notes = "设置为默认退货地址", value = "设置为默认退货地址")
    @ApiImplicitParam(name = "id", value = "地址id", dataType = "Long", paramType = "path")
    public Object refund(@PathVariable() Integer id) {
        orderFeign.refund(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * 删除地址
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/delivery/address/{id}")
    @ApiOperation(notes = "删除地址", value = "删除地址")
    @ApiImplicitParam(name = "id", value = "地址id", dataType = "Long", paramType = "path")
    public Object addDeliveryAddress(@PathVariable Integer id) {
        orderFeign.deleteDeliveryAddress(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 编辑地址
     *
     * @param dto
     * @return
     */
    //@PutMapping(value = "/delivery/address")
    @RequestMapping(value = "/delivery/address", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(notes = "编辑地址", value = "编辑地址")
    @ApiImplicitParam(name = "dto", value = "编辑地址", dataType = "DeliveryAddressDTO", paramType = "body")
    public Object editorDeliveryAddress(@RequestBody DeliveryAddressDTO dto) {
        orderFeign.editorDeliveryAddress(dto);
        return ResponseEntity.noContent().build();
    }
}
