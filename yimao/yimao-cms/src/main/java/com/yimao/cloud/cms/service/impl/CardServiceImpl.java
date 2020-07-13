package com.yimao.cloud.cms.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.cms.mapper.CardMapper;
import com.yimao.cloud.cms.po.Card;
import com.yimao.cloud.cms.service.CardService;
import com.yimao.cloud.pojo.dto.cms.CardDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@Service("cardService")
public class CardServiceImpl implements CardService {
    @Resource
    private CardMapper cardMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Card queryById(Integer id) {
        return cardMapper.selectByPrimaryKey(id);
    }

    /**
     * 修改
     *
     * @param cardDTO
     * @return
     */
    @Override
    public Integer updateCard(CardDTO cardDTO) {
        //参数合法性检查
        this.checkCard(cardDTO);

        Card card = new Card(cardDTO);
        return cardMapper.updateByPrimaryKeySelective(card);
    }

    /**
     * 新增 card
     *
     * @param cardDTO
     * @return
     */
    @Override
    public Integer addCard(CardDTO cardDTO) {
        //参数合法性检查
        this.checkCard(cardDTO);

        Card card = new Card(cardDTO);
        return cardMapper.insert(card);
    }

    /**
     * 批量更新
     *
     * @param list
     * @param cardDTO
     * @return
     */
    @Override
    public Integer batchUpdate(List<Integer> list, CardDTO cardDTO) {
        Card card = new Card(cardDTO);
        Example example = new Example(Card.class);
        example.createCriteria().andIn("id", list);
        return cardMapper.updateByExampleSelective(card, example);
    }

    /**
     * 分页查询
     *
     * @param cardDTO
     * @return
     */
    @Override
    public PageVO<CardDTO> findPage(Integer pageNum, Integer pageSize, CardDTO cardDTO) {
        PageHelper.startPage(pageNum, pageSize);
        Page<CardDTO> page = cardMapper.findPage(cardDTO);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 卡验证
     *
     * @param cardDTO
     */
    private void checkCard(CardDTO cardDTO) {
        Integer cardType = cardDTO.getCardType();
        if (cardType == null) {
            throw new BadRequestException("卡类型不能为空");
        }
        if (StringUtil.isEmpty(cardDTO.getTitle())) {
            throw new BadRequestException("标题不能为空");
        }
        //卡类型 1 代言卡 2 宣传卡
        if (cardType == 1) {
            if (StringUtil.isEmpty(cardDTO.getBackgroundImg())) {
                throw new BadRequestException("背景图片不能为空");
            }
            if (StringUtil.isEmpty(cardDTO.getCardImg())) {
                throw new BadRequestException("代言卡图片不能为空");
            }
        } else {
            if (StringUtil.isEmpty(cardDTO.getH5Url())) {
                throw new BadRequestException("链接地址不能为空");
            }
        }
    }
}