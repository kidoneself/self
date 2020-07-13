package com.yimao.cloud.order.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.yimao.cloud.base.enums.OrderSalePerformTypeEnum;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.mapper.OrderSubMapper;
import com.yimao.cloud.order.mapper.SalePerformRankMapper;
import com.yimao.cloud.order.po.OrderSalePerformRank;
import com.yimao.cloud.pojo.dto.user.SalePerformRankDTO;

import lombok.extern.slf4j.Slf4j;


/***
 * @desc 销售业绩排行榜-统计每个服务站公司下各个经销商的销售业绩(每个月一号0时0分0秒执行)--包含招商销售和产品销售业绩
 * 计算上个月的销售业务
 * @author zhangbaobao
 * @date 2020/4/26
 */
@Slf4j
@DisallowConcurrentExecution//该注解表示前一个任务执行结束再执行下一次任务
public class SalesPerformRankJob extends QuartzJobBean {
    
	private static final String SALESPERFORM_RANK_LOCK = "SALESPERFORM_RANK_LOCK";
	@Resource UserFeign userFeign;
	
	@Resource
	private SalePerformRankMapper salePerformRankMapper;
	
	@Resource
	private OrderSubMapper orderSubMapper;
	
	 @Resource
	 private RedisLock redisLock;
	 
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
        	
            log.info("============经销商业绩排行定时任务开始执行===" + DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
          //获取锁
            if (!redisLock.lockWithTimeout(SALESPERFORM_RANK_LOCK, 300L)) {
                return;
            }
            //开始时间
            String startTime=DateUtil.getMonthBeginTime(DateUtil.transferStringToDate(DateUtil.addMonths(new Date(), -1, "yyyy-MM-dd")));
            
            //结束时间
            String endTime=DateUtil.getCurrentMonthEndTime(DateUtil.transferStringToDate(DateUtil.addMonths(new Date(), -1, "yyyy-MM-dd")));
            
            //统计月份
            String statMonth=DateUtil.getChangeMonthByDate(DateUtil.transferStringToDate(DateUtil.addMonths(new Date(), -1, "yyyy-MM-dd")), 0); 
           
            log.info("============经销商业绩排行定时任务请求参数===startTime=" +startTime+",endTime="+endTime+",statMonth="+statMonth);
            //根据开始时间和结束时间获取招商销售业绩数据
            List<SalePerformRankDTO> dprdList=userFeign.getDistributorPerformRank(startTime,endTime);
            
            log.info("============经销商业绩排行定时任务-dprdList" +dprdList.size());
            List<OrderSalePerformRank> osprList=new ArrayList<OrderSalePerformRank>();
            if(!dprdList.isEmpty()) {
            	for(SalePerformRankDTO sprd:dprdList) {
            		OrderSalePerformRank spr=new OrderSalePerformRank(sprd);
            		spr.setType(OrderSalePerformTypeEnum.INVEST_SALEPERFORM.value);//招商销售
            		//salePerformRankMapper.insertSelective(spr);
            		spr.setStatMonth(statMonth);
            		osprList.add(spr);
                }
            }
            
            //获取产品销售业绩数据
            List<SalePerformRankDTO> poprList=orderSubMapper.getProductOrderPerformRank(startTime,endTime);
            log.info("============经销商业绩排行定时任务-poprList" +poprList.size());
            if(!poprList.isEmpty()) {
            	for(SalePerformRankDTO sprd:poprList) {
            		OrderSalePerformRank spr=new OrderSalePerformRank(sprd);
            		spr.setType(OrderSalePerformTypeEnum.PRODUCT_SALEPERFORM.value);//产品销售
            		//salePerformRankMapper.insertSelective(spr);
            		spr.setStatMonth(statMonth);
            		osprList.add(spr);
                }
            }
            
            //批量保存排行榜数据
            if (!osprList.isEmpty()) {
            	salePerformRankMapper.batchInsert(osprList);
			}
            log.info("============经销商业绩排行定时任务执行完毕=共("+osprList.size()+")条数据==" + DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.error("=====经销商业绩排行定时任务执行异常============"+e.getMessage());
        }finally {
        	redisLock.unLock(SALESPERFORM_RANK_LOCK);
        }
    }
}

