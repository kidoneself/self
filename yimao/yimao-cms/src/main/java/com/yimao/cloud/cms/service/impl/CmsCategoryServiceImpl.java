package com.yimao.cloud.cms.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.cms.mapper.CmsCategoryMapper;
import com.yimao.cloud.cms.po.CmsCategory;
import com.yimao.cloud.cms.service.CmsCategoryService;
import com.yimao.cloud.pojo.dto.cms.CmsCategoryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * (cms CmsCategory)表服务实现类
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@Service("categoryService")
public class CmsCategoryServiceImpl implements CmsCategoryService {

    @Resource
    private CmsCategoryMapper cmsCategoryMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public CmsCategory queryById(Integer id) {
        return cmsCategoryMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param cmsCategory
     * @return
     */
    @Override
    public Integer add(CmsCategory cmsCategory) {
        return cmsCategoryMapper.insertSelective(cmsCategory);
    }

    /**
     * 修改
     *
     * @param cmsCategory
     * @return
     */
    @Override
    public Integer update(CmsCategory cmsCategory) {
        return cmsCategoryMapper.updateByPrimaryKeySelective(cmsCategory);
    }

    /**
     * 批量更新
     *
     * @param list
     * @param cmsCategory
     * @return
     */
    @Override
    public Integer batchUpdate(List<Integer> list, CmsCategory cmsCategory) {
        Example example = new Example(CmsCategory.class);
        example.createCriteria().andIn("id", list);
        Integer count = cmsCategoryMapper.updateByExampleSelective(cmsCategory, list);
        return count;
    }

    /**
     * @param
     * @param cmsCategory
     * @return
     */
    @Override
    public Integer batchAdd(CmsCategory cmsCategory) {
        return cmsCategoryMapper.batchAdd(cmsCategory);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Integer delete(Integer id) {
        Integer count = cmsCategoryMapper.checkout(id);
        if (count > 0) {
            throw new YimaoException("该分类下存在咨询内容不可以删除");
        }
        return cmsCategoryMapper.deleteByPrimaryKey(id);
    }


    /**
     * 分页查询
     */
    @Override
    public PageVO<CmsCategoryDTO> findPage(Integer pageNum, Integer pageSize, CmsCategoryDTO cmsCategory) {
        PageHelper.startPage(pageNum, pageSize);
        cmsCategory.setStatus(1);
        Page<CmsCategoryDTO> page = cmsCategoryMapper.findPage(cmsCategory);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public List<CmsCategoryDTO> getCategory(CmsCategoryDTO cmsCategory) {
        return cmsCategoryMapper.getCategory(cmsCategory);
    }

    @Override
    public CmsCategoryDTO getTopCategoryName(Integer id) {
        CmsCategory cmsCategory = cmsCategoryMapper.selectByPrimaryKey(id);

        for (int i = 0; i <= 3; i++) {
            if (cmsCategory != null && cmsCategory.getLevel() != 1) {
                cmsCategory = cmsCategoryMapper.selectByPrimaryKey(cmsCategory.getParentId());
            }
        }
        CmsCategoryDTO dto = new CmsCategoryDTO();
        cmsCategory.convert(dto);
        return dto;
    }

    /**
     * 站务系统- 概况-消息通知-总部消息-消息分类筛选项获取
     * @return
     */
	public List<CmsCategoryDTO> getHeadOfficeMessageType() {
		
		return cmsCategoryMapper.getHeadOfficeMessageType();
	}


}
