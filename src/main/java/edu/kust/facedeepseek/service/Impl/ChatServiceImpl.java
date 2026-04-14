package edu.kust.facedeepseek.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kust.facedeepseek.entity.ChatRequest;
import edu.kust.facedeepseek.service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
@Service
public class ChatServiceImpl implements ChatService {
    @Value("${deepseek.api.model}")
    private String model;
    @Value("${deepseek.api.url}")
    private String url;

    @Override
    public String chat(String question) throws JsonProcessingException {

        // 新增：打印注入的model，看是否为null或空字符串

        //此业务方法处理逻辑 把从本项目页面获取到的内容 发送给deepseek，然后在把deepseek的信息返回给项目的前端页面
        ChatRequest.Message message = new ChatRequest.Message();
        message.setRole("user");
        message.setContent(question);

        ChatRequest chatRequest =new ChatRequest();
        chatRequest.setModel(model);
        chatRequest.setMessages(Collections.singletonList(message));//参考 javase 文档 集合篇

        //下面一步 把数据发送给 deepseek
        RestTemplate restTemplate =new RestTemplate(); //创建 http请求对象
        Map<Object,String>  maps= restTemplate.postForObject(url,chatRequest, Map.class); //发送的是post请求
        ObjectMapper objectMapper =new ObjectMapper();//处理json数据
        return objectMapper.writeValueAsString(maps);









    }
}
