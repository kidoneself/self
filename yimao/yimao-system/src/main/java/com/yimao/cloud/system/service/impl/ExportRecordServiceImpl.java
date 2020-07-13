package com.yimao.cloud.system.service.impl;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.system.mapper.ExportRecordMapper;
import com.yimao.cloud.system.po.ExportRecord;
import com.yimao.cloud.system.service.ExportRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 描述：导出记录
 *
 * @Author Zhang Bo
 * @Date 2019/11/26
 */
@Service
@Slf4j
public class ExportRecordServiceImpl implements ExportRecordService {

    @Resource
    private UserCache userCache;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ExportRecordMapper exportRecordMapper;

    /**
     * 发起导出操作时调用
     *
     * @param url 某个导出的url
     */
    @Override
    public ExportRecordDTO save(String url, String title) {
        Integer adminId;
        String adminName;
        if (Constant.LOCAL_ENVIRONMENT) {
            adminId = 0;
            adminName = "本地测试";
        } else {
            adminId = userCache.getUserId();
            adminName = userCache.getCurrentAdminRealName();
        }
        Date now = new Date();
        //这里是判断该管理员多次相同的导出操作是不是间隔太短了
        List<ExportRecord> list = exportRecordMapper.selectLastByAdminIdAndUrl(adminId, url);
        if (CollectionUtil.isNotEmpty(list)) {
            if (list.size() >= 1) {
                ExportRecord one = list.get(0);
                if (one.getStatus() == ExportRecordStatus.WAITING.value) {
                    throw new BadRequestException("正在排队导出，请耐心等待。");
                }
                if (one.getStatus() == ExportRecordStatus.IN_EXPORT.value) {
                    throw new BadRequestException("导出中，请耐心等待。");
                }
            }
        }
        list = exportRecordMapper.selectLastByAdminIdAndUrl(adminId, null);
        if (CollectionUtil.isNotEmpty(list)) {
            if (list.size() >= 5) {
                throw new BadRequestException("您已有超过5个导出正在排队，请稍后再继续添加。");
            }
            // if (last.getStatus() == ExportRecordStatus.WAITING.value) {
            //     throw new BadRequestException("正在排队导出，请耐心等待。");
            // }
            // if (last.getStatus() == ExportRecordStatus.IN_EXPORT.value) {
            //     throw new BadRequestException("导出中，请耐心等待。");
            // }
        }
        //查询是不是已经有相同的导出在进行了，如果有本次导出操作不放入队列，只记录一条导出操作，当导出成功后会将相同的导出记录都标记为"导出成功"
        // int count = exportRecordMapper.selectCountByStatusAndUrl(url);
        //保存导出记录
        ExportRecord record = new ExportRecord();
        record.setAdminId(adminId);
        record.setAdminName(adminName);
        record.setStatus(ExportRecordStatus.WAITING.value);
        record.setStatusName(ExportRecordStatus.WAITING.name);
        record.setTitle(title);
        record.setUrl(url);
        record.setDuration(0);
        record.setProgress(0D);
        record.setCreateTime(now);
        exportRecordMapper.insert(record);
        ExportRecordDTO recordDTO = new ExportRecordDTO();
        record.convert(recordDTO);
        return recordDTO;
    }

    /**
     * 导出开始、成功或失败之后调用
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void update(ExportRecordDTO recordDTO) {
        ExportRecord record = new ExportRecord(recordDTO);
        if (record.getId() == null) {
            exportRecordMapper.insert(record);
        } else {
            Date now = new Date();
            ExportRecord exportRecord = exportRecordMapper.selectByPrimaryKey(record.getId());
            record.setDuration((int) DateUtil.betweenSeconds(exportRecord.getCreateTime(), now));
            record.setUpdateTime(now);
            if (record.getStatus() == ExportRecordStatus.SUCCESSFUL.value) {
                record.setProgress(100D);
                redisCache.delete(Constant.EXPORT_PROGRESS + exportRecord.getAdminId() + "_" + record.getId());
            }
            exportRecordMapper.updateByPrimaryKeySelective(record);

        }
    }

    /**
     * 发起导出操作后，定时器循环调用此方法
     */
    @Override
    public ExportRecordDTO getById(Integer id) {
        ExportRecord record = exportRecordMapper.selectByPrimaryKey(id);
        if (record == null) {
            return null;
        }
        ExportRecordDTO dto = new ExportRecordDTO();
        record.convert(dto);
        if (dto.getStatus() == ExportRecordStatus.IN_EXPORT.value) {
            String progress = redisCache.get(Constant.EXPORT_PROGRESS + dto.getAdminId() + "_" + dto.getId());
            if (StringUtil.isNotEmpty(progress)) {
                dto.setProgress(Double.parseDouble(progress));
            } else {
                dto.setProgress(0D);
            }
        }
        return dto;
    }
}
