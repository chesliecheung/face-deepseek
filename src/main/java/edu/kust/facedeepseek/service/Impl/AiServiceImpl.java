package edu.kust.facedeepseek.service.Impl;
import edu.kust.facedeepseek.dto.ChatRequest;
import edu.kust.facedeepseek.dto.ChatResponse;
import edu.kust.facedeepseek.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    @Value("${deepseek.api.url}")
    private String url;

    @Value("${deepseek.api.model}")
    private String model;

    private final RestTemplate rest = new RestTemplate();

    @Override
    public String complete(String action, String text) {
        String system = "你是中文写作助手，请严格按用户意图输出，不添加无关解释。";
        String userPrefix;

        switch (action) {
            case "生成大纲":
                userPrefix = "生成分级标题大纲，用\\n-和编号分层，含各级核心要点简述，不额外说明：\\n";
                break;
            case "优化表达":
                userPrefix = "保留原意润色文本，补充细节增强可读性，直接输出优化后全文：\\n";
                break;
            case "续写内容":
                userPrefix = "基于文本续写4-6段，展开情节/观点，增细节和深度，保持语气：\\n";
                break;
            case "生成摘要":
                userPrefix = "生成200-300字详细摘要，含主要内容、关键细节、核心观点和结论，简练全面：\\n";
                break;
            case "检查语法":
                userPrefix = "指出中文文本语法/用词问题，说明原因，提供修改后版本（可补充使表达更充分）：\\n";
                break;
            default:
                userPrefix = "详细处理以下文本，充分展开内容，输出丰富完整结果：\\n";
        }

        ChatRequest req = new ChatRequest();
        req.setModel(model);

        List<ChatRequest.Message> list = new ArrayList<>();
        ChatRequest.Message sys = new ChatRequest.Message();
        sys.setRole("system");
        sys.setContent(system);
        list.add(sys);

        ChatRequest.Message usr = new ChatRequest.Message();
        usr.setRole("user");
        usr.setContent(userPrefix + text);
        list.add(usr);

        req.setMessages(list);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(req, headers);
        ChatResponse resp = rest.postForObject(url, entity, ChatResponse.class);

        if (resp == null || resp.getMessage() == null) {
            throw new RuntimeException("AI 服务无响应");
        }
        return resp.getMessage().getContent();
    }
}
