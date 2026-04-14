package edu.kust.facedeepseek.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleRequest {
    private String title;
    private String content;
    private String coverImageUrl;
    private Long categoryId;
    private String summary;
    private String status; // draft, published, pending_review, taken_down
    private String visibility; // public, followers_only, private
    private String commentPermission; // everyone, followers_only, closed
    private LocalDateTime publishTime;
    private List<String> tags;
}