package com.example.messaging_stomp_websocket;

import java.util.ArrayList;

import com.example.messaging_stomp_websocket.Answers.Answer;
import com.example.messaging_stomp_websocket.Players.Player;
import com.example.messaging_stomp_websocket.Players.PlayerController;

public class GameController {
    private boolean gameStarted = false;
    private ArrayList<Player> playerList;
    private ArrayList<Answer> answerList;

    public GameController() {
        this.playerList = PlayerController.playerList;
        this.answerList = new ArrayList<>();
    }

    
}
