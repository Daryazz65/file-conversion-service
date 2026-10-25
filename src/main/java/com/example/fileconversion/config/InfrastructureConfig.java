package com.example.fileconversion.config;

import io.minio.MinioClient;
import javax.sql.DataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class InfrastructureConfig {

    @Bean
    public MinioClient minioClient(AppProperties properties) {
        AppProperties.Minio minio = properties.minio();

        return MinioClient.builder()
                .endpoint(minio.endpoint())
                .credentials(minio.accessKey(), minio.secretKey())
                .build();
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        JdbcTemplateLockProvider.Configuration configuration =
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(jdbcTemplate)
                        .usingDbTime()
                        .build();

        return new JdbcTemplateLockProvider(configuration);
    }
}