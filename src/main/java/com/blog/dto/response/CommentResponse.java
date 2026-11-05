package com.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a comment on a blog post.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Blog comment response payload")
public class CommentResponse {

    @Schema(description = "Unique comment identifier", example = "1")
    private Long id;

    @Schema(description = "Comment content text", example = "Great article, very helpful!")
    private String content;

    @Schema(description = "ID of the associated post", example = "1")
    private Long postId;

    @Schema(description = "Author summary")
    private UserSummaryResponse author;

    @Schema(description = "Comment creation timestamp", example = "2026-11-05T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Comment last modification timestamp", example = "2026-11-05T12:00:00")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public UserSummaryResponse getAuthor() {
        return author;
    }

    public void setAuthor(UserSummaryResponse author) {
        this.author = author;
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

