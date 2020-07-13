package com.yimao.cloud.cms.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.cms.po.CmsCategory;
import com.yimao.cloud.cms.service.CmsCategoryService;
import com.yimao.cloud.pojo.dto.cms.CmsCategoryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

/**
 * cms 分类
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@RestController
public class CmsCategoryController {

    @Resource
    private CmsCategoryService categoryService;

    @PostMapping("/category")
    @ApiOperation(value = "内容分类新增")
    @ApiImplicitParam(name = "dto", paramType = "body", dataType = "CmsCategoryDTO", value = "内容分类信息")
    public Object save(@RequestBody CmsCategoryDTO dto) {
        if (StringUtil.isEmpty(dto.getName())) {
            throw new BadRequestException("名称不能为空");
        }
        if (dto.getParentId() == null) {
            throw new BadRequestException("上级id不能为空");
        }
        if (dto.getLevel() == null) {
            throw new BadRequestException("请设置分类等级");
        }
        if (dto.getLevel() != 1) {
            throw new BadRequestException("目前只支持设置一级分类");
        }
        if (dto.getPlatform() == null) {
            throw new BadRequestException("请设置展示端");
        }
        if (dto.getLocation() == null) {
            throw new BadRequestException("请设置是资讯、公告还是协议");
        }
        CmsCategory cmsCategory = new CmsCategory(dto);
        cmsCategory.setStatus(1);
        categoryService.add(cmsCategory);
        return ResponseEntity.noContent().build();

    }


    @PutMapping("/category")
    @ApiOperation(value = "更新内容分类")
    @ApiImplicitParam(name = "dto", paramType = "body", dataType = "CmsCategoryDTO", value = "内容信息")
    public Object update(@RequestBody CmsCategoryDTO dto) {
        CmsCategory cmsCategory = new CmsCategory(dto);
        if (null == dto.getId()) {
            throw new BadRequestException("参数异常");
        }
        cmsCategory.setStatus(1);
        Integer count = categoryService.update(cmsCategory);
        if (count > 0) {
            return ResponseEntity.ok(dto);
        }
        throw new YimaoException("系统异常");
    }

    @DeleteMapping("/category/{categoryId}")
    @ApiOperation(value = "删除内容分类")
    @ApiImplicitParam(name = "categoryId", required = true, dataType = "Long", value = "删除内容信息的ID", paramType = "path")
    public Object delete(@PathVariable("categoryId") Integer categoryId) {
        if (categoryId == null) {
            throw new BadRequestException("参数异常");
        }
        Integer count = categoryService.delete(categoryId);
        if (count > 0) {
            return ResponseEntity.noContent().build();
        }
        throw new YimaoException("系统异常");
    }


    @GetMapping("/category/{id}")
    @ApiOperation(value = "单个查询", notes = "单个查询")
    @ApiImplicitParam(name = "id", required = true, value = "当前类型ID", dataType = "Long", paramType = "path")
    public ResponseEntity<CmsCategoryDTO> findById(@PathVariable("id") Integer id) {
        CmsCategory cmsCategory = categoryService.queryById(id);
        if (cmsCategory == null) {
            throw new NotFoundException("没有找到相关记录");
        }
        CmsCategoryDTO dto = new CmsCategoryDTO();
        cmsCategory.convert(dto);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/category/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询类目信息", notes = "分页查询类目信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, value = "当前页", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "type", value = "类别类型 ，1 总部视频分类，2 总部文章分类，3 服务站视频分类，4 服务站文章分类 ", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "platform", value = "展示端", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "parentCategoryId", value = "一级类目ID", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "level", value = "分类等级", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "location", value = "位置：1资讯 2公号 3协议", dataType = "Long", paramType = "query")
    })
    public Object findPage(@PathVariable(value = "pageNum") Integer pageNum,
                           @PathVariable(value = "pageSize") Integer pageSize,
                           @RequestParam(value = "type") Integer type,
                           @RequestParam(value = "platform", required = false) Integer platform,
                           @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId,
                           @RequestParam(value = "level", required = false) Integer level,
                           @RequestParam(value = "location", required = false) Integer location) {
        CmsCategoryDTO dto = new CmsCategoryDTO();
        dto.setLevel(level);
        dto.setType(type);
        dto.setPlatform(platform);
        dto.setLocation(location);
        if (null != parentCategoryId) {
            dto.setParentId(parentCategoryId);
        }
        PageVO<CmsCategoryDTO> page = categoryService.findPage(pageNum, pageSize, dto);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/category/top/name/{id}")
    @ApiOperation(value = "获取展示端")
    @ApiImplicitParam(name = "id", value = "id", paramType = "path", dataType = "Long")
    public Object getTopCategoryName(@PathVariable("id") Integer id) {
        CmsCategoryDTO cmsCategoryDTO = categoryService.getTopCategoryName(id);
        return ResponseEntity.ok(cmsCategoryDTO);
    }

    @PostMapping(value = "/content/category", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object getCategory(@RequestBody CmsCategoryDTO cmsCategory) {
        return categoryService.getCategory(cmsCategory);
    }
    
    /**
     * 站务系统- 概况-消息通知-总部消息-消息分类筛选项获取
     * @return
     */
    @GetMapping("/category/station/headOfficeMessageType")
    public List<CmsCategoryDTO> getHeadOfficeMessageType(){
    	  return categoryService.getHeadOfficeMessageType();
    }

}
