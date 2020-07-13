// package com.yimao.cloud.openapi.feign;
//
// import com.yimao.cloud.base.constant.Constant;
// import com.yimao.feign.configuration.MultipartSupportConfig;
// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.http.MediaType;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RequestPart;
// import org.springframework.web.multipart.MultipartFile;
//
// @FeignClient(name = Constant.MICROSERVICE_SYSTEM, configuration = MultipartSupportConfig.class)
// public interface UploadFeign {
//
//     /**
//      * 共通单个文件上传接口
//      *
//      * @param file   文件
//      * @param folder 上传目录，为空会上传到common目录下
//      * @param remark 备注
//      */
//     @RequestMapping(value = "/common/upload/single", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//     String upload(@RequestPart("file") MultipartFile file,
//                   @RequestParam(value = "folder", required = false) String folder,
//                   @RequestParam(value = "remark", required = false) String remark);
//
// }
