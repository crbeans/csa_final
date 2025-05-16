package com.example.messaging_stomp_websocket.Players;

import java.util.ArrayList;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.messaging_stomp_websocket.MessageContent;

@Controller
public class PlayerController {
    public static ArrayList<Player> playerList = new ArrayList<>();

    @MessageMapping("/join")
    @SendTo("/topic/joined")
    public MessageContent player(JoiningPlayer joiningPlayer) throws Exception {
        Player a = new Player(joiningPlayer.getName());
        playerList.add(a);
        System.out.println("Player Joined: "+joiningPlayer.getName());
        // onPlayerJoin(a);
        return new MessageContent(a.getName() + " has joined!");
    }
}
