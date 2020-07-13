package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.ThreadUtil;
import com.yimao.cloud.pojo.dto.water.TrafficStatisticsDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.TrafficStatisticsVO;
import com.yimao.cloud.water.enums.PlatformEnum;
import com.yimao.cloud.water.mapper.TrafficStatisticsMapper;
import com.yimao.cloud.water.service.TrafficStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

/**
 * 描述：流量数据统计
 */
@Service
@Slf4j
public class TrafficStatisticsServiceImpl implements TrafficStatisticsService {

    @Resource
    private TrafficStatisticsMapper trafficStatisticsMapper;

    @Override
    public Map<Integer, List<TrafficStatisticsDTO>> queryTrafficListByPlatform(Integer source, String sn) {
        //广告平台：0-翼猫（自有）；1-百度；2-京东；3-科大讯飞；4-简视；5-海大；6-后台交互；7-易售，10-其它
        Map<Integer, List<TrafficStatisticsDTO>> groupMap = new HashMap<>();
        List<TrafficStatisticsDTO> ymList;
        List<TrafficStatisticsDTO> bdList;
        List<TrafficStatisticsDTO> jdList;
        List<TrafficStatisticsDTO> kdxfList;
        List<TrafficStatisticsDTO> jsList;
        List<TrafficStatisticsDTO> hdList;
        List<TrafficStatisticsDTO> systemList;
        List<TrafficStatisticsDTO> ysList;
        List<TrafficStatisticsDTO> otherList ;
        try{
            Callable<List<TrafficStatisticsDTO>> ymCallable = () -> trafficStatisticsMapper.queryTrafficListByPlatform(PlatformEnum.YIMAO.value, source, sn);
            FutureTask<List<TrafficStatisticsDTO>> ymTask = new FutureTask<>(ymCallable);
            ThreadUtil.executor.submit(ymTask);
            Callable<List<TrafficStatisticsDTO>> bdCallable = () -> trafficStatisticsMapper.queryTrafficListByPlatform(PlatformEnum.BAIDU.value, source, sn);
            FutureTask<List<TrafficStatisticsDTO>> bdTask = new FutureTask<>(bdCallable);
            ThreadUtil.executor.submit(bdTask);
            Callable<List<TrafficStatisticsDTO>> jdCallable = () -> trafficStatisticsMapper.queryTrafficListByPlatform(PlatformEnum.JINGDONG.value, source, sn);
            FutureTask<List<TrafficStatisticsDTO>> jdTask = new FutureTask<>(jdCallable);
            ThreadUtil.executor.submit(jdTask);
            Callable<List<TrafficStatisticsDTO>> kdxfCallable = () -> trafficStatisticsMapper.queryTrafficListByPlatform(PlatformEnum.IFLYTEK.value, source, sn);
            FutureTask<List<TrafficStatisticsDTO>> kdxfTask = new FutureTask<>(kdxfCallable);
            ThreadUtil.executor.submit(kdxfTask);
            Callable<List<TrafficStatisticsDTO>> jsCallable = () -> trafficStatisticsMapper.queryTrafficListByPlatform(PlatformEnum.JIANSHI.value, source, sn);
            FutureTask<List<TrafficStatisticsDTO>> jsTask = new FutureTask<>(jsCallable);
            ThreadUtil.executor.submit(jsTask);
            Callable<List<TrafficStatisticsDTO>> hdCallable = () -> trafficStatisticsMapper.queryTrafficListByPlatform(PlatformEnum.HAIDA.value, source, sn);
            FutureTask<List<TrafficStatisticsDTO>> hdTask = new FutureTask<>(hdCallable);
            ThreadUtil.executor.submit(hdTask);
            Callable<List<TrafficStatisticsDTO>> systemCallable = () -> trafficStatisticsMapper.queryTrafficListByPlatform(PlatformEnum.SYSTEM.value, source, sn);
            FutureTask<List<TrafficStatisticsDTO>> systemTask = new FutureTask<>(systemCallable);
            ThreadUtil.executor.submit(systemTask);
            Callable<List<TrafficStatisticsDTO>> otherCallable = () -> trafficStatisticsMapper.queryTrafficListByPlatform(PlatformEnum.OTHER.value, source, sn);
            FutureTask<List<TrafficStatisticsDTO>> otherTask = new FutureTask<>(otherCallable);
            ThreadUtil.executor.submit(otherTask);
            Callable<List<TrafficStatisticsDTO>> ysListCallable = () -> trafficStatisticsMapper.queryTrafficListByPlatform(PlatformEnum.YISHOU.value, source, sn);
            FutureTask<List<TrafficStatisticsDTO>> ysListTask = new FutureTask<>(ysListCallable);
            ThreadUtil.executor.submit(ysListTask);


            ymList  = ymTask.get();
            groupMap.put(PlatformEnum.YIMAO.value, ymList);
            bdList=bdTask.get();
            groupMap.put(PlatformEnum.BAIDU.value, bdList);
            jdList=jdTask.get();
            groupMap.put(PlatformEnum.JINGDONG.value,jdList);
            kdxfList=kdxfTask.get();
            groupMap.put(PlatformEnum.IFLYTEK.value, kdxfList);
            jsList=jsTask.get();
            groupMap.put(PlatformEnum.JIANSHI.value, jsList);
            hdList=hdTask.get();
            groupMap.put(PlatformEnum.HAIDA.value, hdList);
            systemList=systemTask.get();
            groupMap.put(PlatformEnum.SYSTEM.value, systemList);
            otherList=otherTask.get();
            groupMap.put(PlatformEnum.OTHER.value, otherList);
            ysList=ysListTask.get();
            groupMap.put(PlatformEnum.YISHOU.value, ysList);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new YimaoException("流量汇总查询异常！");
        }

        return groupMap;
    }

