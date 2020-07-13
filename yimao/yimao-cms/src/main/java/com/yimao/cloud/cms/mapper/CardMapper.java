package com.yimao.cloud.cms.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.cms.po.Card;
import com.yimao.cloud.pojo.dto.cms.CardDTO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import tk.mybatis.mapper.common.Mapper;

/**
 *
 * 代言卡 宣传卡
 *
 * @auther: liu.lin
 * @date: 2018/12/29
 */
public interface CardMapper extends Mapper<Card> {

    /**
     * 查询分页
     * @param cardDTO
     * @return
     */
    Page<CardDTO> findPage(CardDTO cardDTO);

}