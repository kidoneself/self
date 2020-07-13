//package com.yimao.cloud.system.service.impl;
//
//
//import com.github.pagehelper.Page;
//import com.github.pagehelper.PageHelper;
//import com.yimao.cloud.base.exception.BadRequestException;
//import com.yimao.cloud.base.exception.YimaoException;
//import com.yimao.cloud.base.utils.CollectionUtil;
//import com.yimao.cloud.base.utils.ExcelUtil;
//import com.yimao.cloud.base.utils.StringUtil;
//import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
//import com.yimao.cloud.pojo.dto.system.MessageFilterDTO;
//import com.yimao.cloud.pojo.vo.PageVO;
//import com.yimao.cloud.system.feign.ProductFeign;
//import com.yimao.cloud.system.feign.UserFeign;
//import com.yimao.cloud.system.mapper.MessageFilterMapper;
//import com.yimao.cloud.system.po.MessageFilter;
//import com.yimao.cloud.system.service.MessageFilterService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import tk.mybatis.mapper.entity.Example;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Objects;
//
///***
// * 功能描述:过滤配置
// *
// * @auther: liu yi
// * @date: 2019/5/7 16:23
// */
//@Slf4j
//@Service
//public class MessageFilterServiceImpl implements MessageFilterService {
//    @Resource
//    private MessageFilterMapper messageFilterMapper;
//    @Resource
//    private UserFeign userFeign;
//    @Resource
//    private ProductFeign productFeign;
//
//    @Override
//    public List<MessageFilterDTO> findMessageFilterList(String province, String city, String region, Integer categoryId, Integer type) {
//        Example example = new Example(MessageFilter.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("province", province);
//        criteria.andEqualTo("city", city);
//        criteria.andEqualTo("region", region);
//        criteria.andEqualTo("categoryId", categoryId);
//        criteria.andEqualTo("type", type);
//
//        List<MessageFilter> list = messageFilterMapper.selectByExample(example);
//        List<MessageFilterDTO> mfdList = new ArrayList<>();
//        MessageFilterDTO dto = null;
//        for (MessageFilter mf : list) {
//            dto = new MessageFilterDTO();
//            mf.convert(dto);
//            mfdList.add(dto);
//        }
//
//        return mfdList;
//    }
//
//    @Override
//    public void createMessageFilter(MessageFilterDTO dto) {
//        if (dto == null) {
//            throw new BadRequestException("传入参数不能为空！");
//        }
//
//        List<MessageFilterDTO> list = findMessageFilterList(dto.getProvince(), dto.getCity(), dto.getRegion(), dto.getCategoryId(), dto.getType());
//
//        if (list != null && list.size() > 0) {
//            throw new BadRequestException("推送过滤配置已经存在！");
//        }
//        MessageFilter mf = new MessageFilter(dto);
//
//        messageFilterMapper.insert(mf);
//    }
//
//    @Override
//    public void deleteMessageFilterById(Integer id) {
//        if (id == null) {
//            throw new BadRequestException("传入参数id不能为空！");
//        }
//
//        messageFilterMapper.deleteByPrimaryKey(id);
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
//    public void importMessageFilterExcel(MultipartFile multipartFile) {
//        if (multipartFile != null && multipartFile.getSize() > 0L) {
//            String fileName = multipartFile.getOriginalFilename();
//            String suffixName = fileName.substring(fileName.lastIndexOf(".") + 1);
//            if (!suffixName.equals("xls") && !suffixName.equals("xlsx")) {
//                throw new BadRequestException("请使用.xls或者.xlsx格式导入！");
//            }
//
//            String[] beanPropertys = new String[]{"province", "city", "region", "categoryId", "hours", "typeName"};
//            MessageFilter messageFilter;
//            try {
//                List<MessageFilterDTO> parseList = ExcelUtil.parserExcel(MessageFilterDTO.class, multipartFile, beanPropertys, 1);
//                if (CollectionUtil.isNotEmpty(parseList)) {
//                    for (MessageFilterDTO dto : parseList) {
//
//                        if (StringUtil.isBlank(dto.getTypeName())) {
//                            throw new BadRequestException("消息内容不能为空！");
//                        }
//
//                        if (dto.getHours() == null || dto.getHours() < 0) {
//                            throw new BadRequestException("频次不能为负数！");
//                        }
//
//                        if (StringUtil.isBlank(dto.getProvince())) {
//                            throw new BadRequestException("省不能为空！");
//                        }
//
//                        if (dto.getCategoryId() == null) {
//                            throw new BadRequestException("产品范围不能为空！");
//                        }
//                        ProductCategoryDTO category = productFeign.getProductCategoryById(dto.getCategoryId());
//                        if (category == null) {
//                            throw new BadRequestException("产品不存在！");
//                        }
//                        List<MessageFilterDTO> list = findMessageFilterList(dto.getProvince(), dto.getCity(), dto.getRegion(), dto.getCategoryId(), dto.getType());
//                        if (list != null && list.size() > 0) {
//                            throw new BadRequestException("推送过滤配置已经存在！");
//                        }
//
//                        messageFilter = new MessageFilter();
//                        messageFilter.setProvince(dto.getProvince());
//                        messageFilter.setCity(dto.getCity());
//                        messageFilter.setRegion(dto.getRegion());
//                        messageFilter.setHours(dto.getHours());
//                        int type = 0;//推送内容
//                        if ("余额不足".equals(dto.getTypeName())) {
//                            type = 1;
//                        } else if ("滤芯报警".equals(dto.getTypeName())) {
//                            type = 4;
//                        }
//                        messageFilter.setType(type);
//                        messageFilter.setCreateTime(new Date());
//
//                        MessageFilter mf = new MessageFilter(dto);
//                        messageFilterMapper.insert(mf);
//                        log.info("导入了messageFilter:" + messageFilter);
//                    }
//                }
//            } catch (BadRequestException bre) {
//                bre.printStackTrace();
//                throw new YimaoException(bre.getMessage());
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new YimaoException("导入失败：" + e.getMessage());
//            }
//        }
//    }
//
//    @Override
//    public void updateMessageFilter(MessageFilterDTO dto) {
//        if (Objects.isNull(dto)) {
//            throw new BadRequestException("传入参数不能为空！");
//        }
//        if (dto.getId() == null) {
//            throw new BadRequestException("传入参数有误！");
//        }
//
//        List<MessageFilterDTO> list = findMessageFilterList(dto.getProvince(), dto.getCity(), dto.getRegion(), dto.getCategoryId(), dto.getType());
//
//        if (list != null && list.size() > 0) {
//            throw new BadRequestException("推送过滤配置已经存在！");
//        }
//
//        messageFilterMapper.updateByPrimaryKey(new MessageFilter(dto));
//    }
//
//    @Override
//    public PageVO<MessageFilterDTO> page(String province, String city, String region, int pageSize, int pageNum) {
//        Example example = new Example(MessageFilter.class);
//        Example.Criteria criteria = example.createCriteria();
//
//        criteria.andEqualTo("province", province);
//        criteria.andEqualTo("city", city);
//        criteria.andEqualTo("region", region);
//        example.orderBy("createTime");
//        PageHelper.startPage(pageNum, pageSize);
//        Page<MessageFilter> page = (Page<MessageFilter>) messageFilterMapper.selectByExample(example);
//
//        return new PageVO<>(pageNum, page, MessageFilter.class, MessageFilterDTO.class);
//    }
//
//    @Override
//    public MessageFilterDTO getMessageFilterById(Integer id) {
//        MessageFilter messageFilter = messageFilterMapper.selectByPrimaryKey(id);
//        MessageFilterDTO dto = null;
//        if (messageFilter != null) {
//            dto = new MessageFilterDTO();
//            messageFilter.convert(dto);
//        }
//
//        return dto;
//    }
//}
