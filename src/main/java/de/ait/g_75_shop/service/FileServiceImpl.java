package de.ait.g_75_shop.service;

import de.ait.g_75_shop.config.DOProperties;
import de.ait.g_75_shop.exceptions.types.FileUploadException;
import de.ait.g_75_shop.service.interfaces.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.lang.invoke.StringConcatException;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of FileService interface
 * Handles file uploads to DigitalOcean Spaces (S3-compatible storage)
 *
 * Реализация интерфейса FileService
 * Обрабатывает загрузку файлов в DigitalOcean Spaces (S3-совместимое хранилище)
 */
@Service
public class FileServiceImpl implements FileService {

    private final S3Client client;
    private final DOProperties properties;

    public FileServiceImpl(S3Client client, DOProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    /**
     * Uploads file to cloud storage and returns public URL
     *
     * Загружает файл в облачное хранилище и возвращает публичный URL
     *
     * @param file file to upload / файл для загрузки
     * @return public URL of uploaded file / публичный URL загруженного файла
     * @throws IOException if file processing fails / если ошибка обработки файла
     * @throws FileUploadException if validation fails / если валидация не пройдена
     */
    @Override
    public String uploadAndGetUrl(MultipartFile file) throws IOException {
        // Method logic: 1. Required validations / Логика метода: 1. Необходимые проверки
        Objects.requireNonNull(file, "MultipartFile cannot be null");
        // Objects.requireNonNull(file.getContentType(), "MultipartFile content type cannot be null");

        // Check if file is empty / Проверка на пустой файл
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }
        // Ensure file is an image / Подстраховаться от того, что пришла вообще не картинка
        if (file.getContentType() == null || !file.getContentType().startsWith("image")) {
            throw new FileUploadException("File is not an image");
        }
        // 2. Generate unique filename / Сгенерировать уникальное имя файла
        String uniqueFileName = generateUniqueFileName(file);

        // 3. Create request to upload file to cloud with unique name / Создать запрос на загрузку файла в облако под уникальным именем

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(uniqueFileName)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ) // Make file publicly readable / Делаем файл публично читаемым
                .build();

        // Create request body from file input stream / Создаем тело запроса из входного потока файла
        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

        // 4. Send request (actual file upload to cloud) / Отправка запроса (фактическая загрузка файла в облако)
        client.putObject(request, requestBody);

        // 5. Generate and return public URL of uploaded file / Отправляем ещё запрос с целью получить ссылку на загруженный файл
        // 6. Возвращаем полученную ссылку в качестве результатов работы метода
        return client.utilities().getUrl(
                x -> x.bucket(properties.getBucket()).key(uniqueFileName)
        ).toString();
    }

    /**
     * Generates unique filename to avoid collisions in cloud storage
     * Format: [original-name]-[random-uuid].[extension]
     *
     * Генерирует уникальное имя файла для избежания коллизий в облачном хранилище
     * Формат: [оригинальное-имя]-[случайный-uuid].[расширение]
     *
     * @param file uploaded file / загруженный файл
     * @return unique filename / уникальное имя файла
     */
    private String generateUniqueFileName (MultipartFile file) {
        // Generate random UUID / Генерируем случайный текст. Представим, что текст такой - qksdfjs3df67kl9j
        String randomPart = UUID.randomUUID().toString();
        String fileName = file.getOriginalFilename();

        // Какие есть варианты имени файла, которые придут на вход:
        // Case 1: File without name -> qksdfjs3df67kl9j / 1. Файл пришёл вообще без имени -> qksdfjs3df67kl9j
        if(fileName == null) {
            return randomPart;
        }

        // Подстраховаться от пробелов в имени файлов
        // FaT LAZY cAT.jpeg -> fat-lazy-cat.jpeg
        // cat -> -1
        // cat.jpeg -> 3
        // Normalize filename: trim, replace spaces with hyphens, convert to lowercase / Приводим имя файла к нормальному виду: удаляем пробелы, заменяем пробелы на дефисы, в нижний регистр
        String normalizedFileName = fileName.trim().replace(" ", "-").toLowerCase();

        // Find last dot to separate extension /  Чтобы определить, где расширение, ищем точку с конца строки
        // fat.lazy.cat.jpeg
        int dotIndex = normalizedFileName.lastIndexOf(".");

        // Case 2: File with name but no extension - cat -> cat-qksdfjs3df67kl9j / 2. Файл пришёл с именем, но вообще без расширения - cat -> cat-qksdfjs3df67kl9j
        if (dotIndex == -1) {
            return String.format("%s-%s", normalizedFileName, randomPart);
        }

        // Case 3: File with name and extension - cat.jpeg -> cat-qksdfjs3df67kl9j.jpeg / 3. Файл пришёл с именем и расширением - cat.jpeg -> cat-qksdfjs3df67kl9j.jpeg
        // cat.jpeg -> cat
        String fileNameWithoutExtension = normalizedFileName.substring(0, dotIndex);
        // cat.jpeg -> .jpeg
        String extension = normalizedFileName.substring(dotIndex);
        // cat.jpeg -> cat-qksdfjs3df67kl9j.jpeg
        // .cat -> -qksdfjs3df67kl9j.cat
        // cat. -> cat-qksdfjs3df67kl9j.
        return String.format("%s-%s%s", fileNameWithoutExtension, randomPart, extension);
    }



}
