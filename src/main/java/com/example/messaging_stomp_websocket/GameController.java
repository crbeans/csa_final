package com.example.messaging_stomp_websocket;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.messaging_stomp_websocket.Players.Player;
import com.example.messaging_stomp_websocket.Players.PlayerController;

@Controller
public class GameController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private static ArrayList<Player> editPlayerList;

    private static int lastPlayerIndex = 0;

    @MessageMapping("/startGame")
    @SendTo("/topic/main")
    public MessageContent startGame(MessageContent msg) throws Exception {
        if (msg.getContent().equals("123")) {
            editPlayerList = new ArrayList<>();
            for (Player player : PlayerController.playerList) {
                editPlayerList.add(player);
            }
            return new MessageContent("gamestart");
        }
        return new MessageContent("none");
    }

    @MessageMapping("/manageGame")
    @SendTo("/topic/main")
    public MessageContent manageGame(MessageContent msg) throws Exception {
        String content = msg.getContent();
        String data = msg.getData();
        if (content.equals("getplayersjson")) {
            ArrayList<Player> playerList = PlayerController.playerList;
            JSONArray playerListJSON = new JSONArray();
            for (Player player : playerList) {
                playerListJSON.add(player.toJSON());
            }
            return new MessageContent(playerListJSON.toJSONString());
        } else if (content.equals("startround")) {
            System.out.println("starting round");
            ArrayList<Player> playerList = PlayerController.playerList;
            ArrayList<Player> selectedPlayers = new ArrayList<>();
            if (playerList.size() <= 8) {
                for (Player player : playerList) {
                    selectedPlayers.add(player);
                }
            } else {
                for (int i = 0; i < 8; i++) {
                    selectedPlayers.add(editPlayerList.get(i));
                    editPlayerList.remove(i);
                    i--;
                }
            }

            System.out.println(selectedPlayers);

            for (Player player : selectedPlayers) {
                simpMessagingTemplate.convertAndSendToUser(Integer.toString(player.getPID()), "/topic/main",
                        "answerprompt");
            }
        }
        return null;
    }
}
