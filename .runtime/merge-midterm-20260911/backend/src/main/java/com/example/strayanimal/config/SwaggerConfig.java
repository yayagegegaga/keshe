package com.example.strayanimal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI strayAnimalOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("流浪动物救助与领养协同管理平台 API")
                        .description("第一阶段：基础设施与健康检查接口")
                        .version("1.0.0"));
    }
}
