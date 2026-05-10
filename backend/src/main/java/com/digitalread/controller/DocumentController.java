package com.digitalread.controller;

import com.digitalread.entity.Document;
import com.digitalread.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping
    public ResponseEntity<?> getAllDocuments() {
        try {
            List<Document> documents = documentService.getAllDocuments();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", documents);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/accessible")
    public ResponseEntity<?> getAccessibleDocuments() {
        try {
            List<Document> documents = documentService.getAccessibleDocuments();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", documents);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDocumentById(@PathVariable Long id) {
        try {
            Document document = documentService.getDocumentById(id)
                    .orElseThrow(() -> new RuntimeException("文档不存在"));
            documentService.incrementViewCount(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", document);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchDocuments(@RequestParam String keyword) {
        try {
            List<Document> documents = documentService.searchDocuments(keyword);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", documents);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping
    public ResponseEntity<?> createDocument(@RequestBody Document document) {
        try {
            Document savedDocument = documentService.createDocument(document);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", savedDocument);
            result.put("message", "创建成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocument(@PathVariable Long id, @RequestBody Document documentDetails) {
        try {
            Document updatedDocument = documentService.updateDocument(id, documentDetails);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", updatedDocument);
            result.put("message", "更新成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getDocumentStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", documentService.getTotalDocumentCount());
            stats.put("accessible", documentService.getAccessibleDocumentCount());
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", stats);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
