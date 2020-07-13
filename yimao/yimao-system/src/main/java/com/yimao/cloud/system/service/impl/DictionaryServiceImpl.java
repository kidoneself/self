package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.DictionaryMapper;
import com.yimao.cloud.system.po.Dictionary;
import com.yimao.cloud.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author liuhao@yimaokeji.com
 * 2017112017/11/16
 */
@Service
@Slf4j
public class DictionaryServiceImpl implements DictionaryService {

    @Resource
    private UserCache userCache;
    @Resource
    private DictionaryMapper dictionaryMapper;

    /**
     * 添加字典
     *
     * @param dictionary 字典
     */
    @Override
    public void saveDictionary(Dictionary dictionary) {
        if (StringUtil.isBlank(dictionary.getName())) {
            throw new BadRequestException("名称不能为空。");
        }
        if (StringUtil.isBlank(dictionary.getCode())) {
            throw new BadRequestException("CODE不能为空。");
        }

        if (this.existsByCodeGroup(dictionary.getCode())) {
            throw new BadRequestException("CODE已存在，请重新设置。");
        }
        String creator = userCache.getCurrentAdminRealName();
        dictionary.setCreator(creator);
        dictionary.setCreateTime(new Date());
        dictionary.setUpdater(creator);
        dictionary.setUpdateTime(dictionary.getCreateTime());
        dictionary.setId(null);
        dictionary.setDeleted(false);
        dictionaryMapper.insert(dictionary);
    }

    /**
     * 删除字典
     *
     * @param id 字典ID
     */
    @Override
    public void deleteDictionary(Integer id) {
        Dictionary dictionary = new Dictionary();
        dictionary.setId(id);
        dictionary.setDeleted(true);
        dictionaryMapper.updateByPrimaryKeySelective(dictionary);
    }

    /**
     * 修改字典
     *
     * @param dictionary 字典
     */
    @Override
    public void updateDictionary(Dictionary dictionary) {
        if (StringUtil.isBlank(dictionary.getName())) {
            throw new BadRequestException("名称不能为空。");
        }
        if (StringUtil.isBlank(dictionary.getCode())) {
            throw new BadRequestException("CODE不能为空。");
        }
        if (this.existsByCodeGroup(dictionary.getCode())) {
            throw new BadRequestException("CODE已存在，请重新设置。");
        }
        String updater = userCache.getCurrentAdminRealName();
        dictionary.setUpdater(updater);
        dictionary.setUpdateTime(new Date());
        dictionaryMapper.updateByPrimaryKey(dictionary);
    }

    /**
     * 查询字典数据（分页）
     *
     * @param pageNum   页码
     * @param pageSize  每页大小
     * @param name      字典名称
     * @param code      CODE
     * @param groupCode 分组
     * @param pid       父级ID
     */
    @Override
    public PageVO<DictionaryDTO> pageDictionary(Integer pageNum, Integer pageSize, String name, String code, String groupCode, Integer pid) {
        Example example = new Example(Dictionary.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotBlank(name)) {
            criteria.andLike("name", "%" + name + "%");
        }
        if (StringUtil.isNotBlank(code)) {
            criteria.andLike("code", "%" + code + "%");
        }
        if (StringUtil.isNotBlank(groupCode)) {
            criteria.andEqualTo("groupCode", groupCode);
        }
        if (pid != null) {
            criteria.andEqualTo("pid", pid);
        }
        criteria.andEqualTo("deleted", false);
        example.orderBy("sorts");
        PageHelper.startPage(pageNum, pageSize);
        Page<Dictionary> dictionaries = (Page<Dictionary>) dictionaryMapper.selectByExample(example);
        return new PageVO<>(pageNum, dictionaries, Dictionary.class, DictionaryDTO.class);
    }

    /**
     * 检查是否存在
     *
     * @param code CODE
     * @param
     */
    private boolean existsByCodeGroup(String code) {
        Dictionary record = new Dictionary();
        record.setCode(code);
        int count = dictionaryMapper.selectCount(record);
        if (count > 0) {
            return true;
        }
        return false;
    }

}
