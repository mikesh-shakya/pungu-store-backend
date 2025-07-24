package com.pungu.store.file_storage_service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service for storing, retrieving, and deleting files locally.
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Initializes the upload directory upon application startup.
     * Creates the directory if it does not exist.
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            logger.error("Could not create upload directory!", e);
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    /**
     * Stores the uploaded file on the server with a unique filename.
     *
     * @param file the multipart file to store
     * @return the generated unique filename
     * @throws RuntimeException if storing the file fails
     */
    public String storeFile(MultipartFile file) {
        try {
            String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String fileName = UUID.randomUUID() + "_" + originalName;

            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            logger.error("File upload failed", e);
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves the file from the local filesystem.
     *
     * @param fileName the name of the file to retrieve
     * @return the file as a {@link File} object
     * @throws RuntimeException if the file does not exist
     */
    public File getFile(String fileName) {
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        File file = filePath.toFile();
        if (!file.exists()) {
            throw new RuntimeException("File not found: " + fileName);
        }
        return file;
    }

    /**
     * Deletes the specified file from the local filesystem.
     *
     * @param fileName the name of the file to delete
     * @return
     * @throws RuntimeException if the deletion fails
     */
    public boolean deleteFile(String fileName) {
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Could not delete file: {}", fileName, e);
            throw new RuntimeException("Failed to delete file: " + fileName);
        }
        return true;
    }
}
