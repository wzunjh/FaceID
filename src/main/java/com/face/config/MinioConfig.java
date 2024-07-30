package com.face.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.service.endpoint}")
    private String minioServiceEndpoint;

    @Value("${minio.service.username}")
    private String minioServiceUsername;

    @Value("${minio.service.password}")
    private String minioServicePassword;

    @Bean
    public MinioClient minioClient(){


        return MinioClient.builder()
                .endpoint(minioServiceEndpoint)
                .credentials(minioServiceUsername,minioServicePassword)
                .build();

    }

}
