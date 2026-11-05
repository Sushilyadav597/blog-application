package com.blog.controller;

import com.blog.dto.request.PostRequest;
import com.blog.dto.response.ApiResponse;
import com.blog.dto.response.ErrorResponse;
import com.blog.dto.response.PagedResponse;
import com.blog.dto.response.PostResponse;
import com.blog.security.UserPrincipal;
import com.blog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller exposing endpoints for creating, retrieving, updating, and deleting blog posts.
 */
@RestController
@RequestMapping("/api/posts")
@Tag(name = "Post Management", description = "Endpoints for managing blog posts, search, pagination, and sorting")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @Operation(summary = "Create a new blog post", description = "Allows authenticated users to publish a new blog post.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Post created successfully",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed for title or content",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest postRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PostResponse postResponse = postService.createPost(postRequest, currentUser);
        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all published blog posts", description = "Public endpoint to retrieve blog posts with pagination, sorting, and title search.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Posts retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PagedResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid pagination or sorting parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PagedResponse<PostResponse>> getAllPosts(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @Parameter(description = "Number of posts per page")
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @Parameter(description = "Sort property (e.g., createdAt, title, id)")
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @Parameter(description = "Sort direction (asc or desc)")
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir,
            @Parameter(description = "Optional search query to filter posts by title")
            @RequestParam(value = "title", required = false) String title) {

        PagedResponse<PostResponse> pagedResponse = postService.getAllPosts(page, size, sortBy, sortDir, title);
        return ResponseEntity.ok(pagedResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single blog post by ID", description = "Public endpoint to fetch detailed post information.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Post retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found with given ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PostResponse> getPostById(@PathVariable("id") Long id) {
        PostResponse postResponse = postService.getPostById(id);
        return ResponseEntity.ok(postResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing blog post", description = "Allows only the original post author (or admin) to modify title and content.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Post updated successfully",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Current user is not the post author",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable("id") Long id,
            @Valid @RequestBody PostRequest postRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PostResponse postResponse = postService.updatePost(id, postRequest, currentUser);
        return ResponseEntity.ok(postResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an existing blog post", description = "Allows only the original post author (or admin) to delete a post.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Post deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Current user is not the post author",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse> deletePost(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        postService.deletePost(id, currentUser);
        return ResponseEntity.ok(new ApiResponse(true, "Post deleted successfully"));
    }
}
