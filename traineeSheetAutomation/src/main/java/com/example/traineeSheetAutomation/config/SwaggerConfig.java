package com.example.traineeSheetAutomation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI muCustomConfig(){
        return new OpenAPI()
                .info(
                new Info().title("Trainee Sheet Automation")
                        .description("by Aiman")
                )
                .tags(Arrays.asList(
                        new Tag().name("User APIs"),
                        new Tag().name("Template APIs"),
                        new Tag().name("Module APIs"),
                        new Tag().name("Topic APIs"),
                        new Tag().name("Trainee Template APIs"),
                        new Tag().name("Trainee Module APIs"),
                        new Tag().name("Trainee Topic APIs"),
                        new Tag().name("Uploaded Content APIs"),
                        new Tag().name("Service Line APIs"),
                        new Tag().name("Role APIs")

                        )

                );
    }
}
