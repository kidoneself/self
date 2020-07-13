//package com.yimao.cloud.system.service;
//
//import com.yimao.cloud.pojo.dto.system.MessageFilterDTO;
//import com.yimao.cloud.pojo.vo.PageVO;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//public interface MessageFilterService{
//
//    /***
//     * 功能描述:获取消息过滤
//     *
//     * @param: [province, city, region, categoryId, type]
//     * @auther: liu yi
//     * @date: 2019/4/29 14:11
//     * @return: com.yimao.cloud.pojo.dto.system.MessageFilterDTO
//     */
//    List<MessageFilterDTO> findMessageFilterList(String province, String city, String region, Integer categoryId, Integer type);
//
//    /***
//     * 功能描述:创建消息过滤
//     *
//     * @param: [dto]
//     * @auther: liu yi
//     * @date: 2019/4/29 14:11
//     * @return: void
//     */
//    void createMessageFilter(MessageFilterDTO dto);
//
//    /***
//     * 功能描述:更新消息过滤
//     *
//     * @param: [dto]
//     * @auther: liu yi
//     * @date: 2019/4/29 14:11
//     * @return: void
//     */
//    void updateMessageFilter(MessageFilterDTO dto);
//
//    /***
//     * 功能描述:分页查询消息过滤
//     *
//     * @param: [province, city, region, pageSize, pageNum]
//     * @auther: liu yi
//     * @date: 2019/4/29 14:10
//     * @return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.system.MessageFilterDTO>
//     */
//    PageVO<MessageFilterDTO> page(String province, String city, String region, int pageSize, int pageNum);
//
//    /***
//     * 功能描述:根据id获取消息过滤
//     *
//     * @param: [id]
//     * @auther: liu yi
//     * @date: 2019/4/29 14:10
//     * @return: com.yimao.cloud.pojo.dto.system.MessageFilterDTO
//     */
//    MessageFilterDTO getMessageFilterById(Integer id) ;
//
//    /***
//     * 功能描述:删除
//     *
//     * @param: [id]
//     * @auther: liu yi
//     * @date: 2019/4/29 16:21
//     * @return: void
//     */
//    void deleteMessageFilterById(Integer id);
//
//    /***
//     * 功能描述:导入
//     *
//     * @param: [multipartFile]
//     * @auther: liu yi
//     * @date: 2019/5/7 14:41
//     * @return: void
//     */
//    void importMessageFilterExcel(MultipartFile multipartFile);
//}
