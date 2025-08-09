package com.docusense.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.docusense.model.DocumentEntity;
import com.docusense.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
	private final DocumentService documentService;

    public DocumentController(DocumentService documentService) { this.documentService = documentService; }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestBody UploadRequest req) {
        try {
            DocumentEntity doc = documentService.saveDocument(req.getTitle(), req.getContent());
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    public static class UploadRequest {
        private String title;
        private String content;
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
	

}
