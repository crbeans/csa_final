package com.example.messaging_stomp_websocket.Players;

public class Player {

  private String name;
  private int points;

  public Player() {
  }

  public Player(String name) {
    this.name = name;
    this.points = 0;
  }

  public String getName() {
    return name;
  }

  public void incrementPoints(int pts) {
    points += pts;
  }

  public int getPoints() {
    return points;
  }
}