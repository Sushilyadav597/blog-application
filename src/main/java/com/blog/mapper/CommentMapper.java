package com.blog.mapper;

import com.blog.dto.response.CommentResponse;
import com.blog.entity.Comment;
import org.springframework.stereotype.Component;

/**
 * Mapper utility component to convert Comment entity to CommentResponse DTO.
 */
@Component
public class CommentMapper {

    private final UserMapper userMapper;

    public CommentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public CommentResponse toCommentResponse(Comment comment) {
        if (comment == null) {
            return null;
        }
        Long postId = comment.getPost() != null ? comment.getPost().getId() : null;

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .postId(postId)
                .author(userMapper.toUserSummaryResponse(comment.getAuthor()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
