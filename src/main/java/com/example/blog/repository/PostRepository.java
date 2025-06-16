package com.example.blog.repository;

import com.example.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
    
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author ORDER BY p.createdAt DESC")
    List<Post> findAllWithAuthor();
    
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.comments c LEFT JOIN FETCH c.user WHERE p.id = :id")
    Optional<Post> findByIdWithCommentsAndUsers(Long id);
}
