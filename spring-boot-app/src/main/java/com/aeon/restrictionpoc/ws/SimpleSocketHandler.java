package com.aeon.restrictionpoc.ws;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public final class SimpleSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final Consumer<SouthboundResponse> responseConsumer;

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        log.info("handleMessage: {}", message.getPayload());
        try {
            SouthboundResponse response = objectMapper.readValue(
                    message.getPayload().toString(),
                    SouthboundResponse.class
            );
            responseConsumer.accept(response);
        } catch (Exception ex) {
            log.error("Error handling message: {}", message, ex);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        log.info("afterConnectionEstablished: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        log.info("afterConnectionClosed");
    }
}