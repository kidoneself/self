package com.yimao.cloud.station.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.SmsUtil;
import com.yimao.cloud.base.utils.VerificationCodeImgUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import com.yimao.cloud.pojo.query.system.StationQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.StationCompanyVO;
import com.yimao.cloud.pojo.vo.station.StationVO;
import com.yimao.cloud.station.constant.StationConstant;
import com.yimao.cloud.station.feign.SystemFeign;
import com.yimao.cloud.station.service.StationAdminService;
import com.yimao.cloud.station.util.CheckUtils;
import com.yimao.cloud.station.util.YunPianMsgRes;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 公共模块（无需权限）
 *
 * @author yaoweijun
 */
@RestController
@Slf4j
public class CommonController {

    @Resource
    private SystemFeign systemFeign;

    @Resource
    private UserCache userCache;

    @Resource
    private RedisCache redisCache;

    @Resource
    private StationAdminService stationAdminService;

    /**
     * 登录-验证码发送
     *
     * @param phone
     * @return
     */
    @GetMapping(value = "/common/sendCode")
    @ApiOperation(value = "登录-验证码发送")
    public void sendCode(@RequestParam("phone")String phone) {

    	 if (StringUtils.isBlank(phone)) {
             throw new BadRequestException("手机号码不能为空。");
         }

         if(! CheckUtils.isMobile(phone)) {
         	throw new BadRequestException("手机号格式错误。");
         }


         boolean exist= redisCache.hasKey(StationConstant.STATION_PHONETOKEN+phone);
         //校验该手机号发送次数
         if(exist) {
        	 Integer count=(Integer) redisCache.hget(StationConstant.STATION_PHONETOKEN+phone, "count");
        	 if(count>=3) {
        		 throw new BadRequestException("短信发送失败，短信请求限制1小时内3次，请勿重复频繁请求！请在1小时后重新获取验证码！");
        	 }else {
        		 //未满则次数+1
        		 redisCache.hset(StationConstant.STATION_PHONETOKEN+phone, "count",++count);
        		 //防止设置次数时key失效，导致设置的该key为永久
        		 if(! redisCache.hHasKey(StationConstant.STATION_PHONETOKEN+phone, "code")) {
        			 redisCache.delete(StationConstant.STATION_PHONETOKEN+phone);
        			 throw new BadRequestException("发送短信验证码失效，重新再试。");
        		 }
        	 }

         }else {
        	 Map<String,Object> map=new HashedMap();
        	 map.put("code", (int)((Math.random()*9+1)*100000));
        	 map.put("count", 1);
        	 redisCache.hmset(StationConstant.STATION_PHONETOKEN+phone, map, StationConstant.STATION_PHONETOKEN_EXPIRE);
         }

         //查询用户有效性
         boolean flag = stationAdminService.checkStationAdminByPhone(phone);

         if(!flag) {
        	 throw new BadRequestException("用户已禁用，发送失败");
         }

         //发送短信
         int sendCode=(int) redisCache.hget(StationConstant.STATION_PHONETOKEN+phone, "code");
         log.info("sendCode={}",sendCode);
         String messageContent=StationConstant.LOGIN_PHONE_MESSAGE;
         messageContent=messageContent.replaceAll("#code#", sendCode+"");

         String msgRes = SmsUtil.sendSms(messageContent, phone);
         log.info("发送消息返回结果={}",msgRes);

         YunPianMsgRes res=transferMsgRes(msgRes);
         //新增短信发送纪录(待定)
//         StationMessageCodeRecord record=new StationMessageCodeRecord();
//         record.setPhone(phone);
//         record.setCode(sendCode);
//         record.setMessageText(messageContent);
//         record.setSendStatus(res.getHttpStatusCode());
//         record.setErrorCode(res.getCode());
//         record.setDetail(res.getDetail());
//         record.setSendTime(new Date());
//         stationMessageCodeRecordMapper.insertSelective(record);

         if(res.getCode() != 0) {

        	if(res.getCode() == 53) {
         		throw new BadRequestException("短信发送失败，短信请求限制1小时内3次，请勿重复频繁请求！请在1小时后重新获取验证码！");
         	}

        	 throw new BadRequestException("短信发送失败，"+res.getMsg());
         }

    }

    public YunPianMsgRes transferMsgRes(String msgRes) {

    	YunPianMsgRes res=JSON.parseObject(msgRes, YunPianMsgRes.class);
		return res;
 
    }

    /**
     * 系统-员工管理-新增/编辑-根据省市区查询服务站公司（超管调用）
     *
     * @param province
     * @param city
     * @param region
     * @return
     */
    @GetMapping(value = "/common/station/company")
    @ApiOperation(value = " 系统-员工管理-新增/编辑-根据省市区查询服务站公司")
    public List<StationCompanyVO> getStationCompanyByPCR(@RequestParam String province,
                                                   @RequestParam String city,
                                                   @RequestParam String region) {

        if (Objects.isNull(province) || Objects.isNull(city) || Objects.isNull(region)) {
            throw new YimaoException("省市区不能为空");
        }

        //根据省市区查询售前售后门店集合
        List<StationCompanyDTO> dto = systemFeign.getStationCompanyListByPCR(province, city, region);

        if (CollectionUtil.isEmpty(dto)) {
            return null;
        }

        List<StationCompanyVO> vo = new ArrayList<StationCompanyVO>();

       for (StationCompanyDTO stationCompanyDTO : dto) {
    	StationCompanyVO stationCompanyVO =new StationCompanyVO();
		BeanUtils.copyProperties(stationCompanyDTO, stationCompanyVO);
		vo.add(stationCompanyVO);
       }

        return vo;

    }

