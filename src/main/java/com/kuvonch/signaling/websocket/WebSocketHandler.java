package com.kuvonch.signaling.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebSocketHandler extends TextWebSocketHandler {
    private final List<User> users = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("New connection established: " + session.getId());
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) data.get("type");
            String username = (String) data.get("username");
            String target = (String) data.get("target");
            User currentUser = findUser(username);
            User userToReceive = findUser(target);

            System.out.println(data);

            switch (type) {
                case "SignIn":
                    if (currentUser == null) {
                        users.add(new User(username, session));
                    }
                    break;
                case "StartStreaming":
                    if (userToReceive != null) {
                        sendToConnection(userToReceive.getSession(), data);
                    }
                    break;
                case "Offer":
                    if (userToReceive != null) {
                        sendToConnection(userToReceive.getSession(), data);
                    }
                    break;
                case "Answer":
                    if (userToReceive != null) {
                        sendToConnection(userToReceive.getSession(), data);
                    }
                    break;
                case "IceCandidates":
                    if (userToReceive != null) {
                        sendToConnection(userToReceive.getSession(), data);
                    }
                    break;
                case "EndCall":
                    if (userToReceive != null) {
                        sendToConnection(userToReceive.getSession(), data);
                    }
                    break;
                case "RemoteControl":
                    if (userToReceive != null) {
                        sendToConnection(userToReceive.getSession(), data);
                    }
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error handling message: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        users.removeIf(user -> user.getSession().equals(session));
        System.out.println("Connection closed: " + session.getId());
    }

    private void sendToConnection(WebSocketSession session, Map<String, Object> message) throws Exception {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
    }

    private User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private static class User {
        private final String username;
        private final WebSocketSession session;

        public User(String username, WebSocketSession session) {
            this.username = username;
            this.session = session;
        }

        public String getUsername() {
            return username;
        }

        public WebSocketSession getSession() {
            return session;
        }
    }
}
