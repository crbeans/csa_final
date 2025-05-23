package com.example.messaging_stomp_websocket.Players;

import org.json.simple.JSONObject;

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

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("name", name);
    json.put("points", points);
    json.put("pid", pid);
    return json;
  }
}