package com.example.messaging_stomp_websocket;

public class JoiningPlayer {

  private String name;

  public JoiningPlayer() {
  }

  public JoiningPlayer(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}