    /**
     * @param @param  request
     * @param @param  response
     * @param @return
     * @param @throws Exception    设定文件
     * @return String    返回类型
     * @throws
     * @Title: validateCode
     * @Description: (响应验证码页面)
     */
    @RequestMapping(value = "/common/validateCode")
    public String validateCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置响应的类型格式为图片格式
        response.setContentType("image/jpeg");
        //禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession();

        VerificationCodeImgUtil vCode = VerificationCodeImgUtil.getStationLoginCodeImg();
        session.setAttribute(VerificationCodeImgUtil.STATION_LOGIN_CODE, vCode.getCode());
        vCode.write(response.getOutputStream());
        return null;
    }

    /**
     * 系统-员工管理-新增/编辑-区域筛选列表（超管调用）
     *
     * @return
     */
    @GetMapping("/common/area")
    @ApiOperation(value = "系统-员工管理-新增/编辑-获取全部省市区列表", notes = "获取全部省市区列表")
    public ResponseEntity areaList() {

        Integer cacheStationCompanyId = userCache.getJWTInfo().getStationCompanyId();
        //判断调用用户可看列表
        List<AreaDTO> areas = new ArrayList<>();
        if (cacheStationCompanyId.equals(StationConstant.SUPERCOMPANYID)) {
            areas = systemFeign.areaList();
        } else {

            StationCompanyDTO stationCompanyDTO = systemFeign.getStationCompanyById(cacheStationCompanyId);

            if (Objects.isNull(stationCompanyDTO)) {
                throw new BadRequestException("用户服务站公司不存在");
            }

            List<AreaDTO> allAreas = systemFeign.areaList();

            String province = stationCompanyDTO.getProvince();
            String city = stationCompanyDTO.getCity();
            String region = stationCompanyDTO.getRegion();

            for (AreaDTO areaDTO : allAreas) {
                if (areaDTO.getName().equals(province) || areaDTO.getName().equals(city) || areaDTO.getName().equals(region)) {
                    areas.add(areaDTO);
                }
            }


        }

        return ResponseEntity.ok(areas);
    }


    /**
     * 列表查询通用筛选条件-查询区域筛选下拉列表（未使用）
     *
     * @return
     */
