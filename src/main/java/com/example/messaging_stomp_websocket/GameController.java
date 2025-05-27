package com.example.messaging_stomp_websocket;

import java.lang.reflect.Array;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.messaging_stomp_websocket.Answers.Answer;
import com.example.messaging_stomp_websocket.Answers.AnswerController;
import com.example.messaging_stomp_websocket.Answers.RecAnswer;
import com.example.messaging_stomp_websocket.Players.Player;
import com.example.messaging_stomp_websocket.Players.PlayerController;

@Controller
public class GameController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private static ArrayList<Player> editPlayerList;

    private static int lastPlayerIndex = 0;

    private static ArrayList<String> promptList = new ArrayList<>(Arrays.asList(
            "The worst thing to hear during a job interview.",
            "The secret ingredient in grandma's mystery casserole.",
            "What NOT to say on a first date.",
            "A bad excuse for being late to work.",
            "The strangest thing you'd find in a witch's purse.",
            "The most awkward thing to find in your boss's office.",
            "The worst thing to hear from your Uber driver.",
            "What your pet is secretly thinking about you right now.",
            "A terrible pickup line that actually worked once.",
            "The strangest law you'd make if you were president."));
    private static int lastPromptIndex = 0;

    private static final int PLAYERS_PER_ROUND = 4;

    private static ArrayList<Player> votersList;
    private static int recievedAnswers = 0;

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
            voteList = new int[4];
            votes = 0;

            if (playerList.size() <= PLAYERS_PER_ROUND) {
                for (Player player : playerList) {
                    selectedPlayers.add(player);
                }
                for (Player player : selectedPlayers) {
                    simpMessagingTemplate.convertAndSendToUser(Integer.toString(player.getPID()), "/topic/main",
                            new MessageContent("answerprompt", promptList.get(lastPromptIndex)));
                }
            } else {
                // get player list from PlayerController and get first PLAYERS_PER_ROUND players
                // and remove them
                // from editPlayerList
                editPlayerList = new ArrayList<>(PlayerController.playerList);
                int count = Math.min(PLAYERS_PER_ROUND, editPlayerList.size());
                List<Player> currentSelection = new ArrayList<>(editPlayerList.subList(0, count));
                selectedPlayers.addAll(currentSelection);
                editPlayerList.subList(0, count).clear(); // remove from editPlayerList

                for (Player player : currentSelection) {
                    simpMessagingTemplate.convertAndSendToUser(Integer.toString(player.getPID()), "/topic/main",
                            new MessageContent("answerprompt", promptList.get(lastPromptIndex)));
                }
                votersList = new ArrayList<>(editPlayerList);
                for (Player player : editPlayerList) {
                    simpMessagingTemplate.convertAndSendToUser(Integer.toString(player.getPID()), "/topic/main",
                            new MessageContent("voting"));
                }
            }

            System.out.println(selectedPlayers);

            lastPromptIndex++;
        }
        return null;
    }

    private static ArrayList<Answer> answerList = new ArrayList<>();

    @MessageMapping("/submit")
    public void player(@Payload RecAnswer ans, Principal principal) throws Exception {
        if (ans.getAnswer() == null) {
            return;
        }
        System.out.println("got answer " + ans.getAnswer() + ". from " + ans.getSubmitter());
        answerList.add(new Answer(ans.getAnswer(), ans.getSubmitter(), Integer.parseInt(principal.getName())));
        monitorSubmissions();
    }

    public void monitorSubmissions() {
        recievedAnswers++;
        if (recievedAnswers == PLAYERS_PER_ROUND) {
            // ArrayList<Answer> answers = AnswerController.getAnswerList();
            for (Player player : votersList) {
                List<String> justAnswers = answerList.subList(0, 2).stream()
                        .map(Answer::getAnswer)
                        .collect(Collectors.toList());
                simpMessagingTemplate.convertAndSendToUser(Integer.toString(player.getPID()), "/topic/main",
                        new MessageContent("votingOn", JSONArray.toJSONString(justAnswers)));
            }
        }
    }

    public static int[] voteList = new int[4];
    public static int votes = 0;

    @MessageMapping("/vote")
    public void onVote(Vote vote) throws Exception {
        votes++;
        int option = vote.getOption();
        voteList[option - 1]++;
        System.out.println(voteList.toString());

        JSONObject json = new JSONObject();
        json.put("voter", vote.getSubmitter());
        json.put("option", option);

        simpMessagingTemplate.convertAndSend("/topic/voting", new MessageContent("vote", json.toJSONString()));
        if (votes == votersList.size()) {
            simpMessagingTemplate.convertAndSend("/topic/voting", new MessageContent("votingend"));
        }
    }
}
