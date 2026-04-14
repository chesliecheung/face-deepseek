package edu.kust.facedeepseek.dto;

import lombok.Data;

@Data
public class AiRequest {
    private String action;  // 生成大纲|优化表达|续写内容|生成摘要|检查语法
    private String text;    // 编辑器内容或标题
}