//    @GetMapping("/common/areaList")
//    @ApiOperation(value = "列表查询通用筛选条件-获取查询区域筛选条件下拉列表")
//    public ResponseEntity getAreaList() {
//
//        //获取用户可查询区域
//    	//查询售前+售后
//        Set<Integer> areas = userCache.getStationUserAreas(1,PermissionTypeEnum.ALL.value);
//
//        if (CollectionUtil.isEmpty(areas)) {
//            return ResponseEntity.ok(areas);
//        } else {
//            List<AreaDTO> areaList = new ArrayList<>();
//
//            List<AreaDTO> allAreas = systemFeign.areaList();
//
//            for (AreaDTO areaDTO : allAreas) {
//                if (areas.contains(areaDTO.getId())) {
//                    areaList.add(areaDTO);
//                }
//            }
//
//            return ResponseEntity.ok(areaList);
//        }
//
//
//    }


    /**
     * 系统-员工-员工列表- 获取服务站公司筛选条件下拉列表
     *
     * @return
     */
    @GetMapping("/common/stationCompanyList")
    @ApiOperation(value = "系统-员工-员工列表- 获取服务站公司筛选条件下拉列表")
    public List<StationCompanyVO> getStationCompanyList() {

        Integer cacheStationCompanyId = userCache.getJWTInfo().getStationCompanyId();

        if (Objects.isNull(cacheStationCompanyId)) {
            return null;
        }

        List<StationCompanyVO> res = new ArrayList<>();

        if (cacheStationCompanyId.equals(StationConstant.SUPERCOMPANYID)) {
            //查询当前用户
            res = stationAdminService.getAllStationCompany();
            return res;
        } else {
            StationCompanyDTO dto = systemFeign.getStationCompanyById(cacheStationCompanyId);
            if (Objects.isNull(dto)) {
                return null;
            }

            StationCompanyVO vo = new StationCompanyVO();
            vo.setId(dto.getId());
            vo.setName(dto.getName());
            res.add(vo);
            return res;
        }

    }

    /**
     * 统计-查询用户所有门店筛选列及其区域
     */
    @GetMapping("/common/stationList")
    @ApiOperation(value = "统计-查询用户所有门店筛选列及其服务区域")
    public Object getStationList() {

        //获取用户绑定门店
        Set<Integer> stations = userCache.getStationUserAreas(0,null);

        if (CollectionUtil.isEmpty(stations)) {
            return null;
        } else {

            StationQuery query = new StationQuery();
            query.setIds(stations);
            //查询所有复合条件的服务站
            PageVO<StationDTO> result = systemFeign.adminStationList(0, 0, query);

            List<StationDTO> stationList = result.getResult();

            if (CollectionUtil.isEmpty(stationList)) {
                return null;
            } else {

                List<StationVO> list = new ArrayList();
                //获取用户绑定的区域（售前+售后）
                Set<Integer> areas = userCache.getStationUserAreas(1,PermissionTypeEnum.ALL.value);

                for (StationDTO stationDTO : stationList) {
                    List<StationServiceAreaDTO> serviceAreas = stationDTO.getServiceAreas();

                    if (CollectionUtil.isEmpty(serviceAreas)) {
                        continue;
                    } else {
                        StationVO stationVo = new StationVO();
                        stationVo.setId(stationDTO.getId());
                        stationVo.setName(stationDTO.getName());
                        List<StationServiceAreaDTO> serviceAreaList = new ArrayList();
                        
                        Integer serviceType=null;
                        
                        //判断用户绑定区域是否包含
                        for (StationServiceAreaDTO dto : serviceAreas) {
                            Integer areaId = dto.getAreaId();
                            if (areas.contains(areaId)) {
                            	
                            	Integer areaServiceType=dto.getServiceType();
                            	//设置筛选门店的售前售后属性
                            	if(Objects.isNull(serviceType)) {
                            		serviceType=areaServiceType;
                            	}else {
                            		if(PermissionTypeEnum.ALL.value != serviceType) {
                                		if(! serviceType.equals(areaServiceType)) {
                                			serviceType=PermissionTypeEnum.ALL.value;
                                		}
                                	}
                            	}
                            	
                            	
                                StationServiceAreaDTO vo = new StationServiceAreaDTO();
                                vo.setId(areaId);
                                vo.setProvince(dto.getProvince());
                                vo.setCity(dto.getCity());
                                vo.setRegion(dto.getRegion());
                                vo.setServiceType(dto.getServiceType());
                                serviceAreaList.add(vo);
                            }

                        }
                        
                        stationVo.setServiceType(serviceType);

                        if (CollectionUtil.isNotEmpty(serviceAreaList)) {
                            stationVo.setServiceAreas(serviceAreaList);
                            list.add(stationVo);
                        }

                    }
                }

                return list;
            }
        }


    }

    /**
     * 统计-根据筛选门店id集合获取区域列表(未使用)
     */

//    @GetMapping("/common/areaListByStationIds")
//    public Object getAreaListByStationIds(Integer[] stationIds) {
//
//        if (Objects.isNull(stationIds) || stationIds.length < 1) {
//            return null;
//        }
//
//        //获取用户绑定的区域(售前+售后)
//        Set<Integer> areas = userCache.getStationUserAreas(1,PermissionTypeEnum.ALL.value);
//
//        if (CollectionUtil.isEmpty(areas)) {
//            return null;
//        }
//
//        //根据服务站id查询其服务区域
//        Set<Integer> stationIdlist = new HashSet<>(Arrays.asList(stationIds));
//        StationQuery query = new StationQuery();
//        query.setIds(stationIdlist);
//        //查询所有复合条件的服务站
//        PageVO<StationDTO> result = systemFeign.adminStationList(0, 0, query);
//
//        List<StationDTO> stationList = result.getResult();
//
//        if (CollectionUtil.isEmpty(stationList)) {
//            return null;
//        } else {
//
//            List<StationServiceAreaDTO> res = new ArrayList();
//
//            for (StationDTO stationDTO : stationList) {
//                List<StationServiceAreaDTO> serviceAreas = stationDTO.getServiceAreas();
//
//                if (CollectionUtil.isEmpty(serviceAreas)) {
//                    continue;
//                } else {
//                    //判断用户绑定区域是否包含
//                    for (StationServiceAreaDTO dto : serviceAreas) {
//                        Integer areaId = dto.getAreaId();
//                        if (areas.contains(areaId)) {
//                            StationServiceAreaDTO vo = new StationServiceAreaDTO();
//                            vo.setId(areaId);
//                            vo.setProvince(dto.getProvince());
//                            vo.setCity(dto.getCity());
//                            vo.setRegion(dto.getRegion());
//                            res.add(vo);
//                        }
//
//                    }
//
//                }
//            }
//
//            return res;
//
//        }
//
//    }

    /**
     * 获取附件名称
     *
     * @param path 文件路径(相对路径)
     * @param folder 文件目录
     */
    @RequestMapping(value = "/common/getFileName", method = RequestMethod.GET)
    @ApiOperation(value = "获取附件名称", notes = "获取附件名称")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "文件路径(相对路径)", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "folder", value = "文件目录", required = true, dataType = "String", paramType = "query")
    })
    public Object getFileName(@RequestParam String path,
                              @RequestParam String folder) {

        return systemFeign.getFileName(path,folder);
    }
}
