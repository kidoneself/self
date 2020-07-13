package com.yimao.cloud.system.jobs;

import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.SFTPUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.system.mapper.ExportRecordMapper;
import com.yimao.cloud.system.po.ExportRecord;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 描述：将导出时间在2天之前的导出记录删除，释放服务器空间
 *
 * @Author Zhang Bo
 * @Date 2019/11/27
 */
@Slf4j
@DisallowConcurrentExecution//该注解表示前一个任务执行结束再执行下一次任务
public class RemoveExportRecordJob extends QuartzJobBean {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private ExportRecordMapper exportRecordMapper;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        log.info("将导出时间在2天之前的导出记录删除，释放服务器空间---任务开始执行");
        try {
            Date compareTime = DateUtil.dayAfter(new Date(), -2);
            List<ExportRecord> list = exportRecordMapper.list2DaysAgoData(compareTime);
            if (CollectionUtil.isNotEmpty(list)) {
                String domain = domainProperties.getImage();
                for (ExportRecord record : list) {
                    String downloadLink = record.getDownloadLink();
                    if (StringUtil.isNotEmpty(downloadLink)) {
                        String[] paths = downloadLink.split(domain);
                        if (paths.length == 2) {
                            log.info("删除的导出记录为：{}", paths[1]);
                            try {
                                SFTPUtil.delete(paths[1]);
                                exportRecordMapper.deleteByPrimaryKey(record.getId());
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("将导出时间在2天之前的导出记录删除，释放服务器空间---任务执行出错");
            log.error(e.getMessage(), e);
        }
        log.info("将导出时间在2天之前的导出记录删除，释放服务器空间---任务结束执行");
    }

}
