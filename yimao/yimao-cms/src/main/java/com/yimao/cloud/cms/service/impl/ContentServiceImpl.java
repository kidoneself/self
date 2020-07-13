package com.yimao.cloud.cms.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.CmsContentStatusEnum;
import com.yimao.cloud.base.enums.CmsContentTypeEnum;
import com.yimao.cloud.base.enums.ContentOperationEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.cms.dto.ContentGeneralSituationDTO;
import com.yimao.cloud.cms.mapper.CmsCategoryMapper;
import com.yimao.cloud.cms.mapper.ContentCategoryMapper;
import com.yimao.cloud.cms.mapper.ContentMapper;
import com.yimao.cloud.cms.mapper.ContentUserMapper;
import com.yimao.cloud.cms.po.CmsCategory;
import com.yimao.cloud.cms.po.Content;
import com.yimao.cloud.cms.po.ContentCategory;
import com.yimao.cloud.cms.po.UserRead;
import com.yimao.cloud.cms.service.ContentService;
import com.yimao.cloud.pojo.dto.cms.CategoryRelationDTO;
import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import com.yimao.cloud.pojo.query.station.HeadOfficeMessageQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * 内容表，用于存放比如文章、帖子、商品、问答等用户自定义模型内容。也用来存放比如菜单、购物车、消费记录等系统模型。(Content)表服务实现类
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@Service
@Slf4j
public class ContentServiceImpl implements ContentService {

