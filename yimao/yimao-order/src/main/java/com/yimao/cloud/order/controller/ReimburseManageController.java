package com.yimao.cloud.order.controller;

import com.yimao.cloud.order.service.ReimburseManageService;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.refundManageExportDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019-09-18
 */
@RestController
@Slf4j
public class ReimburseManageController {

    @Resource
    private ReimburseManageService reimburseManageService;


    /**
     * 退款管理
     *
     * @param pageNum
     * @param pageSize
     * @param dto
     * @return
     */
    @PostMapping("/reimburse/manage/online/{pageNum}/{pageSize}")
    public ResponseEntity<PageVO<OrderSubDTO>> onlineReimburseManage(@PathVariable("pageNum") Integer pageNum,
                                                                     @PathVariable("pageSize") Integer pageSize,
                                                                     @RequestBody OrderSubDTO dto) {
        PageVO<OrderSubDTO> pages = reimburseManageService.onlineReimburseManagePage(pageNum, pageSize, dto);
        return ResponseEntity.ok(pages);
    }

    /**
     * 退款管理导出
     *
     * @param dto
     * @return
     */
//    @PostMapping(value = {"/reimburse/manage/export"})
//    public ResponseEntity exportReimburse(@RequestBody OrderSubDTO dto) {
//        List<refundManageExportDTO> resultList = reimburseManageService.exportReimburse(dto);
//        return ResponseEntity.ok(resultList);
//    }


    /**
     * 退款管理导出
     *
     * @param dto
     * @return
     */
    @PostMapping(value = {"/refund/record/export"})
    public ResponseEntity exportRefund(@RequestBody OrderSubDTO dto) {
        List<refundManageExportDTO> resultList = reimburseManageService.exportRefund(dto);
        return ResponseEntity.ok(resultList);
    }
}
