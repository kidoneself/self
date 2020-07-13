package com.yimao.cloud.system.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.GoodsCategoryEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.system.GoodsCategoryDTO;
import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.query.system.GoodsCategoryQuery;
import com.yimao.cloud.pojo.query.system.GoodsMaterialsQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.ProductFeign;
import com.yimao.cloud.system.mapper.GoodsCategoryMapper;
import com.yimao.cloud.system.mapper.GoodsMaterialsMapper;
import com.yimao.cloud.system.po.GoodsCategory;
import com.yimao.cloud.system.po.GoodsMaterials;
import com.yimao.cloud.system.service.GoodsCategoryService;
import com.yimao.cloud.system.service.GoodsMaterialsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(tags = "GoodsManagerController")
public class GoodsManagerController {
    @Resource
    private UserCache userCache;
    @Resource
    private GoodsCategoryService goodsCategoryService;
    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;
    @Resource
    private GoodsMaterialsService goodsMaterialsService;
    @Resource
    private GoodsMaterialsMapper goodsMaterialsMapper;
    @Resource
    private ProductFeign productFeign;

    @PostMapping("/goods/category")
    @ApiOperation(value = "新增库存物资分类")
    @ApiImplicitParam(name = "goodsCategory", value = "耗材分类", dataType = "GoodsCategory", required = true, paramType = "body")
    public Object addGoodsCategory(@RequestBody GoodsCategory goodsCategory) {

        //校验
        checkGoodsCategory(goodsCategory);

        goodsCategoryService.saveGoodsCategory(goodsCategory);

        return ResponseEntity.noContent().build();
    }

    private void checkGoodsCategory(GoodsCategory goodsCategory) {
    	
    	if (Objects.isNull(goodsCategory.getLevel())) {
            throw new BadRequestException("库存物资类目等级为空");
        }

        if (goodsCategory.getLevel() == 1 && Objects.isNull(goodsCategory.getType())) {
            throw new BadRequestException("库存物资类型为空");
        }

        if (goodsCategory.getLevel() == 1 && Objects.isNull(GoodsCategoryEnum.find(goodsCategory.getType()))) {
            throw new BadRequestException("库存物资类型错误");
        }

        if (StringUtil.isEmpty(goodsCategory.getName())) {
            throw new BadRequestException("库存物资类目名称不能为空");
        }       

        if (goodsCategory.getLevel() > 2 || goodsCategory.getLevel() < 1) {
            throw new BadRequestException("库存物资类目等级错误");
        }

        if (goodsCategory.getLevel() == 2 && Objects.isNull(goodsCategory.getPid())) {
            throw new BadRequestException("库存物资类目未指定上级");
        }

    }

