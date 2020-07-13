package com.yimao.cloud.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface SystemFileService {

    /**
     * 共通单个文件上传接口
     *
     * @param file   文件
     * @param folder 上传目录，为空会上传到common目录下
     * @param remark 备注
     */
    String uploadAndSave(MultipartFile file, String folder, String remark);

    /**
     * 共通多个文件上传接口
     *
     * @param files  文件
     * @param folder 上传目录，为空会上传到common目录下
     * @param remark 备注
     */
    Map<String, String> uploadAndSaveMore(MultipartFile[] files, String folder, String remark);

    /**
     * 根据文件目录和文件路径获取文件名称
     *
     * @param path
     * @param folder
     * @return
     */
    String getFileName(String path, String folder);

}
