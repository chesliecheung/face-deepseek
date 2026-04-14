package edu.kust.facedeepseek.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kust.facedeepseek.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/user-info")
public class ChatController {

    @Autowired
    public ChatService chatService;

    @PostMapping("/chats")
    public String chat(@RequestBody String question){

        //message这个对象

        if(question.equals("")|| Objects.isNull(question)){
            return "fail";
        }

        try {
            return chatService.chat(question);
        } catch (JsonProcessingException e) {
            return "erro";
        }


    }


}
