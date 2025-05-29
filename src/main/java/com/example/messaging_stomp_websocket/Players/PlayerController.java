package com.example.messaging_stomp_websocket.Players;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.messaging_stomp_websocket.MessageContent;

@Controller
public class PlayerController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public static ArrayList<Player> playerList = new ArrayList<>();
    private static int playerId = 0;

    public static int getUniquePID() {
        int pid = playerId;
        playerId++;
        return pid;
    }

    public static Player findPlayerByPID(int pid) {
        for (Player player : playerList) {
            if (player.getPID() == pid) {
                return player;
            }
        }
        return null;
    }

    @MessageMapping("/join")
    @SendTo("/topic/joined")
    public MessageContent player(@Payload JoiningPlayer joiningPlayer, Principal principal) throws Exception {
        Player a = new Player(joiningPlayer.getName(), Integer.parseInt(principal.getName()));
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getName().equals(a.getName())) {
                System.out.println("kicking " + playerList.get(i).getName());
                simpMessagingTemplate.convertAndSendToUser(Integer.toString(playerList.get(i).getPID()), "/topic/main",
                        new MessageContent("kicked"));
                playerList.remove(i);
                break;
            }
        }
        System.out.println("Player Joined: " + joiningPlayer.getName() + " PID: " + a.getPID());
        playerList.add(a);
        // for (Player player : playerList) {
        // System.out.println(player.getName() + "," + player.getPID());
        // }
        return new MessageContent(a.getName() + " has joined!", Integer.toString(a.getPID()));
    }

    @MessageMapping("/kickPlayer")
    public MessageContent kickPlayer(MessageContent msg) throws Exception {

        System.out.println(msg.getContent());

        int pid = Integer.parseInt(msg.getContent());
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getPID() == pid) {
                String playerName = playerList.get(i).getName();
                playerList.remove(i);
                return new MessageContent(playerName + " kicked sucessfully");
            }
        }
        return new MessageContent("Player not found");
    }
}
