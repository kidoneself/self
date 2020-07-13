package com.yimao.cloud.cms.controller;

import com.yimao.cloud.base.constant.CmsConstant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.cms.feign.SystemFeign;
import com.yimao.cloud.cms.mapper.CardMapper;
import com.yimao.cloud.cms.po.Card;
import com.yimao.cloud.cms.service.CardService;
import com.yimao.cloud.pojo.dto.cms.CardDTO;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 卡 （代言和 宣传卡）
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@RestController
@Slf4j
public class CardController {

    @Resource
    private CardService cardService;
    @Resource
    private CardMapper cardMapper;
    @Resource
    private SystemFeign systemFeign;

    @PostMapping("/card")
    @ApiOperation(value = "新增卡（代言和宣传卡）", notes = "新增卡（代言和宣传卡）")
    @ApiImplicitParam(name = "card", value = "卡信息", dataType = "CardDTO", paramType = "body")
    public ResponseEntity<CardDTO> addDYCard(@RequestBody CardDTO card) {
        Integer count = cardService.addCard(card);
        if (count > 0) {
            return ResponseEntity.ok(card);
        }
        throw new YimaoException("系统异常");
    }


    /**
     * @param card
     * @return java.lang.Object
     * @description 更新单个卡信息
     * @author liulin
     * @date 2019/1/26 10:45
     */
    @PutMapping("/card")
    @ApiOperation(value = "更新单个卡信息", notes = "更新单个卡信息")
    @ApiImplicitParam(name = "card", value = "单个卡信息", dataType = "CardDTO", paramType = "body")
    public Object update(@RequestBody CardDTO card) {
        Integer count = cardService.updateCard(card);
        if (count > 0) {
            return ResponseEntity.noContent().build();
        }
        throw new YimaoException("更新失败！");
    }

    /**
     * @param cardIds    卡id集合
     * @param cardStatus 状态
     * @return java.lang.Object
     * @description 描述
     * @author liulin
     * @date 2019/1/23 9:27
     */
    @PatchMapping("/card")
    @ApiOperation(value = "批量更新 卡状态", notes = "批量更新 卡状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cardIds", required = true, value = "卡ids集合(数组)", dataType = "Long", allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "cardStatus", value = "状态id", dataType = "Long", paramType = "query")
    })
    public Object batchUpdate(@RequestParam("cardIds") List<Integer> cardIds,
                              @RequestParam("cardStatus") Integer cardStatus) {
        try {
            if (cardIds != null) {
                CardDTO cardDTO = new CardDTO();
                cardDTO.setCardStatus(cardStatus);
                cardService.batchUpdate(cardIds, cardDTO);
                return ResponseEntity.noContent().build();
            } else {
                throw new BadRequestException("ID不能为空！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("系统异常！");
        }
    }


    /**
     * @param cardId
     * @return java.lang.Object
     * @description 描述
     * @author liulin
     * @date 2019/1/22 18:02
     */
    @GetMapping("/card/{cardId}")
    @ApiOperation(value = "查询单个卡", notes = "查询单个卡")
    @ApiImplicitParam(name = "cardId", value = "单个卡券id", required = true, dataType = "Long", paramType = "path")
    public Object findById(@PathVariable("cardId") Integer cardId) {
        Card card = cardService.queryById(cardId);
        if (card != null) {
            CardDTO cardDTO = new CardDTO();
            card.convert(cardDTO);
            return ResponseEntity.ok(cardDTO);
        }
        throw new NotFoundException("未找到相关数据");

    }

    /**
     * @return java.lang.Object
     * @description 描述
     * @author liulin
     * @date 2019/1/22
     */
    @GetMapping("/card/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询卡券列表", notes = "分页查询卡券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, value = "当前页", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "status", value = "状态", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "typeCode", value = "卡code", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "cardType", value = "卡类型 1 代言卡 2 宣传卡", paramType = "query", dataType = "Integer")
    })
    public ResponseEntity<PageVO<CardDTO>> findPage(@PathVariable("pageNum") Integer pageNum,
                                                    @PathVariable("pageSize") Integer pageSize,
                                                    @RequestParam(value = "cardType", required = false) Integer cardType,
                                                    @RequestParam(value = "status", required = false) Integer status,
                                                    @RequestParam(value = "typeCode", required = false) String typeCode,
                                                    @RequestParam(value = "title", required = false) String title) {
        CardDTO cardDTO = new CardDTO();
        cardDTO.setCardStatus(status);
        cardDTO.setCardType(cardType);
        cardDTO.setTitle(title);
        cardDTO.setTypeCode(typeCode);
        return ResponseEntity.ok(cardService.findPage(pageNum, pageSize, cardDTO));
    }

    /**
     * @return java.lang.Object
     * @description 查询代言卡类型code
     * @author liulin
     * @date 2019/1/26 10:36
     */
    @GetMapping("/card/endorsement/cardCode")
    @ApiOperation(value = "查询所有代言卡类型", notes = "查询所有代言卡类型")
    @ApiImplicitParam(name = "groupCode", value = "卡类型 1 代言卡 2 宣传卡", required = true, paramType = "query", dataType = "String")
    public ResponseEntity findEndorsementCardCode(@RequestParam(value = "groupCode") String groupCode) {
        String cardType = CmsConstant.PROPAGANDA_CARD;  //宣传卡类型
        if (StringUtil.isEmpty(groupCode) || Objects.equals("1", groupCode)) {
            cardType = CmsConstant.ENDORSEMENT_CARD;  //代言卡类型
        }
        PageVO<DictionaryDTO> pageVO = systemFeign.findDictionaryByType(1, 100, null, null, cardType, null);
        if (pageVO != null) {
            return ResponseEntity.ok(pageVO.getResult());
        }
        throw new NotFoundException("没有找到相关记录");
    }

    /**
     * 公众号菜单【我的固定二维码】跳转到品牌代言卡
     */
    @GetMapping("/card/brand")
    public Object getBrandCardId() {
        Example example = new Example(Card.class);
        Example.Criteria criteria = example.createCriteria();
        //卡类型 1 代言卡 2 宣传卡
        criteria.andEqualTo("cardType", 1);
        //状态1已发布 2 未发布以保存，3 已删除
        criteria.andEqualTo("cardStatus", 1);
        criteria.andEqualTo("typeCode", CmsConstant.DYK_PP);
        example.orderBy("sorts").desc();
        List<Card> cardList = cardMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(cardList)) {
            return cardList.get(0).getId();
        }
        return null;
    }

}
