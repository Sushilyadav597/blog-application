package com.blog.controller;

import com.blog.dto.request.PostRequest;
import com.blog.dto.response.PagedResponse;
import com.blog.dto.response.PostResponse;
import com.blog.dto.response.UserSummaryResponse;
import com.blog.entity.Role;
import com.blog.entity.User;
import com.blog.exception.ResourceNotFoundException;
import com.blog.security.CustomUserDetailsService;
import com.blog.security.JwtTokenProvider;
import com.blog.security.UserPrincipal;
import com.blog.service.PostService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Post Controller Integration Tests")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPrincipal authorPrincipal;
    private PostResponse samplePostResponse;

    @BeforeEach
    void setUp() {
        User author = User.builder()
                .id(1L)
                .username("sushil")
                .email("sushil@example.com")
                .role(Role.ROLE_USER)
                .build();
        authorPrincipal = UserPrincipal.create(author);

        UserSummaryResponse authorSummary = UserSummaryResponse.builder()
                .id(1L)
                .username("sushil")
                .email("sushil@example.com")
                .role(Role.ROLE_USER)
                .build();

        samplePostResponse = PostResponse.builder()
                .id(1L)
                .title("Spring Boot 3 Exploration")
                .content("Deep dive into Spring Boot 3 with Java 21...")
                .author(authorSummary)
                .commentCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/posts should be publicly accessible without authentication")
    void shouldAllowPublicGetAllPosts() throws Exception {
        PagedResponse<PostResponse> pagedResponse = PagedResponse.<PostResponse>builder()
                .content(List.of(samplePostResponse))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(postService.getAllPosts(anyInt(), anyInt(), anyString(), anyString(), any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Spring Boot 3 Exploration"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/posts/{id} should return 200 OK for existing post")
    void shouldAllowPublicGetPostById() throws Exception {
        when(postService.getPostById(1L)).thenReturn(samplePostResponse);

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Spring Boot 3 Exploration"));
    }

    @Test
    @DisplayName("GET /api/posts/{id} should return 404 Not Found for non-existing post")
    void shouldReturn404WhenPostNotFound() throws Exception {
        when(postService.getPostById(99L)).thenThrow(new ResourceNotFoundException("Post", "id", 99L));

        mockMvc.perform(get("/api/posts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/posts should return 401 Unauthorized when unauthenticated")
    void shouldRejectUnauthenticatedPostCreation() throws Exception {
        PostRequest request = PostRequest.builder()
                .title("Unauthorized Post")
                .content("Content")
                .build();

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/posts should return 201 Created when authenticated")
    void shouldCreatePostWhenAuthenticated() throws Exception {
        PostRequest request = PostRequest.builder()
                .title("Spring Boot 3 Exploration")
                .content("Deep dive into Spring Boot 3 with Java 21...")
                .build();

        when(postService.createPost(any(PostRequest.class), any(UserPrincipal.class))).thenReturn(samplePostResponse);

        mockMvc.perform(post("/api/posts")
                        .with(user(authorPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Spring Boot 3 Exploration"));
    }

    @Test
    @DisplayName("PUT /api/posts/{id} should return 403 Forbidden when user is not the owner")
    void shouldReturnForbiddenWhenNotOwner() throws Exception {
        PostRequest request = PostRequest.builder()
                .title("Updated Title")
                .content("Updated Content")
                .build();

        when(postService.updatePost(eq(1L), any(PostRequest.class), any(UserPrincipal.class)))
                .thenThrow(new AccessDeniedException("You do not have permission to update this post."));

        mockMvc.perform(put("/api/posts/1")
                        .with(user(authorPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("You do not have permission to update this post."));
    }

    @Test
    @DisplayName("DELETE /api/posts/{id} should return 200 OK when successfully deleted by owner")
    void shouldDeletePostSuccessfully() throws Exception {
        doNothing().when(postService).deletePost(eq(1L), any(UserPrincipal.class));

        mockMvc.perform(delete("/api/posts/1")
                        .with(user(authorPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post deleted successfully"));
    }
}

