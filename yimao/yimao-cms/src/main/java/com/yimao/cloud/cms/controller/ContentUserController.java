package com.yimao.cloud.cms.controller;

import com.yimao.cloud.cms.service.ContentUserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lizhqiang
 * @date 2019-08-26
 */
@RestController
public class ContentUserController {


    @Resource
    private ContentUserService contentUserService;


    @PutMapping("/user/read")
    @ApiOperation(value = "已读未读删除未删除", notes = "已读未读删除未删除")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "contentId", name = "资讯id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(value = "type", name = "1-读 2-删", dataType = "Long", paramType = "query")
    })
    public void userRead(@RequestParam(value = "contentId") Integer contentId,
                         @RequestParam(value = "type") Integer type) {
        contentUserService.userRead(contentId, type);
    }


    @GetMapping("/user/read/count")
    @ApiOperation(value = "未读条数", notes = "未读条数")
    public Map<String, Integer> userReadCount() {
        Integer count = contentUserService.userReadCount();
        Map<String, Integer> map = new HashMap<>();
        map.put("count", count);
        return map;
    }



    /**
     * 更新公告状态
     * @param id
     */
    @PatchMapping("/engineer/notice/read/{id}")
    public Object updateContentToRead(@PathVariable(value = "id") Integer id){
        contentUserService.updateContentToRead(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * 删除公告
     * @param ids
     * @return
     */
    @DeleteMapping(value = {"/engineer/notice/{ids}/batch"})
    public Object deleteNotice(@PathVariable("ids") String ids){
        contentUserService.deleteNotice(ids);
        return ResponseEntity.noContent().build();
    }

}
