package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.SuggestDTO;
import com.yimao.cloud.pojo.query.system.SuggestQuery;
import com.yimao.cloud.system.po.Suggest;
import tk.mybatis.mapper.common.Mapper;

public interface SuggestMapper extends Mapper<Suggest> {

    Page<SuggestDTO> pageSuggest(SuggestQuery query);
}
