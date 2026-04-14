package edu.kust.facedeepseek.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatRequest {
    private String model;
    private List<Message> messages;
    private boolean stream = false;

    @Data
    public static class Message {
        private String role;    // system | user | assistant
        private String content;
    }
}
