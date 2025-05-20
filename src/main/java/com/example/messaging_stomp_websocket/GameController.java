package com.example.messaging_stomp_websocket;

import java.util.ArrayList;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import com.example.messaging_stomp_websocket.Answers.Answer;
import com.example.messaging_stomp_websocket.Players.Player;
import com.example.messaging_stomp_websocket.Players.PlayerController;

public class GameController {

    public GameController() {
    }

    @MessageMapping("/startGame")
    @SendTo("/topic/main")
    public MessageContent startGame() throws Exception{
        return new MessageContent("gamestart");
    }
}
