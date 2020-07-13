package com.yimao.cloud.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAuditRecordDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAuditRecordExportDTO;
import com.yimao.cloud.pojo.dto.user.DistributorProtocolDTO;
import com.yimao.cloud.pojo.query.user.DistributorOrderAuditRecordQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.feign.SystemFeign;
import com.yimao.cloud.user.mapper.DistributorOrderAuditRecordMapper;
import com.yimao.cloud.user.mapper.DistributorProtocolMapper;
import com.yimao.cloud.user.service.DistributorOrderAuditRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2018/12/25
 */

@Service
@Slf4j
public class DistributorOrderAuditRecordServiceImpl implements DistributorOrderAuditRecordService {

    @Resource
    private DistributorOrderAuditRecordMapper distributorOrderAuditRecordMapper;

    @Resource
    private DistributorProtocolMapper distributorProtocolMapper;

    @Resource
    private SystemFeign systemFeign;

    /**
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @Override
    public PageVO<DistributorOrderAuditRecordDTO> page(Integer pageNum, Integer pageSize, DistributorOrderAuditRecordDTO query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<DistributorOrderAuditRecordDTO> page = distributorOrderAuditRecordMapper.pageAudit(query);

        return new PageVO<>(pageNum, page);
    }

    @Override
    public List<DistributorOrderAuditRecordExportDTO> exportDistributorOrderAuditRecordAudit(DistributorOrderAuditRecordQuery query) {
        //查询所有符合条件的财务审核信息
        List<DistributorOrderAuditRecordExportDTO> queryList = distributorOrderAuditRecordMapper.distributorOrderAuditRecordExport(query);
        if (CollectionUtil.isEmpty(queryList)) {
            throw new YimaoException("没有找到审核记录信息");
        }
        return queryList;
    }
}
