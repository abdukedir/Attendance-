package com.example.myapplication;

public class AttendanceLog {

    private String userId;
    private String sessionId;
    private long timestamp;

    // Constructor
    public AttendanceLog(String userId, String sessionId, long timestamp) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
