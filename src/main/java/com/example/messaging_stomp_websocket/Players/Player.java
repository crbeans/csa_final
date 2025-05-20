package com.example.messaging_stomp_websocket.Players;

public class Player {

  private String name;
  private int points;
  private int pid;

  public Player() {
  }

  public Player(String name, int pid) {
    this.name = name;
    this.points = 0;
    this.pid = pid;
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

  public int getPID() {
    return pid;
  }
}