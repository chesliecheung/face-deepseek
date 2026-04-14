package edu.kust.facedeepseek.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ChatService {


    public String chat(String question) throws JsonProcessingException;

}
