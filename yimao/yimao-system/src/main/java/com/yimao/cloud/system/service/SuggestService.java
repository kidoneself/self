package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.SuggestDTO;
import com.yimao.cloud.pojo.dto.system.SuggestTypeDTO;
import com.yimao.cloud.pojo.query.system.SuggestQuery;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;
import java.util.Map;

public interface SuggestService {
    void save(SuggestDTO dto);

    PageVO<SuggestDTO> page(SuggestQuery query, Integer pageNum, Integer pageSize);

    void replySuggest(Integer suggestId, String replyContent, String accessory);

    void saveSuggestType(SuggestTypeDTO dto);

    PageVO<SuggestTypeDTO> pageSuggestType(Integer terminal, Integer pageNum, Integer pageSize);

    void updateSuggestType(SuggestTypeDTO dto);

    void deleteSuggestType(Integer id);

    List<SuggestTypeDTO> listSuggestTypeTree();

    List<SuggestTypeDTO> listSuggestType(Integer terminal);
}
