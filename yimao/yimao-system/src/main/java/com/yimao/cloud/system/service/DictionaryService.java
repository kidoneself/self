package com.yimao.cloud.system.service;


import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.Dictionary;

/**
 * @author liuhao@yimaokeji.com
 */
public interface DictionaryService {

    /**
     * 添加字典
     *
     * @param dictionary 字典
     */
    void saveDictionary(Dictionary dictionary);

    /**
     * 删除字典
     *
     * @param id 字典ID
     */
    void deleteDictionary(Integer id);

    /**
     * 修改字典
     *
     * @param dictionary 字典
     */
    void updateDictionary(Dictionary dictionary);

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
    PageVO<DictionaryDTO> pageDictionary(Integer pageNum, Integer pageSize, String name, String code, String groupCode, Integer pid);

}
