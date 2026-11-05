package com.blog.mapper;

import com.blog.dto.response.PostResponse;
import com.blog.entity.Post;
import org.springframework.stereotype.Component;

/**
 * Mapper utility component to convert Post entity to PostResponse DTO.
 */
@Component
public class PostMapper {

    private final UserMapper userMapper;

    public PostMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public PostResponse toPostResponse(Post post) {
        if (post == null) {
            return null;
        }
        long commentCount = post.getComments() != null ? post.getComments().size() : 0;

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(userMapper.toUserSummaryResponse(post.getAuthor()))
                .commentCount(commentCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}

