package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.system.AreaManageDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.AreaManage;
import com.yimao.cloud.system.service.AreaManageService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import javax.annotation.Resource;

/**
 * @author Lizhqiang
 * @date 2019-08-19
 */
@RestController
@Slf4j
public class AreaManageController {

    @Resource
    private AreaManageService areaManageService;


    /**
     * 分页展示地区管理
     *
     * @param pageSize
     * @param pageNum
     * @param id
     * @param level
     * @return
     */
    @GetMapping(value = "/area/manage/page/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页展示地区管理", notes = "分页展示地区管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "id", value = "地区id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pid", value = "地区上级id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "level", value = "等级", dataType = "Long", paramType = "query")
    })
    public Object page(@PathVariable(value = "pageSize") Integer pageSize,
                       @PathVariable(value = "pageNum") Integer pageNum,
                       @RequestParam(value = "id", required = false) Integer id,
                       @RequestParam(value = "pid", required = false) Integer pid,
                       @RequestParam(value = "level", required = false) Integer level) {
        PageVO<AreaManage> page = areaManageService.page(pageNum, pageSize, id, level, pid);
        return ResponseEntity.ok(page);
    }

    @PutMapping(value = "/area/manage/update")
    @ApiOperation(value = "地区管理更新", notes = "地区管理更新")
    @ApiImplicitParam(name = "dto", value = "地区", dataType = "AreaManageDTO", paramType = "body")
    public void update(@RequestBody AreaManageDTO areaManageDTO) {
        AreaManage areaManage = new AreaManage(areaManageDTO);
        areaManageService.update(areaManage);
    }


    /**
     * 导入地区管理信息
     *
     * @return
     */
    @RequestMapping(value = "/area/manage/import", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "导入地区管理信息", notes = "导入地区管理信息")
    public Object importExcel(@RequestPart("multipartFile") MultipartFile multipartFile) {
        areaManageService.importExcel(multipartFile);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 获取地区管理筛选列表
     * 
     *
     */
    @RequestMapping(value = "/area/manage/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取导入地区筛选", notes = "获取导入地区筛选")
    public ResponseEntity<List<AreaManageDTO>> importExcel() {
        
        return ResponseEntity.ok(areaManageService.getAreaManagerList());
    }
}
