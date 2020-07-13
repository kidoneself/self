package com.yimao.cloud.out.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.cms.*;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @auther: liu.Long jie
 * @date: 2019/12/18
 */
@FeignClient(name = Constant.MICROSERVICE_CMS)
public interface CmsFeign {

    @GetMapping("/banner/engineerApp/imgae/list")
    List<BannerDTO> getEngineerAppImage();

}
