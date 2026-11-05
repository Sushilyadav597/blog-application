package com.blog.service.impl;

import com.blog.dto.request.PostRequest;
import com.blog.dto.response.PagedResponse;
import com.blog.dto.response.PostResponse;
import com.blog.entity.Post;
import com.blog.entity.Role;
import com.blog.entity.User;
import com.blog.exception.BadRequestException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.PostMapper;
import com.blog.repository.PostRepository;
import com.blog.repository.UserRepository;
import com.blog.security.UserPrincipal;
import com.blog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Service implementation containing business logic for blog posts.
 */
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository,
                           PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
    }

    @Override
    @Transactional
    public PostResponse createPost(PostRequest postRequest, UserPrincipal currentUser) {
        User author = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        Post post = Post.builder()
                .title(postRequest.getTitle().trim())
                .content(postRequest.getContent().trim())
                .author(author)
                .build();

        Post savedPost = postRepository.save(post);
        return postMapper.toPostResponse(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PostResponse> getAllPosts(int page, int size, String sortBy, String sortDir, String title) {
        validatePageNumberAndSize(page, size);

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String safeSortBy = sanitizeSortField(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, safeSortBy));

        Page<Post> postsPage;
        if (StringUtils.hasText(title)) {
            postsPage = postRepository.findByTitleContainingIgnoreCase(title.trim(), pageable);
        } else {
            postsPage = postRepository.findAll(pageable);
        }

        List<PostResponse> content = postsPage.getContent().stream()
                .map(postMapper::toPostResponse)
                .toList();

        return PagedResponse.<PostResponse>builder()
                .content(content)
                .page(postsPage.getNumber())
                .size(postsPage.getSize())
                .totalElements(postsPage.getTotalElements())
                .totalPages(postsPage.getTotalPages())
                .last(postsPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        return postMapper.toPostResponse(post);
    }

    @Override
    @Transactional
    public PostResponse updatePost(Long id, PostRequest postRequest, UserPrincipal currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        verifyOwnershipOrAdmin(post.getAuthor().getId(), currentUser, "update");

        post.setTitle(postRequest.getTitle().trim());
        post.setContent(postRequest.getContent().trim());

        Post updatedPost = postRepository.save(post);
        return postMapper.toPostResponse(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long id, UserPrincipal currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        verifyOwnershipOrAdmin(post.getAuthor().getId(), currentUser, "delete");

        postRepository.delete(post);
    }

    private void verifyOwnershipOrAdmin(Long authorId, UserPrincipal currentUser, String operation) {
        boolean isOwner = authorId.equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException(
                    String.format("You do not have permission to %s this post. Only the author or an admin may perform this action.", operation)
            );
        }
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }
        if (size <= 0) {
            throw new BadRequestException("Page size must be greater than zero.");
        }
        if (size > 100) {
            throw new BadRequestException("Page size must not exceed 100 elements.");
        }
    }

    private String sanitizeSortField(String sortBy) {
        if ("title".equalsIgnoreCase(sortBy)) return "title";
        if ("id".equalsIgnoreCase(sortBy)) return "id";
        if ("updatedAt".equalsIgnoreCase(sortBy)) return "updatedAt";
        return "createdAt";
    }
}

