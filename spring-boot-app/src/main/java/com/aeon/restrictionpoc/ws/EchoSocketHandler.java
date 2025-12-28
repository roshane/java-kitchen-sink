package com.aeon.restrictionpoc.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class EchoSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(EchoSocketHandler.class);

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        logger.info("TextMessage {}: {}", session.getId(), message.getPayload());
        var msg = new TextMessage("message received: %s".formatted(message.getPayload()));
        try {
            session.sendMessage(msg);
        } catch (IOException ex) {
            logger.error("Error", ex);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        logger.info("ConnectionClosed {}: {}", session.getId(), status);
    }
}
