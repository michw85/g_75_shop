package de.ait.g_75_shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

// Spring-конфигурационный класс, который создаёт бин S3Client для работы с DigitalOcean Spaces.
@Configuration
// — метка для Spring, что в этом классе есть методы, которые возвращают бины (объекты, управляемые Spring).
// Spring при запуске вызовет метод amazonClient() и сохранит результат в контексте.
public class AmazonClientConfig {

    @Bean // — метод возвращает готовый объект S3Client, который потом можно будет внедрить через @Autowired
    // или конструктор в другие классы (например, в FileServiceImpl).
    // DOProperties — это класс с настройками DigitalOcean Spaces (endpoint, ключи доступа, bucket, region)
    public S3Client amazonClient(DOProperties properties) {

        // Создаём специальный объект, который содержит оба ключа к бакету
        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // Создаём специальный провайдер ключей (объект, который управляет ключами доступа)
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        // Получим из настроек регион (Дата-центр) и эндпоинт
        String region = properties.getRegion();
        String endpoint = properties.getEndpoint();

        // Создаем объект региона из строкового имени региона
        Region regionInstance = Region.of(region);

        // Создаем URI для эндпоинта
        URI endointUri = URI.create(endpoint);

        return S3Client.builder()
                .endpointOverride(endointUri)
                .region(regionInstance)
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();

        // Стандартный минимальный конфиг формирует URI бакета так:
        // https://shop-75-bucket.digitalocean.com - работает не всегда
        // pathStyleAccessEnabled(true) формирует URI бакета так:
        // https://digitalocean.com/shop-75-bucket - работает всегда
    }
}
