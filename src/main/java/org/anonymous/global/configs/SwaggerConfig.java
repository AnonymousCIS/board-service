package org.anonymous.global.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title="게시판 API", description = "게시판, 게시글, 댓글에 관한 API를 제공"))
public class SwaggerConfig {
    
    @Bean
    public GroupedOpenApi apiGroup() {

        return GroupedOpenApi.builder()
                .group("게시판 API v1")
                .pathsToMatch("/**")
                .build();
    }
}
