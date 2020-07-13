package com.yimao.cloud.water.feign;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.system.MessageContentDTO;
import com.yimao.cloud.pojo.dto.system.MessagePushDTO;
import com.yimao.cloud.pojo.dto.system.MessageRecordDTO;
import com.yimao.cloud.pojo.dto.system.MessageTemplateDTO;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.feign.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * 描述：SYSTEM微服务远程调用类。
 *
 * @Author Zhang Bo
 * @Date 2019/2/18 13:44
 */
@FeignClient(name = Constant.MICROSERVICE_SYSTEM, configuration = MultipartSupportConfig.class)
public interface SystemFeign {

    /**
     * 共通单个文件上传接口
     *
     * @param file   文件
     * @param folder 上传目录，为空会上传到common目录下
     * @param remark 备注
     */
    @RequestMapping(value = "/common/upload/single", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String upload(@RequestPart("file") MultipartFile file,
                  @RequestParam(value = "folder", required = false) String folder,
                  @RequestParam(value = "remark", required = false) String remark);

    /**
     * 根据ID查询区域信息
     *
     * @param id 省市区信息
     */
    @RequestMapping(value = "/area/{id}", method = RequestMethod.GET)
    AreaDTO getAreaById(@PathVariable("id") Integer id);

    /**
     * 根据名称查询省市区ID集合
     *
     * @param name 省市区名称
     */
    @RequestMapping(value = "/area/ids", method = RequestMethod.GET)
    List<Integer> getAreaIdsByName(@RequestParam("name") String name);

    /**
     * 获取所有省份
     */
    @GetMapping(value = "/area/provinces")
    List<AreaDTO> listProvince();

    /**
     * 服务站是否升级新流程校验
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @GetMapping(value = "/onlinearea")
    OnlineAreaDTO getOnlineAreaByPCR(@RequestParam(name = "province") String province,
                                     @RequestParam(name = "city") String city,
                                     @RequestParam(name = "region") String region);

    /**
     * 查询所有下级区域
     *
     * @param pid 父级ID
     */
    @GetMapping(value = "/area/subs")
    List<AreaDTO> listSubArea(@RequestParam("pid") Integer pid);

    /**
     * 测试
     */
    @GetMapping(value = "/test/concurrent")
    JSONObject test();

    //---------------------消息记录 start----------------------------------

    /**
     * 功能描述:根据条件查找该时间最后一次记录
     */
    @GetMapping(value = "/messagePush/lastMessagePush")
    MessagePushDTO findLastMessagePush(@RequestParam(value = "deviceId", required = false) String deviceId,
                                       @RequestParam(value = "filterType", required = false) Integer filterType,
                                       @RequestParam(value = "compareDate", required = false) Date compareDate);

    @GetMapping(value = {"/messageTemplate"})
    MessageTemplateDTO findMessageTemplateByParam(@RequestParam(value = "type") String type,
                                                  @RequestParam(value = "mechanism") String mechanism,
                                                  @RequestParam(value = "pushObject") String pushObject,
                                                  @RequestParam(value = "pushMode") String pushMode);

    @RequestMapping(value = "/message/content", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void messageContentSave(@RequestBody MessageContentDTO dto);

    @RequestMapping(value = "/message/record", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void messageRecordSave(@RequestBody MessageRecordDTO recordDTO);
    
    /**
     * 根据省市区查询system_area区域id
     * @param province
     * @param city
     * @param region
     * @return
     */
    @GetMapping(value = "/area/getRegionIdByPCR")
    Integer getRegionIdByPCR(@RequestParam(value = "province") String province, @RequestParam(value = "city") String city, @RequestParam(value = "region") String region);

    //---------------------消息记录 end----------------------------------

}
