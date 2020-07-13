package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.wechat.feign.UserFeign;
import com.yimao.cloud.wechat.service.WxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2018/10/31.
 */
@RestController
@Slf4j
@Api(tags = "MiniQRCodeController")
public class MiniQRCodeController {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private WxService wxService;
    @Resource
    private UserFeign userFeign;

    /**
     * @param userId
     * @Description: 用户专属小程序二维码
     * @author ycl
     * @Return: java.lang.Object
     * @Create: 2019/5/10 9:32
     */
    @GetMapping(value = {"/wxacode"})
    @ApiOperation(value = "获取用户专属小程序二维码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "query")
    })
    public Object getWxcode(@RequestParam Integer userId) {
        UserDTO dto = userFeign.getUserDTOById(userId);
        if (Objects.isNull(dto)) {
            log.error("获取用户专属小程序二维码失败，用户信息不存在。");
            throw new YimaoException("用户不存在");
        }

        if (StringUtil.isNotEmpty(dto.getWxacode())) {
            return ResponseEntity.ok(domainProperties.getWechat() + dto.getWxacode());
        }

        String scene = "shareId=" + userId;
        log.info("==========公众号小程序码获取时的参数============");
        log.info("===" + userId + "===" + scene + "===" + dto.getHeadImg() + "===");
        log.info("======================");
        String wxacode;
        try {
            String headImg = dto.getHeadImg();
            if (StringUtil.isNotEmpty(headImg) && !headImg.startsWith("http")) {
                if (EnvironmentEnum.LOCAL.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
                    headImg = "http://192.168.10.63" + headImg;
                } else {
                    headImg = domainProperties.getWechat() + headImg;
                }
            }
            wxacode = wxService.getWxacode(userId, scene, WechatConstant.MINI_PAGE, headImg, null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("远程调用,获取wxacode异常");
        }

        if (StringUtil.isEmpty(wxacode)) {
            log.error("获取用户专属小程序二维码失败，获取二维码失败。");
            throw new YimaoException("操作失败，请重试。");
        }

        dto.setWxacode(wxacode);
        userFeign.update(dto);
        return ResponseEntity.ok(domainProperties.getWechat() + wxacode);
    }


    /**
     * B接口，生成小程序码，可接受页面参数较短，生成个数不受限
     * 适用于需要的码数量极多的业务场景
     *
     * @param page
     * @param scene
     * @return
     */
    @GetMapping(value = {"/getwxacodeunlimit"})
    @ApiOperation(value = "生成小程序码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "scene", value = "参数scene为空", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "page", required = true, dataType = "String", paramType = "query")
    })
    public Object getwxacodeunlimit(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "scene") String scene,
            @RequestParam(name = "page") String page) {
        log.info("#######传入参数:page=" + page + ",scene=" + scene + ",userId= " + userId + "###########");
        if (userId == null) {
            throw new BadRequestException("传入参数userId为空!");
        }
        if (StringUtil.isEmpty(scene)) {
            throw new BadRequestException("传入参数scene为空!");
        }
        if (StringUtil.isEmpty(page)) {
            throw new BadRequestException("传入参数page为空!");
        }
        UserDTO userDTO = userFeign.getUserDTOById(userId);
        // 远程调用失败
        if (userDTO == null) {
            throw new NotFoundException("小程序码获取失败。");
        }
        // 远程调用成功
        String headImg = userDTO.getHeadImg();
        String oldWxacode = userDTO.getWxacode();
        String wxacode;
        try {
            if (StringUtil.isNotEmpty(headImg) && !headImg.startsWith("http")) {
                if (EnvironmentEnum.LOCAL.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
                    headImg = "http://192.168.10.63" + headImg;
                } else {
                    headImg = domainProperties.getWechat() + headImg;
                }
            }
            wxacode = wxService.getWxacode(userId, scene, page, headImg, oldWxacode);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("远程调用,获取wxacode异常");
        }
        if (StringUtil.isEmpty(wxacode)) {
            throw new YimaoException("小程序码获取失败。");
        }
        return wxacode;
    }


    @GetMapping("/store/getStoreCode")
    public Object getStoreCode(@RequestParam(name = "userId") Integer userId,
                               @RequestParam(name = "scene") String scene,
                               @RequestParam(name = "page") String page) {

        return wxService.getStoreCode(userId, scene, page);

    }

}
