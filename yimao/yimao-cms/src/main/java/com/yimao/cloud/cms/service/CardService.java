package com.yimao.cloud.cms.service;

import com.yimao.cloud.cms.po.Card;
import com.yimao.cloud.pojo.dto.cms.CardDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;

/**
 * 代言卡 宣传卡管理
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
public interface CardService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Card queryById(Integer id);


    /**
     *  新增 card
     * @param cardDTO
     * @return
     */
    Integer addCard(CardDTO cardDTO);


    /**
     *  修改
     * @param cardDTO
     * @return
     */
    Integer updateCard(CardDTO cardDTO);


    /**
     *  批量更新
     * @param list
     * @param cardDTO
     * @return
     */
    Integer batchUpdate(List<Integer> list,CardDTO cardDTO);


    /**
     *  分页查询
     * @param cardDTO
     * @return
     */
    PageVO<CardDTO> findPage(Integer pageNum, Integer pageSize, CardDTO cardDTO);

}