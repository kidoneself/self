package com.yimao.cloud.hra.mapper;

import com.yimao.cloud.hra.po.MiniSecondClassify;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;
import java.util.Map;

/**
 * @author ycl
 * @Create: 2019/4/22 14:12
*/
public interface MiniSecondClassifyMapper extends Mapper<MiniSecondClassify> {

    /**
     * @Description: 查询症状的二级分类
     * @author ycl
     * @param
     * @Return: java.util.List<com.yimao.cloud.hra.po.HealthySecondClassify>
     * @Create: 2019/4/22 14:11
    */
    List<MiniSecondClassify> findAllSecond();

    /**
     * @Description: 查询健康的二级分类
     * @author ycl
     * @param:
     * @Return: java.util.List<com.yimao.cloud.hra.po.HealthySecondClassify>
     * @Create: 2019/4/22 14:11
    */
    List<MiniSecondClassify> findAllHealthSecond();

    /**
     * @Description: 获取症状类型为全部
     * @author ycl
     * @param pid
     * @Return: com.yimao.cloud.hra.po.HealthySecondClassify
     * @Create: 2019/4/22 14:11
    */
    MiniSecondClassify selectByPid(Integer pid);

    /**
     * @Description: 获取健康类型
     * @author ycl
     * @param pid
     * @Return: com.yimao.cloud.hra.po.HealthySecondClassify
     * @Create: 2019/4/22 14:11
    */
    MiniSecondClassify selectHealthByPid(@Param("pid") Integer pid);


    List<MiniSecondClassify> selectClassifyByMap(Map<String, String> map);
}


