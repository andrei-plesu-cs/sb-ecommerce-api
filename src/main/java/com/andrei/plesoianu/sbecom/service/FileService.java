package com.andrei.plesoianu.sbecom.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    String uploadImage(Path path, MultipartFile file) throws IOException;
}
