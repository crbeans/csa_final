package com.example.messaging_stomp_websocket;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.example.messaging_stomp_websocket.Players.PlayerController;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        String pid = Integer.toString(PlayerController.getUniquePID());

        // System.out.println(pid);

        return new Principal() {
            @Override
            public String getName() {
                return pid;
            }
        };
    }
}