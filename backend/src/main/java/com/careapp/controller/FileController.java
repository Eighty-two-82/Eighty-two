package com.careapp.controller;

import com.careapp.domain.FileRecord;
import com.careapp.service.FileService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Resource
    private FileService fileService;

    // Upload metadata (front-end already generates preview URL locally; here we accept URL and metadata)
    @PostMapping
    public Result<FileRecord> uploadFile(@RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            String category = (String) body.get("category");
            String uploadedBy = (String) body.get("uploadedBy");
            String fileUrl = (String) body.get("fileUrl");
            String contentType = (String) body.get("contentType");
            Long size = body.get("size") == null ? null : Long.valueOf(body.get("size").toString());
            String comment = (String) body.getOrDefault("comment", "");

            if (name == null || fileUrl == null) {
                return Result.error("400", "name and fileUrl are required");
            }

            FileRecord record = new FileRecord();
            record.setName(name);
            record.setCategory(category);
            record.setUploadedBy(uploadedBy);
            record.setFileUrl(fileUrl);
            record.setContentType(contentType);
            record.setSize(size);
            record.setComment(comment);

            FileRecord saved = fileService.save(record);
            return Result.success(saved, "File metadata saved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to save file metadata: " + e.getMessage());
        }
    }

    // List files (optional filters)
    @GetMapping
    public Result<List<FileRecord>> listFiles(@RequestParam(required = false) String category,
                                              @RequestParam(required = false) String uploadedBy) {
        try {
            if (category != null && !category.isEmpty()) {
                return Result.success(fileService.listByCategory(category), "Files retrieved successfully!");
            }
            if (uploadedBy != null && !uploadedBy.isEmpty()) {
                return Result.success(fileService.listByUploader(uploadedBy), "Files retrieved successfully!");
            }
            return Result.success(fileService.listAll(), "Files retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve files: " + e.getMessage());
        }
    }

    // Get by ID
    @GetMapping("/{id}")
    public Result<FileRecord> getFile(@PathVariable String id) {
        try {
            Optional<FileRecord> record = fileService.getById(id);
            if (record.isPresent()) {
                return Result.success(record.get(), "File retrieved successfully!");
            }
            return Result.error("404", "File not found!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve file: " + e.getMessage());
        }
    }

    // Update comment
    @PutMapping("/{id}/comment")
    public Result<FileRecord> updateComment(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String comment = body.get("comment");
            FileRecord updated = fileService.updateComment(id, comment == null ? "" : comment);
            if (updated == null) {
                return Result.error("404", "File not found!");
            }
            return Result.success(updated, "Comment updated successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to update comment: " + e.getMessage());
        }
    }

    // Delete file metadata
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable String id) {
        try {
            boolean ok = fileService.deleteById(id);
            if (ok) {
                return Result.success(true, "File deleted successfully!");
            }
            return Result.error("404", "File not found!");
        } catch (Exception e) {
            return Result.error("500", "Failed to delete file: " + e.getMessage());
        }
    }
}


