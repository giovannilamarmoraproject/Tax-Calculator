package io.github.giovannilamarmora.tax_calculator.config;

import io.github.giovannilamarmora.tax_calculator.authentication.dto.UserData;
import io.github.giovannilamarmora.utils.config.OpenAPIConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@ComponentScan(basePackages = "io.github.giovannilamarmora.utils")
@Configuration
@OpenAPIDefinition(
    info = @Info(title = "Tax Calculator Swagger", version = "1.0.0"),
    servers = {
      @Server(
          url = "https://tax-calculator.giovannilamarmora.com",
          description = "Default Server URL"),
      @Server(url = "http://localhost:8080", description = "Local Server URL")
    })
public class AppConfig {

  @Bean
  public UserData userInfo() {
    return new UserData();
  }

  @Bean
  public OpenApiCustomizer applyStandardOpenAPIModifications() {
    return openApi -> {
      Paths paths = new Paths();
      openApi.getPaths().entrySet().stream()
          .sorted(Map.Entry.comparingByKey())
          .forEach(
              entry ->
                  paths.addPathItem(
                      entry.getKey(),
                      OpenAPIConfig.addJSONExamplesOnResource(entry.getValue(), AppConfig.class)));
      openApi.setPaths(paths);
    };
  }
}
