package com.yimao.cloud.engineer.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.service.SmsService;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.SmsUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.engineer.feign.UserFeign;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.EngineerServiceAreaDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/****
 * 安装工服务
 *
 * @author zhangbaobao
 *
 */
@RestController
@Api(tags = "EngineerApiController")
@Slf4j
public class EngineerController {

	@Resource
	private UserFeign userFeign;

	@Resource
	private UserCache userCache;

	@Resource
	private SmsService smsService;

	/****
	 * 修改密码
	 *
	 * @param oldPwd     旧密码
	 * @param newPwd     新密码
	 * @param confPwd    确认新密码
	 * @return
	 */
	@PatchMapping(value = "/engineer/change/pwd")
	@ApiOperation(value = "安装工修改密码")
	public void change(@RequestParam("oldPwd") String oldPwd,
			           @RequestParam("newPwd") String newPwd, 
			           @RequestParam("confPwd") String confPwd) {
		Integer engineerId=userCache.getCurrentEngineerId();
		this.updateEngineerPwd(engineerId, oldPwd, newPwd, confPwd,null);
	}

	/****
	 * 找回密码
	 *
	 * @param oldPwd
	 * @param newPwd
	 * @param confPwd
	 */
	@PatchMapping(value = "/engineer/find/pwd")
	@ApiOperation(value = "找回密码")
	public void findpwd(@RequestParam(value = "oldPwd", required = false) String oldPwd, 
						@RequestParam("newPwd") String newPwd,
						@RequestParam("confPwd") String confPwd, 
						@RequestParam("phone") String phone,
						@RequestParam("code") String code) {

		EngineerDTO engineer = userFeign.getEngineerByPhone(phone);
		if (engineer == null) {
			throw new YimaoException("该手机号还未注册");
		}
		// 校验手机验证码
		Boolean bool = smsService.verifyCode(engineer.getPhone(), Constant.COUNTRY_CODE, code);
		if (!bool) {
			throw new YimaoException("验证码不正确");
		}
		this.updateEngineerPwd(null, oldPwd, newPwd, confPwd,phone);
	}

	/**
	 * 安装工详情
	 */
	@GetMapping(value = "/engineer/detail")
	@ApiOperation(value = "安装工详情")
	public Object details() {
		Integer engineerId = userCache.getCurrentEngineerId();
		EngineerDTO engineer = userFeign.getEngineerById(engineerId);
		if (engineer == null) {
			throw new YimaoException("安装工信息不存在");
		}
		engineer.setPassword(null);
		if (engineer.getForbidden()) {
			throw new YimaoException("安装工已被禁用");
		}
		List<EngineerServiceAreaDTO> list = userFeign.getEngineerServiceArea(engineer.getId());
		if (CollectionUtil.isNotEmpty(list)) {
			engineer.setServiceAreaList(list);
		}
		return ResponseEntity.ok(engineer);
	}

	/**
	 * 验证安装工手机号是否绑定
	 */
	@PostMapping(value = "/engineer/phone/bind")
	public Object bind(@RequestParam String username, @RequestParam String phone) {// 客服
		EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(), null);
		if (engineer == null) {
			throw new YimaoException("安装工信息不存在");
		}
		String telphone = engineer.getPhone();
		if (!phone.equals(telphone)) {
			throw new YimaoException("安装工手机号不匹配");
		}
		return ResponseEntity.ok(true);
	}

	/****
	 * 发送验证码
	 *
	 * @param phone
	 * @return
	 */
	@PostMapping(value = "/smscode")
	@ApiOperation(value = "安装工获取验证码")
	public void smscode(@RequestParam() String phone) {
		EngineerDTO engineer = userFeign.getEngineerByPhone(phone);
		if (engineer == null) {
			throw new YimaoException("安装工信息不存在");
		}
		try {
			String code = smsService.getCode(engineer.getPhone(), Constant.COUNTRY_CODE);
			String text = "【翼猫服务APP】您绑定手机的验证码是" + code + "。";
			String s = SmsUtil.sendSms(text, engineer.getPhone());
			log.info("安装工找回密码[" + engineer.getPhone() + ",发送的验证码为]：" + code + "，短信接口返回为：" + s);
		} catch (Exception e) {
			log.error("====" + engineer.getPhone() + ",发送验证码失败,异常信息=====" + e.getMessage());
			throw new YimaoException("发送验证码失败");
		}
	}

	/**
	 * 验证验证码
	 *//*
	 * @PostMapping(value = "/validate/code")
	 *
	 * @ApiOperation(value = "安装工验证验证码") public void validateCode(@RequestParam
	 * String username, @RequestParam String phone, @RequestParam() String code) {
	 * EngineerDTO engineer = userFeign.getEngineerByUserName(username.trim(),
	 * null); if (engineer == null) { throw new YimaoException("安装工信息不存在"); }
	 * //校验手机验证码 Boolean bool = smsService.verifyCode(engineer.getPhone(),
	 * Constant.COUNTRY_CODE, code); if (!bool) { throw new
	 * YimaoException("验证码不正确"); } }
	 */

	/***
	 * 更新密码
	 *
	 * @param engineerId
	 * @param oldPwd
	 * @param newPwd
	 * @param confPwd
	 */
	private void updateEngineerPwd(Integer engineerId, String oldPwd, String newPwd, String confPwd,String phone) {
		EngineerDTO engineer=null;
		if(engineerId!=null) {
			engineer = userFeign.getEngineerById(engineerId);
		}else if (!StringUtil.isEmpty(phone)) {
			engineer = userFeign.getEngineerByPhone(phone);
		}
		
		if (engineer == null) {
			throw new YimaoException("安装工信息不存在");
		}

		// 校验输入的原密码是否正确
		if (!StringUtil.isEmpty(oldPwd)) {
			if (!StringUtil.isEmpty(engineer.getPassword()) && !oldPwd.equals(engineer.getPassword())) {
				throw new YimaoException("旧密码不正确");
			}
			
			
			if (oldPwd.equals(newPwd)) {
				throw new YimaoException("新密码不能与旧密码一致");
			}
		}
		
		// 两次新密码不一致
		if (!newPwd.equals(confPwd)) {
			throw new YimaoException("两次新密码不一致");
		}

		EngineerDTO updateDTO = new EngineerDTO();
		updateDTO.setId(engineer.getId());
		updateDTO.setPassword(newPwd);
		userFeign.updatePassword(updateDTO);

	}

	/**
	 * 更新安装工信息
	 */
	@PutMapping(value = "/engineer/head")
	public void updateHeadImg(@RequestBody EngineerDTO dto) {
		userFeign.updateHeadImg(dto);
	}
}
