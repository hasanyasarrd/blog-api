package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequest {
    @NotBlank
    @Size(min = 1, max = 1000)
    private String content;
    
    public CommentRequest() {}
    
    public CommentRequest(String content) {
        this.content = content;
    }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
