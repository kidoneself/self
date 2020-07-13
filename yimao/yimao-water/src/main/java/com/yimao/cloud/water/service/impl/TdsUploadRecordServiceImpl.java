package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.query.water.TdsUploadRecordQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.TdsUploadRecordVO;
import com.yimao.cloud.water.mapper.TdsMapper;
import com.yimao.cloud.water.mapper.TdsUploadRecordMapper;
import com.yimao.cloud.water.mapper.WaterDeviceMapper;
import com.yimao.cloud.water.po.FilterSetting;
import com.yimao.cloud.water.po.Tds;
import com.yimao.cloud.water.po.TdsUploadRecord;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.service.FilterSettingService;
import com.yimao.cloud.water.service.TdsUploadRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/7/11
 */
@Service
public class TdsUploadRecordServiceImpl implements TdsUploadRecordService {

    @Resource
    private UserCache userCache;
    @Resource
    private TdsUploadRecordMapper tdsUploadRecordMapper;
    @Resource
    private WaterDeviceMapper waterDeviceMapper;
    @Resource
    private TdsMapper tdsMapper;
    @Resource
    private FilterSettingService filterSettingService;

    /**
     * 创建设备TDS值上传记录
     *
     * @param tdsUploadRecord TDS值上传记录
     */
    @Override
    public void save(TdsUploadRecord tdsUploadRecord) {
        this.check(tdsUploadRecord);
        tdsUploadRecord.setCreateTime(new Date());
        tdsUploadRecord.setId(null);
        tdsUploadRecordMapper.insert(tdsUploadRecord);
    }

    /**
     * 修改设备TDS值上传记录
     *
     * @param tdsUploadRecord TDS值上传记录
     */
    @Override
    public void update(TdsUploadRecord tdsUploadRecord) {
        this.check(tdsUploadRecord);
        tdsUploadRecordMapper.updateByPrimaryKey(tdsUploadRecord);
    }

