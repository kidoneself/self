package com.yimao.cloud.out.controller.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yimao.cloud.base.enums.StatusEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.enums.MessageAppTypeEnum;
import com.yimao.cloud.base.enums.MessagePushObjectEnum;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.out.enums.MessageTypeEnum;
import com.yimao.cloud.out.feign.SystemFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.utils.ResultUtil;
import com.yimao.cloud.out.vo.MessageVO;
import com.yimao.cloud.pojo.dto.system.MessagePushDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/***
 * 功能描述:安装工app调用
 *
 * @auther: liu yi
 * @date: 2019/5/5 17:38
 */
@RestController
@Slf4j
@RequestMapping({"/api/message"})
@Api(tags = "MessageApiController")
public class MessageApiController {
    @Resource
    private UserFeign userFeign;
    @Resource
    private SystemFeign systemFeign;

    /**
     * 安装工消息列表（原云平台）
     *
     * @param request         request
     * @param username        用户名
     * @param type            消息类型
     * @param app             用户类型
     * @param currentPosition currentPosition
     * @param pageNo          pageNo
     * @param pageSize        pageSize
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "安装工消息列表（原云平台）")
    public ResponseEntity<Object> messagePushList(HttpServletRequest request,
                                                  @RequestParam String username,
                                                  @RequestParam(required = false) Integer type,
                                                  @RequestParam(required = false) Integer app,
                                                  @RequestParam(required = false) String currentPosition,
                                                  @RequestParam(required = false, defaultValue = "1") int pageNo,
                                                  @RequestParam(required = false, defaultValue = "20") int pageSize) {
        Map<String, Object> resultMap = new HashMap<>();
        List<MessageVO> voList = new ArrayList<>();

        if (StringUtil.isEmpty(username)) {
            this.setMessageInfoToMap(resultMap, voList, 0);
        } else {
            EngineerDTO engineer = userFeign.getEngineerByUserName(username, null);
            if (engineer != null) {
                PageVO<MessagePushDTO> page = systemFeign.getMessagePushPage(engineer.getId(), type, null, null, null, null, MessagePushObjectEnum.ENGINEER.value, pageSize, pageNo);
                if (page != null && page.getResult() != null) {
                    MessageVO vo;
                    for (MessagePushDTO msg : page.getResult()) {
                        vo = new MessageVO();
                        vo.setId(String.valueOf(msg.getId()));
                        vo.setTitle(msg.getTitle());
                        vo.setContent(msg.getContent());
                        if(msg.getWorkorderId() !=null){
                            vo.setWorkorderId(msg.getWorkorderId());
                            vo.setWorkorderType("install");
                        }

                        if (msg.getCreateTime() != null) {
                            vo.setCreateTime(DateUtil.transferDateToString(msg.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                        }
                        if (msg.getReadStatus() != null && msg.getReadStatus() == 1) {
                            vo.setReadStatus(StatusEnum.YES.value());
                        } else {
                            vo.setReadStatus(StatusEnum.NO.value());
                        }
                        voList.add(vo);
                    }

                    Integer unReadCount = this.systemFeign.getMessageUnReadNum(engineer.getId(), type, null, app);
                    this.setMessageInfoToMap(resultMap, voList, unReadCount);
                } else {
                    this.setMessageInfoToMap(resultMap, voList, 0);
                }
            } else {
                this.setMessageInfoToMap(resultMap, voList, 0);
            }
        }

        ResultUtil.success(resultMap);
        return ResponseEntity.ok(resultMap);
    }

    /***
     * 功能描述:原安装工app调用云平台消息列表
     *
     * @param: [request, username, content, type, app, pageNo, pageSize]
     * @auther: liu yi
     * @date: 2019/5/5 17:36
     * @return: java.util.Map<java.lang.String   ,   java.lang.Object>
     */
    @GetMapping(value = {"/query"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "推送类型:10-系统推送 1-阈值提醒,2-续费推送,3-报警推送,4-创建账号,5-新工单分配,6-工单完成,7-审核通过,8-审核不通过,9-经销商续配额", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "内容", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Map<String, Object> query(HttpServletRequest request,
                                     @RequestParam String username,
                                     @RequestParam(required = false) String content,
                                     @RequestParam(required = false) Integer type,
                                     @RequestParam(required = false) Integer app,
                                     @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                     @RequestParam(required = false, defaultValue = "20") Integer pageSize) {

        Map<String, Object> resultMap = new HashMap();
        List<MessageVO> voList = new ArrayList<>();
        PageVO<MessagePushDTO> page;
        if (StringUtil.isEmpty(username)) {
            this.setMessageInfoToMap(resultMap, voList, 0);
        } else {
            EngineerDTO engineer = userFeign.getEngineerByUserName(username, null);
            if (engineer != null) {
                page = systemFeign.getMessagePushPage(engineer.getId(), type, content, null, null, null, MessagePushObjectEnum.ENGINEER.value, pageSize, pageNo);
                if (page != null && page.getResult() != null) {
                    voList = messagePushToVO(page.getResult());
                    Integer unReadMESNum = this.systemFeign.getMessageUnReadNum(engineer.getId(), type, content, MessageAppTypeEnum.customer());
                    this.setMessageInfoToMap(resultMap, voList, unReadMESNum);
                } else {
                    this.setMessageInfoToMap(resultMap, voList, 0);
                }
            } else {
                this.setMessageInfoToMap(resultMap, voList, 0);
            }
        }
        ResultUtil.success(resultMap);
        return resultMap;
    }

    @GetMapping(value = {"/type"})
    @ApiOperation(value = "安装工消息类型")
    @ApiImplicitParam(name = "username", value = "username", dataType = "String", paramType = "query")
    public Map<String, Object> getMessageType(@RequestParam(required = false) String username) {
        Map<String, Object> ru = new HashMap();
        ru.put("data", MessageTypeEnum.getMessageTypeJson());
        ResultUtil.success(ru);
        return ru;
    }

    private void setMessageInfoToMap(Map ru, List<MessageVO> voList, Integer unReadMESNum) {
        ru.put("list", voList);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", unReadMESNum);

        ru.put("data", jsonObject);
    }

    private List<MessageVO> messagePushToVO(List<MessagePushDTO> messages) {
        MessageVO vo;
        List<MessageVO> volist = new ArrayList();
        for (MessagePushDTO msg : messages) {
            vo = new MessageVO();
            vo.setId(msg.getId().toString());
            vo.setTitle(msg.getTitle());
            vo.setContent(msg.getContent());
            if(!StringUtil.isBlank(msg.getWorkorderId())){
                vo.setWorkorderId(msg.getWorkorderId());
                vo.setWorkorderType("install");
            }
            vo.setCreateTime(DateUtil.getDateToString(msg.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            if (msg.getReadStatus() != null && msg.getReadStatus() == 1) {
                vo.setReadStatus(StatusEnum.YES.value());
            } else {
                vo.setReadStatus(StatusEnum.NO.value());
            }

            volist.add(vo);
        }

        return volist;
    }

    @PostMapping(value = {"/read"})
    @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query")
    public Map<String, Object> read(@RequestParam String id) {
        log.info("==================messagereadReq:id=" + id);
        Map<String, Object> ru = new HashMap<String, Object>();
        if (StringUtil.isEmpty(id)) {
            ResultUtil.error(ru, "id不得为空");
            return ru;
        }
        List<MessageVO> list = new ArrayList<MessageVO>();
        //设置已读
        systemFeign.setMessagePushIsRead(Integer.valueOf(id));
        MessagePushDTO readResult = systemFeign.getMessagePushDetails(Integer.valueOf(id));
        MessageVO vo = new MessageVO();
        vo.setId(readResult.getId().toString());
        vo.setTitle(readResult.getTitle());
        vo.setContent(readResult.getContent());
        if(!StringUtil.isBlank(readResult.getWorkorderId())){
            vo.setWorkorderId(readResult.getWorkorderId());
            vo.setWorkorderType("install");//安装工app需要
        }
        vo.setWorkorderEntityId(readResult.getWorkorderId());
        vo.setCreateTime(DateUtil.getDateToString(readResult.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        if (readResult.getReadStatus() != null && readResult.getReadStatus() == 1) {
            vo.setReadStatus(StatusEnum.YES.value());
        } else {
            vo.setReadStatus(StatusEnum.NO.value());
        }

        if (!Objects.isNull(readResult)) {
            Integer unReadCount = this.systemFeign.getMessageUnReadNum(readResult.getReceiverId(), null, null, null);
            list.add(vo);
            this.setMessageInfoToMap(ru, list, unReadCount);
            ResultUtil.success(ru);
        } else {
            ResultUtil.error(ru, "读取失败!");
        }
        return ru;
    }

    /***
     * 功能描述:删除消息
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/5/7 14:30
     * @return: java.util.Map<java.lang.String , java.lang.Object>
     */
    @PostMapping(value = {"/delete"})
    @ApiImplicitParam(name = "id", value = "id 多个用逗号隔开", required = true, dataType = "String", paramType = "query")
    public Map<String, Object> delete(@RequestParam String id) {
        Map<String, Object> ru = new HashMap();
        this.systemFeign.deleteMessage(id);
        ResultUtil.success(ru);
        return ru;
    }
}
