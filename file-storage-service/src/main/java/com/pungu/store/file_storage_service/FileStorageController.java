package com.pungu.store.file_storage_service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * REST controller for handling file storage operations like upload, download, and delete.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    /**
     * Uploads a file to the server.
     *
     * @param file Multipart file to be uploaded
     * @return ResponseEntity containing success or error message
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        return ResponseEntity.ok("File uploaded successfully: " + fileName);
    }

    /**
     * Downloads a file from the server.
     *
     * @param fileName Name of the file to download
     * @return ResponseEntity containing the file as a Resource
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileName") String fileName) {
        File file = fileStorageService.getFile(fileName);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    /**
     * Deletes a file from the server.
     *
     * @param fileName Name of the file to delete
     * @return ResponseEntity containing success or error message
     */
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable("fileName") String fileName) {
        boolean deleted = fileStorageService.deleteFile(fileName);
        if (deleted) {
            return ResponseEntity.ok("File deleted successfully: " + fileName);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File deletion failed or file not found");
        }
    }
}