    /**
     * 恢复设备TDS
     *
     * @param sn 设备SN码
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void reset(String sn) {
        WaterDevice query = new WaterDevice();
        query.setSn(sn);
        WaterDevice device = waterDeviceMapper.selectOne(query);
        if (device == null) {
            throw new BadRequestException("设备不存在。");
        }
        if (device.getTdsId() == null) {
            throw new BadRequestException("该设备当前未设置k,t值，无法恢复。");
        }
        FilterSetting setting = filterSettingService.getByPCR(device.getProvince(), device.getCity(), device.getRegion(), device.getDeviceModel());
        Tds tds = new Tds(1.0D, 1.0D);
        Tds tdsed = tdsMapper.selectByPrimaryKey(device.getTdsId());
        if (setting != null) {
            tds = new Tds(setting.getK(), setting.getT());
        }
        if (tds.isSame(tdsed)) {
            throw new BadRequestException("设备当前的k,t值已经是默认值，不进行恢复。");
        }
        Tds dbTds = tdsMapper.selectByKT(tds.getK(), tds.getT());
        if (dbTds == null) {
            tdsMapper.insert(tds);
        } else {
            tds.setId(dbTds.getId());
        }

        WaterDevice update = new WaterDevice();
        update.setId(device.getId());
        update.setTdsId(tds.getId());
        waterDeviceMapper.updateByPrimaryKeySelective(update);

        //保存设备TDS值上传记录
        Date date = new Date();
        TdsUploadRecord record = new TdsUploadRecord();
        record.setDeviceId(device.getId());
        record.setSn(device.getSn());
        record.setEngineerId(device.getEngineerId());
        record.setEngineerName(device.getEngineerName());
        record.setType(1);
        record.setTypeName("恢复");
        record.setK(tdsed.getK());
        record.setT(tdsed.getT());
        record.setCurrentK(tds.getK());
        record.setCurrentT(tds.getT());
        record.setCreateTime(date);
        record.setVerifyStatus(StatusEnum.YES.value());
        record.setVerifyResult(StatusEnum.YES.value());
        record.setVerifyUser(userCache.getCurrentAdminRealName());
        record.setVerifyTime(date);
        tdsUploadRecordMapper.insert(record);
    }

    /**
     * 审核
     *
     * @param id           TDS值上传记录ID
     * @param verifyResult 审核结果：Y-审核通过；N-审核不通过；
     * @param verifyReason 审核原因
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void verify(Integer id, String verifyResult, String verifyReason) {
        if (!StatusEnum.isYes(verifyResult) && !StatusEnum.isNo(verifyResult)) {
            throw new BadRequestException("审核结果参数错误。");
        }

        TdsUploadRecord update = new TdsUploadRecord();
        update.setId(id);
        if (StatusEnum.isYes(verifyResult)) {
            update.setVerifyStatus(StatusEnum.YES.value());
            update.setVerifyResult(StatusEnum.YES.value());
            update.setVerifyUser(userCache.getCurrentAdminRealName());
            update.setVerifyTime(new Date());

            //审核通过需要修改设备对应的KT值
            TdsUploadRecord tdsUploadRecord = tdsUploadRecordMapper.selectByPrimaryKey(id);
            Tds tds = tdsMapper.selectByKT(tdsUploadRecord.getCurrentK(), tdsUploadRecord.getCurrentT());
            if (tds == null) {
                tds = new Tds();
                tds.setK(tdsUploadRecord.getCurrentK());
                tds.setT(tdsUploadRecord.getCurrentT());
                tdsMapper.insert(tds);
            }
            WaterDevice device = new WaterDevice();
            device.setId(tdsUploadRecord.getDeviceId());
            device.setTdsId(tds.getId());
            waterDeviceMapper.updateByPrimaryKeySelective(device);
        } else {
            update.setVerifyStatus(StatusEnum.YES.value());
            update.setVerifyResult(StatusEnum.NO.value());
            update.setVerifyReason(verifyReason);
            update.setVerifyUser(userCache.getCurrentAdminRealName());
            update.setVerifyTime(new Date());
        }
        tdsUploadRecordMapper.updateByPrimaryKeySelective(update);
    }

    /**
     * 查询设备TDS值上传记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @Override
    public PageVO<TdsUploadRecordVO> page(Integer pageNum, Integer pageSize, TdsUploadRecordQuery query) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<TdsUploadRecordVO> page = tdsUploadRecordMapper.selectPage(query);
        // PO对象转成DTO对象
        return new PageVO<>(pageNum, page);
    }

    /**
     * 详情
     *
     * @param id TDS值上传记录ID
     */
    @Override
    public TdsUploadRecordVO getDetail(Integer id) {
        return tdsUploadRecordMapper.getDetailById(id);
    }

    /**
     * 必要信息校验
     *
     * @param tdsUploadRecord TDS值上传记录
     */
    private void check(TdsUploadRecord tdsUploadRecord) {
        if (tdsUploadRecord.getK() == null) {
            throw new BadRequestException("原K不能为空。");
        }
        if (tdsUploadRecord.getT() == null) {
            throw new BadRequestException("原T不能为空。");
        }
        if (tdsUploadRecord.getCurrentK() == null) {
            throw new BadRequestException("新K不能为空。");
        }
        if (tdsUploadRecord.getCurrentT() == null) {
            throw new BadRequestException("新T不能为空。");
        }
        if (tdsUploadRecord.getType() == null) {
            throw new BadRequestException("操作类型不能为空。");
        }
        if (StringUtil.isBlank(tdsUploadRecord.getSn())) {
            throw new BadRequestException("设备SN码不能为空。");
        }
        if (StringUtil.isBlank(tdsUploadRecord.getVerifyStatus())) {
            throw new BadRequestException("审核状态不能为空。");
        }
        if (!StatusEnum.isYes(tdsUploadRecord.getVerifyStatus()) && !StatusEnum.isNo(tdsUploadRecord.getVerifyStatus())) {
            throw new BadRequestException("审核状态参数错误。");
        }
    }
}