    @Override
    public PageVO<TrafficStatisticsVO> queryTrafficListBySn(Integer source, String sn, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TrafficStatisticsVO> page = trafficStatisticsMapper.queryTrafficListBySn(source, sn);
        for (TrafficStatisticsVO vo : page) {
            if (vo.getYimaoTotal() == null) {
                vo.setYimaoTotal(new BigDecimal("0"));
            }
            if (vo.getBaiduTotal() == null) {
                vo.setBaiduTotal(new BigDecimal("0"));
            }
            if (vo.getJingdongTotal() == null) {
                vo.setJingdongTotal(new BigDecimal("0"));
            }
            if (vo.getJianshiTotal() == null) {
                vo.setJianshiTotal(new BigDecimal("0"));
            }
            if (vo.getHaidaTotal() == null) {
                vo.setHaidaTotal(new BigDecimal("0"));
            }
            if (vo.getKdxfTotal() == null) {
                vo.setKdxfTotal(new BigDecimal("0"));
            }
            if (vo.getSystemTotal() == null) {
                vo.setSystemTotal(new BigDecimal("0"));
            }
            if (vo.getOtherTotal() == null) {
                vo.setOtherTotal(new BigDecimal("0"));
            }
            if (vo.getYishouTotal() == null) {
                vo.setYishouTotal(new BigDecimal("0"));
            }
            BigDecimal total = vo.getYimaoTotal().add(vo.getBaiduTotal()).add(vo.getJingdongTotal()).add(vo.getJianshiTotal()).add(vo.getHaidaTotal()).add(vo.getSystemTotal()).add(vo.getOtherTotal().add(vo.getYishouTotal()));
            vo.setTotal(total);
        }

        return new PageVO<>(pageNum, page);
    }

    /**
     * 保存流量统计数据
     *
     * @param list
     */
    @Override
    public void saveTraffic(List<TrafficStatisticsDTO> list) {
        Map<Integer, List<TrafficStatisticsDTO>> groupBy = list.stream().collect(Collectors.groupingBy(TrafficStatisticsDTO::getPlatform));
        for (Map.Entry<Integer, List<TrafficStatisticsDTO>> entry : groupBy.entrySet()) {
            if (PlatformEnum.YIMAO.value != entry.getKey()
                    && PlatformEnum.BAIDU.value != entry.getKey()
                    && PlatformEnum.JINGDONG.value != entry.getKey()
                    && PlatformEnum.IFLYTEK.value != entry.getKey()
                    && PlatformEnum.JIANSHI.value != entry.getKey()
                    && PlatformEnum.HAIDA.value != entry.getKey()
                    && PlatformEnum.SYSTEM.value != entry.getKey()
                    && PlatformEnum.OTHER.value != entry.getKey()
                    && PlatformEnum.YISHOU.value != entry.getKey()){
                throw new YimaoException("流量上传失败，平台类型不存在！");
            }
            trafficStatisticsMapper.insertBatch(entry.getValue(), entry.getKey());
        }
    }
}
