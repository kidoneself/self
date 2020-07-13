package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.water.TdsUploadRecordDTO;
import com.yimao.cloud.pojo.query.water.TdsUploadRecordQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.TdsUploadRecordVO;
import com.yimao.cloud.water.po.TdsUploadRecord;
import com.yimao.cloud.water.service.TdsUploadRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：设备TDS值上传记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@RestController
public class TdsUploadRecordController {

    @Resource
    private TdsUploadRecordService tdsUploadRecordService;

    /**
     * 创建设备TDS值上传记录
     *
     * @param dto TDS值上传记录
     */
    @PostMapping(value = "/tds/record")
    public void save(@RequestBody TdsUploadRecordDTO dto) {
        TdsUploadRecord tdsUploadRecord = new TdsUploadRecord(dto);
        tdsUploadRecordService.save(tdsUploadRecord);
    }

    /**
     * 修改设备TDS值上传记录
     *
     * @param dto TDS值上传记录
     */
    @PutMapping(value = "/tds/record")
    public void update(@RequestBody TdsUploadRecordDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            TdsUploadRecord tdsUploadRecord = new TdsUploadRecord(dto);
            tdsUploadRecordService.update(tdsUploadRecord);
        }
    }

    /**
     * 恢复设备TDS
     *
     * @param sn 设备SN码
     */
    @PostMapping(value = "/tds/record/reset")
    public void reset(@RequestParam String sn) {
        if (StringUtil.isBlank(sn)) {
            throw new BadRequestException("设备SN码不能为空。");
        }
        tdsUploadRecordService.reset(sn);
    }

    /**
     * 审核
     *
     * @param id           TDS值上传记录ID
     * @param verifyResult 审核结果：Y-审核通过；N-审核不通过；
     * @param verifyReason 审核原因
     */
    @PostMapping(value = "/tds/record/{id}/verify")
    public void verify(@PathVariable Integer id, @RequestParam String verifyResult, @RequestParam(required = false) String verifyReason) {
        tdsUploadRecordService.verify(id, verifyResult, verifyReason);
    }

    /**
     * 查询设备TDS值上传记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/tds/record/{pageNum}/{pageSize}")
    public PageVO<TdsUploadRecordVO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody TdsUploadRecordQuery query) {
        return tdsUploadRecordService.page(pageNum, pageSize, query);
    }

    /**
     * 详情
     *
     * @param id TDS值上传记录ID
     */
    @GetMapping(value = "/tds/record/{id}/detail")
    public TdsUploadRecordVO detail(@PathVariable Integer id) {
        return tdsUploadRecordService.getDetail(id);
    }

}
