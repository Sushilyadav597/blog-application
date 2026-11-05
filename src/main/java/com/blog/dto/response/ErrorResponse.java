package com.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard API error response payload returned when an operation fails.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API error response payload")
public class ErrorResponse {

    @Schema(description = "Failure flag", example = "false")
    @Builder.Default
    private boolean success = false;

    @Schema(description = "Detailed error message", example = "Post not found")
    private String message;

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "HTTP error title", example = "Not Found")
    private String error;

    @Schema(description = "Error timestamp", example = "2026-11-05T14:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Endpoint URI where error originated", example = "/api/posts/99")
    private String path;

    @Schema(description = "Map of field-level validation errors on 400 Bad Request")
    private Map<String, String> validationErrors;

    public ErrorResponse(boolean success, String message, int status) {
        this.success = success;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
