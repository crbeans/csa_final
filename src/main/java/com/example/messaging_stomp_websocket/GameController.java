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

import jakarta.annotation.PostConstruct;

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
        } else {
            return new MessageContent("gamestart");
        }
    }

    private static int playersThisRound = 0;

    private static ArrayList<Player> usedPlayers = new ArrayList<>();

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
            ArrayList<Player> selectedPlayers = new ArrayList<>();
            voteList = new int[4];
            votes = 0;
            if (editPlayerList.size() <= PLAYERS_PER_ROUND) {
                System.out.println("here");

                playersThisRound = editPlayerList.size();

                for (Player player : editPlayerList) {
                    selectedPlayers.add(player);
                }

                votersList = new ArrayList<>(usedPlayers);
                for (Player player : usedPlayers) {
                    simpMessagingTemplate.convertAndSendToUser(Integer.toString(player.getPID()), "/topic/main",
                            new MessageContent("voting"));
                }

                for (Player player : selectedPlayers) {
                    simpMessagingTemplate.convertAndSendToUser(Integer.toString(player.getPID()), "/topic/main",
                            new MessageContent("answerprompt", promptList.get(lastPromptIndex)));
                    usedPlayers.add(player);
                }
            } else {
                System.out.println("there");
                // get player list from PlayerController and get first PLAYERS_PER_ROUND players
                // and remove them
                // from editPlayerList
                // editPlayerList = new ArrayList<>(PlayerController.playerList);
                int count = Math.min(PLAYERS_PER_ROUND, editPlayerList.size());
                playersThisRound = count;
                List<Player> currentSelection = new ArrayList<>(editPlayerList.subList(0, count));
                selectedPlayers.addAll(currentSelection);
                editPlayerList.subList(0, count).clear(); // remove from editPlayerList
                // for (int i = 0; i < count; i++) {
                // editPlayerList.remove(i);
                // i--;
                // }

                votersList = new ArrayList<>(editPlayerList);
                for (Player player : editPlayerList) {
                    simpMessagingTemplate.convertAndSendToUser(Integer.toString(player.getPID()), "/topic/main",
                            new MessageContent("voting"));
                }
                for (Player player : usedPlayers) {
                    simpMessagingTemplate.convertAndSendToUser(Integer.toString(player.getPID()), "/topic/main",
                            new MessageContent("voting"));
                }
                for (Player player : currentSelection) {
                    simpMessagingTemplate.convertAndSendToUser(Integer.toString(player.getPID()), "/topic/main",
                            new MessageContent("answerprompt", promptList.get(lastPromptIndex)));
                    usedPlayers.add(player);
                }
            }

            lastPromptIndex++;
        } else if (content.equals("resetplayerlist12344")) {
            System.out.println("resetting all lists");
            editPlayerList = new ArrayList<>();
            votersList = new ArrayList<>();
            answerList = new ArrayList<>();
            voteList = new int[4];
            playersThisRound = 0;
        } else if (content.equals("newround")) {
            System.out.println("empying tables for new round");
            votersList = new ArrayList<>();
            answerList = new ArrayList<>();
            voteList = new int[4];
            playersThisRound = 0;
            recievedAnswers = 0;
        } else if (content.equals("initEditList")) {
            System.out.println("init edit list");
            editPlayerList = new ArrayList<>(PlayerController.playerList);
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
        if (recievedAnswers == playersThisRound) {
            // ArrayList<Answer> answers = AnswerController.getAnswerList();
            for (Player player : votersList) {
                List<String> justAnswers = answerList.stream()
                        .map(Answer::getAnswer)
                        .collect(Collectors.toList());
                List<String> justSubmitters = answerList.stream()
                        .map(Answer::getSubmitter)
                        .collect(Collectors.toList());
                simpMessagingTemplate.convertAndSendToUser(Integer.toString(player.getPID()), "/topic/main",
                        new MessageContent("votingOn", JSONArray.toJSONString(justAnswers)));
                simpMessagingTemplate.convertAndSend("/topic/main",
                        new MessageContent("votingOnPrompt", promptList.get(lastPromptIndex - 1)));
                simpMessagingTemplate.convertAndSend("/topic/voting",
                        new MessageContent("votingOn", JSONArray.toJSONString(justAnswers)));
                simpMessagingTemplate.convertAndSend("/topic/voting",
                        new MessageContent("votingOnSubmitters", JSONArray.toJSONString(justSubmitters)));

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
        System.out.println(Arrays.toString(voteList));

        JSONObject json = new JSONObject();
        json.put("voter", vote.getSubmitter());
        json.put("option", option);

        simpMessagingTemplate.convertAndSend("/topic/voting", new MessageContent("vote", json.toJSONString()));
        if (votes == votersList.size()) {
            // simpMessagingTemplate.convertAndSend("/topic/voting", new
            // MessageContent("votingend"));
            onVotingComplete();
        }
    }

    public void onVotingComplete() throws Exception {
        int maxVotes = 0;
        int[] voteListL = Arrays.copyOfRange(voteList, 0, playersThisRound);

        // Step 1: Find max vote count
        for (int votes : voteListL) {
            if (votes > maxVotes) {
                maxVotes = votes;
            }
        }

        // Step 2: Collect all indices with maxVotes
        List<Integer> winners = new ArrayList<>();
        for (int i = 0; i < voteListL.length; i++) {
            if (voteListL[i] == maxVotes) {
                winners.add(i);
            }
        }

        // Step 3: Report all winners
        for (int winnerIndex : winners) {
            Answer winnerAnswer = answerList.get(winnerIndex);
            System.out
                    .println(winnerAnswer.getSubmitter() + " won/tied! They said \"" + winnerAnswer.getAnswer() + "\"");
        }

        // Step 4: Send vote result data
        JSONArray voteCounts = new JSONArray();
        for (int number : voteListL) {
            voteCounts.add(number);
        }

        JSONArray winnerIndices = new JSONArray();
        for (int winnerIndex : winners) {
            winnerIndices.add(winnerIndex); // These are the indices of the tied winners
        }

        JSONObject json = new JSONObject();
        json.put("voteCounts", voteCounts);
        json.put("winners", winnerIndices);

        simpMessagingTemplate.convertAndSend("/topic/voting",
                new MessageContent("votingend", json.toJSONString()));
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("GameController initialized");
    }

}
