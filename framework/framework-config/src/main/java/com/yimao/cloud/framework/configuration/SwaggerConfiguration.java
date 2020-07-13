package com.yimao.cloud.framework.configuration;

import com.yimao.cloud.base.constant.Constant;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Zhang Bo
 * @date 2018/8/11.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Value("${spring.application.name}")
    private String appName;

    //swagger2的配置文件，这里可以配置swagger2的一些基本的内容，比如扫描的包等等
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(Constant.LOCAL_ENVIRONMENT || Constant.DEV_ENVIRONMENT || Constant.TEST_ENVIRONMENT)
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }

    //构建 api文档的详细信息函数,注意这里的注解引用的是哪个
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title(appName + "服务API文档")
                // .description("基于swagger2的RESTful API文档")
                // .termsOfServiceUrl("")
                // .contact(new Contact("Zhang Bo", "https://ylfjmy.gitee.io/", ""))
                .version("2.0")
                .build();
    }

}
