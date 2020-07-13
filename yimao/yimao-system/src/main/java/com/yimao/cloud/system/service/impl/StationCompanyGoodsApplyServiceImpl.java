package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.StationCompanyGoodsApplyStateEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.pojo.dto.system.StationCompanyGoodsApplyDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.StationCompanyGoodsApplyAuditRecordMapper;
import com.yimao.cloud.system.mapper.StationCompanyGoodsApplyMapper;
import com.yimao.cloud.system.mapper.StationCompanyStoreHouseMapper;
import com.yimao.cloud.system.mapper.StoreHouseAllMapper;
import com.yimao.cloud.system.po.StationCompanyGoodsApply;
import com.yimao.cloud.system.po.StationCompanyGoodsApplyAuditRecord;
import com.yimao.cloud.system.po.StationCompanyStoreHouse;
import com.yimao.cloud.system.po.StoreHouseAll;
import com.yimao.cloud.system.service.StationCompanyGoodsApplyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class StationCompanyGoodsApplyServiceImpl implements StationCompanyGoodsApplyService {

    @Resource
    private StationCompanyGoodsApplyMapper stationCompanyGoodsApplyMapper;

    @Resource
    private UserCache userCache;

    @Resource
    private StationCompanyGoodsApplyAuditRecordMapper stationCompanyGoodsApplyAuditRecordMapper;

    @Resource
    private StationCompanyStoreHouseMapper stationCompanyStoreHouseMapper;

    @Resource
    private StoreHouseAllMapper storeHouseAllMapper;

    @Override
    public PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyFirstAudit(Integer pageNum, Integer pageSize, String province, String city, String region, Integer categoryId) {
        PageHelper.startPage(pageNum, pageSize);
        Page<StationCompanyGoodsApplyDTO> page = stationCompanyGoodsApplyMapper.pageGoodsApplyFirstAudit(province, city, region, categoryId);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyAfterAudit(Integer pageNum, Integer pageSize, String province, String city, String region, Integer categoryId) {
        PageHelper.startPage(pageNum, pageSize);
        Page<StationCompanyGoodsApplyDTO> page = stationCompanyGoodsApplyMapper.pageGoodsApplyAfterAudit(province, city, region, categoryId);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyTwoAudit(Integer pageNum, Integer pageSize, String province, String city, String region, Integer categoryId, Integer status, Integer isAfterAudit) {
        PageHelper.startPage(pageNum, pageSize);
        Page<StationCompanyGoodsApplyDTO> page = stationCompanyGoodsApplyMapper.pageGoodsApplyTwoAudit(province, city, region, categoryId, status, isAfterAudit);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyHistory(Integer pageNum, Integer pageSize, String province, String city, String region, Integer isAfterAudit) {
        PageHelper.startPage(pageNum, pageSize);
        Page<StationCompanyGoodsApplyDTO> page = stationCompanyGoodsApplyMapper.pageGoodsApplyHistory(province, city, region, isAfterAudit);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyStation(Integer pageNum, Integer pageSize, Integer stationCompanyId, Integer categoryId, Date startTime, Date endTime) {
        PageHelper.startPage(pageNum, pageSize);
        Page<StationCompanyGoodsApplyDTO> page = stationCompanyGoodsApplyMapper.pageGoodsApplyStation(stationCompanyId, categoryId, startTime, endTime);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyStationHistory(Integer pageNum, Integer pageSize, Integer stationCompanyId, Integer categoryId, Integer status, Date startTime, Date endTime) {
        PageHelper.startPage(pageNum, pageSize);
        Page<StationCompanyGoodsApplyDTO> page = stationCompanyGoodsApplyMapper.pageGoodsApplyStationHistory(stationCompanyId, categoryId, status, startTime, endTime);
        return new PageVO<>(pageNum, page);
    }

    /**
     * @param id
     * @param isPass
     * @param cause
     * @param type   1-售后审核 2-初审
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void goodsApplyFirstAudit(String id, Integer isPass, String cause, Integer type) {
        if (isPass == 0 && StringUtil.isEmpty(cause)) {
            throw new BadRequestException("请填写审核不通过原因！");
        }
        StationCompanyGoodsApply stationCompanyGoodsApply = stationCompanyGoodsApplyMapper.selectByPrimaryKey(id);
        if (stationCompanyGoodsApply == null) {
            throw new YimaoException("物资申请订单不存在！");
        }
        if (stationCompanyGoodsApply.getStatus() != StationCompanyGoodsApplyStateEnum.WAITING_AUDIT.value) {
            throw new YimaoException("物资申请订单状态异常,不能进行审核！");
        }
        if (type == 2) {
            if (stationCompanyGoodsApply.getIsAfterAudit()) {
                //不属于售后审核
                throw new YimaoException("该物资申请订单归售后审核！");
            }
        } else if (type == 1) {
            if (!stationCompanyGoodsApply.getIsAfterAudit()) {
                //属于售后审核
                throw new YimaoException("该物资申请订单不需要进行售后审核！");
            }
        }
        //校验总仓库存数量是否充足
        this.checkStoreHouseAllStockCount(stationCompanyGoodsApply.getGoodsId(), stationCompanyGoodsApply.getCount());

        Date now = new Date();
        StationCompanyGoodsApply update = new StationCompanyGoodsApply();
        update.setId(id);
        update.setFirstAuditor(userCache.getCurrentAdminRealName());
        update.setFirstAuditTime(now);
        if (isPass == 1) {
            //审核通过，将订单状态改为待复核
            update.setStatus(StationCompanyGoodsApplyStateEnum.WAITING_RE_AUDIT.value);
        } else {
            //审核不通过，打回
            update.setStatus(StationCompanyGoodsApplyStateEnum.REPULSE.value);
            update.setCause(cause);
        }
        stationCompanyGoodsApplyMapper.updateByPrimaryKeySelective(update);

        //记录审核日志
        StationCompanyGoodsApplyAuditRecord auditRecord = new StationCompanyGoodsApplyAuditRecord();
        auditRecord.setApplyOrderId(id);
        auditRecord.setAuditor(userCache.getCurrentAdminRealName());
        auditRecord.setAuditTime(now);
        auditRecord.setCause(cause);
        auditRecord.setType(1);//初审
        auditRecord.setStatus(isPass);//审核状态 1-通过 0-不通过
        stationCompanyGoodsApplyAuditRecordMapper.insertSelective(auditRecord);
    }

    @Override
    public void goodsApplyTwoAudit(String id, Integer isPass, String cause) {
        if (isPass == 0 && StringUtil.isEmpty(cause)) {
            throw new BadRequestException("请填写审核不通过原因！");
        }
        StationCompanyGoodsApply stationCompanyGoodsApply = stationCompanyGoodsApplyMapper.selectByPrimaryKey(id);
        if (stationCompanyGoodsApply == null) {
            throw new YimaoException("物资申请订单不存在！");
        }
        if (stationCompanyGoodsApply.getStatus() != StationCompanyGoodsApplyStateEnum.WAITING_RE_AUDIT.value) {
            throw new YimaoException("物资申请订单状态异常,不能进行审核！");
        }
        //校验总仓库存数量是否充足
        this.checkStoreHouseAllStockCount(stationCompanyGoodsApply.getGoodsId(), stationCompanyGoodsApply.getCount());

        Date now = new Date();
        StationCompanyGoodsApply update = new StationCompanyGoodsApply();
        update.setId(id);
        update.setTwoAuditor(userCache.getCurrentAdminRealName());
        update.setTwoAuditTime(now);
        if (isPass == 1) {
            //审核通过，将订单状态改为待发货
            update.setStatus(StationCompanyGoodsApplyStateEnum.WAITING_SHIPMENTS.value);
        } else {
            //审核不通过，将订单状态改为待初审，并保存复审不通过原因
            update.setStatus(StationCompanyGoodsApplyStateEnum.WAITING_AUDIT.value);
            update.setTwoAuditNotPassCause(cause);
        }
        stationCompanyGoodsApplyMapper.updateByPrimaryKeySelective(update);

        //记录审核日志
        StationCompanyGoodsApplyAuditRecord auditRecord = new StationCompanyGoodsApplyAuditRecord();
        auditRecord.setApplyOrderId(id);
        auditRecord.setAuditor(userCache.getCurrentAdminRealName());
        auditRecord.setAuditTime(now);
        auditRecord.setCause(cause);
        auditRecord.setType(2);//复核
        auditRecord.setStatus(isPass);//审核状态 1-通过 0-不通过
        stationCompanyGoodsApplyAuditRecordMapper.insertSelective(auditRecord);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void goodsApplyDeliverGoods(String id) {
        StationCompanyGoodsApply stationCompanyGoodsApply = stationCompanyGoodsApplyMapper.selectByPrimaryKey(id);
        if (stationCompanyGoodsApply == null) {
            throw new YimaoException("物资申请订单不存在！");
        }
        if (stationCompanyGoodsApply.getStatus() != StationCompanyGoodsApplyStateEnum.WAITING_SHIPMENTS.value) {
            throw new YimaoException("物资申请订单状态异常，不能发货！");
        }
        //校验总仓库存数量是否充足
        StoreHouseAll storeHouseAll = this.checkStoreHouseAllStockCount(stationCompanyGoodsApply.getGoodsId(), stationCompanyGoodsApply.getCount());

        //扣减库存
        int count = storeHouseAllMapper.pruneStockCountById(storeHouseAll.getId(), stationCompanyGoodsApply.getCount());
        if (count != 1) {
            throw new YimaoException("总仓物资扣减库存失败！");
        }

        //修改物资申请状态
        StationCompanyGoodsApply update = new StationCompanyGoodsApply();
        update.setId(id);
        update.setStatus(StationCompanyGoodsApplyStateEnum.SHIPMENTS.value);//将状态改为配送中
        update.setShipmentsTime(new Date());//发货时间
        stationCompanyGoodsApplyMapper.updateByPrimaryKeySelective(update);
    }

    /**
     * 校验总仓对物资的库存数量是否充足
     *
     * @param goodsId 物资id
     * @param count   申请的物资数量
     * @return
     */
    private StoreHouseAll checkStoreHouseAllStockCount(Integer goodsId, Integer count) {
        //扣减总仓对该物资的库存数量
        StoreHouseAll query = new StoreHouseAll();
        query.setGoodsId(goodsId);
        StoreHouseAll storeHouseAll = storeHouseAllMapper.selectOne(query);
        if (storeHouseAll == null) {
            throw new YimaoException("总仓不存在该物资库存！");
        }
        if (storeHouseAll.getStockCount() - count < 0) {
            throw new YimaoException("总仓该物资库存数量不足，无法发货！");
        }
        return storeHouseAll;
    }

    @Override
    public void goodsApplyAnewSubmit(StationCompanyGoodsApplyDTO dto) {
        StationCompanyGoodsApply stationCompanyGoodsApply = stationCompanyGoodsApplyMapper.selectByPrimaryKey(dto.getId());
        if (stationCompanyGoodsApply == null) {
            throw new BadRequestException("物资申请订单不存在！");
        }
        if (stationCompanyGoodsApply.getStatus() != StationCompanyGoodsApplyStateEnum.REPULSE.value) {
            throw new BadRequestException("物资申请订单状态异常，不能重新提交申请！");
        }
        StationCompanyGoodsApply update = new StationCompanyGoodsApply();
        update.setId(dto.getId());
        update.setStatus(StationCompanyGoodsApplyStateEnum.WAITING_AUDIT.value);
        update.setAccessory(dto.getAccessory());
        update.setAddress(dto.getAddress());
        update.setProvince(dto.getProvince());
        update.setCity(dto.getCity());
        update.setRegion(dto.getRegion());
        update.setStationCompanyId(dto.getStationCompanyId());
        update.setStationCompanyName(dto.getStationCompanyName());
        update.setApplicantAccount(dto.getApplicantAccount());
        update.setApplicantName(dto.getApplicantName());
        update.setFirstCategoryId(dto.getFirstCategoryId());
        update.setFirstCategoryName(dto.getFirstCategoryName());
        update.setTwoCategoryId(dto.getTwoCategoryId());
        update.setTwoCategoryName(dto.getTwoCategoryName());
        update.setGoodsId(dto.getGoodsId());
        update.setGoodsName(dto.getGoodsName());
        update.setIsAfterAudit(dto.getIsAfterAudit());
        update.setCount(dto.getCount());
        update.setApplyTime(new Date());
        update.setCreateTime(stationCompanyGoodsApply.getCreateTime());
        stationCompanyGoodsApplyMapper.updateByPrimaryKey(update);
    }

    @Override
    public void goodsApplySave(StationCompanyGoodsApplyDTO dto) {
        StationCompanyGoodsApply stationCompanyGoodsApply = new StationCompanyGoodsApply(dto);
        stationCompanyGoodsApply.setId(UUIDUtil.getStationCompanyGoodsApplyOrderId());
        stationCompanyGoodsApplyMapper.insertSelective(stationCompanyGoodsApply);
    }

    @Override
    public void goodsApplyConfirm(String id, String confirmImg) {
        if (StringUtil.isEmpty(confirmImg)) {
            throw new BadRequestException("请上传收货图片！");
        }
        StationCompanyGoodsApply stationCompanyGoodsApply = stationCompanyGoodsApplyMapper.selectByPrimaryKey(id);
        if (stationCompanyGoodsApply == null) {
            throw new BadRequestException("物资申请订单不存在！");
        }
        if (stationCompanyGoodsApply.getStatus() != StationCompanyGoodsApplyStateEnum.SHIPMENTS.value) {
            throw new YimaoException("物资申请订单状态异常,不能进行收货操作！");
        }
        //确认收货后服务站公司对应该物品数量增加
        StationCompanyStoreHouse query = new StationCompanyStoreHouse();
        query.setStationCompanyId(stationCompanyGoodsApply.getStationCompanyId());
        query.setGoodsId(stationCompanyGoodsApply.getGoodsId());
        StationCompanyStoreHouse stationCompanyStoreHouse = stationCompanyStoreHouseMapper.selectOne(query);
        if (stationCompanyStoreHouse != null) {
            //存在则增加相应数量
            int count = stationCompanyStoreHouseMapper.addStockCountById(stationCompanyStoreHouse.getId(), stationCompanyGoodsApply.getCount());
            if (count != 1) {
                throw new YimaoException("服务站公司物资库存增加失败！");
            }
        } else {
            //不存在则新增
            int count = stationCompanyStoreHouseMapper.save(stationCompanyGoodsApply.getStationCompanyId(), stationCompanyGoodsApply.getGoodsId(), stationCompanyGoodsApply.getCount());
            if (count != 1) {
                throw new BadRequestException("服务站公司新增物资存库失败！");
            }
        }
        StationCompanyGoodsApply update = new StationCompanyGoodsApply();
        update.setId(id);
        update.setConfirmImg(confirmImg);
        update.setStatus(StationCompanyGoodsApplyStateEnum.COMPLETED.value);//状态设为已完成
        update.setCompletionTime(new Date());
        stationCompanyGoodsApplyMapper.updateByPrimaryKeySelective(update);
    }

    @Override
    public void goodsApplyCancel(String id) {
        StationCompanyGoodsApply stationCompanyGoodsApply = stationCompanyGoodsApplyMapper.selectByPrimaryKey(id);
        if (stationCompanyGoodsApply == null) {
            throw new BadRequestException("物资申请订单不存在！");
        }
        if (stationCompanyGoodsApply.getStatus() != StationCompanyGoodsApplyStateEnum.REPULSE.value ||
                stationCompanyGoodsApply.getStatus() != StationCompanyGoodsApplyStateEnum.WAITING_AUDIT.value ||
                stationCompanyGoodsApply.getStatus() != StationCompanyGoodsApplyStateEnum.WAITING_RE_AUDIT.value) {
            throw new YimaoException("物资申请订单状态异常,不能进行取消申请操作！");
        }
        StationCompanyGoodsApply update = new StationCompanyGoodsApply();
        update.setId(id);
        update.setStatus(StationCompanyGoodsApplyStateEnum.ALREADY_CANCEL.value); //将状态设为已取消
        stationCompanyGoodsApplyMapper.updateByPrimaryKeySelective(update);
    }
}
