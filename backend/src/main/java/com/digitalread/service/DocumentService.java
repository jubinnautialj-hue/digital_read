package com.digitalread.service;

import com.digitalread.entity.Document;
import com.digitalread.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public List<Document> getAccessibleDocuments() {
        return documentRepository.findByAccessibleTrue();
    }

    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public List<Document> searchDocuments(String keyword) {
        return documentRepository.searchDocuments(keyword);
    }

    public List<Document> getDocumentsByCategory(String category) {
        return documentRepository.findByCategory(category);
    }

    public Document createDocument(Document document) {
        return documentRepository.save(document);
    }

    public Document updateDocument(Long id, Document documentDetails) {
        return documentRepository.findById(id).map(doc -> {
            if (documentDetails.getTitle() != null) doc.setTitle(documentDetails.getTitle());
            if (documentDetails.getDescription() != null) doc.setDescription(documentDetails.getDescription());
            if (documentDetails.getAuthor() != null) doc.setAuthor(documentDetails.getAuthor());
            if (documentDetails.getCategory() != null) doc.setCategory(documentDetails.getCategory());
            if (documentDetails.getContent() != null) doc.setContent(documentDetails.getContent());
            if (documentDetails.getFileType() != null) doc.setFileType(documentDetails.getFileType());
            if (documentDetails.getStructured() != null) doc.setStructured(documentDetails.getStructured());
            if (documentDetails.getAccessible() != null) doc.setAccessible(documentDetails.getAccessible());
            return documentRepository.save(doc);
        }).orElseThrow(() -> new RuntimeException("文档不存在"));
    }

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }

    public void incrementViewCount(Long id) {
        documentRepository.findById(id).ifPresent(doc -> {
            doc.setViewCount(doc.getViewCount() + 1);
            documentRepository.save(doc);
        });
    }

    public long getTotalDocumentCount() {
        return documentRepository.countAll();
    }

    public long getAccessibleDocumentCount() {
        return documentRepository.countAccessible();
    }
}
