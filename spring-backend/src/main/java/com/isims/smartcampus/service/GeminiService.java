package com.isims.smartcampus.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isims.smartcampus.dto.GeminiAnalysisResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final String geminiApiKey;
    private final String geminiApiUrl;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GeminiService(
            @Value("${gemini.api.key}") String geminiApiKey,
            @Value("${gemini.api.url}") String geminiApiUrl,
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper) {
        this.geminiApiKey = geminiApiKey;
        this.geminiApiUrl = geminiApiUrl;
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public GeminiAnalysisResult analyzeImage(String base64Image, String description) {
        String prompt = """
                You are an environmental issue classifier for a university campus.
                Analyze this image and the following description: "%s"
                Respond ONLY in valid JSON with exactly these keys:
                {
                  "category": "<one of: Waste, Energy, Water, Greenspace, Pollution, Other>",
                  "ecoPoints": <integer between 10 and 100>,
                  "reasoning": "<one sentence>"
                }
                """.formatted(description);

        Map<String, Object> requestBody = createRequestBody(base64Image, prompt);

        try {
            String responseStr = webClient.post()
                    .uri(geminiApiUrl + "?key=" + geminiApiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseResponse(responseStr);
        } catch (Exception e) {
            e.printStackTrace();
            return new GeminiAnalysisResult("Other", 10, "Failed to analyze image with AI.");
        }
    }

    private Map<String, Object> createRequestBody(String base64Image, String prompt) {
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> inlineData = new HashMap<>();
        inlineData.put("mimeType", "image/jpeg");
        inlineData.put("data", base64Image);

        Map<String, Object> imagePart = new HashMap<>();
        imagePart.put("inlineData", inlineData);

        Map<String, Object> parts = new HashMap<>();
        parts.put("parts", List.of(textPart, imagePart));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(parts));

        return requestBody;
    }

    private GeminiAnalysisResult parseResponse(String responseStr) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseStr);
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    String jsonText = parts.get(0).path("text").asText();
                    // Clean up markdown block if present
                    if (jsonText.startsWith("```json")) {
                        jsonText = jsonText.substring(7, jsonText.length() - 3).trim();
                    } else if (jsonText.startsWith("```")) {
                        jsonText = jsonText.substring(3, jsonText.length() - 3).trim();
                    }
                    return objectMapper.readValue(jsonText, GeminiAnalysisResult.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new GeminiAnalysisResult("Other", 10, "Failed to parse AI response.");
    }
}
