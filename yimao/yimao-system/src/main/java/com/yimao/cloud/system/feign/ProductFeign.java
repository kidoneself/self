package com.yimao.cloud.system.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.product.ProductActivityDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductCompanyDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.product.ProductPropertyDTO;
import com.yimao.cloud.pojo.dto.product.ProductRenewDTO;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.query.product.ProductActivityQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductActivityVO;
import com.yimao.cloud.pojo.vo.product.ProductBuyPermissionVO;
import com.yimao.cloud.pojo.vo.product.ProductCompanyVO;
import com.yimao.cloud.pojo.vo.product.ProductCostVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/7/4.
 */
@FeignClient(name = Constant.MICROSERVICE_PRODUCT)
public interface ProductFeign {
    /**
     * 查询产品（单个，基本信息）
     *
     * @param id 产品ID
     */
    @GetMapping(value = "/product/{id}")
    ProductDTO getProductById(@PathVariable(value = "id") Integer id);

    /**
     * 创建产品类目
     *
     * @param dto 产品类目
     */
    @RequestMapping(value = "/product/category", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveProductCategory(@RequestBody ProductCategoryDTO dto);

    /**
     * 删除产品类目
     *
     * @param id 产品类目ID
     */
    @RequestMapping(value = "/product/category/{id}", method = RequestMethod.DELETE)
    void deleteProductCategoryById(@PathVariable("id") Integer id);

    /**
     * 更新产品分类信息
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/product/category", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateProductCategory(@RequestBody ProductCategoryDTO dto);

    /**
     * 单个查询产品分类
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/product/category/{id}")
    ProductCategoryDTO getProductCategoryById(@PathVariable(value = "id") Integer id);


    /**
     * 查询产品类目（分页）
     *
     * @param name      类目名称
     * @param type      前台类目还是后台类目：1-后台类目；2-前台类目；
     * @param terminal  终端：1-健康e家公众号；2-小猫店小程序；3-经销商APP；
     * @param pid       父类目ID
     * @param level     产品类目等级：1-一级；2-二级；3-三级；
     * @param companyId 产品公司ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageNum   页码
     * @param pageSize  每页大小
     */
    @GetMapping(value = "/product/category/{pageNum}/{pageSize}")
    PageVO<ProductCategoryDTO> pageProductCategory(@RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "type", required = false) Integer type,
                                                   @RequestParam(value = "terminal", required = false) Integer terminal,
                                                   @RequestParam(value = "pid", required = false) Integer pid,
                                                   @RequestParam(value = "level", required = false) Integer level,
                                                   @RequestParam(value = "companyId", required = false) Integer companyId,
                                                   @RequestParam(value = "startTime", required = false) Date startTime,
                                                   @RequestParam(value = "endTime", required = false) Date endTime,
                                                   @PathVariable(value = "pageNum") Integer pageNum,
                                                   @PathVariable(value = "pageSize") Integer pageSize);


