package com.example.messaging_stomp_websocket.Answers;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.messaging_stomp_websocket.GameController;
import com.example.messaging_stomp_websocket.MessageContent;
import com.example.messaging_stomp_websocket.Players.Player;

@Controller
public class AnswerController {
    private static ArrayList<Answer> answerList = new ArrayList<>();

    // @MessageMapping("/submit")
    // public void player(@Payload RecAnswer ans, Principal principal) throws
    // Exception {
    // if (ans.getAnswer() == null) {
    // return;
    // }
    // System.out.println("got answer " + ans.getAnswer() + ". from " +
    // ans.getSubmitter());
    // answerList.add(new Answer(ans.getAnswer(), ans.getSubmitter(),
    // Integer.parseInt(principal.getName())));
    // }

    public static void clearAnswerList() {
        answerList = new ArrayList<>();
    }

    public static ArrayList<Answer> getAnswerList() {
        return new ArrayList<>(answerList);
    }
}