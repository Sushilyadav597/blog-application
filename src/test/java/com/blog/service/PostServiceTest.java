package com.blog.service;

import com.blog.dto.request.PostRequest;
import com.blog.dto.response.PagedResponse;
import com.blog.dto.response.PostResponse;
import com.blog.dto.response.UserSummaryResponse;
import com.blog.entity.Post;
import com.blog.entity.Role;
import com.blog.entity.User;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.PostMapper;
import com.blog.repository.PostRepository;
import com.blog.repository.UserRepository;
import com.blog.security.UserPrincipal;
import com.blog.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post Service Unit Tests")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private User authorUser;
    private User anotherUser;
    private User adminUser;
    private UserPrincipal authorPrincipal;
    private UserPrincipal anotherPrincipal;
    private UserPrincipal adminPrincipal;
    private Post samplePost;
    private PostResponse samplePostResponse;

    @BeforeEach
    void setUp() {
        authorUser = User.builder()
                .id(1L)
                .username("author")
                .email("author@example.com")
                .role(Role.ROLE_USER)
                .build();

        anotherUser = User.builder()
                .id(2L)
                .username("another")
                .email("another@example.com")
                .role(Role.ROLE_USER)
                .build();

        adminUser = User.builder()
                .id(99L)
                .username("admin")
                .email("admin@example.com")
                .role(Role.ROLE_ADMIN)
                .build();

        authorPrincipal = UserPrincipal.create(authorUser);
        anotherPrincipal = UserPrincipal.create(anotherUser);
        adminPrincipal = UserPrincipal.create(adminUser);

        samplePost = Post.builder()
                .id(10L)
                .title("Initial Title")
                .content("Initial Content")
                .author(authorUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserSummaryResponse authorSummary = UserSummaryResponse.builder()
                .id(1L)
                .username("author")
                .email("author@example.com")
                .role(Role.ROLE_USER)
                .build();

        samplePostResponse = PostResponse.builder()
                .id(10L)
                .title("Initial Title")
                .content("Initial Content")
                .author(authorSummary)
                .commentCount(0)
                .createdAt(samplePost.getCreatedAt())
                .updatedAt(samplePost.getUpdatedAt())
                .build();
    }

    @Test
    @DisplayName("Should create post successfully")
    void shouldCreatePostSuccessfully() {
        PostRequest request = PostRequest.builder()
                .title("New Post Title")
                .content("New Post Content")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(authorUser));
        when(postRepository.save(any(Post.class))).thenReturn(samplePost);
        when(postMapper.toPostResponse(samplePost)).thenReturn(samplePostResponse);

        PostResponse response = postService.createPost(request, authorPrincipal);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("Should retrieve paginated posts")
    void shouldGetAllPostsWithPagination() {
        Page<Post> pagedPosts = new PageImpl<>(List.of(samplePost));
        when(postRepository.findAll(any(Pageable.class))).thenReturn(pagedPosts);
        when(postMapper.toPostResponse(samplePost)).thenReturn(samplePostResponse);

        PagedResponse<PostResponse> result = postService.getAllPosts(0, 10, "createdAt", "desc", null);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Should retrieve post by valid ID")
    void shouldGetPostById() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(samplePost));
        when(postMapper.toPostResponse(samplePost)).thenReturn(samplePostResponse);

        PostResponse response = postService.getPostById(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when post not found")
    void shouldThrowExceptionWhenPostNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(999L));
    }

    @Test
    @DisplayName("Should update post when user is the author")
    void shouldUpdatePostByAuthor() {
        PostRequest updateRequest = PostRequest.builder()
                .title("Updated Title")
                .content("Updated Content")
                .build();

        when(postRepository.findById(10L)).thenReturn(Optional.of(samplePost));
        when(postRepository.save(any(Post.class))).thenReturn(samplePost);
        when(postMapper.toPostResponse(samplePost)).thenReturn(samplePostResponse);

        PostResponse response = postService.updatePost(10L, updateRequest, authorPrincipal);

        assertNotNull(response);
        verify(postRepository).save(samplePost);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-author attempts to update post")
    void shouldDenyUpdateByNonAuthor() {
        PostRequest updateRequest = PostRequest.builder()
                .title("Unauthorized Title")
                .content("Unauthorized Content")
                .build();

        when(postRepository.findById(10L)).thenReturn(Optional.of(samplePost));

        assertThrows(AccessDeniedException.class, () -> postService.updatePost(10L, updateRequest, anotherPrincipal));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should delete post when user is the author")
    void shouldDeletePostByAuthor() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(samplePost));

        assertDoesNotThrow(() -> postService.deletePost(10L, authorPrincipal));
        verify(postRepository, times(1)).delete(samplePost);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-author attempts to delete post")
    void shouldDenyDeleteByNonAuthor() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(samplePost));

        assertThrows(AccessDeniedException.class, () -> postService.deletePost(10L, anotherPrincipal));
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    @DisplayName("Should allow admin to delete any post")
    void shouldAllowAdminToDeletePost() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(samplePost));

        assertDoesNotThrow(() -> postService.deletePost(10L, adminPrincipal));
        verify(postRepository, times(1)).delete(samplePost);
    }
}

