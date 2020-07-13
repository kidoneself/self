package com.yimao.cloud.cms.service;

import com.yimao.cloud.cms.po.Attachment;
import com.yimao.cloud.pojo.vo.PageVO;

/**
 * 附件表，用于保存用户上传的附件内容。(Attachment)表服务接口
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:27
 */
public interface AttachmentService {

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    PageVO<Attachment> queryAllByLimit(int offset, int limit);


}