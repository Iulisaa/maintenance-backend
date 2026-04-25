package swbg.solutions.com.maintenanceapp.common.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openApi(): OpenAPI =
        OpenAPI().info(
            Info()
                .title("Maintenance App API")
                .version("v1")
                .description("API for planning equipment maintenance, reports, and engineer workloads")
        )
}
