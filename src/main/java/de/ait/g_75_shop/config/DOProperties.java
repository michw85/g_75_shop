package de.ait.g_75_shop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for DigitalOcean Spaces
 * Binds properties with prefix "do" from application.properties/yml
 *
 * Свойства конфигурации для DigitalOcean Spaces
 * Связывает свойства с префиксом "do" из application.properties/yml
 */
@Configuration
@ConfigurationProperties(prefix = "do")
public class DOProperties {
    /**
     * DigitalOcean Spaces keys, endpoint URL, region, bucket
     * Ключи, URL эндпоинт, регион, имя бакета для хранения файлов DigitalOcean Spaces
     */
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String region;
    private String bucket;

    // Getters and Setters with comments / Геттеры и сеттеры с комментариями
    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
