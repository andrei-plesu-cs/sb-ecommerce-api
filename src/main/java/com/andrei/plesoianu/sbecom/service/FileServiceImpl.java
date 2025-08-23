package com.andrei.plesoianu.sbecom.service;

import com.andrei.plesoianu.sbecom.exceptions.ApiException;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadImage(@NonNull Path path, @NonNull MultipartFile file) throws IOException {
        if (Files.exists(path) && !Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory");
        }

        String randomId = UUID.randomUUID().toString();
        randomId += getFileExtension(file);

        Files.createDirectories(path);
        path = path.resolve(randomId);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return randomId;
    }

    private String getFileExtension(MultipartFile file) {
        return switch(file.getContentType()) {
            case MediaType.IMAGE_PNG_VALUE -> ".png";
            case MediaType.IMAGE_JPEG_VALUE -> ".jpg";
            case MediaType.IMAGE_GIF_VALUE -> ".gif";
            default -> throw new ApiException("File type not supported");
        };
    }
}
