package com.yimao.cloud.hra.service;

import com.yimao.cloud.pojo.dto.hra.ReportDTO;
import com.yimao.cloud.pojo.dto.hra.ReportDetailDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/10/31.
 */
public interface HraReportService {

    /**
     * @param flag
     * @Description: 查询我的报告
     * @author ycl
     * @param: uid
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.hra.ReportDTO>
     * @Create: 2019/4/22 13:46
     */
    List<ReportDTO> getMyReportList(Integer uid, Integer flag);

    /**
     * @param ticketNo
     * @Description: 查询体检报告详情
     * @author ycl
     * @Return: com.yimao.cloud.pojo.dto.hra.ReportDetailDTO
     * @Create: 2019/4/22 13:47
     */
    ReportDetailDTO getReportDetail(String ticketNo);

    /**
     * @param userId
     * @Description: 查询当前用户下所有评估报告
     * @author ycl
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.hra.ReportDTO>
     * @Create: 2019/4/22 13:59
     */
    List<ReportDTO> getAllReport(Integer userId);

    /**
     * @param ticketNos
     * @Description: 对比评估报告
     * @author ycl
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.hra.ReportDetailDTO>
     * @Create: 2019/4/22 14:00
     */
    List<ReportDetailDTO> getReportDetailList(String[] ticketNos);

    /**
     * @param fromUserId
     * @param ticketNo
     * @param sharedUserId
     * @Description: 分享评估报告
     * @author ycl
     * @Return: java.lang.Object
     * @Create: 2019/4/22 14:01
     */
    Object shareReport(Integer fromUserId, String ticketNo, Integer sharedUserId);

    PageVO<ReportDTO> listReport(Integer pageNum, Integer pageSize, String phone);

    int showReport(String phone, String ticketNo);

    int addReportRecord(String phone, String ticketNo, Integer userId);

    PageVO<ReportDTO> listReportRecord(Integer pageNum, Integer pageSize, Integer userId);

    void saveReportRecord(Integer userId);

    Integer deleteRecord(Integer reportRecordId);
}
