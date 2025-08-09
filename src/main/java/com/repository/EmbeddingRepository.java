package com.docusense.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.docusense.model.EmbeddingEntity;

@Repository
public interface EmbeddingRepository extends JpaRepository<EmbeddingEntity, Long>{
	 List<EmbeddingEntity> findAll();
	    List<EmbeddingEntity> findByDocumentId(Long documentId);
}
