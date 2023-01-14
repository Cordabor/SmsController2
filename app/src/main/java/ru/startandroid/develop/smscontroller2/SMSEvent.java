package ru.startandroid.develop.smscontroller2;

public class SMSEvent {
    private String message;
    private String sender;
    private String timestamp;

    public SMSEvent(String message, String sender, String timestamp) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getTimestamp() {
        return timestamp;
    }
}