package com.example.messaging_stomp_websocket.Answers;

public class Answer {
    private String answer;
    private String submitter;
    private int votes;

    public Answer(String answer, String submitter) {
        this.answer = answer;
        this.submitter = submitter;
        this.votes = 0;
    }

    public String getAnswer() {
        return answer;
    }
    public String getSubmitter() {
        return submitter;
    }
    public int getVotes() {
        return votes;
    }
    public void setVotes(int inc) {
        votes = inc;
    }
}
