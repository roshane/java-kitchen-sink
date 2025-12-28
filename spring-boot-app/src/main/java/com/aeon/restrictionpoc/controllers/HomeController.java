package com.aeon.restrictionpoc.controllers;

import com.aeon.restrictionpoc.dto.Message;
import com.aeon.restrictionpoc.dto.ResponseMessage;
import com.aeon.restrictionpoc.ws.SimpleSocketHandler;
import com.aeon.restrictionpoc.ws.SouthboundRequest;
import com.aeon.restrictionpoc.ws.SouthboundResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@RestController
public class HomeController {
    private final String webSocketUrl;
    private final WebSocketClient webSocketClient;
    private final SimpleSocketHandler simpleSocketHandler;
    private final ObjectMapper objectMapper;

    private WebSocketSession webSocketSession;
    private final Map<UUID, CompletableFuture<SouthboundResponse>> cache = new ConcurrentHashMap<>();

    public HomeController(@Value("${application.remote.socket-url}") String webSocketUrl,
                          WebSocketClient webSocketClient,
                          ObjectMapper objectMapper) {
        this.webSocketUrl = webSocketUrl;
        this.webSocketClient = webSocketClient;
        this.objectMapper = objectMapper;
        this.simpleSocketHandler = new SimpleSocketHandler(objectMapper, southboundResponse -> {
            var task = Objects.requireNonNull(cache.get(southboundResponse.getRequest().getMessage().getId()), "Cache miss");
            task.complete(southboundResponse);
        });
    }

    @PostConstruct
    void postConstruct() throws ExecutionException, InterruptedException, TimeoutException {
        webSocketClient.execute(simpleSocketHandler, webSocketUrl)
//                .thenApply(wss -> webSocketSession = new ConcurrentWebSocketSessionDecorator(wss, 100, 32))
                .thenApply(wss -> webSocketSession = wss)
                .get(3, TimeUnit.SECONDS);
    }

    @GetMapping
    public String health() throws SQLException {
        return "service is healthy";
    }

    @PostMapping
    public ResponseMessage sendMessage(@RequestBody Message message) {
        log.info("HTTP request received: {}", message);
        var wss = Objects.requireNonNull(webSocketSession, "session is not initialized");
        if (!wss.isOpen()) {
            throw new RuntimeException("socket closed");
        }
        var task = new CompletableFuture<SouthboundResponse>();
        cache.put(message.getId(), task);
        try {
            var requestMessage = new SouthboundRequest(message, Instant.now());
            synchronized (wss) {
                wss.sendMessage(new TextMessage(toJson(requestMessage)));
            }
            return task.thenApply(it -> new ResponseMessage(it.getRequest().getMessage(), it.getRequest().getRequestTime(), it.getProcessedTime()))
                    .get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    <T> String toJson(T message){
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