    /**
     * 创建产品公司信息
     *
     * @param dto 产品公司信息
     * @return
     */
    @RequestMapping(value = "/product/company", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object productCompanySave(@RequestBody ProductCompanyDTO dto);


    /**
     * 获取产品公司CODE
     *
     * @return
     */
    @PostMapping(value = "/product/company/code")
    Object productCompanyGetNewCode();

    /**
     * 删除产品公司信息
     *
     * @param id 管理员ID
     * @return
     */
    @DeleteMapping(value = "/product/company/{id}")
    Object productCompanyDelete(@PathVariable("id") Integer id);


    /**
     * 更新产品公司信息
     *
     * @param dto 产品公司信息
     * @return
     */
    @RequestMapping(value = "/product/company", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object productCompanyUpdate(@RequestBody ProductCompanyDTO dto);


    /**
     * 查询特定产品公司信息
     *
     * @param id 产品公司信息根据ID
     */
    @GetMapping(value = "/product/company/{id}")
    ProductCompanyVO productCompanyGetById(@PathVariable("id") Integer id);


    /**
     * 查询产品公司信息列表
     *
     * @param pageNum   第几页
     * @param pageSize  每页大小
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param name      公司名称
     * @param code      公司编码
     */
    @GetMapping(value = "/product/company/{pageNum}/{pageSize}")
    PageVO<ProductCompanyVO> productCompanyPage(@PathVariable(value = "pageNum") Integer pageNum,
                                                @PathVariable(value = "pageSize") Integer pageSize,
                                                @RequestParam(value = "startTime", required = false) String startTime,
                                                @RequestParam(value = "endTime", required = false) String endTime,
                                                @RequestParam(value = "name", required = false) String name,
                                                @RequestParam(value = "code", required = false) String code);

    /**
     * 上传公司头像
     *
     * @param image
     * @return
     */
    @PutMapping("/product/upload/companyImage")
    Object uploadCompanyImage(@RequestParam("image") MultipartFile image);


    /**
     * =================================== 价格模板
     */

    /**
     * 新增计费方式
     *
     * @param dto 计费方式
     */
    @RequestMapping(value = "/product/cost", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveProductCost(@RequestBody ProductCostDTO dto);

    /**
     * 删除计费方式
     *
     * @param id 计费方式ID
     */
    @DeleteMapping(value = "/product/cost/{id}")
    void deleteProductCost(@PathVariable("id") Integer id);

    /**
     * 修改计费方式
     *
     * @param dto 计费方式
     */
    @RequestMapping(value = "/product/cost", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateProductCost(@RequestBody ProductCostDTO dto);

    /**
     * 启用/禁用计费方式
     *
     * @param ids     计费方式ID集合
     * @param deleted 删除标识：0-未删除；1-已删除；
     */
    @RequestMapping(value = "/product/cost", method = RequestMethod.PATCH)
    Object forbiddenProductCost(@RequestParam(value = "ids") Integer[] ids, @RequestParam(value = "deleted") Boolean deleted);

    /**
     * 根据ID获取计费方式
     *
     * @param id 产品价格体系模版
     */
    @RequestMapping(value = "/product/cost/{id}", method = RequestMethod.GET)
    ProductCostDTO getProductCostById(@PathVariable("id") Integer id);

    /**
     * 分页获取计费方式
     *
     * @param pageNum          第几页
     * @param pageSize         每页大小
     * @param name             计费方式名称
     * @param type             计费方式：1-流量计费；2-时长计费；
     * @param firstCategoryId  后台一级产品类目ID
     * @param secondCategoryId 后台二级产品类目ID
     */
    @RequestMapping(value = "/product/cost/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<ProductCostVO> pageProductCost(@PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize,
                                          @RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "type", required = false) Integer type,
                                          @RequestParam(value = "modelType", required = false) Integer modelType,
                                          @RequestParam(value = "firstCategoryId", required = false) Integer firstCategoryId,
                                          @RequestParam(value = "secondCategoryId", required = false) Integer secondCategoryId,
                                          @RequestParam(value = "deleted", required = false) Integer deleted);

    /**
     * 获取续费计费方式列表
     */
    @GetMapping(value = "/product/renewcost")
    List<ProductCostDTO> listProductRenewCostByOldCostId(@RequestParam(value = "oldCostId") Integer oldCostId,
                                                         @RequestParam(value = "type", required = false) Integer type);


    /**
     *  ==================================== 产品属性
     */

    /**
     * 保存产品属性
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/property/property", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object productPropertySave(@RequestBody ProductPropertyDTO dto);

    /**
     * 更新产品属性
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/property/property", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object productPropertyUpdate(@RequestBody ProductPropertyDTO dto);

    /**
     * 批量删除产品属性
     *
     * @param propertyIds
     * @return
     */
    @DeleteMapping("/property/property/batch")
    Object productPropertyBatchDelete(@RequestParam("propertyIds") Integer[] propertyIds);

    /**
     * 查询产品属性列表 根据type
     *
     * @param typeId
     * @return
     */
    @GetMapping("/property/property")
    Object productPropertyListForType(@RequestParam("typeId") Integer typeId);

    /**
     * 产品产品属性分页
     *
     * @param name
     * @param startUpdateTime
     * @param endUpdateTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/property/property/{pageNum}/{pageSize}", method = RequestMethod.GET)
    Object productPropertyPage(@PathVariable(value = "pageNum") Integer pageNum,
                               @PathVariable(value = "pageSize") Integer pageSize,
                               @RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "startUpdateTime", required = false) Date startUpdateTime,
                               @RequestParam(value = "endUpdateTime", required = false) Date endUpdateTime);

    /**
     * 创建商品续费
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/product/renew", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object productRenewSave(@RequestBody ProductRenewDTO dto);

    /**
     * 删除商品续费
     *
     * @param id 管理员ID
     * @return
     */
    @DeleteMapping(value = "/product/renew/{id}")
    Object productRenewDelete(@PathVariable("id") Integer id);


    /**
     * 更新商品续费
     *
     * @param dto 商品续费信息
     * @return
     */
    @RequestMapping(value = "/product/renew", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object productRenewUpdate(@RequestBody ProductRenewDTO dto);

    /**
     * 查询特定商品续费
     *
     * @param id 产品商品续费
     * @return
     */
    @GetMapping(value = "/product/renew/{id}")
    Object productRenewGetById(@PathVariable("id") Integer id);

    /**
     * 查询商品续费列表
     *
     * @param pageNum          第几页
     * @param pageSize         每页大小
     * @param categoryId       一级分类id
     * @param secondCategoryId 二级分类id
     * @return
     */
    @GetMapping(value = "/product/renew/{pageNum}/{pageSize}")
    Object productRenewPage(@PathVariable(value = "pageNum") Integer pageNum,
                            @PathVariable(value = "pageSize") Integer pageSize,
                            @RequestParam(value = "categoryId", required = false) Integer categoryId,
                            @RequestParam(value = "secondCategoryId", required = false) Integer secondCategoryId,
                            @RequestParam(value = "thirdCategoryId", required = false) Integer thirdCategoryId);


    /**
     * ====================================产品信息
     */


    /**
     * 新增产品
     *
     * @param dto 产品信息
     * @return
     */
    @RequestMapping(value = "/product", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object productSave(@RequestBody ProductDTO dto);

    /**
     * 更新某个产品信息
     *
     * @param dto 产品信息体
     * @return
     */
    @RequestMapping(value = "/product", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object productUpdate(@RequestBody ProductDTO dto);

    /**
     * 修改产品状态、排序等
     *
     * @param id     产品
     * @param status 产品状态：1-未上架；2-已上架；3-已下架；4-已删除；
     * @param sorts  产品排序
     */
    @RequestMapping(value = "/product/{id}", method = RequestMethod.PATCH)
    void updateProductById(@PathVariable("id") Integer id, @RequestParam(value = "status", required = false) Integer status,
                           @RequestParam(value = "sorts", required = false) Integer sorts, @RequestParam(value = "hot", required = false) Integer hot);

    /**
     * 查询单个产品信息（带扩展信息）
     *
     * @param id 产品ID
     */
    @GetMapping(value = "/product/{id}/expansion")
    ProductDTO findFullProductById(@PathVariable("id") Integer id);


    /**
     * @param name
     * @param categoryId
     * @param hot
     * @param companyId
     * @param startTime
     * @param endTime
     * @param onShelfStartTime
     * @param onShelfEndTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/product/{pageNum}/{pageSize}")
    Object findProductPage(@RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "categoryId", required = false) Integer categoryId,
                           @RequestParam(value = "statusList", required = false) List<Integer> statusList,
                           @RequestParam(value = "hot", required = false) Integer hot,
                           @RequestParam(value = "companyId", required = false) Integer companyId,
                           @RequestParam(value = "startTime", required = false) Date startTime,
                           @RequestParam(value = "endTime", required = false) Date endTime,
                           @RequestParam(value = "onShelfStartTime", required = false) Date onShelfStartTime,
                           @RequestParam(value = "onShelfEndTime", required = false) Date onShelfEndTime,
                           @PathVariable("pageNum") Integer pageNum,
                           @PathVariable("pageSize") Integer pageSize);

    /**
     * 产品统计
     *
     * @return
     */
    @GetMapping("/product/statistic")
    Object findProductStatistics();


    /**
     * 上下架
     *
     * @param status
     * @param ids
     * @return
     */
    @PatchMapping("/product")
    Object updateProductStatus(@RequestParam("ids") List<Integer> ids, @RequestParam(name = "status") Integer status);


    /**
     * 获取产品可购买人群列表
     */
    @GetMapping("/product/buy/permissions")
    List<ProductBuyPermissionVO> getProductBuyPermissions();


    /**
     * 发布产品时获取所有产品销售类型：1-经销产品；2-站长专供产品；3-特供产品；4-特批水机；
     */
    @GetMapping("/product/branch/sys")
    List<DictionaryDTO> listProductBranchSys();

    /**
     * @Description: 查询M/Y卡下的商品
     * @author ycl
     * @param: * @param
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.product.ProductDTO>
     * @Create: 2019/2/23 10:18
     */
    @GetMapping("/product/MYFCard")
    List<ProductDTO> findMYCardProductList(@RequestParam("categoryName") String categoryName);

    /**
     * @Description: 查询F卡下的商品
     * @author ycl
     * @param: * @param
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.product.ProductDTO>
     * @Create: 2019/2/27 18:07
     */
    @GetMapping("/product/FCard")
    List<ProductDTO> findFCardProductList();

    /**
     * 更新产品分类排序
     *
     * @param id    产品类目ID
     * @param sorts 排序值
     */
    @PatchMapping(value = "/product/category/{id}")
    void updateCategorySorts(@PathVariable("id") Integer id, @RequestParam(value = "sorts", required = false) Integer sorts);

    /**
     * 获取产品列表
     */
    @GetMapping("/product")
    List<ProductDTO> findProductList();

    @GetMapping("/product/modifyorder")
    List<ProductDTO> listProductForModifyOrder(@RequestParam(value = "orderId", required = false) Long orderId,
                                               @RequestParam(value = "productId", required = false) Integer productId,
                                               @RequestParam(value = "costId", required = false) Integer costId);

    /***
     * 更新活动信息
     * @param update
     */
    @PostMapping(value = "/product/activity", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateProductActivity(@RequestBody ProductActivityDTO update);

    /****
     * 查询活动产品列表
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping(value = "/product/activity/list/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<ProductActivityVO> productActivityList(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, @RequestBody ProductActivityDTO query);

    /****
     * 删除活动
     * @param id
     */
    @DeleteMapping(value = "/product/activity/{id}")
    void deleteActivity(@PathVariable("id") Integer id);

    /****
     * 查询单个活动商品信息
     * @param query
     * @return
     */
    @PostMapping(value = "/product/activity/detail", consumes = MediaType.APPLICATION_JSON_VALUE)
    ProductActivityVO productActivityInfo(@RequestBody ProductActivityQuery query);

    /**
     * 添加活动时需要获取未上架的活动产品
     */
    @GetMapping(value = "/product/activitys")
    List<ProductDTO> findActivityProduct(@RequestParam("statusList") List<Integer> statusList);

    /***
     * 激活活动
     * @param id
     * @return
     */
    @GetMapping(value = "product/activity/open/{id}")
    void openProductActivity(@PathVariable("id") Integer id);

    /**
     * 根据产品id获取到其三级分类对应库存物资id
     *
     * @param productId 产品ID
     */
    @GetMapping(value = "/product/category/goodsId/{productId}")
    Integer getGoodsIdByProductId(@PathVariable("productId") Integer productId);

}
