package com.yimao.cloud.cms.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.cms.po.Content;
import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import com.yimao.cloud.pojo.query.station.HeadOfficeMessageQuery;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 内容信息
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
public interface ContentMapper extends Mapper<Content> {

    Page<ContentDTO> findPage(ContentDTO content);

    Integer getArticleNum();

    List<Map<String, Integer>> getContentArticleTendency(Integer days);

    Integer addWatchCount(@Param("id") Integer id, @Param("viewCount") Integer viewCount);

    Integer countAllNotice();

    List<ContentDTO> getInformationByTop();

	Page<ContentDTO> getHeadOfficeMessageList(HeadOfficeMessageQuery query);

    Page<ContentDTO> getNoticeMessage();
}
