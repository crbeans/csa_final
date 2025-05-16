package com.example.messaging_stomp_websocket.Answers;

import java.util.ArrayList;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.messaging_stomp_websocket.MessageContent;

@Controller
public class AnswerController {
    ArrayList<Answer> answerList = new ArrayList<>();

    @MessageMapping("/submit")
    public void player(Answer ans) throws Exception {
        if(ans.getAnswer() == null) {return;}
        System.out.println("got answer " + ans.getAnswer() + ". from " + ans.getSubmitter());
        answerList.add(ans);
        for (Answer answer : answerList) {
            System.out.println(answer.getAnswer() + " by " + answer.getSubmitter());
        }
    }
}