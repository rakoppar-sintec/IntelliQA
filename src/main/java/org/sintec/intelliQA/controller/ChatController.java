package org.sintec.intelliQA.controller;

import org.sintec.intelliQA.model.ChatRequest;
import org.sintec.intelliQA.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(@RequestBody ChatRequest request) {
        log.info("ChatController -> Received request : {}", request.getQuestion());
        return chatService.streamAnswer(request.getQuestion())
                .map(token -> ServerSentEvent.<String>builder(token).build())
                .onErrorResume(ex -> {
                    log.error("ChatController -> Error occurred: {}", ex.getMessage());
                    return Flux.just(ServerSentEvent.<String>builder()
                            .event("error")
                            .data("Error occurred: " + ex.getMessage())
                            .build());
                });
    }

    @GetMapping("/health")
    public String health() {
        return "IntelliQA is running!!";
    }
}
