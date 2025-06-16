package com.example.blog.controller;

import com.example.blog.dto.PostRequest;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/posts")
@Tag(name = "Posts", description = "Blog post management endpoints")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @GetMapping
    @Operation(summary = "Get all posts", description = "Retrieve all blog posts with their authors")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get post by ID", description = "Retrieve a specific post with comments")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        if (post.isPresent()) {
            return ResponseEntity.ok(post.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    @Operation(summary = "Create a new post", description = "Create a new blog post")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostRequest postRequest,
                                       @Parameter(hidden = true) Authentication authentication) {
        try {
            System.out.println("=== POST CREATE DEBUG ===");
            
            // Manual authentication check
            if (authentication == null) {
                authentication = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("Got authentication from SecurityContext: " + (authentication != null));
            }
            
            if (authentication == null) {
                System.out.println("Authentication is null!");
                return ResponseEntity.badRequest().body("Error: Authentication is null");
            }
            
            System.out.println("Authentication type: " + authentication.getClass().getSimpleName());
            System.out.println("Is authenticated: " + authentication.isAuthenticated());
            System.out.println("Principal type: " + authentication.getPrincipal().getClass().getSimpleName());
            
            User currentUser = (User) authentication.getPrincipal();
            System.out.println("Current user: " + currentUser.getUsername());
            
            Post post = postService.createPost(
                    postRequest.getTitle(),
                    postRequest.getContent(),
                    currentUser
            );
            
            System.out.println("Post created successfully with ID: " + post.getId());
            return ResponseEntity.ok(post);
            
        } catch (Exception e) {
            System.out.println("Exception in createPost: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a post", description = "Update an existing post (only by author)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                       @Valid @RequestBody PostRequest postRequest,
                                       @Parameter(hidden = true) Authentication authentication) {
        try {
            if (authentication == null) {
                authentication = SecurityContextHolder.getContext().getAuthentication();
            }
            
            User currentUser = (User) authentication.getPrincipal();
            Post updatedPost = postService.updatePost(
                    id,
                    postRequest.getTitle(),
                    postRequest.getContent(),
                    currentUser
            );
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post", description = "Delete a post (only by author)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deletePost(@PathVariable Long id,
                                       @Parameter(hidden = true) Authentication authentication) {
        try {
            if (authentication == null) {
                authentication = SecurityContextHolder.getContext().getAuthentication();
            }
            
            User currentUser = (User) authentication.getPrincipal();
            postService.deletePost(id, currentUser);
            return ResponseEntity.ok("Post deleted successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
