package com.yimao.cloud.framework.aop.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.aop.dto.OperationLogDTO;
import com.yimao.cloud.framework.aop.mapper.OperationLogMapper;
import com.yimao.cloud.framework.aop.po.OperationLog;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/8/15.
 */
@Service
@Slf4j
public class OperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    /**
     * 保存操作日志对象
     *
     * @param dto 操作日志对象
     */
    public void save(OperationLogDTO dto) {
        OperationLog operationLog = new OperationLog(dto);
        operationLogMapper.insert(operationLog);
    }

    /**
     * 查询操作日志列表
     *
     * @param pageNum   页码
     * @param pageSize  每页显示数量
     * @param operator  操作人
     * @param startTime 操作开始时间
     * @param endTime   操作结束时间
     */
    public PageVO<OperationLogDTO> pageOperationLog(Integer pageNum, Integer pageSize, String operator, Date startTime, Date endTime) {
        Example example = new Example(OperationLog.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotBlank(operator)) {
            criteria.andLike("operator", "%" + operator + "%");
        }
        if (startTime != null) {
            criteria.andGreaterThanOrEqualTo("operationDate", startTime);
        }
        if (endTime != null) {
            criteria.andLessThanOrEqualTo("operationDate", endTime);
        }
        example.orderBy("operationDate").desc();

        PageHelper.startPage(pageNum, pageSize);
        Page<OperationLog> page = (Page<OperationLog>) operationLogMapper.selectByExample(example);
        return new PageVO<>(pageNum, page, OperationLog.class, OperationLogDTO.class);
    }

}
