package com.example.messaging_stomp_websocket.Players;

import java.util.ArrayList;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.messaging_stomp_websocket.MessageContent;

@Controller
public class PlayerController {
    public static ArrayList<Player> playerList = new ArrayList<>();
    public static int playerId = 0;

    public static Player findPlayerByPID(int pid) {
        for (Player player : playerList) {
            if(player.getPID() == pid) {
                return player;
            }
        }
        return null;
    }

    @MessageMapping("/join")
    @SendTo("/topic/joined")
    public MessageContent player(JoiningPlayer joiningPlayer) throws Exception {
        Player a = new Player(joiningPlayer.getName(), playerId);
        playerId++;
        playerList.add(a);
        System.out.println("Player Joined: "+joiningPlayer.getName());
        // onPlayerJoin(a);
        for (Player player : playerList) {
            System.out.println(player.getName() + "," + player.getPID());
        }
        return new MessageContent(a.getName() + " has joined!", Integer.toString(a.getPID()));
    }

    @MessageMapping("/kickPlayer")
    public MessageContent kickPlayer(MessageContent msg) throws Exception {

        System.out.println(msg.getContent());

        int pid = Integer.parseInt(msg.getContent());
        for (int i = 0; i < playerList.size(); i++) {
            if(playerList.get(i).getPID() == pid) {
                String playerName = playerList.get(i).getName();
                playerList.remove(i);
                return new MessageContent(playerName + " kicked sucessfully");
            }
        }
        return new MessageContent("Player not found");
    }
}
