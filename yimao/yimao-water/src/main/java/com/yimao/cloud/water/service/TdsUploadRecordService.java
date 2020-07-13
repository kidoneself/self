package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.query.water.TdsUploadRecordQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.TdsUploadRecordVO;
import com.yimao.cloud.water.po.TdsUploadRecord;

public interface TdsUploadRecordService {

    /**
     * 创建设备TDS值上传记录
     *
     * @param tdsUploadRecord TDS值上传记录
     */
    void save(TdsUploadRecord tdsUploadRecord);

    /**
     * 修改设备TDS值上传记录
     *
     * @param tdsUploadRecord TDS值上传记录
     */
    void update(TdsUploadRecord tdsUploadRecord);

    /**
     * 恢复设备TDS
     *
     * @param sn 设备SN码
     */
    void reset(String sn);

    /**
     * 审核
     *
     * @param id           TDS值上传记录ID
     * @param verifyResult 审核结果：Y-审核通过；N-审核不通过；
     * @param verifyReason 审核原因
     */
    void verify(Integer id, String verifyResult, String verifyReason);

    /**
     * 查询设备TDS值上传记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    PageVO<TdsUploadRecordVO> page(Integer pageNum, Integer pageSize, TdsUploadRecordQuery query);

    /**
     * 详情
     *
     * @param id TDS值上传记录ID
     */
    TdsUploadRecordVO getDetail(Integer id);
}
