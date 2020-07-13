package com.yimao.cloud.product.controller;

import com.yimao.cloud.pojo.dto.product.ProductCompanyDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductCompanyVO;
import com.yimao.cloud.product.po.ProductCompany;
import com.yimao.cloud.product.service.ProductCompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 产品公司
 *
 * @author Liu Yi
 * @date 2018/11/26.
 */
@RestController
@Slf4j
@Api(tags = "ProductCompanyController")
public class ProductCompanyController {

    @Resource
    private ProductCompanyService productCompanyService;

    /**
     * 创建产品公司
     *
     * @param dto 产品公司
     */
    @PostMapping(value = "/product/company")
    @ApiOperation(value = "创建产品公司")
    @ApiImplicitParam(name = "dto", value = "产品公司", required = true, dataType = "ProductCompanyDTO", paramType = "body")
    public ResponseEntity<Object> save(@RequestBody ProductCompanyDTO dto) {
        ProductCompany productCompany = new ProductCompany(dto);
        productCompanyService.saveProductCompany(productCompany);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除产品公司
     *
     * @param id 产品公司ID
     */
    @DeleteMapping(value = "/product/company/{id}")
    @ApiOperation(value = "删除产品公司")
    @ApiImplicitParam(name = "id", value = "产品公司ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer id) {
        productCompanyService.deleteProductCompanyById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 修改产品公司
     *
     * @param dto 产品公司
     */
    @PutMapping(value = "/product/company")
    @ApiOperation(value = "修改产品公司")
    @ApiImplicitParam(name = "dto", value = "产品公司", required = true, dataType = "ProductCompanyDTO", paramType = "body")
    public ResponseEntity<Object> update(@RequestBody ProductCompanyDTO dto) {
        ProductCompany productCompany = new ProductCompany(dto);
        productCompanyService.update(productCompany);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询产品公司（单个）
     *
     * @param id 产品公司ID
     */
    @GetMapping(value = "/product/company/{id}")
    @ApiOperation(value = "根据ID查询产品公司")
    @ApiImplicitParam(name = "id", value = "产品公司ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity<Object> getById(@PathVariable("id") Integer id) {
        ProductCompany productCompany = productCompanyService.getProductCompanyById(id);
        if (productCompany == null) {
            return ResponseEntity.noContent().build();
        }
        ProductCompanyVO vo = new ProductCompanyVO();
        productCompany.convert(vo);
        return ResponseEntity.ok(vo);
    }

    /**
     * 查询产品公司（分页）
     *
     * @param pageNum   第几页
     * @param pageSize  每页大小
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param name      公司名称
     * @param code      公司编码
     */
    @GetMapping(value = "/product/company/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询所有产品公司信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "公司编码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity<Object> page(@PathVariable(value = "pageNum") Integer pageNum,
                                       @PathVariable(value = "pageSize") Integer pageSize,
                                       @RequestParam(value = "startTime", required = false) Date startTime,
                                       @RequestParam(value = "endTime", required = false) Date endTime,
                                       @RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "code", required = false) String code) {
        PageVO<ProductCompanyVO> pageList = productCompanyService.listProductCompany(pageNum, pageSize, code, name, startTime, endTime);
        return ResponseEntity.ok(pageList);
    }

}
