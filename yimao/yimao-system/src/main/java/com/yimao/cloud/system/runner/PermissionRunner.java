// package com.yimao.cloud.system.runner;
//
// import com.yimao.cloud.base.enums.SystemType;
// import com.yimao.cloud.pojo.dto.user.PermissionDTO;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.method.HandlerMethod;
// import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
// import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//
// import javax.annotation.Resource;
// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Map;
//
// /**
//  * @author Zhang Bo
//  * @date 2018/12/20.
//  */
// @Configuration
// public class PermissionRunner implements CommandLineRunner {
//
//     @Resource
//     private RequestMappingHandlerMapping handlerMapping;
//
//     /**
//      * 初始化权限数据到数据库，权限和菜单的关联要在管理系统页面上设置修改
//      *
//      * @param args incoming main method arguments
//      * @throws Exception on error
//      */
//     @Override
//     public void run(String... args) throws Exception {
//         Map<RequestMappingInfo, HandlerMethod> map = this.handlerMapping.getHandlerMethods();
//         Iterator<Map.Entry<RequestMappingInfo, HandlerMethod>> iterator = map.entrySet().iterator();
//
//         List<PermissionDTO> permissionList = new ArrayList<>();
//         while (iterator.hasNext()) {
//             Map.Entry<RequestMappingInfo, HandlerMethod> entry = iterator.next();
//             RequestMappingInfo info = entry.getKey();
//             HandlerMethod handler = entry.getValue();
//
//             Object[] patternArray = info.getPatternsCondition().getPatterns().toArray();
//             Object[] methodArray = info.getMethodsCondition().getMethods().toArray();
//             if (patternArray.length > 0 && methodArray.length > 0) {
//                 PermissionDTO dto = new PermissionDTO();
//                 // 接口地址
//                 String pattern = patternArray[0].toString();
//                 // 请求方法类型
//                 String method = methodArray[0].toString();
//                 dto.setCode(pattern);
//                 dto.setMethod(method);
//                 dto.setSysType(SystemType.SYSTEM.value);
//                 permissionList.add(dto);
//             }
//         }
//         System.out.println("接口总数=" + permissionList.size());
//         // 批量新增权限
//         // userFeign.batchSavePermission(permissionList);
//     }
// }
