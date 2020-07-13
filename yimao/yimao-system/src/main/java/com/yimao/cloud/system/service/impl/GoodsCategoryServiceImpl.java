package com.yimao.cloud.system.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.system.mapper.GoodsCategoryMapper;
import com.yimao.cloud.system.po.GoodsCategory;
import com.yimao.cloud.system.service.GoodsCategoryService;

import tk.mybatis.mapper.entity.Example;
@Service
public class GoodsCategoryServiceImpl implements GoodsCategoryService {
	@Resource
	private UserCache userCache;
	@Resource
	private GoodsCategoryMapper goodsCategoryMapper;

	@Override
	public void saveGoodsCategory(GoodsCategory goodsCategory) {
		
		if(goodsCategory.getLevel() == 2){
			//判断一级类目是否存在
			GoodsCategory firstGoodsCategory=goodsCategoryMapper.selectByPrimaryKey(goodsCategory.getPid());
			
			if(Objects.isNull(firstGoodsCategory) || firstGoodsCategory.getLevel() != 1) {
				throw new BadRequestException("库存耗材一级分类不存在");
			}	
			//默认二级目录类型为一级目录类型
			goodsCategory.setType(firstGoodsCategory.getType());
			
		}
		
		//判断同级类目下是否重名
		Example example = new Example(GoodsCategory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", goodsCategory.getName());
        criteria.andEqualTo("level", goodsCategory.getLevel());
        criteria.andEqualTo("type", goodsCategory.getType());
	
        int count=goodsCategoryMapper.selectCountByExample(example);
        
        if(count>0) {
        	throw new BadRequestException("库存耗材分类重名");
        }
        
		
		if(Objects.isNull(goodsCategory.getSorts())) {
			//查询该同类型级别下的最大排序值
			int maxsort=goodsCategoryMapper.selectMaxSortsValue(goodsCategory.getType(),goodsCategory.getLevel());
			goodsCategory.setSorts(++maxsort);
		}
		
		goodsCategory.setCreator(userCache.getCurrentAdminRealName());
		goodsCategory.setCreateTime(new Date());
		
		goodsCategoryMapper.insertSelective(goodsCategory);
		
	}
}
