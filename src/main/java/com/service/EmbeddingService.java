package com.docusense.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmbeddingService {
	@Value("${openai.api.key}")
    private String openaiApiKey;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public double[] embedText(String text) throws Exception {
        String url = "https://api.openai.com/v1/embeddings";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        // Here using a default embedding model — you can change to another model.
        String body = "{\"input\": " + mapper.writeValueAsString(text) + ", \"model\": \"text-embedding-3-small\"}";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = rest.postForEntity(url, entity, String.class);

        if (resp.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Embedding API failed: " + resp.getBody());
        }

        JsonNode root = mapper.readTree(resp.getBody());
        JsonNode embeddingNode = root.get("data").get(0).get("embedding");
        double[] vector = new double[embeddingNode.size()];
        for (int i = 0; i < embeddingNode.size(); i++) {
            vector[i] = embeddingNode.get(i).asDouble();
        }
        return vector;
    }

}
