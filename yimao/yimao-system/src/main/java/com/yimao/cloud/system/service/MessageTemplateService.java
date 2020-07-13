package com.yimao.cloud.system.service;


import com.yimao.cloud.pojo.dto.system.MessageTemplateDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;

/***
 * 功能描述:消息模版
 *
 * @auther: liu yi
 * @date: 2019/4/30 10:25
 */
public interface MessageTemplateService  {
    /***
     * 功能描述:分页查询
     *
     * @param: [type, mechanism, pushObject, pushMode, pageSize, pageNum]
     * @auther: liu yi
     * @date: 2019/4/30 10:25
     * @return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.system.MessageTemplateDTO>
     */
    PageVO<MessageTemplateDTO> page(Integer type, Integer mechanism, Integer pushObject, Integer pushMode, int pageSize, int pageNum);

    /***
     * 功能描述:判断是否存在
     *
     * @param: [type, mechanism, pushObject, pushMode]
     * @auther: liu yi
     * @date: 2019/4/30 10:25
     * @return: boolean
     */
    boolean exists(Integer type, Integer mechanism, Integer pushObject, Integer pushMode);

    /***
     * 功能描述:新增
     *
     * @param: [MessageTemplate]
     * @auther: liu yi
     * @date: 2019/4/30 10:26
     * @return: void
     */
    void addMessageTemplate(MessageTemplateDTO messageTemplate);

    /***
     * 功能描述:编辑
     *
     * @param: [MessageTemplate]
     * @auther: liu yi
     * @date: 2019/4/30 10:26
     * @return: void
     */
    void updateMessageTemplate(MessageTemplateDTO messageTemplate);

    /***
     * 功能描述:查询
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/4/30 10:26
     * @return: void
     */
    MessageTemplateDTO getMessageTemplateById(Integer id);

    /***
     * 功能描述:根据条件查询
     *
     * @param: [type, mechanism, pushObject, pushMode]
     * @auther: liu yi
     * @date: 2019/5/8 10:55
     * @return: com.yimao.cloud.pojo.dto.system.MessageTemplateDTO
     */
    MessageTemplateDTO getMessageTemplateByParam(Integer type, Integer mechanism, Integer pushObject, Integer pushMode);
}
