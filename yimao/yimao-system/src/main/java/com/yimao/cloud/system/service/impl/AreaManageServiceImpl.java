package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.OrderStatusEnum;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.ExcelUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.system.AreaManageDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.AreaManageMapper;
import com.yimao.cloud.system.po.AreaManage;
import com.yimao.cloud.system.po.AreaManageBean;
import com.yimao.cloud.system.service.AreaManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Lizhqiang
 * @date 2019-08-19
 */
@Slf4j
@Service
public class AreaManageServiceImpl implements AreaManageService {

    @Resource
    private AreaManageMapper areaManageMapper;
    @Resource
    private RedisCache redisCache;


    @Override
    public PageVO<AreaManage> page(Integer pageNum, Integer pageSize, Integer id, Integer level, Integer pid) {
        PageHelper.startPage(pageNum, pageSize);
        Page<AreaManage> page = areaManageMapper.page(id, level, pid);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public void update(AreaManage areaManage) {
        areaManageMapper.updateByPrimaryKeySelective(areaManage);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void importExcel(MultipartFile multipartFile) {
        String[] beanPropertys = new String[]{"province", "city", "region", "level", "population", "numerous", "siteCount", "premium", "directSale"};
        try {
        	
        	//导入数据封装对象集合
            List<AreaManageBean> deliveryList = ExcelUtil.parserExcel(AreaManageBean.class, multipartFile, beanPropertys, 1);
            //查询后台区域管理现有数据
            List<AreaManage> areaList=areaManageMapper.selectAll();
            
            Map<Integer,List<AreaManage>> areaListMap=new HashMap<Integer,List<AreaManage>>();
            //初始化存入省的集合
            areaListMap.put(10, new ArrayList<AreaManage>());
            
        	//一级目录name对应id(省)
        	Map<String,Integer> firstLevelNameWithIdMap=new HashMap<String,Integer>();
        	//二级目录name对应id(市)
        	Map<String,Integer> secondLevelNameWithIdMap=new HashMap<String,Integer>();
        	//三级目录name对应的id(区)
        	Map<String,Integer> thirdLevelNameWithIdMap=new HashMap<String,Integer>();
            
            if(Objects.nonNull(areaList) && areaList.size()>0) {            	
            	
            	for (AreaManage areaManage : areaList) {
            		//获取level
            		Integer level=areaManage.getLevel();
            		
            		//存在省和市同名的情况需要根据level区分
            		if(level == 1) {
            			firstLevelNameWithIdMap.put(areaManage.getName(), areaManage.getId());
            		}else if(level == 2) {
            			secondLevelNameWithIdMap.put(areaManage.getName(), areaManage.getId());
            		}else if(level ==3) {
            			thirdLevelNameWithIdMap.put(areaManage.getName(), areaManage.getId());
            		}
            		           		
        			if(areaListMap.containsKey(areaManage.getPid())) {
        				areaListMap.get(areaManage.getPid()).add(areaManage);
        			}else {
        				List<AreaManage> list=new ArrayList<AreaManage>();
        				list.add(areaManage);
        				areaListMap.put(areaManage.getPid(), list);
        			}
            		           	
				}
            	//将二级目录每个key对应的几个进行排序，按id的值进行升序排列，便于后期新增id
            	for (List<AreaManage> areaManageList : areaListMap.values()) {
					if(areaManageList.size()>1) {						
						areaManageList.sort(new Comparator<AreaManage>() {
							@Override
							public int compare(AreaManage o1, AreaManage o2) {
								
								return o1.getId()-o2.getId();
							}
							
						});
					}
				}
            	
            }	
            	            	
        	//对比导入数据
        	for (AreaManageBean areaManageBean : deliveryList) {
        		//获取level
        		Integer level=areaManageBean.getLevel();
        		
        		if(Objects.isNull(level)) {
        			continue;
        		}
        		
        		if(level == 1) {//导入为省，判断是否存在该省
        			if(Objects.isNull(areaManageBean.getProvince())) {
        				continue;
        			}
        			
        			if(firstLevelNameWithIdMap.containsKey(areaManageBean.getProvince())) {
        				 //更新改条省数据
        				 AreaManage updateAreaManage = new AreaManage();
        				 BeanUtils.copyProperties(areaManageBean, updateAreaManage);
        				 updateAreaManage.setId(firstLevelNameWithIdMap.get(areaManageBean.getProvince()));
        				 areaManageMapper.updateByPrimaryKeySelective(updateAreaManage);
        				 //替换map中该省
        				 List<AreaManage> provinceList=areaListMap.get(10);
        				 provinceList.set(provinceList.indexOf(updateAreaManage), updateAreaManage);
        				 
        			}else {
        				//省不存在新增
        				AreaManage insertProvinceArea = new AreaManage();
        				BeanUtils.copyProperties(areaManageBean, insertProvinceArea);
        				insertProvinceArea.setPid(10);
        				insertProvinceArea.setName(areaManageBean.getProvince());
        				//获取map中的省
        				List<AreaManage> provinceList=areaListMap.get(10);
        				if(provinceList.size() < 1) {
        					insertProvinceArea.setId(10*10+10);        					
        				}else {
        					insertProvinceArea.setId(provinceList.get(provinceList.size()-1).getId()+10);
        				}
        				areaManageMapper.insertSelective(insertProvinceArea);
        				//将省加入map
        				firstLevelNameWithIdMap.put(insertProvinceArea.getName(), insertProvinceArea.getId());
        				areaListMap.get(10).add(insertProvinceArea);
        				areaListMap.put(insertProvinceArea.getId(), new ArrayList<AreaManage>());        				
        			}
        			
        		}else if(level == 2) {//导入为市，获取对应省名
        			if(Objects.isNull(areaManageBean.getProvince()) || Objects.isNull(areaManageBean.getCity())) {
        				continue;
        			}
        			
        			if(firstLevelNameWithIdMap.containsKey(areaManageBean.getProvince())) {
        				//判断是否存在该市
        				if(secondLevelNameWithIdMap.containsKey(areaManageBean.getCity())) {
        					 //更新市
        					 AreaManage updateCityArea = new AreaManage();
        					 BeanUtils.copyProperties(areaManageBean, updateCityArea);
        					 updateCityArea.setId(secondLevelNameWithIdMap.get(areaManageBean.getCity()));
            				 areaManageMapper.updateByPrimaryKeySelective(updateCityArea);
            				 //替换map中该市所对应的省下市集合中的市
            				 List<AreaManage> cityList=areaListMap.get(firstLevelNameWithIdMap.get(areaManageBean.getProvince()));
            				 cityList.set(cityList.indexOf(updateCityArea), updateCityArea);
        				}else {
        					 //不存在新增       					
        					 AreaManage insertCityArea = new AreaManage();
        					 BeanUtils.copyProperties(areaManageBean, insertCityArea);
        					 insertCityArea.setName(areaManageBean.getCity());
        					 insertCityArea.setPid(firstLevelNameWithIdMap.get(areaManageBean.getProvince()));
        					 List<AreaManage> cityList=areaListMap.get(insertCityArea.getPid());
        					 if(cityList.size()<1) {
        						 insertCityArea.setId(insertCityArea.getPid()*10+1);
        					 }else {
        						 insertCityArea.setId(cityList.get(cityList.size()-1).getId()+1); 
        					 }           					 
        					 areaManageMapper.insertSelective(insertCityArea);
        					 //将市放入map
        					 secondLevelNameWithIdMap.put(insertCityArea.getName(), insertCityArea.getId());
        					 areaListMap.get(insertCityArea.getPid()).add(insertCityArea);
        					 areaListMap.put(insertCityArea.getId(), new ArrayList<AreaManage>());           					 
        				}
        				
        			}else {
        				//新增省       				
        				AreaManage insertProvinceArea = new AreaManage();
        				insertProvinceArea.setName(areaManageBean.getProvince());
        				insertProvinceArea.setLevel(1);
        				insertProvinceArea.setPid(10);
        				List<AreaManage> provinceList=areaListMap.get(10);
        				if(provinceList.size()<1) {
        					insertProvinceArea.setId(10*10+10);
        				}else {
        					insertProvinceArea.setId(provinceList.get(provinceList.size()-1).getId()+10);
        				}        				
        				areaManageMapper.insertSelective(insertProvinceArea);
        				//将省加入map
        				firstLevelNameWithIdMap.put(insertProvinceArea.getName(), insertProvinceArea.getId());
        				areaListMap.get(10).add(insertProvinceArea);
        				areaListMap.put(insertProvinceArea.getId(), new ArrayList<AreaManage>());      
        				//新增市
        				AreaManage insertCityArea = new AreaManage();
	   					BeanUtils.copyProperties(areaManageBean, insertCityArea);
	   					insertCityArea.setName(areaManageBean.getCity());
	   					insertCityArea.setPid(insertProvinceArea.getId());	   					
	   					insertCityArea.setId(insertCityArea.getPid()*10+1);	   					         					 
	   					areaManageMapper.insertSelective(insertCityArea);
	   					//将市放入map
	   					secondLevelNameWithIdMap.put(insertCityArea.getName(), insertCityArea.getId());
	   					areaListMap.get(insertCityArea.getPid()).add(insertCityArea);
	   					areaListMap.put(insertCityArea.getId(), new ArrayList<AreaManage>());           
        				
        			}
        			
        		}else if(level == 3){//导入为区，获取对应市名
        			if(Objects.isNull(areaManageBean.getProvince()) || Objects.isNull(areaManageBean.getCity()) || Objects.isNull(areaManageBean.getRegion())) {
        				continue;
        			}
        			//是否存在该市
        			if(secondLevelNameWithIdMap.containsKey(areaManageBean.getCity())) {
        				//是否存在该区
        				if(thirdLevelNameWithIdMap.containsKey(areaManageBean.getRegion())) {
        					 //更新区
        					 AreaManage updateAreaManage = new AreaManage();
        					 BeanUtils.copyProperties(areaManageBean, updateAreaManage);
        					 updateAreaManage.setId(thirdLevelNameWithIdMap.get(areaManageBean.getRegion()));
        					 areaManageMapper.updateByPrimaryKeySelective(updateAreaManage);
        					 //替换map中该区所对应的市下市集合中的区
        					 List<AreaManage> regionList=areaListMap.get(secondLevelNameWithIdMap.get(areaManageBean.getCity()));
        					 regionList.set(regionList.indexOf(updateAreaManage), updateAreaManage); 
        				}else {
        					 //新增区
        					 AreaManage insertAreaManage = new AreaManage();
        					 BeanUtils.copyProperties(areaManageBean, insertAreaManage);
        					 insertAreaManage.setName(areaManageBean.getRegion());
        					 insertAreaManage.setPid(secondLevelNameWithIdMap.get(areaManageBean.getCity()));
        					 List<AreaManage> pidList=areaListMap.get(insertAreaManage.getPid());
        					 if(pidList.size()<1) {
        						 insertAreaManage.setId(insertAreaManage.getPid()*100+1); 
        					 }else {
        						 insertAreaManage.setId(pidList.get(pidList.size()-1).getId()+1);
        					 }            					
        					 areaManageMapper.insertSelective(insertAreaManage);
        					 //将区加入map
        					 thirdLevelNameWithIdMap.put(insertAreaManage.getName(), insertAreaManage.getId());
        					 areaListMap.get(insertAreaManage.getPid()).add(insertAreaManage);
        				}
        				
        			}else if(firstLevelNameWithIdMap.containsKey(areaManageBean.getProvince())) {//判断是否存在省
        				//新增市其他数据默认值为0
        				 AreaManage insertCityArea = new AreaManage();
        				 insertCityArea.setName(areaManageBean.getCity());
        				 insertCityArea.setLevel(2);
        				 insertCityArea.setPid(firstLevelNameWithIdMap.get(areaManageBean.getProvince()));
        				 List<AreaManage> cityList=areaListMap.get(insertCityArea.getPid());
        				 if(cityList.size()<1) {
        					 insertCityArea.setId(insertCityArea.getPid()*10+1); 
        				 }else {
        					 insertCityArea.setId(cityList.get(cityList.size()-1).getId()+1); 
        				 }
        				 areaManageMapper.insertSelective(insertCityArea);
        				 //将市加入map中
        				 secondLevelNameWithIdMap.put(insertCityArea.getName(), insertCityArea.getId());
        				 areaListMap.get(insertCityArea.getPid()).add(insertCityArea);
        				 areaListMap.put(insertCityArea.getId(), new ArrayList<AreaManage>());       				 
        				
        				 //在新增区
        				 AreaManage insertRegionArea = new AreaManage();
        				 BeanUtils.copyProperties(areaManageBean, insertRegionArea);
        				 insertRegionArea.setName(areaManageBean.getRegion());
        				 insertRegionArea.setPid(insertCityArea.getId());
        				 insertRegionArea.setId(insertRegionArea.getPid()*100+1);
        				 areaManageMapper.insertSelective(insertRegionArea);
        				 //将区加入map
        				 thirdLevelNameWithIdMap.put(insertRegionArea.getName(), insertRegionArea.getId());
        				 areaListMap.get(insertRegionArea.getPid()).add(insertRegionArea);
        				 
        			}else {
        				//新增省
        				 AreaManage insertProvinceArea = new AreaManage();
        				 insertProvinceArea.setLevel(1);
        				 insertProvinceArea.setPid(10);
        				 insertProvinceArea.setName(areaManageBean.getProvince());
        				 //获取map中的省
         				 List<AreaManage> provinceList=areaListMap.get(10);
        				 if(provinceList.size()<1) {
        					 insertProvinceArea.setId(10*10+10);
        				 }else {
        					 insertProvinceArea.setId(provinceList.get(provinceList.size()-1).getId()+10);
        				 }
        				 areaManageMapper.insertSelective(insertProvinceArea);
        				 //将省加入map
         				 firstLevelNameWithIdMap.put(insertProvinceArea.getName(), insertProvinceArea.getId());
         				 areaListMap.get(10).add(insertProvinceArea);
         				 areaListMap.put(insertProvinceArea.getId(), new ArrayList<AreaManage>()); 
        				 //新增市
        				 AreaManage insertCityArea = new AreaManage();
        				 insertCityArea.setLevel(2);
        				 insertCityArea.setPid(insertProvinceArea.getId());
        				 insertCityArea.setName(areaManageBean.getCity());
        				 insertCityArea.setId(insertCityArea.getPid()*10+1);
        				 areaManageMapper.insertSelective(insertCityArea);
        				 //将市加入map中
        				 secondLevelNameWithIdMap.put(insertCityArea.getName(), insertCityArea.getId());
        				 areaListMap.get(insertCityArea.getPid()).add(insertCityArea);
        				 areaListMap.put(insertCityArea.getId(), new ArrayList<AreaManage>());
        				//新增区
        				 AreaManage insertRegionArea = new AreaManage();
        				 BeanUtils.copyProperties(areaManageBean, insertRegionArea);
        				 insertRegionArea.setPid(insertCityArea.getId());
        				 insertRegionArea.setId(insertRegionArea.getPid()*100+1);
        				 insertRegionArea.setName(areaManageBean.getRegion());
        				 areaManageMapper.insertSelective(insertRegionArea);
        				 //将区加入map
        				 thirdLevelNameWithIdMap.put(insertRegionArea.getName(), insertRegionArea.getId());
        				 areaListMap.get(insertRegionArea.getPid()).add(insertRegionArea);
        			}
        			
        			           			
        		}else {
        			continue;
        		}
			}
        	
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("导入地区管理信息失败！");
        }
    }

	@Override
	public List<AreaManageDTO> getAreaManagerList() {
		
		return areaManageMapper.getAreaManagerList();
	}

}
