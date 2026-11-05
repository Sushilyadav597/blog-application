package com.blog.repository;

import com.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Post entity persistence operations.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE " +
            "(:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:content IS NULL OR LOWER(p.content) LIKE LOWER(CONCAT('%', :content, '%'))) AND " +
            "(:authorId IS NULL OR p.author.id = :authorId)")
    Page<Post> searchPosts(
            @Param("title") String title,
            @Param("content") String content,
            @Param("authorId") Long authorId,
            Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Post> findByAuthorId(Long authorId, Pageable pageable);
}
