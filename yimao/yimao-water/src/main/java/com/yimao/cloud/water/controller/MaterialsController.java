package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.SFTPUtil;
import com.yimao.cloud.pojo.dto.water.MaterialsDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.Materials;
import com.yimao.cloud.water.service.MaterialsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 描述：物料
 *
 * @Author Shen HuiYang
 * @Date 2019/1/30 14:36
 * @Version 1.0
 */
@RestController
@Api(tags = "MaterialsController")
@Slf4j
public class MaterialsController {

    @Resource
    private MaterialsService materialsService;

    @PostMapping(value = "/materials")
    @ApiOperation(value = "创建物料", notes = "创建物料")
    @ApiImplicitParam(name = "dto", value = "物料信息", required = true, dataType = "MaterialsDTO", paramType = "body")
    public Object save(@RequestBody MaterialsDTO dto) {
        Materials materials = new Materials(dto);
        materialsService.save(materials);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/materials/audit")
    @ApiOperation(value = "审核物料", notes = "审核物料")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "物料ID", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "payAudit", value = "支付审核(1-审核通过，2-审核不通过)", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "contentAudit", value = "内容审核(1-审核通过，2-审核不通过)", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "specificationAudit", value = "规格审核  (1-审核通过，2-审核不通过)", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "payAuditReason", value = "支付审核不通过原因", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "contentAuditReason", value = "内容审核不通过原因", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "specificationAuditReason", value = "规格审核不通过原因", dataType = "String", paramType = "query")
    })
    public Object auditMaterials(@RequestParam(value = "id") Integer id,
                                 @RequestParam(value = "payAudit", required = false) Integer payAudit,
                                 @RequestParam(value = "contentAudit", required = false) Integer contentAudit,
                                 @RequestParam(value = "specificationAudit", required = false) Integer specificationAudit,
                                 @RequestParam(value = "payAuditReason", required = false) String payAuditReason,
                                 @RequestParam(value = "contentAuditReason", required = false) String contentAuditReason,
                                 @RequestParam(value = "specificationAuditReason", required = false) String specificationAuditReason) {
        Materials materials = new Materials();
        materials.setId(id);
        materials.setPayAudit(payAudit);
        materials.setContentAudit(contentAudit);
        materials.setSpecificationAudit(specificationAudit);
        materials.setPayAuditReason(payAuditReason);
        materials.setContentAuditReason(contentAuditReason);
        materials.setSpecificationAuditReason(specificationAuditReason);
        materialsService.auditMaterials(materials);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/materials/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询物料列表", notes = "查询物料列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "materialsDTO", value = "查询条件", dataType = "MaterialsDTO", required = true, paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path")
    })
    public ResponseEntity<PageVO<MaterialsDTO>> list(@RequestBody MaterialsDTO materialsDTO,
                                                     @PathVariable(value = "pageNum") Integer pageNum,
                                                     @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<MaterialsDTO> list = materialsService.list(materialsDTO, pageNum, pageSize);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping(value = {"/materials/{id}"})
    @ApiOperation(value = "根据物料ID删除物料", notes = "根据物料ID删除物料")
    @ApiImplicitParam(name = "id", value = "物料ID", dataType = "Long", required = true, paramType = "path")
    public Object deleteMaterials(@PathVariable("id") Integer id) {
        Materials materials =new Materials();
        materials.setId(id);
        materialsService.deleteMaterials(materials);
        return ResponseEntity.noContent().build();
    }


    @RequestMapping(value = "/materials/upload/image", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "上传物料")
    public ResponseEntity<String> uploadWaterImage(@RequestPart("image") MultipartFile image) {
        try {
            if (image != null) {
                // 图片限制
                long fileSize = image.getSize();
                if (fileSize > 1 * 1024 * 1024) {
                    throw new BadRequestException("文件大小不能超过1M。");
                }
                String url = SFTPUtil.upload(image.getInputStream(), "waterImages", image.getOriginalFilename(), null);
                if (StringUtils.isNotEmpty(url)) {
                    return ResponseEntity.ok(url);
                }
                throw new YimaoException("操作失败。");
            }
            throw new BadRequestException("请选择图片或者视频。");
        } catch (Exception e) {
            throw new YimaoException("操作失败。");
        }
    }


    @GetMapping("/materials/count")
    @ApiOperation(value = "查询物料待审核以及可投放的数量", notes = "查询物料待审核以及可投放的数量")
    public Object queryCount() {
        Map<String,Object> map = materialsService.queryCount();
        return ResponseEntity.ok(map);
    }
}
