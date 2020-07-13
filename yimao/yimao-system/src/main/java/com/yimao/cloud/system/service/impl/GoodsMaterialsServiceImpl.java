package com.yimao.cloud.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.GoodsCategoryEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.system.GoodsCategoryDTO;
import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.query.system.GoodsMaterialsQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.ProductFeign;
import com.yimao.cloud.system.mapper.GoodsCategoryMapper;
import com.yimao.cloud.system.mapper.GoodsMaterialsMapper;
import com.yimao.cloud.system.mapper.MaterialsAdaptationModelMapper;
import com.yimao.cloud.system.mapper.StoreHouseAllMapper;
import com.yimao.cloud.system.po.GoodsCategory;
import com.yimao.cloud.system.po.GoodsMaterials;
import com.yimao.cloud.system.po.MaterialsAdaptationModel;
import com.yimao.cloud.system.po.StoreHouseAll;
import com.yimao.cloud.system.service.GoodsMaterialsService;

import tk.mybatis.mapper.entity.Example;
@Service
public class GoodsMaterialsServiceImpl implements GoodsMaterialsService {
	@Resource
	private UserCache userCache;
    @Resource
    private GoodsMaterialsMapper goodsMaterialsMapper;
    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;
    @Resource
    private MaterialsAdaptationModelMapper materialsAdaptationModelMapper;
    @Resource
    private StoreHouseAllMapper storeHouseAllMapper;
    @Resource
    private ProductFeign productFeign;
    
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveGoodsMaterials(GoodsMaterialsDTO goodsMaterialsDTO) {
		
		 GoodsMaterials goodsMaterials =new GoodsMaterials();
		 
		 BeanUtils.copyProperties(goodsMaterialsDTO, goodsMaterials);
		 //校验
		 checkGoods(goodsMaterials);		 
		 //根据物资类目id查询类目对应的类型
		 GoodsCategory goodsCategory  = goodsCategoryMapper.selectByPrimaryKey(goodsMaterials.getGoodsCategoryId());
		 if(Objects.isNull(goodsCategory)) {
			 throw new BadRequestException("库存物资类目不存在");
		 }
		 
		 if(goodsCategory.getLevel() != 2) {
			 throw new BadRequestException("库存物资类目非二级目录");
		 }
		 
		 //校验同类目名称是否重复
		 int count = goodsMaterialsMapper.checkRename(goodsCategory.getType(),goodsMaterials.getName(),goodsMaterials.getId());
		 
		 if(count > 0) {
			 throw new BadRequestException("库存物资同类型名称重复");
		 }
		 //耗材类型判断型号
		 List<MaterialsAdaptationModel> adaptationModelList=new ArrayList();
		 if(goodsCategory.getType() == GoodsCategoryEnum.MATERIAL.value) {
			 
			 List<ProductCategoryDTO>  productCategoryList= goodsMaterialsDTO.getAdaptationModelList();
			 
			 if(CollectionUtil.isEmpty(productCategoryList)) {
				 throw new BadRequestException("适配型号为空");
			 }
			 
			 for (ProductCategoryDTO productCategoryDTO : productCategoryList) {
				 MaterialsAdaptationModel adaptionModel=new MaterialsAdaptationModel();
				 adaptionModel.setDeviceModelName(productCategoryDTO.getName());
				 adaptionModel.setProductCategoryId(productCategoryDTO.getId());
				 adaptationModelList.add(adaptionModel);
			 }
			
		 }
		 
		 goodsMaterials.setType(goodsCategory.getType());
		 goodsMaterials.setCreateTime(new Date());
		 goodsMaterials.setCreator(userCache.getCurrentAdminRealName());
		 
		 int result=goodsMaterialsMapper.insertSelective(goodsMaterials);
		 
		 if(result < 1) {
			 throw new YimaoException("库存物资新增失败");
		 }else {
			 if(CollectionUtil.isNotEmpty(adaptationModelList)) {
				 
				 for (MaterialsAdaptationModel materialsAdaptationModel : adaptationModelList) {
					 materialsAdaptationModel.setGoodsMaterialsId(goodsMaterials.getId());
				 }
				 
				 materialsAdaptationModelMapper.batchInsert(adaptationModelList);
			 }
			
		 }
		 
		 //同步该物资到总仓
		 StoreHouseAll storeHouseAll =new StoreHouseAll();
		 storeHouseAll.setGoodsId(goodsMaterials.getId());
		 storeHouseAll.setStockCount(0);
		 int storeHouseAllRes=storeHouseAllMapper.insertSelective(storeHouseAll);
		 
		 if(storeHouseAllRes < 1) {
			 throw new YimaoException("库存物资同步总仓失败");
		 }
		 
	}
	
