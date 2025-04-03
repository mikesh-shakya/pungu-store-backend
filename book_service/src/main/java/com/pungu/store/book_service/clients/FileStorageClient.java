package com.pungu.store.book_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-storage-service", url = "http://localhost:8083/api/files")
public interface FileStorageClient {

    // 📌 Upload File (Multipart)
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file);

    // 📌 Download File
    @GetMapping("/download/{fileName}")
    ResponseEntity<byte[]> downloadFile(@PathVariable("fileName") String fileName);

    // 📌 Delete File
    @DeleteMapping("/delete/{fileName}")
    ResponseEntity<String> deleteFile(@PathVariable("fileName") String fileName);

}
