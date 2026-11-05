package com.blog.service;

import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.dto.response.UserSummaryResponse;
import com.blog.entity.Comment;
import com.blog.entity.Post;
import com.blog.entity.Role;
import com.blog.entity.User;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.CommentMapper;
import com.blog.repository.CommentRepository;
import com.blog.repository.PostRepository;
import com.blog.repository.UserRepository;
import com.blog.security.UserPrincipal;
import com.blog.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Comment Service Unit Tests")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User authorUser;
    private User anotherUser;
    private User adminUser;
    private UserPrincipal authorPrincipal;
    private UserPrincipal anotherPrincipal;
    private UserPrincipal adminPrincipal;
    private Post samplePost;
    private Comment sampleComment;
    private CommentResponse sampleCommentResponse;

    @BeforeEach
    void setUp() {
        authorUser = User.builder()
                .id(1L)
                .username("commenter")
                .email("commenter@example.com")
                .role(Role.ROLE_USER)
                .build();

        anotherUser = User.builder()
                .id(2L)
                .username("stranger")
                .email("stranger@example.com")
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
                .title("Sample Post")
                .content("Content")
                .build();

        sampleComment = Comment.builder()
                .id(50L)
                .content("Insightful post!")
                .post(samplePost)
                .author(authorUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserSummaryResponse authorSummary = UserSummaryResponse.builder()
                .id(1L)
                .username("commenter")
                .email("commenter@example.com")
                .role(Role.ROLE_USER)
                .build();

        sampleCommentResponse = CommentResponse.builder()
                .id(50L)
                .content("Insightful post!")
                .postId(10L)
                .author(authorSummary)
                .createdAt(sampleComment.getCreatedAt())
                .updatedAt(sampleComment.getUpdatedAt())
                .build();
    }

    @Test
    @DisplayName("Should create comment successfully")
    void shouldCreateCommentSuccessfully() {
        CommentRequest request = CommentRequest.builder()
                .content("Insightful post!")
                .build();

        when(postRepository.findById(10L)).thenReturn(Optional.of(samplePost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(authorUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(sampleComment);
        when(commentMapper.toCommentResponse(sampleComment)).thenReturn(sampleCommentResponse);

        CommentResponse response = commentService.createComment(10L, request, authorPrincipal);

        assertNotNull(response);
        assertEquals(50L, response.getId());
        assertEquals("Insightful post!", response.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when post for comment does not exist")
    void shouldThrowExceptionWhenPostDoesNotExist() {
        CommentRequest request = CommentRequest.builder().content("Content").build();
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(999L, request, authorPrincipal));
    }

    @Test
    @DisplayName("Should retrieve comments for a post")
    void shouldGetCommentsByPostId() {
        when(postRepository.existsById(10L)).thenReturn(true);
        when(commentRepository.findByPostIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(sampleComment));
        when(commentMapper.toCommentResponse(sampleComment)).thenReturn(sampleCommentResponse);

        List<CommentResponse> comments = commentService.getCommentsByPostId(10L);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(50L, comments.get(0).getId());
    }

    @Test
    @DisplayName("Should update comment by author")
    void shouldUpdateCommentByAuthor() {
        CommentRequest updateRequest = CommentRequest.builder().content("Updated comment").build();
        when(commentRepository.findById(50L)).thenReturn(Optional.of(sampleComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(sampleComment);
        when(commentMapper.toCommentResponse(sampleComment)).thenReturn(sampleCommentResponse);

        CommentResponse response = commentService.updateComment(50L, updateRequest, authorPrincipal);

        assertNotNull(response);
        verify(commentRepository).save(sampleComment);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-author updates comment")
    void shouldDenyUpdateByNonAuthor() {
        CommentRequest updateRequest = CommentRequest.builder().content("Hacked comment").build();
        when(commentRepository.findById(50L)).thenReturn(Optional.of(sampleComment));

        assertThrows(AccessDeniedException.class, () -> commentService.updateComment(50L, updateRequest, anotherPrincipal));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should delete comment by author")
    void shouldDeleteCommentByAuthor() {
        when(commentRepository.findById(50L)).thenReturn(Optional.of(sampleComment));

        assertDoesNotThrow(() -> commentService.deleteComment(50L, authorPrincipal));
        verify(commentRepository, times(1)).delete(sampleComment);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-author deletes comment")
    void shouldDenyDeleteByNonAuthor() {
        when(commentRepository.findById(50L)).thenReturn(Optional.of(sampleComment));

        assertThrows(AccessDeniedException.class, () -> commentService.deleteComment(50L, anotherPrincipal));
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    @DisplayName("Should allow admin to delete any comment")
    void shouldAllowAdminToDeleteComment() {
        when(commentRepository.findById(50L)).thenReturn(Optional.of(sampleComment));

        assertDoesNotThrow(() -> commentService.deleteComment(50L, adminPrincipal));
        verify(commentRepository, times(1)).delete(sampleComment);
    }
}
