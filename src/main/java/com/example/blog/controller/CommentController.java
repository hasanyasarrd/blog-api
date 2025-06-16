package com.example.blog.controller;

import com.example.blog.dto.CommentRequest;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/posts")
@Tag(name = "Comments", description = "Comment management endpoints")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @PostMapping("/{id}/comments")
    @Operation(summary = "Add comment to post", description = "Add a new comment to a specific post")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> addComment(@PathVariable Long id,
                                       @Valid @RequestBody CommentRequest commentRequest,
                                       @Parameter(hidden = true) Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Comment comment = commentService.addComment(
                    id,
                    commentRequest.getContent(),
                    currentUser
            );
            return ResponseEntity.ok(comment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
