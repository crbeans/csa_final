package com.example.messaging_stomp_websocket;

public class Vote {
    private int option;
    private String submitter;

    public Vote(String option, String submitter) {
        this.option = Integer.parseInt(option);
        this.submitter = submitter;
    }

    public int getOption() {
        return option;
    }

    public String getSubmitter() {
        return submitter;
    }
}
