package de.ait.g_75_shop.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    String uploadAndGetUrl(MultipartFile file) throws IOException;
}
