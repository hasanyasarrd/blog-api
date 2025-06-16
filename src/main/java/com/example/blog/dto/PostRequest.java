package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequest {
    @NotBlank
    @Size(min = 5, max = 200)
    private String title;
    
    @NotBlank
    @Size(min = 10)
    private String content;
    
    public PostRequest() {}
    
    public PostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
