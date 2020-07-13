package com.yimao.cloud.engineer.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.engineer.feign.SystemFeign;
import com.yimao.cloud.engineer.service.CommonService;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class CommonServiceImpl  implements CommonService{
	
	@Resource
	private SystemFeign systemFeign;
	
	@Override
	public String upload(MultipartFile file1, MultipartFile file2, MultipartFile file3, String folder, String remark) {
		String path = "";
	    try {
	        if (file1 != null) {
	            String path1 = systemFeign.upload(file1, folder, remark);
	            path += StringUtil.isNotBlank(path1) ? path1 : "";
	        }
	        if (file2 != null) {
	            String path2 = systemFeign.upload(file2, folder, remark);
	            path += StringUtil.isNotBlank(path2) ? "," + path2 : "";
	        }
	        if (file3 != null) {
	            String path3 = systemFeign.upload(file3, folder, remark);
	            path += StringUtil.isNotBlank(path3) ? "," + path3 : "";
	        }
	    } catch (Exception e) {
	        log.error("==========上传失败=========" + e.getMessage());
	        throw new YimaoException("上传文件失败");
	    }
	    log.info("============上传文件path:"+path);
	    return path;
	}

}
