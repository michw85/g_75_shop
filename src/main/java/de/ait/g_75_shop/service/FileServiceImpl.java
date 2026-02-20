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

@Service
public class FileServiceImpl implements FileService {

    private final S3Client client;
    private final DOProperties properties;

    public FileServiceImpl(S3Client client, DOProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public String uploadAndGetUrl(MultipartFile file) throws IOException {
        // Логика метода:
        // 1. Необходимые проверки
        Objects.requireNonNull(file, "MultipartFile cannot be null");
        // Objects.requireNonNull(file.getContentType(), "MultipartFile content type cannot be null");

        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }
        // Подстраховаться от того, что пришла вообще не картинка
        if (file.getContentType() == null || !file.getContentType().startsWith("image")) {
            throw new FileUploadException("File is not an image");
        }
        // 2. Сгенерировать уникальное имя файла
        String uniqueFileName = generateUniqueFileName(file);

        // 3. Создать запрос на загрузку файла в облако под уникальным именем

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(uniqueFileName)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

        // 4. Отправка запроса (фактическая загрузка файла в облако)
        client.putObject(request, requestBody);

        // 5. Отправляем ещё запрос с целью получить ссылку на загруженный файл
        // 6. Возвращаем полученную ссылку в качестве результатов работы метода
        return client.utilities().getUrl(
                x -> x.bucket(properties.getBucket()).key(uniqueFileName)
        ).toString();
    }

    private String generateUniqueFileName (MultipartFile file) {
        // Генерируем случайный текст. Представим, что текст такой - qksdfjs3df67kl9j
        String randomPart = UUID.randomUUID().toString();
        String fileName = file.getOriginalFilename();

        // Какие есть варианты имени файла, которые придут на вход:
        // 1. Файл пришёл вообще без имени -> qksdfjs3df67kl9j
        if(fileName == null) {
            return randomPart;
        }

        // Подстраховаться от пробелов в имени файлов
        // FaT LAZY cAT.jpeg -> fat-lazy-cat.jpeg
        // cat -> -1
        // cat.jpeg -> 3
        String normalizedFileName = fileName.trim().replace(" ", "-").toLowerCase();

        // Чтобы определить, где расширение, ищем точку с конца строки
        // fat.lazy.cat.jpeg
        int dotIndex = normalizedFileName.lastIndexOf(".");
        // 2. Файл пришёл с именем, но вообще без расширения - cat -> cat-qksdfjs3df67kl9j
        if (dotIndex == -1) {
            return String.format("%s-%s", normalizedFileName, randomPart);
        }
        // 3. Файл пришёл с именем и расширением - cat.jpeg -> cat-qksdfjs3df67kl9j.jpeg
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
