package com.yimao.cloud.cms.service.impl;

import com.github.pagehelper.PageHelper;
import com.yimao.cloud.cms.mapper.AttachmentMapper;
import com.yimao.cloud.cms.po.Attachment;
import com.yimao.cloud.cms.service.AttachmentService;
import com.yimao.cloud.pojo.vo.PageVO;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 附件表，用于保存用户上传的附件内容。(AttachmentDTO)表服务实现类
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@Service("attachmentService")
public class AttachmentServiceImpl implements AttachmentService {
    @Resource
    private AttachmentMapper attachmentMapper;

    /**
     * 查询多条数据
     *
     * @param pageNum 查询起始位置
     * @param pageSize  查询条数
     * @return 对象列表
     */
    @Override
    public PageVO<Attachment> queryAllByLimit(int pageNum, int pageSize)
    {
        PageHelper.startPage(pageNum,pageSize);

        return null;
    }

}