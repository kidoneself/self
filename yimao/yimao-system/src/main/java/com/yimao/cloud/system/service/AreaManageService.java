package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.AreaManageDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.AreaManage;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lizhqiang
 * @date 2019-08-19
 */
public interface AreaManageService {

    PageVO<AreaManage> page(Integer pageNum, Integer pageSize, Integer id, Integer level, Integer pid);

    void update(AreaManage areaManage);

    void importExcel(MultipartFile multipartFile);

	List<AreaManageDTO> getAreaManagerList();
}