	private void checkGoods(GoodsMaterials goodsMaterials) {
		
		if (StringUtil.isEmpty(goodsMaterials.getName())) {
            throw new BadRequestException("库存物资名称不能为空");
        }
				
		if(Objects.isNull(goodsMaterials.getGoodsCategoryId())) {
			throw new BadRequestException("库存物资类目为空");
		}
	
	}

	@Override
	public PageVO<GoodsMaterialsDTO> listGoodsMaterials(Integer pageNum, Integer pageSize, Integer type, GoodsMaterialsQuery query) {
		if(Objects.isNull(type)) {
			throw new BadRequestException("未指定查询物资类型");
		}
		
		PageHelper.startPage(pageNum, pageSize);
		Page<GoodsMaterialsDTO> page = null;
		if(type == GoodsCategoryEnum.MATERIAL.value) {
			page = goodsMaterialsMapper.listMaterials(query);
		}else if(type == GoodsCategoryEnum.DEVICE.value || type == GoodsCategoryEnum.DISPLAY_DEVICE.value) {
			query.setType(type);
			page = goodsMaterialsMapper.listGoods(query);
		}else {
			throw new BadRequestException("查询物资类型错误");
		}
		    
        return new PageVO<>(pageNum, page);

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateGoodsMaterials(GoodsMaterialsDTO goodsMaterialsDTO) {
		
		if(Objects.isNull(goodsMaterialsDTO.getId())) {
			throw new BadRequestException("编辑物资id为空");
		}
		
		//根据id查询物资
		GoodsMaterials goodsMaterials = goodsMaterialsMapper.selectByPrimaryKey(goodsMaterialsDTO.getId());
		
		if(Objects.isNull(goodsMaterials)) {
			throw new BadRequestException("编辑物资不存在");
		}
		
		GoodsMaterials update=new GoodsMaterials();
		update.setId(goodsMaterialsDTO.getId());
		update.setUpdater(userCache.getCurrentAdminRealName());
		update.setUpdateTime(new Date());
		
		if(StringUtil.isNotEmpty(goodsMaterialsDTO.getName())) {
			 //校验同类目名称是否重复
			 int count = goodsMaterialsMapper.checkRename(goodsMaterials.getType(),goodsMaterialsDTO.getName(),goodsMaterialsDTO.getId());
			 
			 if(count > 0) {
				 throw new BadRequestException("库存物资同类型名称重复");
			 }
			 
			 update.setName(goodsMaterialsDTO.getName());
		}
		
		if(Objects.nonNull(goodsMaterialsDTO.getGoodsCategoryId())) {
			//判断是否变更物资类型
			if(! goodsMaterials.getGoodsCategoryId().equals(goodsMaterialsDTO.getGoodsCategoryId())) {
				//判断变更物资类型是否存在
				GoodsCategory goodsCategory  = goodsCategoryMapper.selectByPrimaryKey(goodsMaterialsDTO.getGoodsCategoryId());
				 if(Objects.isNull(goodsCategory)) {
					 throw new BadRequestException("库存物资类目不存在");
				 }
				 
				 if(goodsCategory.getLevel() != 2) {
					 throw new BadRequestException("库存物资类目非二级目录");
				 }
				 
				 if(! goodsCategory.getType().equals(goodsMaterials.getType()) ) {
					 throw new BadRequestException("库存物资类型匹配类目类型错误");
				 }
				 
				 update.setGoodsCategoryId(goodsCategory.getId());
				
			}
		}
		
		if(goodsMaterials.getType() ==  GoodsCategoryEnum.MATERIAL.value) {
			//判断是否变更型号
			List<ProductCategoryDTO> productCategoryList= goodsMaterialsDTO.getAdaptationModelList();
			
			if(CollectionUtil.isNotEmpty(productCategoryList)) {
				
				List<MaterialsAdaptationModel> adaptationModelList=new ArrayList();
				
				for (ProductCategoryDTO productCategoryDTO : productCategoryList) {
					 MaterialsAdaptationModel adaptionModel=new MaterialsAdaptationModel();
					 adaptionModel.setDeviceModelName(productCategoryDTO.getName());
					 adaptionModel.setProductCategoryId(productCategoryDTO.getId());
					 adaptionModel.setGoodsMaterialsId(goodsMaterials.getId());
					 adaptationModelList.add(adaptionModel);
				 }
				
				 MaterialsAdaptationModel example=new MaterialsAdaptationModel();
				 example.setGoodsMaterialsId(goodsMaterials.getId() );
				 materialsAdaptationModelMapper.delete(example);
				 materialsAdaptationModelMapper.batchInsert(adaptationModelList);
			}
		}
		
		goodsMaterialsMapper.updateByPrimaryKeySelective(update);
		
		
	}
}
