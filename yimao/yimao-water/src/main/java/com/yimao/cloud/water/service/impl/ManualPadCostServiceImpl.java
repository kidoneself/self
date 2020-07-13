package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.export.water.ManualPadCostExport;
import com.yimao.cloud.pojo.query.water.ManualPadCostQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.ManualPadCostVO;
import com.yimao.cloud.water.mapper.ManualPadCostMapper;
import com.yimao.cloud.water.mapper.WaterDeviceMapper;
import com.yimao.cloud.water.po.ManualPadCost;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.service.ManualPadCostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 描述：手动修改水机配额
 *
 * @Author Zhang Bo
 * @Date 2019/5/7
 */
@Service
public class ManualPadCostServiceImpl implements ManualPadCostService {

    @Resource
    private ManualPadCostMapper manualPadCostMapper;
    @Resource
    private WaterDeviceMapper waterDeviceMapper;

    /**
     * 创建手动修改水机配额
     *
     * @param record 手动修改水机配额
     */
    @Override
    public void save(ManualPadCost record) {
        this.check(record);
        if (StringUtil.isEmpty(record.getSn())) {
            throw new BadRequestException("设备SN码不能为空。");
        }
        String sn = record.getSn();
        WaterDevice deviceQuery = new WaterDevice();
        deviceQuery.setSn(sn);
        int count = waterDeviceMapper.selectCount(deviceQuery);
        if (count <= 0) {
            throw new BadRequestException("请输入正确的sn码。");
        } else {
            ManualPadCost query = new ManualPadCost();
            query.setSn(sn);
            //同步状态：0-未同步；1-同步完成；2-同步失败；
            query.setSyncStatus(0);
            count = manualPadCostMapper.selectCount(query);
            if (count > 0) {
                throw new BadRequestException("该sn码已存在未同步记录，无法再次提交。");
            } else {
                record.setCreateTime(new Date());
                record.setSyncStatus(0);
                manualPadCostMapper.insert(record);
            }
        }
    }

    /**
     * 修改手动修改水机配额
     *
     * @param record 手动修改水机配额
     */
    @Override
    public void update(ManualPadCost record) {
        this.check(record);
        ManualPadCost update = new ManualPadCost();
        update.setId(record.getId());
        update.setBalance(record.getBalance());
        update.setDischarge(record.getDischarge());
        update.setOpen(record.getOpen());
        int i = manualPadCostMapper.updateByPrimaryKeySelective(update);
        if (i <= 0) {
            throw new BadRequestException("操作失败。");
        }
    }

    /**
     * 查询手动修改水机配额（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @Override
    public PageVO<ManualPadCostVO> page(Integer pageNum, Integer pageSize, ManualPadCostQuery query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ManualPadCostVO> page = manualPadCostMapper.list(query);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 查询手动修改水机配额
     *
     * @param sncode SN码
     */
    @Override
    public List<ManualPadCost> listBySn(String sncode) {
        ManualPadCost query = new ManualPadCost();
        query.setSn(sncode);
        query.setSyncStatus(0);
        query.setOpen(true);
        return manualPadCostMapper.select(query);
    }

    @Override
    public List<ManualPadCostExport> export(ManualPadCostQuery query) {
        return manualPadCostMapper.export(query);
    }

    /**
     * 参数校验
     *
     * @param mpc 校验对象
     */
    private void check(ManualPadCost mpc) {
        if (mpc.getBalance() == null) {
            throw new BadRequestException("余额不能为空。");
        }
        if (mpc.getDischarge() == null) {
            throw new BadRequestException("已用流量不能为空。");
        }
        if (mpc.getOpen() == null) {
            throw new BadRequestException("是否开启不能为空。");
        }
    }
}