    @Resource
    private ContentMapper contentMapper;
    @Resource
    private ContentCategoryMapper contentCategoryMapper;
    @Resource
    private ContentUserMapper contentUserMapper;
    @Resource
    private CmsCategoryMapper cmsCategoryMapper;
    @Resource
    private UserCache userCache;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void addViewCount(Integer id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        if (Objects.isNull(content)) {
            throw new YimaoException("咨询信息不存在或已删除！");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("type", ContentOperationEnum.WATCH.value);
        map.put("count", 1);
        rabbitTemplate.convertAndSend(RabbitConstant.CONTENT_COUNT_ADD, map);
    }

    @Override
    public void updateContentCount(Map<String, Object> map) {
        Content content = contentMapper.selectByPrimaryKey(Integer.parseInt(map.get("id").toString()));
        if (content != null) {
            String type = map.get("type").toString();
            Integer count = Integer.parseInt(map.get("count").toString());
            Content update = new Content();
            if (StringUtil.isNotEmpty(type)) {
                log.info("===============类型：" + type + ",视频ID：" + content.getId() + "================");
                update.setId(content.getId());
                //TODO 目前 只做了访问量的增加，其它操作后续根据需求再加
                if (Objects.equals(ContentOperationEnum.WATCH.value, type)) {
                    update.setViewCount(content.getViewCount() != null ? content.getViewCount() + count : count);
                }
                int i = contentMapper.updateByPrimaryKeySelective(update);
                if (i < 1) {
                    throw new YimaoException("增加访问量失败！");
                }
            }
        }
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public ContentDTO queryById(Integer id) {
        ContentDTO dto = new ContentDTO();
        Content content = contentMapper.selectByPrimaryKey(id);
        if (null == content) {
            return null;
        }
        content.convert(dto);
        List<CategoryRelationDTO> list = contentCategoryMapper.findContentRelationInfo(id);
        if (!CollectionUtils.isEmpty(list)) {
            dto.setCategoryRelationList(list);
        }
        Integer i = contentMapper.addWatchCount(id, dto.getViewCount() + 1);
        if (i < 1) {
            throw new YimaoException("浏览次数增加失败");
        }
        return dto;
    }


    /**
     * 保存
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void save(ContentDTO dto) {
        //服务站内容设置待审核
        if (CmsContentTypeEnum.TYPE_DIVISION.value == dto.getType()) {
            dto.setStatus(CmsContentStatusEnum.STATUS_PENDINGREVIEW.value);//待审核
        }
        Content content = new Content(dto);
        Integer[] categorys = dto.getCategoryIds();
        if (dto.getCategoryIds() == null || dto.getCategoryIds().length == 0) {
            throw new BadRequestException("请选择分类");
        }
        for (Integer categoryId : categorys) {
            CmsCategory cmsCategory = cmsCategoryMapper.selectByPrimaryKey(categoryId);
            if (cmsCategory != null) {
                content.setContentMode(cmsCategory.getLocation());
                break;
            }
        }
        contentMapper.insertSelective(content);
        //保存分类关系

        saveContentCategory(content.getId(), categorys);
    }

    /**
     * 更新
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Integer update(ContentDTO dto) {
        //服务站内容设置待审核
        if (CmsContentTypeEnum.TYPE_DIVISION.value == dto.getType()) {
            dto.setStatus(CmsContentStatusEnum.STATUS_PENDINGREVIEW.value);//待审核
        }
        Content content = new Content(dto);
        Integer[] categorys = dto.getCategoryIds();
        // if (dto.getCategoryIds() == null || dto.getCategoryIds().length == 0) {
        //     throw new BadRequestException("请选择分类");
        // }
        Integer count = contentMapper.updateByPrimaryKeySelective(content);
        //保存分类关系
        if (categorys != null && categorys.length > 0) {
            //删除之前的关系
            Example example = new Example(ContentCategory.class);
            example.createCriteria().andEqualTo("contentId", content.getId());
            contentCategoryMapper.deleteByExample(example);
            saveContentCategory(content.getId(), categorys);
        }
        return count;
    }

    /**
     * 保存内容分类关系
     */
    private void saveContentCategory(Integer contentId, Integer[] categorys) {
        if (categorys != null && categorys.length > 0) {
            List<ContentCategory> result = new ArrayList<>();
            for (int i = 0; i < categorys.length; i++) {
                Integer categoryId = categorys[i];
                ContentCategory contentCategory = new ContentCategory();
                contentCategory.setCategoryId(categoryId);
                contentCategory.setContentId(contentId);
                result.add(contentCategory);
            }
            contentCategoryMapper.batchSave(result);
        }
    }

    /**
     * 分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param dto
     * @return
     */
    @Override
    public PageVO<ContentDTO> findPage(Integer pageNum, Integer pageSize, ContentDTO dto, Integer userId) {
        List<Integer> ids = new ArrayList<>();
        Integer categoryId = dto.getParentCategoryId();
        //二级分类ID不为空，根据二级分类ID查找即可
        if (categoryId != null) {
            ids.add(categoryId);
        }

        dto.setIds(ids);
        PageHelper.startPage(pageNum, pageSize);

        Page<ContentDTO> page = contentMapper.findPage(dto);
        List<ContentDTO> result = page.getResult();
        if (CollectionUtil.isNotEmpty(result)) {
            for (ContentDTO contentDTO : result) {
                contentDTO.setHasRead(false);
                if (userId != null) {
                    UserRead query = new UserRead();
                    query.setContentId(contentDTO.getId());
                    query.setUserId(userId);
                    query.setDeleted(0);
                    int count = contentUserMapper.selectCount(query);
                    if (count > 0) {
                        contentDTO.setHasRead(true);
                    }
                }
                List<CategoryRelationDTO> list = contentCategoryMapper.findContentRelationInfo(contentDTO.getId());
                contentDTO.setCategoryRelationList(list);
            }
        }
        // //获取分类名称
        // List<ContentDTO> result = page.getResult();
        // if (CollectionUtil.isNotEmpty(result)) {
        //     //去重复对象
        //     Set<ContentDTO> h = new HashSet<>(result);
        //     result.clear();
        //     result.addAll(h);
        //     for (ContentDTO item : page.getResult()) {
        //         List<CategoryRelationDTO> list = contentCategoryMapper.findContentRelationInfo(item.getId());
        //         if (CollectionUtil.isNotEmpty(list)) {
        //             for (CategoryRelationDTO categoryRelationDTO : list) {
        //                 if (categoryRelationDTO == null) {
        //                     throw new YimaoException("资讯内容为空");
        //                 }
        //                 Integer parentId = categoryRelationDTO.getParentId();
        //                 if (parentId != 0) {
        //                     CmsCategory cmsCategory = cmsCategoryMapper.selectByPrimaryKey(parentId);
        //                     if (cmsCategory != null) {
        //                         categoryRelationDTO.setParentCategoryName(cmsCategory.getName());
        //                     }
        //                 }
        //             }
        //             item.setCategoryRelationList(list);
        //         }
        //     }
        // }
        // page.setTotal(page.getResult().size());
        //
        // result.sort((o1, o2) -> {
        //     if (o1.getSorts() < o2.getSorts()) {
        //         return 1;
        //     } else if (o1.getSorts() == o2.getSorts()) {
        //         return 0;
        //     } else {
        //         return -1;
        //     }
        // });
        return new PageVO<>(pageNum, page);
    }

    /**
     * 批量更新
     *
     * @param list
     * @param content
     * @return
     */
    @Override
    public Integer batchUpdate(List<Integer> list, Content content) {
        Example example = new Example(Content.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", list);
        return contentMapper.updateByExampleSelective(content, example);
    }

    /**
     * 获取内容今日概况
     *
     * @return com.yimao.cloud.cms.dto.ContentGeneralSituationDTO
     * @Author lizhiqiang
     * @Date 2019/3/12
     * @Param []
     */
    @Override
    public ContentGeneralSituationDTO getContentGeneralSituation() {
        Integer articleNum = contentMapper.getArticleNum();
        ContentGeneralSituationDTO dto = new ContentGeneralSituationDTO();
        dto.setArticle(articleNum);
        return dto;
    }

    // @Override
    // public PageVO<ContentDTO> systemNews(Integer pageNum, Integer pageSize, Integer parentCategoryId, Integer userId) {
    //     List<Integer> ids = null;
    //     if (parentCategoryId != null) {
    //         ids = contentMapper.listSecondCategoryId(parentCategoryId);
    //         ids.add(parentCategoryId);
    //     }
    //     PageHelper.startPage(pageNum, pageSize);
    //     Page<ContentDTO> page = contentMapper.getSystemNews(ids, userId);
    //     return new PageVO<>(pageNum, page);
    // }

    /**
     * 获取内容文章趋势
     *
     * @return java.util.List
     * @Author lizhiqiang
     * @Date 2019/3/12
     * @Param [days]
     */
    @Override
    public List<Map<String, Integer>> getContentArticleTendency(Integer days) {
        return contentMapper.getContentArticleTendency(days);
    }

    @Override
    public List<ContentDTO> getInformationByTop() {

        return contentMapper.getInformationByTop();
    }

	/**
	 * 站务系统获取总部消息列表
	 */
	public PageVO<ContentDTO> getHeadOfficeMessageList(Integer pageNum, Integer pageSize, HeadOfficeMessageQuery query) {
		PageHelper.startPage(pageNum, pageSize);

        Page<ContentDTO> page = contentMapper.getHeadOfficeMessageList(query);        
    
		return new PageVO<>(pageNum, page);
	}

    @Override
    public PageVO<ContentDTO> getNoticeMessage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ContentDTO> page = contentMapper.getNoticeMessage();
        return new PageVO<>(pageNum, page);
    }
}
