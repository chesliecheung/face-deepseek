package edu.kust.facedeepseek.entity;
import lombok.Data;

import java.util.List;


@Data
public class ChatRequest {

    //需要的模型
    private String model;
    // 需要的信息
    private List<Message> messages;
    //流式模式
    private boolean stream =false;


    @Data
    public  static  class  Message{// 内部类
        //确定需要的角色是什么
        private String role;
        // 你的问答内容
        private String content;

    }


}