    @GetMapping("/goods/category/{pageNum}/{pageSize}")
    @ApiOperation(value = "展示库存物资分类")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "query", value = "筛选条件", dataType = "GoodsCategoryQuery", required = false, paramType = "query")
    })
    public PageVO<GoodsCategoryDTO> goodsCategoryPage(@PathVariable("pageNum") Integer pageNum,
                                                      @PathVariable("pageSize") Integer pageSize,
                                                      GoodsCategoryQuery query) {

        PageHelper.startPage(pageNum, pageSize);
        Page<GoodsCategoryDTO> page = goodsCategoryMapper.listGoodsCategory(query);
        return new PageVO<>(pageNum, page);

    }

    @PutMapping(value = "/goods/category")
    @ApiOperation(value = "修改库存物资分类")
    @ApiImplicitParam(name = "dto", value = "产品类目", required = true, dataType = "GoodsCategoryDTO", paramType = "body")
    public Object editGoodsCategory(@RequestBody GoodsCategoryDTO dto) {

        GoodsCategory goodsCategory = new GoodsCategory();
        goodsCategory.setId(dto.getId());
        goodsCategory.setName(dto.getName());
        goodsCategory.setSorts(dto.getSorts());
        goodsCategory.setUpdater(userCache.getCurrentAdminRealName());
        goodsCategory.setUpdateTime(new Date());

        goodsCategoryMapper.updateByPrimaryKeySelective(goodsCategory);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/goods/category/filterItem")
    @ApiOperation(value = "库存物资分类筛选项")
    @ApiImplicitParam(name = "type", value = "类型", dataType = "Long", paramType = "query")
    public List<GoodsCategoryDTO> getGoodsCategoryFilter(@RequestParam(value = "type", required = false) Integer type) {

        return goodsCategoryMapper.getGoodsCategoryFilter(type);

    }
    
    @GetMapping("/goods/filterItem/{goodCategoryId}")
    @ApiOperation(value = "根据物资分类筛选项获取物资列表")
    @ApiImplicitParam(name = "goodCategoryId", value = "物资分类id", dataType = "Long", paramType = "path")
    public List<GoodsMaterialsDTO> getGoodsByCategoryId(@PathVariable(value = "goodCategoryId") Integer goodCategoryId) {
    	if(Objects.isNull(goodCategoryId)) {
    		throw new BadRequestException("物资分类id为空");
    	}
    	
        return goodsMaterialsMapper.getGoodsByCategoryId(goodCategoryId);

    }


    @PostMapping("/goods")
    @ApiOperation(value = "新增库存物资")
    @ApiImplicitParam(name = "goodsMaterialsDTO", dataType = "GoodsMaterialsDTO", required = true, paramType = "body")
    public Object addGoods(@RequestBody GoodsMaterialsDTO goodsMaterialsDTO) {

        goodsMaterialsService.saveGoodsMaterials(goodsMaterialsDTO);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/goods")
    @ApiOperation(value = "编辑库存物资")
    @ApiImplicitParam(name = "goodsMaterialsDTO", dataType = "GoodsMaterialsDTO", required = true, paramType = "body")
    public Object editGoods(@RequestBody GoodsMaterialsDTO goodsMaterialsDTO) {

        goodsMaterialsService.updateGoodsMaterials(goodsMaterialsDTO);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/goods/{type}/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询库存物资")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "type", value = "类型", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "query", value = "筛选条件", dataType = "GoodsMaterialsQuery", required = false, paramType = "query")
    })
    public PageVO<GoodsMaterialsDTO> goodsPage(@PathVariable("pageNum") Integer pageNum,
                                               @PathVariable("pageSize") Integer pageSize,
                                               @PathVariable("type") Integer type,
                                               GoodsMaterialsQuery query) {

        return goodsMaterialsService.listGoodsMaterials(pageNum, pageSize, type, query);
    }

    @GetMapping("/goods/{id}")
    @ApiOperation(value = "根据id查询水机或展示机库存物资")
    @ApiImplicitParam(name = "id", value = "库存物资id", dataType = "Long", required = true, paramType = "path")
    public GoodsMaterialsDTO goods(@PathVariable("id") Integer id) {
        if (Objects.isNull(id)) {
            throw new BadRequestException("库存物资id为空");
        }
        return goodsMaterialsMapper.selectGoodsById(id);
    }

    @GetMapping("/goods/material/adaptationModel")
    @ApiOperation(value = "获取耗材设备型号")
    public List<ProductCategoryDTO> getMaterialAdaptationModel() {
        List<ProductCategoryDTO> result = new ArrayList();

        //查询一级类目为翼猫科技(上海)发展有限公司下的类目
        PageVO<ProductCategoryDTO> firstLevelPage = productFeign.pageProductCategory(null, 1, null, null, 1, 10000, null, null, 0, 0);
        List<ProductCategoryDTO> firstLevelList = firstLevelPage.getResult();

        if (CollectionUtil.isEmpty(firstLevelList)) {
            throw new BadRequestException("净水服务一级类目不存在");
        }

        ProductCategoryDTO firstLevelDTO = firstLevelList.get(0);

        //查找一级类目下的二级类目
        PageVO<ProductCategoryDTO> secondLevelPage = productFeign.pageProductCategory(null, 1, null, firstLevelDTO.getId(), 2, null, null, null, 0, 0);
        List<ProductCategoryDTO> secondLevelList = secondLevelPage.getResult();

        if (CollectionUtil.isEmpty(secondLevelList)) {
            throw new BadRequestException("净水服务二级类目不存在");
        }

        for (ProductCategoryDTO productCategoryDTO : secondLevelList) {
            //查询三级类目
            PageVO<ProductCategoryDTO> thirdLevelPage = productFeign.pageProductCategory(null, 1, null, productCategoryDTO.getId(), 3, null, null, null, 0, 0);
            result.addAll(thirdLevelPage.getResult());
        }
        return result;
    }

    /**
     * 根据产品三级类目id查询匹配耗材列表
     *
     * @param productCategoryId
     * @return
     */
    @GetMapping(value = "/goods/material/{productCategoryId}")
    @ApiOperation(value = "根据产品三级类目id查询匹配耗材列表")
    @ApiImplicitParam(name = "productCategoryId", value = "产品三级分类id", dataType = "Long", required = true, paramType = "path")
    public List<GoodsCategoryDTO> getMaterialListByCategoryId(@PathVariable(value = "productCategoryId") Integer productCategoryId) {

        if (Objects.isNull(productCategoryId)) {
            throw new BadRequestException("产品三级分类id为空");
        }

        List<GoodsMaterialsDTO> list = goodsMaterialsMapper.getMaterialListByCategoryId(productCategoryId);

        if (CollectionUtil.isEmpty(list)) {
            return null;
        } else {
            List<GoodsCategoryDTO> res = new ArrayList<>();
            Map<Integer, GoodsCategoryDTO> goodsCategoryMap = new HashMap();
            Map<Integer, List<GoodsMaterialsDTO>> map = new HashMap();

            for (GoodsMaterialsDTO goodsMaterials : list) {
                //给每个耗材根据分类id分类
                if (map.containsKey(goodsMaterials.getGoodsCategoryId())) {
                    map.get(goodsMaterials.getGoodsCategoryId()).add(goodsMaterials);
                } else {
                    List<GoodsMaterialsDTO> materialList = new ArrayList();
                    map.put(goodsMaterials.getGoodsCategoryId(), materialList);
                }

                //根据分类id对应分类对象
                if (!goodsCategoryMap.containsKey(goodsMaterials.getGoodsCategoryId())) {
                    GoodsCategoryDTO category = new GoodsCategoryDTO();
                    category.setId(goodsMaterials.getGoodsCategoryId());
                    category.setName(goodsMaterials.getSecondLevelCategoryName());
                    category.setSorts(goodsMaterials.getCategorySorts());
                    goodsCategoryMap.put(goodsMaterials.getGoodsCategoryId(), category);
                }

            }

            for (Map.Entry<Integer, GoodsCategoryDTO> entry : goodsCategoryMap.entrySet()) {
                Integer categoryId = entry.getKey();
                GoodsCategoryDTO dto = entry.getValue();

                if (map.containsKey(categoryId)) {
                    dto.setMaterialsList(map.get(categoryId));
                }

                res.add(dto);
            }

            //排序
            Collections.sort(res, new Comparator<GoodsCategoryDTO>() {

                public int compare(GoodsCategoryDTO o1, GoodsCategoryDTO o2) {

                    return o1.getSorts() - o2.getSorts();
                }
            });

            return res;
        }

    }


}
