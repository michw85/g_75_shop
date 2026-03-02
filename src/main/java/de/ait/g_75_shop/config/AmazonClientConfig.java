package de.ait.g_75_shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * Spring configuration class for DigitalOcean Spaces S3 client
 * Creates and configures S3Client bean for file storage operations
 *
 * Spring-конфигурационный класс для S3 клиента DigitalOcean Spaces
 * Создает и настраивает бин S3Client для операций с файловым хранилищем
 */
@Configuration
// — метка для Spring, что в этом классе есть методы, которые возвращают бины (объекты, управляемые Spring).
// Spring при запуске вызовет метод amazonClient() и сохранит результат в контексте.
public class AmazonClientConfig {
/**
 * Creates and configures S3Client bean with DigitalOcean Spaces settings
 * @param properties configuration properties for DigitalOcean Spaces
 * @return configured S3Client instance / настроенный экземпляр S3Client
 */
    @Bean // — метод возвращает готовый объект S3Client, который потом можно будет внедрить через @Autowired
    // или конструктор в другие классы (например, в FileServiceImpl).
    // DOProperties — это класс с настройками DigitalOcean Spaces (endpoint, ключи доступа, bucket, region)
    public S3Client amazonClient(DOProperties properties) {

        // Create credentials object with access and secret keys
        // Создаем объект учетных данных с ключами доступа к бакету
        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // Create provider that manages access credentials
        // Создаём специальный провайдер ключей (объект, который управляет ключами доступа)
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        // Get region and endpoint from configuration
        // Получим из настроек регион (Дата-центр) и эндпоинт
        String region = properties.getRegion();
        String endpoint = properties.getEndpoint();

        // Create region object from string
        // Создаем объект региона из строкового имени региона
        Region regionInstance = Region.of(region);

        // Create URI for endpoint
        // Создаем URI для эндпоинта
        URI endointUri = URI.create(endpoint);

        /**
         * Build and return S3 client with custom configuration
         * pathStyleAccessEnabled(true) ensures bucket URL works correctly:
         * Standard: https://bucket-name.digitalocean.com
         * With path style: https://digitalocean.com/bucket-name (always works)
         *
         * Создаем и возвращаем S3 клиент с пользовательской конфигурацией
         * pathStyleAccessEnabled(true) обеспечивает корректную работу URL бакета:
         * Стандартный: https://bucket-name.digitalocean.com
         * С path style: https://digitalocean.com/bucket-name (работает всегда)
         */
        return S3Client.builder()
                .endpointOverride(endointUri) // Set custom endpoint / Устанавливаем пользовательский эндпоинт
                .region(regionInstance) // Set region / Устанавливаем регион
                .credentialsProvider(credentialsProvider) // Set credentials / Устанавливаем учетные данные
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build()) // Enable path style access / Включаем path style доступ
                .build();
        // https://shop-75-bucket.digitalocean.com - работает не всегда
    }
}
