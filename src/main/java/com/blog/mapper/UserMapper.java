package com.blog.mapper;

import com.blog.dto.response.UserSummaryResponse;
import com.blog.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper utility component to convert User entity to safe UserSummaryResponse DTO.
 */
@Component
public class UserMapper {

    public UserSummaryResponse toUserSummaryResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
