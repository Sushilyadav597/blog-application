package com.blog.controller;

import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.dto.response.UserSummaryResponse;
import com.blog.entity.Role;
import com.blog.entity.User;
import com.blog.security.CustomUserDetailsService;
import com.blog.security.JwtTokenProvider;
import com.blog.security.UserPrincipal;
import com.blog.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Comment Controller Integration Tests")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPrincipal commenterPrincipal;
    private CommentResponse sampleCommentResponse;

    @BeforeEach
    void setUp() {
        User commenter = User.builder()
                .id(1L)
                .username("sushil")
                .email("sushil@example.com")
                .role(Role.ROLE_USER)
                .build();
        commenterPrincipal = UserPrincipal.create(commenter);

        UserSummaryResponse authorSummary = UserSummaryResponse.builder()
                .id(1L)
                .username("sushil")
                .email("sushil@example.com")
                .role(Role.ROLE_USER)
                .build();

        sampleCommentResponse = CommentResponse.builder()
                .id(50L)
                .content("Insightful post!")
                .postId(1L)
                .author(authorSummary)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/posts/{postId}/comments should be publicly accessible")
    void shouldAllowPublicGetCommentsForPost() throws Exception {
        when(commentService.getCommentsByPostId(1L)).thenReturn(List.of(sampleCommentResponse));

        mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(50))
                .andExpect(jsonPath("$[0].content").value("Insightful post!"));
    }

    @Test
    @DisplayName("GET /api/comments/{id} should be publicly accessible")
    void shouldAllowPublicGetSingleComment() throws Exception {
        when(commentService.getCommentById(50L)).thenReturn(sampleCommentResponse);

        mockMvc.perform(get("/api/comments/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.content").value("Insightful post!"));
    }

    @Test
    @DisplayName("POST /api/posts/{postId}/comments should return 401 Unauthorized when unauthenticated")
    void shouldRejectUnauthenticatedCommentCreation() throws Exception {
        CommentRequest request = CommentRequest.builder().content("Unauthenticated comment").build();

        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/posts/{postId}/comments should return 201 Created when authenticated")
    void shouldCreateCommentWhenAuthenticated() throws Exception {
        CommentRequest request = CommentRequest.builder().content("Insightful post!").build();
        when(commentService.createComment(eq(1L), any(CommentRequest.class), any(UserPrincipal.class)))
                .thenReturn(sampleCommentResponse);

        mockMvc.perform(post("/api/posts/1/comments")
                        .with(user(commenterPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.content").value("Insightful post!"));
    }

    @Test
    @DisplayName("PUT /api/comments/{id} should return 403 Forbidden when user is not the owner")
    void shouldReturnForbiddenWhenNotCommentOwner() throws Exception {
        CommentRequest request = CommentRequest.builder().content("Updated comment").build();
        when(commentService.updateComment(eq(50L), any(CommentRequest.class), any(UserPrincipal.class)))
                .thenThrow(new AccessDeniedException("You do not have permission to update this comment."));

        mockMvc.perform(put("/api/comments/50")
                        .with(user(commenterPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("DELETE /api/comments/{id} should return 200 OK when deleted by owner")
    void shouldDeleteCommentSuccessfully() throws Exception {
        doNothing().when(commentService).deleteComment(eq(50L), any(UserPrincipal.class));

        mockMvc.perform(delete("/api/comments/50")
                        .with(user(commenterPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Comment deleted successfully"));
    }
}

