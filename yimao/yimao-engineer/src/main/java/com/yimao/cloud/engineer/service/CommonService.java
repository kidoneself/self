package com.yimao.cloud.engineer.service;

import org.springframework.web.multipart.MultipartFile;

/****
 * 公共服务
 * @author zhangbaobao
 *
 */
public interface CommonService {
	
	/***
	 * 文件上传
	 * @param file1
	 * @param file2
	 * @param file3
	 * @param folder
	 * @param remark
	 * @return
	 */
	public String upload(MultipartFile file1, MultipartFile file2, MultipartFile file3, String folder, String remark);

}
