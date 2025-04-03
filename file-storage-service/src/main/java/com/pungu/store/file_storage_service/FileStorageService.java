package com.pungu.store.file_storage_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}") // Read from application.properties
    private String uploadDir;

    // Method to store file
    public String storeFile(MultipartFile file) throws IOException {
        // Ensure the directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate a unique file name
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath.toFile());

        return fileName;
    }

    // Method to retrieve a file
    public File getFile(String fileName) {
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        File file = filePath.toFile();
        return file.exists() ? file : null;
    }

    // Method to delete a file
    public boolean deleteFile(String fileName) {
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
