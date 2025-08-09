package com.docusense.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "embeddings")
public class EmbeddingEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;
    private int chunkIndex;

    @Column(length = 100000)
    private String chunkText;

    @Column(length = 1000)
    private String vectorJson;

//	public EmbeddingEntity() {
//		super();
//		this.id = id;
//		this.documentId = documentId;
//		this.chunkIndex = chunkIndex;
//		this.chunkText = chunkText;
//		this.vectorJson = vectorJson;
//	}
	
	
	

	public EmbeddingEntity() {
		super();
	}




	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public int getChunkIndex() {
		return chunkIndex;
	}

	public void setChunkIndex(int chunkIndex) {
		this.chunkIndex = chunkIndex;
	}

	public String getChunkText() {
		return chunkText;
	}

	public void setChunkText(String chunkText) {
		this.chunkText = chunkText;
	}

	public String getVectorJson() {
		return vectorJson;
	}

	public void setVectorJson(String vectorJson) {
		this.vectorJson = vectorJson;
	} 
    
    
    

}
