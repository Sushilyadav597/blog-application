package com.blog.service;

import com.blog.dto.request.PostRequest;
import com.blog.dto.response.PagedResponse;
import com.blog.dto.response.PostResponse;
import com.blog.security.UserPrincipal;

/**
 * Service interface for blog post management operations.
 */
public interface PostService {

    PostResponse createPost(PostRequest postRequest, UserPrincipal currentUser);

    PagedResponse<PostResponse> getAllPosts(int page, int size, String sortBy, String sortDir, String title);

    PostResponse getPostById(Long id);

    PostResponse updatePost(Long id, PostRequest postRequest, UserPrincipal currentUser);

    void deletePost(Long id, UserPrincipal currentUser);
}

