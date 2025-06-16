package com.example.blog.service;

import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Cacheable("posts")
    public List<Post> getAllPosts() {
        return postRepository.findAllWithAuthor();
    }
    
    public Optional<Post> getPostById(Long id) {
        return postRepository.findByIdWithCommentsAndUsers(id);
    }
    
    @CacheEvict(value = "posts", allEntries = true)
    public Post createPost(String title, String content, User author) {
        Post post = new Post(title, content, author);
        return postRepository.save(post);
    }
    
    @CacheEvict(value = "posts", allEntries = true)
    public Post updatePost(Long id, String title, String content, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only edit your own posts");
        }
        
        post.setTitle(title);
        post.setContent(content);
        return postRepository.save(post);
    }
    
    @CacheEvict(value = "posts", allEntries = true)
    public void deletePost(Long id, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own posts");
        }
        
        postRepository.delete(post);
    }
}
