package com.example.TraSeApp;

public class MessageEvent {
    public static final String FROM_A_TO_B = "FROM_A_TO_B";
    public static final String FROM_B_TO_A = "FROM_B_TO_A";
    public String message;
    public String type;

    public MessageEvent(String message, String type) {
        this.message = message;
        this.type = type;
    }
}
