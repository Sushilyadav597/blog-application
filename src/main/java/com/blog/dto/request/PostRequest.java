package com.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for creating or updating a blog post.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload required to create or modify a blog post")
public class PostRequest {

    @NotBlank(message = "Post title cannot be empty")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    @Schema(description = "Title of the blog post", example = "Getting Started with Spring Boot 3 and Java 21")
    private String title;

    @NotBlank(message = "Post content cannot be empty")
    @Schema(description = "Full markdown or text content of the post", example = "Spring Boot 3 introduces powerful capabilities...")
    private String content;

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
}

