package com.blog.service;

import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.security.UserPrincipal;

import java.util.List;

/**
 * Service interface for blog comment management operations.
 */
public interface CommentService {

    CommentResponse createComment(Long postId, CommentRequest commentRequest, UserPrincipal currentUser);

    List<CommentResponse> getCommentsByPostId(Long postId);

    CommentResponse getCommentById(Long id);

    CommentResponse updateComment(Long id, CommentRequest commentRequest, UserPrincipal currentUser);

    void deleteComment(Long id, UserPrincipal currentUser);
}
