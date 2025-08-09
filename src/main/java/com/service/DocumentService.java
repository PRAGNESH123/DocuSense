package com.docusense.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.docusense.model.DocumentEntity;
import com.docusense.repository.DocumentRepository;

@Service
public class DocumentService {
	private final DocumentRepository documentRepository;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;

    public DocumentService(DocumentRepository documentRepository,
                           EmbeddingService embeddingService,
                           VectorStoreService vectorStoreService) {
        this.documentRepository = documentRepository;
        this.embeddingService = embeddingService;
        this.vectorStoreService = vectorStoreService;
    }

    public DocumentEntity saveDocument(String title, String content) throws Exception {
        DocumentEntity doc = new DocumentEntity(null, content, content, null);
        doc.setTitle(title);
        doc.setContent(content);
        doc = documentRepository.save(doc);

        // Chunk the content (simple fixed-size chunking)
        List<String> chunks = chunkText(content, 500); // 500 chars each chunk

        int idx = 0;
        for (String chunk : chunks) {
            double[] vector = embeddingService.embedText(chunk);
            vectorStoreService.saveEmbedding(doc.getId(), idx++, chunk, vector);
        }
        return doc;
    }

    private List<String> chunkText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int i = 0;
        while (i < text.length()) {
            int end = Math.min(i + chunkSize, text.length());
            chunks.add(text.substring(i, end));
            i = end;
        }
        return chunks;
    }

}
