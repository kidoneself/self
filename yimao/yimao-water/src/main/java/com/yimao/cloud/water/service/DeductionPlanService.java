package com.yimao.cloud.water.service;

import com.yimao.cloud.water.po.DeductionPlan;
import com.yimao.cloud.water.po.DeductionPlanChangeRecord;
import com.yimao.cloud.water.po.WaterDevice;

import java.util.List;

public interface DeductionPlanService {
    void save(DeductionPlan plan);

    void update(DeductionPlan plan);

    List<DeductionPlan> listByDeviceId(Integer deviceId);

    List<DeductionPlanChangeRecord> listChangeRecordByDeviceId(Integer deviceId);

    DeductionPlan caculate(WaterDevice device);

    DeductionPlan getNext(DeductionPlan currentPlan);

    void updatePlansForChangeDevice(Integer deviceId, Integer currentTotalFlow, String oldSn, String newSn);

    void changePlan(Integer planId);
}
