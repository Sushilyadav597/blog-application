package com.blog.service.impl;

import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
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
import com.blog.service.CommentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation containing business logic for comments on blog posts.
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              UserRepository userRepository,
                              CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest commentRequest, UserPrincipal currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        User author = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        Comment comment = Comment.builder()
                .content(commentRequest.getContent().trim())
                .post(post)
                .author(author)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        // Ensure parent post exists
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post", "id", postId);
        }

        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId).stream()
                .map(commentMapper::toCommentResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        return commentMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest commentRequest, UserPrincipal currentUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        verifyOwnershipOrAdmin(comment.getAuthor().getId(), currentUser, "update");

        comment.setContent(commentRequest.getContent().trim());
        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.toCommentResponse(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id, UserPrincipal currentUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        verifyOwnershipOrAdmin(comment.getAuthor().getId(), currentUser, "delete");

        commentRepository.delete(comment);
    }

    private void verifyOwnershipOrAdmin(Long authorId, UserPrincipal currentUser, String operation) {
        boolean isOwner = authorId.equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException(
                    String.format("You do not have permission to %s this comment. Only the author or an admin may perform this action.", operation)
            );
        }
    }
}
