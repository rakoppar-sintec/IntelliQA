package org.sintec.intelliQA.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ChatServiceTest {

    private WebClient mockWebClient;
    private WebClient.RequestBodyUriSpec mockRequestBodyUriSpec;
    private WebClient.RequestBodySpec mockRequestBodySpec;
    private WebClient.RequestHeadersSpec mockRequestHeadersSpec;
    private WebClient.ResponseSpec mockResponseSpec;

    private ChatService chatService;

    @BeforeEach
    public void setup() {
        mockWebClient = mock(WebClient.class);
        mockRequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        mockRequestBodySpec = mock(WebClient.RequestBodySpec.class);
        mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri("/v1/chat/completions")).thenReturn(mockRequestBodySpec);
        when(mockRequestBodySpec.bodyValue(any(Map.class))).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);

        Flux<String> responseFlux = Flux.just("Berlin");

        when(mockResponseSpec.bodyToFlux(String.class)).thenReturn(responseFlux);

        chatService = new ChatService(mockWebClient);
    }

    @Test
    public void testStreamAnswer_whenQuestionAsked_shouldReturnExpectedAnswer() {
        //arrange
        String question = "What is the capital of Germany?";

        //act
        Flux<String> result = chatService.streamAnswer(question);

        //assert
        StepVerifier.create(result)
                .expectNext("Berlin")
                .verifyComplete();

        verify(mockWebClient).post();
    }

    @Test
    public void testStreamAnswer_whenErrorOccurred_shouldThrowError() {
        //arrange
        String question = "What is the capital of Germany?";
        when(mockResponseSpec.bodyToFlux(String.class))
                .thenReturn(Flux.error(new RuntimeException("LLM server error")));

        //act
        Flux<String> result = chatService.streamAnswer(question);

        //assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("LLM server error"))
                .verify();
    }

}
