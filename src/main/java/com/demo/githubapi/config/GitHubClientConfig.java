package com.demo.githubapi.config;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class GitHubClientConfig {

    @Value("${GITHUB_JWT}")
    private String githubToken;

    @Bean
    public GitHub gitHub() {
        try {
            return GitHubBuilder.fromEnvironment()
                    .withJwtToken(githubToken)
                    .build();
        } catch (IOException e) {
            log.error("Error occurred while creating GitHub instance", e);
            throw new RuntimeException(e);
        }
    }
}
