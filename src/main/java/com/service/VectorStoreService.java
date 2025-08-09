package com.docusense.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.docusense.model.EmbeddingEntity;
import com.docusense.repository.EmbeddingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VectorStoreService {
	private final EmbeddingRepository embeddingRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public VectorStoreService(EmbeddingRepository embeddingRepository) {
        this.embeddingRepository = embeddingRepository;
    }

    public void saveEmbedding(Long documentId, int chunkIndex, String chunkText, double[] vector) throws Exception {
        EmbeddingEntity e = new EmbeddingEntity();
        e.setDocumentId(documentId);
        e.setChunkIndex(chunkIndex);
        e.setChunkText(chunkText);
        e.setVectorJson(mapper.writeValueAsString(vector));
        embeddingRepository.save(e);
    }

    public List<EmbeddingEntity> search(double[] queryVector, int topK) throws Exception {
        List<EmbeddingEntity> all = embeddingRepository.findAll();
        List<Pair> scores = new ArrayList<>();
        for (EmbeddingEntity e : all) {
            double[] v = mapper.readValue(e.getVectorJson(), double[].class);
            double sim = cosineSimilarity(queryVector, v);
            scores.add(new Pair(e, sim));
        }
        return scores.stream()
                .sorted(Comparator.comparingDouble(Pair::getScore).reversed())
                .limit(topK)
                .map(Pair::getEntity)
                .collect(Collectors.toList());
    }

    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0.0, na = 0.0, nb = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb) + 1e-10);
    }

    private static class Pair {
        EmbeddingEntity entity;
        double score;
        Pair(EmbeddingEntity e, double s) { this.entity = e; this.score = s; }
        EmbeddingEntity getEntity() { return entity; }
        double getScore() { return score; }
    }
}
