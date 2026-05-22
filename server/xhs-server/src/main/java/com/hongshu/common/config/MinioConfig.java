package com.hongshu.common.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String imgBucket;

    public String getEndpoint() { return endpoint; } public void setEndpoint(String e) { this.endpoint = e; }
    public String getAccessKey() { return accessKey; } public void setAccessKey(String k) { this.accessKey = k; }
    public String getSecretKey() { return secretKey; } public void setSecretKey(String k) { this.secretKey = k; }
    public String getBucketName() { return bucketName; } public void setBucketName(String n) { this.bucketName = n; }
    public String getImgBucket() { return imgBucket; } public void setImgBucket(String b) { this.imgBucket = b; }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
