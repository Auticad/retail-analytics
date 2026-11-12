package io.github.auticad.retail.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI retailAnalyticsOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Retail Analytics API")
                .version("v1")
                .description("Negozi, vendite e KPI di una rete retail. I KPI sono pensati per Power BI."));
    }
}
