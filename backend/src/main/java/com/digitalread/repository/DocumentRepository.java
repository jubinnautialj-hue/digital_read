package com.digitalread.repository;

import com.digitalread.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByCreatedById(Long userId);
    List<Document> findByCategory(String category);
    List<Document> findByAccessibleTrue();
    
    @Query("SELECT d FROM Document d WHERE d.title LIKE %?1% OR d.description LIKE %?1%")
    List<Document> searchDocuments(String keyword);
    
    @Query("SELECT COUNT(d) FROM Document d")
    long countAll();
    
    @Query("SELECT COUNT(d) FROM Document d WHERE d.accessible = true")
    long countAccessible();
}
