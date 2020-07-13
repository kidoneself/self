package com.yimao.cloud.system.controller;

import com.yimao.cloud.system.service.SystemFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 描述：共通文件上传
 *
 * @Author Zhang Bo
 * @Date 2019/3/13
 */
@RestController
@Api(tags = "UploadController")
@Slf4j
public class UploadController {

    @Resource
    private SystemFileService systemFileService;

    /**
     * 共通单个文件上传接口
     *
     * @param file   文件
     * @param folder 上传目录，为空会上传到common目录下
     * @param remark 备注
     */
    @RequestMapping(value = "/common/upload/single", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "共通单个文件上传接口")
    public String upload(@RequestPart(value = "file", required = false) MultipartFile file,
                         @RequestPart(value = "file1", required = false) MultipartFile file1,
                         @RequestPart(value = "file2", required = false) MultipartFile file2,
                         @RequestPart(value = "file3", required = false) MultipartFile file3,
                         @RequestParam(required = false) String folder,
                         @RequestParam(required = false) String remark) {
        if (file != null) {
            return systemFileService.uploadAndSave(file, folder, remark);
        } else if (file1 != null) {
            return systemFileService.uploadAndSave(file1, folder, remark);
        } else if (file2 != null) {
            return systemFileService.uploadAndSave(file2, folder, remark);
        } else if (file3 != null) {
            return systemFileService.uploadAndSave(file3, folder, remark);
        } else {
            return null;
        }
    }

    /**
     * 共通多个文件上传接口
     *
     * @param files  文件
     * @param folder 上传目录，为空会上传到common目录下
     * @param remark 备注
     */
    @RequestMapping(value = "/common/upload/more", method = RequestMethod.POST)
    @ApiOperation(value = "共通多个文件上传接口")
    public ResponseEntity<Object> uploadMore(@RequestParam(value = "file") MultipartFile[] files,
                                             @RequestParam(value = "folder", required = false) String folder,
                                             @RequestParam(value = "remark", required = false) String remark) {
        return ResponseEntity.ok(systemFileService.uploadAndSaveMore(files, folder, remark));
    }

}
