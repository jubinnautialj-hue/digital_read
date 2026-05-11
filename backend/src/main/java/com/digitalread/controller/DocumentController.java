package com.digitalread.controller;

import com.digitalread.dto.StructuredNode;
import com.digitalread.entity.Document;
import com.digitalread.service.DocumentParserService;
import com.digitalread.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentParserService documentParserService;

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

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "category", required = false) String category) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("请选择文件");
            }

            String filename = file.getOriginalFilename();
            if (!documentParserService.isSupported(filename)) {
                throw new RuntimeException("不支持的文件格式，支持: PDF, DOC, DOCX, TXT, HTML, MD");
            }

            StructuredNode structuredNode = documentParserService.parseDocument(file);
            String plainText = structuredNode.toPlainText();
            String structuredJson = documentParserService.nodeToJson(structuredNode);
            List<String> headings = documentParserService.extractHeadings(structuredNode);
            List<String> paragraphs = documentParserService.getAllParagraphs(structuredNode);

            Document document = new Document();
            document.setTitle(title != null ? title : filename);
            document.setCategory(category);
            document.setContent(plainText);
            document.setFileType(documentParserService.getFileExtension(filename));
            document.setStructured(true);
            document.setAccessible(true);

            Document savedDocument = documentService.createDocument(document);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", savedDocument);
            result.put("structuredData", structuredNode);
            result.put("headings", headings);
            result.put("paragraphs", paragraphs);
            result.put("message", "文档上传并解析成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/{id}/structured")
    public ResponseEntity<?> getStructuredDocument(@PathVariable Long id) {
        try {
            Document document = documentService.getDocumentById(id)
                    .orElseThrow(() -> new RuntimeException("文档不存在"));

            StructuredNode structuredNode = documentParserService.parseContent(
                    document.getContent(),
                    document.getFileType()
            );

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", structuredNode);
            result.put("headings", documentParserService.extractHeadings(structuredNode));
            result.put("paragraphs", documentParserService.getAllParagraphs(structuredNode));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/parse")
    public ResponseEntity<?> parseContent(@RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            String fileType = request.getOrDefault("fileType", "txt");

            StructuredNode structuredNode = documentParserService.parseContent(content, fileType);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", structuredNode);
            result.put("headings", documentParserService.extractHeadings(structuredNode));
            result.put("paragraphs", documentParserService.getAllParagraphs(structuredNode));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/supported-formats")
    public ResponseEntity<?> getSupportedFormats() {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", List.of("pdf", "doc", "docx", "txt", "html", "htm", "md", "markdown"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
