package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.SuggestStatusEnum;
import com.yimao.cloud.base.enums.Terminal;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.SuggestDTO;
import com.yimao.cloud.pojo.dto.system.SuggestTypeDTO;
import com.yimao.cloud.pojo.query.system.SuggestQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.ReplyMapper;
import com.yimao.cloud.system.mapper.SuggestMapper;
import com.yimao.cloud.system.mapper.SuggestTypeMapper;
import com.yimao.cloud.system.po.Reply;
import com.yimao.cloud.system.po.Suggest;
import com.yimao.cloud.system.po.SuggestType;
import com.yimao.cloud.system.service.SuggestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SuggestServiceImpl implements SuggestService {

    @Resource
    private SuggestMapper suggestMapper;
    @Resource
    private ReplyMapper replyMapper;
    @Resource
    private SuggestTypeMapper suggestTypeMapper;
    @Resource
    private UserCache userCache;

    @Override
    public void save(SuggestDTO dto) {
        Suggest suggest = new Suggest(dto);
        int count = suggestMapper.insertSelective(suggest);
        if (count < 1) {
            throw new YimaoException("反馈上传失败");
        }
    }

    /**
     * 客服--建议反馈--展示反馈列表
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<SuggestDTO> page(SuggestQuery query, Integer pageNum, Integer pageSize) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<SuggestDTO> page = suggestMapper.pageSuggest(query);
        return new PageVO<>(pageNum, page);
    }

    @Override
    @Transactional
    public void replySuggest(Integer suggestId, String replyContent, String accessory) {
        Suggest suggest = suggestMapper.selectByPrimaryKey(suggestId);
        if (Objects.isNull(suggest)) {
            throw new YimaoException("建议反馈不存在！");
        }
        if (StringUtil.isEmpty(replyContent)) {
            throw new YimaoException("回复内容不能为空！");
        }
        Reply reply = new Reply();
        reply.setReplyContent(replyContent);
        reply.setAccessory(accessory);
        reply.setSuggestId(suggestId);
        reply.setReplier(userCache.getCurrentAdminRealName());
        reply.setTime(new Date());
        int count = replyMapper.insertSelective(reply);
        if (count < 1) {
            throw new YimaoException("回复失败！");
        }
        Suggest update = new Suggest();
        update.setId(suggestId);
        update.setStatus(SuggestStatusEnum.ALREADY_REPLY.value); //设置反馈回复状态为已回复
        count = suggestMapper.updateByPrimaryKeySelective(update);
        if (count < 1) {
            throw new YimaoException("操作失败！");
        }
    }

    @Override
    public void saveSuggestType(SuggestTypeDTO dto) {
        //必传参数校验
        if (dto.getTerminal() == null) {
            throw new BadRequestException("展示端不能为空！");
        }
        if (StringUtil.isEmpty(dto.getName())) {
            throw new BadRequestException("反馈类型名称不能为空！");
        }
        if (dto.getSort() == null) {
            throw new YimaoException("排序不能为空！");
        }
        //查询是否存在同展示端同名的反馈类型
        SuggestType query = new SuggestType();
        query.setTerminal(dto.getTerminal());
        query.setName(dto.getName());
        int count = suggestTypeMapper.selectCount(query);
        if (count > 0) {
            throw new YimaoException("该展示端已存在同名的反馈类型");
        }
        SuggestType suggestType = new SuggestType(dto);
        suggestType.setCreator(userCache.getCurrentAdminRealName());
        suggestType.setCreateTime(new Date());
        count = suggestTypeMapper.insertSelective(suggestType);
        if (count < 1) {
            throw new YimaoException("新增反馈类型失败！");
        }
    }

    @Override
    public PageVO<SuggestTypeDTO> pageSuggestType(Integer source, Integer pageNum, Integer pageSize) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<SuggestTypeDTO> page = suggestTypeMapper.pageSuggestType(source);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public void updateSuggestType(SuggestTypeDTO dto) {
        SuggestType suggestType = suggestTypeMapper.selectByPrimaryKey(dto.getId());
        if (Objects.isNull(suggestType)) {
            throw new YimaoException("修改对象不存在");
        }
        if (dto.getTerminal().intValue() != suggestType.getTerminal()) {
            throw new YimaoException("修改对象展示端不能更变！");
        }
        Example example = new Example(SuggestType.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", dto.getName());
        criteria.andEqualTo("terminal", dto.getTerminal());
        criteria.andNotEqualTo("id", dto.getId());
        int count = suggestTypeMapper.selectCountByExample(example);
        if (count > 0) {
            throw new YimaoException("该展示端已存在同名的反馈类型");
        }
        SuggestType update = new SuggestType(dto);
        update.setUpdater(userCache.getCurrentAdminRealName());
        update.setUpdateTime(new Date());
        count = suggestTypeMapper.updateByPrimaryKeySelective(update);
        if (count < 1) {
            throw new YimaoException("修改建议类型失败！");
        }
    }

    @Override
    public void deleteSuggestType(Integer id) {
        SuggestType suggestType = suggestTypeMapper.selectByPrimaryKey(id);
        if (Objects.isNull(suggestType)) {
            throw new YimaoException("删除对象不存在");
        }
        int count = suggestTypeMapper.deleteByPrimaryKey(id);
        if (count != 1) {
            throw new YimaoException("删除建议类型失败！");
        }
    }

    @Override
    public List<SuggestTypeDTO> listSuggestTypeTree() {
        List<SuggestType> all = suggestTypeMapper.selectAll();
        if (CollectionUtil.isEmpty(all)) {
            return null;
        }
        //查询不同的展示端
        List<SuggestTypeDTO> suggestTypeTree = suggestTypeMapper.querySuggestTypeGroupByTerminal();
        for (SuggestTypeDTO suggestType : suggestTypeTree) {
            suggestType.setTerminalStr(Terminal.getName(suggestType.getTerminal()));
            List<String> suggestTypeName = new ArrayList<>();
            for (SuggestType type : all) {
                if (type.getTerminal().intValue() == suggestType.getTerminal()) {
                    suggestTypeName.add(type.getName());
                }
            }
            suggestType.setSuggestTypeName(suggestTypeName);
        }
        return suggestTypeTree;
    }

    @Override
    public List<SuggestTypeDTO> listSuggestType(Integer terminal) {
        List<SuggestTypeDTO> suggestTypes = suggestTypeMapper.querySuggestTypeByTerminal(terminal);
        if (CollectionUtil.isEmpty(suggestTypes)) {
            return null;
        }
        return suggestTypes;
    }
}
