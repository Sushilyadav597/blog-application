package com.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a public blog post.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Blog post response payload")
public class PostResponse {

    @Schema(description = "Unique post identifier", example = "1")
    private Long id;

    @Schema(description = "Post title", example = "Getting Started with Spring Boot 3")
    private String title;

    @Schema(description = "Post content", example = "Spring Boot 3 introduces powerful capabilities...")
    private String content;

    @Schema(description = "Author summary")
    private UserSummaryResponse author;

    @Schema(description = "Total number of comments", example = "5")
    private long commentCount;

    @Schema(description = "Post creation timestamp", example = "2026-11-05T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Post last modification timestamp", example = "2026-11-05T12:00:00")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserSummaryResponse getAuthor() {
        return author;
    }

    public void setAuthor(UserSummaryResponse author) {
        this.author = author;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
