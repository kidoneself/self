package com.yimao.cloud.cms.service;

import com.yimao.cloud.cms.dto.ContentGeneralSituationDTO;
import com.yimao.cloud.cms.po.Content;
import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import com.yimao.cloud.pojo.query.station.HeadOfficeMessageQuery;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 内容表，用于存放比如文章、帖子、商品、问答等用户自定义模型内容。也用来存放比如菜单、购物车、消费记录等系统模型。(Content)表服务接口
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
public interface ContentService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ContentDTO queryById(Integer id);

    /**
     * 保存
     */
    void save(ContentDTO dto);

    /**
     * 更新
     */
    Integer update(ContentDTO dto);

    /**
     * 分页查询
     */
    PageVO<ContentDTO> findPage(Integer pageNum, Integer pageSize, ContentDTO dto, Integer userId);

    /**
     * 批量更新
     *
     * @param list
     * @param content
     * @return
     */
    Integer batchUpdate(List<Integer> list, Content content);

    ContentGeneralSituationDTO getContentGeneralSituation();

    List<Map<String, Integer>> getContentArticleTendency(Integer days);

    List<ContentDTO> getInformationByTop();

    void addViewCount(Integer id);

    void updateContentCount(Map<String, Object> map);

    PageVO<ContentDTO> getHeadOfficeMessageList(Integer pageNum, Integer pageSize, HeadOfficeMessageQuery query);

    PageVO<ContentDTO> getNoticeMessage(Integer pageNum, Integer pageSize);

    // PageVO<ContentDTO> systemNews(Integer pageNum, Integer pageSize, Integer parentCategoryId, Integer userId);

}
