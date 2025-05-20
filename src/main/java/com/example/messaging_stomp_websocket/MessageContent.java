package com.example.messaging_stomp_websocket;

public class MessageContent {

  private String content = "";
  private String data = "";

  public MessageContent() {
  }

  public MessageContent(String content) {
    this.content = content;
  }
  public MessageContent(String content, String data) {
    this.content = content;
    this.data = data;
  }

  public String getContent() {
    return content;
  }

  public String getData() {
    return data;
  }

}