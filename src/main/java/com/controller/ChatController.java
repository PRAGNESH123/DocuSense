package com.docusense.controller;

import com.docusense.model.EmbeddingEntity;
import com.docusense.service.EmbeddingService;
import com.docusense.service.VectorStoreService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
	private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;
    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String openaiApiKey;

    public ChatController(EmbeddingService embeddingService, VectorStoreService vectorStoreService) {
        this.embeddingService = embeddingService;
        this.vectorStoreService = vectorStoreService;
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody ChatRequest req) {
        try {
            double[] qv = embeddingService.embedText(req.getQuestion());
            List<EmbeddingEntity> tops = vectorStoreService.search(qv, 5);

            String context = tops.stream()
                    .map(e -> e.getChunkText())
                    .collect(Collectors.joining("\n\n---\n\n"));

            String systemPrompt = "You are a helpful assistant. Use the provided context to answer the question. If the context doesn't contain the answer, say you don't know.";

            String prompt = systemPrompt + "\n\nCONTEXT:\n" + context + "\n\nQUESTION:\n" + req.getQuestion();

            // Call OpenAI chat/completions (simple completion)
            String responseText = callOpenAiCompletion(prompt);
            return ResponseEntity.ok(new ChatResponse(responseText));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    private String callOpenAiCompletion(String prompt) throws Exception {
        String url = "https://api.openai.com/v1/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        // Using gpt-4o-mini or gpt-4o etc may vary — change model name as needed.
        String body = mapper.writeValueAsString(new Object() {
            public String model = "gpt-4o-mini";
            public Object[] messages = new Object[] {
                    new Object() { public String role="system"; public String content= "You are a helpful assistant."; },
                    new Object() { public String role="user"; public String content= prompt; }
            };
            public int max_tokens = 512;
            public double temperature = 0.0;
        });

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = rest.postForEntity(url, entity, String.class);

        if (resp.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("OpenAI completion failed: " + resp.getBody());
        }

        JsonNode root = mapper.readTree(resp.getBody());
        JsonNode contentNode = root.get("choices").get(0).get("message").get("content");
        return contentNode.asText();
    }

    public static class ChatRequest {
        private String question;
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
    }

    public static class ChatResponse {
        private String answer;
        public ChatResponse(String answer) { this.answer = answer; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }

}
