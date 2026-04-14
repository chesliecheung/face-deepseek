package edu.kust.facedeepseek.controller;

import edu.kust.facedeepseek.dto.AiRequest;
import edu.kust.facedeepseek.dto.ApiResponse;
import edu.kust.facedeepseek.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/complete")
    public ApiResponse<String> complete(@RequestBody AiRequest req){
        try {
            String data = aiService.complete(req.getAction(), req.getText());
            return ApiResponse.ok(data);
        } catch (Exception e){
            return ApiResponse.fail(e.getMessage());
        }
    }
}
