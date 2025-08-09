package com.docusense.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.docusense.model.EmbeddingEntity;
import com.docusense.service.EmbeddingService;
import com.docusense.service.VectorStoreService;

@RestController
@RequestMapping("/api/search")
public class SearchController {
	 private final EmbeddingService embeddingService;
	    private final VectorStoreService vectorStoreService;

	    public SearchController(EmbeddingService embeddingService, VectorStoreService vectorStoreService) {
	        this.embeddingService = embeddingService;
	        this.vectorStoreService = vectorStoreService;
	    }

	    @PostMapping
	    public ResponseEntity<?> search(@RequestBody QueryRequest req) {
	        try {
	            double[] qv = embeddingService.embedText(req.getQuery());
	            List<EmbeddingEntity> tops = vectorStoreService.search(qv, req.getTopK() <= 0 ? 5 : req.getTopK());
	            return ResponseEntity.ok(tops);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body(e.getMessage());
	        }
	    }

	    public static class QueryRequest {
	        private String query;
	        private int topK = 5;
	        public String getQuery() { return query; }
	        public void setQuery(String query) { this.query = query; }
	        public int getTopK() { return topK; }
	        public void setTopK(int topK) { this.topK = topK; }
	    }

}
