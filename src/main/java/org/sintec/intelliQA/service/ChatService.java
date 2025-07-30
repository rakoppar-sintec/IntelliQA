package org.sintec.intelliQA.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class ChatService {
    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private static final String llmPath = "/v1/chat/completions";

    public ChatService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<String> streamAnswer(String question) {
        log.info("ChatService -> streamAnswer called with question: {}", question);
        Map<String, Object> body = Map.of(
                "model", "llama-2-7b-chat",
                "stream", true,
                "messages", new Object[] {
                        Map.of("role", "system", "content", "You're a helpful assistant"),
                        Map.of("role", "user", "content", question)
                }
        );

        log.info("ChatService -> calling the LLM");
        return webClient.post()
                .uri(llmPath)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class);
    }
}
