package edu.kust.facedeepseek.dto;

import lombok.Data;

@Data
public class ChatResponse {
    private String model;
    private String created_at;
    private Message message;
    private boolean done;

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
