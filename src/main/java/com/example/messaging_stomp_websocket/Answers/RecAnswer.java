package com.example.messaging_stomp_websocket.Answers;

public class RecAnswer {
    private String answer;
    private String submitter;

    public RecAnswer(String answer, String submitter) {
        this.answer = answer;
        this.submitter = submitter;
    }

    public String getAnswer() {
        return answer;
    }

    public String getSubmitter() {
        return submitter;
    }
}