package com.yimao.cloud.system.service.impl;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.UploadFolder;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.SFTPUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.system.mapper.SystemFileMapper;
import com.yimao.cloud.system.po.SystemFile;
import com.yimao.cloud.system.service.SystemFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：文件
 *
 * @Author Zhang Bo
 * @Date 2019/3/13
 */
@Service
@Slf4j
public class SystemFileServiceImpl implements SystemFileService {

    @Resource
    private SystemFileMapper systemFileMapper;
    @Resource
    private DomainProperties domainProperties;

    /**
     * 共通单个文件上传接口
     *
     * @param file   文件
     * @param folder 上传目录，为空会上传到common目录下
     * @param remark 备注
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public String uploadAndSave(MultipartFile file, String folder, String remark) {
        try {
            if (file == null) {
                throw new YimaoException("文件不能为空。");
            }
            long fileSize = file.getSize();
            //头像文件合法性检查
            String fileName = file.getOriginalFilename();
            if (StringUtil.isBlank(fileName)) {
                throw new YimaoException("文件不能为空。");
            }
            if (Constant.IMAGE_PATTERN.matcher(fileName).matches()) {
                if (fileSize > 2 * 1024 * 1024) {
                    throw new YimaoException("图片文件大小不能超过2M");
                }
            } else if (Constant.VIDEO_PATTERN.matcher(fileName).matches()) {
                if (fileSize > 300 * 1024 * 1024) {
                    throw new YimaoException("视频文件大小不能超过300M");
                }
            } else if (Constant.PDF_PATTERN.matcher(fileName).matches()) {
                if (fileSize > 10 * 1024 * 1024) {
                    throw new YimaoException("PDF文件大小不能超过10M");
                }
            } else {
                throw new YimaoException("暂不支持该格式文件上传。");
            }
            //sftp上传文件到文件服务器，返回文件在sftp服务器的访问路径
            if (StringUtil.isBlank(folder)) {
                folder = UploadFolder.COMMON.name;
            }
            if (folder.contains(".")) {
                throw new YimaoException("目录命名错误。");
            }
            String path = SFTPUtil.upload(file.getInputStream(), folder, fileName, null);
            if (StringUtil.isEmpty(path)) {
                throw new YimaoException("文件上传失败。");
            }

            SystemFile systemFile = new SystemFile();
            systemFile.setName(fileName);
            systemFile.setSize((int) fileSize);
            systemFile.setPath(path);
            systemFile.setFolder(folder);
            systemFile.setRemark(remark);
            systemFileMapper.insert(systemFile);

            return domainProperties.getImage() + path;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof YimaoException) {
                throw new YimaoException(e.getMessage());
            } else {
                throw new YimaoException("文件上传失败！！");
            }
        }
    }

    /**
     * 共通多个文件上传接口
     *
     * @param files  文件
     * @param folder 上传目录，为空会上传到common目录下
     * @param remark 备注
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Map<String, String> uploadAndSaveMore(MultipartFile[] files, String folder, String remark) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < files.length; i++) {
            String path = this.uploadAndSave(files[i], folder, remark);
            map.put("path" + (i + 1), path);
        }
        return map;
    }

    /**
     * 根据文件目录和文件路径获取文件名称
     *
     * @param path
     * @param folder
     * @return
     */
    @Override
    public String getFileName(String path, String folder) {
        SystemFile query = new SystemFile();
        query.setPath(path);
        query.setFolder(folder);
        SystemFile systemFile = systemFileMapper.selectOne(query);
        if (systemFile == null) {
            return null;
        }
        return systemFile.getName();
    }
}
