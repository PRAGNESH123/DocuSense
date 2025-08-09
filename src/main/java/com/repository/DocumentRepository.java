package com.docusense.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.docusense.model.DocumentEntity;


@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long>{

}
