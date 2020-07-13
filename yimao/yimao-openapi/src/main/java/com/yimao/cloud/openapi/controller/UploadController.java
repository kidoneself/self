package com.yimao.cloud.openapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.openapi.feign.SystemFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 描述：文件上传
 *
 * @Author Zhang Bo
 * @Date 2019/3/13
 */
@RestController
@Api(tags = "UploadController")
@Slf4j
public class UploadController {

    @Resource
    private SystemFeign systemFeign;

    /**
     * 共通单个文件上传接口
     *
     * @param file   文件
     * @param folder 上传目录，为空会上传到common目录下
     * @param remark 备注
     */
    @RequestMapping(value = "/common/upload/single", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "共通单个文件上传接口")
    public Object upload(@RequestPart("file") MultipartFile file,
                         @RequestParam(required = false) String folder,
                         @RequestParam(required = false) String remark) {

        log.info("==================文件名称===========================", file.getOriginalFilename());
        String path = systemFeign.upload(file, folder, remark);
        JSONObject result = new JSONObject();
        result.put("path", path);
        log.info("==================path===========================", result);
        return result;
    }

}
