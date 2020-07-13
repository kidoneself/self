package com.yimao.cloud.product.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.RemoteCallException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.product.ProductRenewDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.product.service.ProductRenewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Slf4j
@Api(tags = "ProductRenewController")
public class ProductRenewController {
    @Resource
    private ProductRenewService productRenewService;
    @Resource
    private UserCache userCache;

    /**
     * 创建商品续费
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/product/renew")
    @ApiOperation(value = "创建商品续费", notes = "创建商品续费")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", required = true, value = "", dataType = "ProductRenewDTO", paramType = "body")
    })
    public Object save(@RequestBody ProductRenewDTO dto) {
        String categoryIds = dto.getCategoryIds();
        String costIds = dto.getCostIds();
        try {
            List<Integer> categoryIdList = Arrays.stream(categoryIds.split(",")).map(s -> Integer.parseInt(s.trim())).
                    collect(Collectors.toList());

            List<Integer> costIdList = Arrays.stream(costIds.split(",")).map(s -> Integer.parseInt(s.trim())).
                    collect(Collectors.toList());

            Integer count = productRenewService.saveProductRenew(categoryIdList, costIdList);
            if (count != null && count == -1) {
                throw new RemoteCallException();
            }

            return ResponseEntity.noContent().build();
        } catch (NumberFormatException nfe) {
            log.error(nfe.getMessage(), nfe);
            throw new YimaoException("参数异常！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("系统异常！");
        }
    }

    /**
     * 删除商品续费
     *
     * @param id 管理员ID
     * @return
     */
    @DeleteMapping(value = "/product/renew/{id}")
    @ApiOperation(value = "删除商品续费", notes = "删除商品续费")
    @ApiImplicitParam(name = "id", value = "价格体系ID", required = true, dataType = "Long", paramType = "path")
    public Object delete(@PathVariable("id") Integer id) {
        productRenewService.deleteProductRenewById(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * 更新商品续费
     *
     * @param dto 商品续费信息
     * @return
     */
    @PutMapping(value = "/product/renew")
    @ApiOperation(value = "更新商品续费", notes = "更新商品续费")
    @ApiImplicitParam(name = "dto", value = "商品续费", required = true, dataType = "ProductRenewDTO", paramType = "body")
    public Object update(@RequestBody ProductRenewDTO dto) {
        String updater = userCache.getCurrentAdminRealName();
        dto.setUpdateTime(new Date());
        dto.setUpdater(updater);
        Integer result = productRenewService.updateProductRenew(dto);

        if (result != null && result == -1) {
            throw new RemoteCallException();
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * 查询特定商品续费
     *
     * @param id 产品商品续费
     * @return
     */
    @GetMapping(value = "/product/renew/{id}")
    @ApiOperation(value = "根据ID查询商品续费", notes = "根据ID查询商品续费")
    @ApiImplicitParam(name = "id", value = "续费id", required = true, dataType = "Long", paramType = "path")
    public Object getById(@PathVariable("id") Integer id) {
        ProductRenewDTO productRenewDTO = productRenewService.getProductRenewById(id);

        if (productRenewDTO.getId() == -1) {
            throw new YimaoException(404, "该续费模版不存在！");
        }

        return ResponseEntity.ok(productRenewDTO);
    }

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
    @ApiOperation(value = "分页查询所有商品续费", notes = "分页查询所有商品续费")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "一级分类id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "secondCategoryId", value = "二级分类id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "thirdCategoryId", value = "三级级分类id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object page(@PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize,
                       @RequestParam(value = "categoryId", required = false) Integer categoryId,
                       @RequestParam(value = "secondCategoryId", required = false) Integer secondCategoryId,
                       @RequestParam(value = "thirdCategoryId", required = false) Integer thirdCategoryId) {
        PageVO<ProductRenewDTO> pageList = productRenewService.listProductRenews(pageNum, pageSize, categoryId, secondCategoryId, thirdCategoryId);

        return ResponseEntity.ok(pageList);
    }

}
