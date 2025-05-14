package com.example.messaging_stomp_websocket;

import java.util.ArrayList;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class PlayerController {
    private static ArrayList<Player> playerList = new ArrayList<>();

    @MessageMapping("/join")
    @SendTo("topic/joined")
    public Player player(JoiningPlayer joiningPlayer) throws Exception {
        Player a = new Player(joiningPlayer.getName());
        playerList.add(a);
        System.out.println("Joined");
        System.out.println(playerList);
        return a;
    }
}
