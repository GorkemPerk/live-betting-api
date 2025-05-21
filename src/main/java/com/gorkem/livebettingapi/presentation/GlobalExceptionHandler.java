package com.gorkem.livebettingapi.presentation;

import com.gorkem.livebettingapi.domain.constant.ErrorCodes;
import com.gorkem.livebettingapi.domain.exception.BaseBusinessException;
import com.gorkem.livebettingapi.presentation.model.error.ErrorDetail;
import com.gorkem.livebettingapi.presentation.model.error.ErrorResponse;
import com.gorkem.livebettingapi.presentation.model.response.Response;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

/**
 * Global exception handler for the entire API
 * Handles all exceptions and returns appropriate HTTP status codes and error messages
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles business logic exceptions
     *
     * @param ex The business exception that was thrown
     * @return A response entity with appropriate error details
     */
    @ExceptionHandler({BaseBusinessException.class})
    @ApiResponse(responseCode = "400", description = "Business rule violation",
            content = @Content(schema = @Schema(implementation = Response.class)))
    public ResponseEntity<Response<Object>> handleBusinessException(BaseBusinessException ex) {
        return getResponse(ex.getClass().getSimpleName(), ex.getErrorCode(), ex.getHttpStatus(), ex.getMessage());
    }

    /**
     * Handles validation exceptions for request body objects
     *
     * @param ex The validation exception that was thrown
     * @return A response entity with validation error details
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
            content = @Content(schema = @Schema(implementation = Response.class)))
    public ResponseEntity<Response<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return handleValidationException(ex.getBindingResult(), ex.getClass().getSimpleName());
    }

    /**
     * Handles constraint violation exceptions
     *
     * @param ex The constraint violation exception that was thrown
     * @return A response entity with constraint violation details
     */
    @ExceptionHandler({ConstraintViolationException.class})
    @ApiResponse(responseCode = "400", description = "Constraint violation",
            content = @Content(schema = @Schema(implementation = Response.class)))
    public ResponseEntity<Response<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        return getResponse(ex.getClass().getSimpleName(), ErrorCodes.REQUEST_VALIDATION, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles data retrieval exceptions (e.g., entity not found)
     *
     * @param ex The data retrieval exception that was thrown
     * @return A response entity with not found error details
     */
    @ExceptionHandler({DataRetrievalFailureException.class})
    @ApiResponse(responseCode = "404", description = "Resource not found",
            content = @Content(schema = @Schema(implementation = Response.class)))
    public ResponseEntity<Response<Object>> handleConstraintViolationException(DataRetrievalFailureException ex) {
        return getResponse(ex.getClass().getSimpleName(), ErrorCodes.NOT_FOUND, HttpStatus.NOT_FOUND, "{0}: {1}", ex.getClass().getSimpleName(), ex.getMessage());
    }

    /**
     * Handles all other unexpected exceptions
     *
     * @param ex The exception that was thrown
     * @return A response entity with internal server error details
     */
    @ExceptionHandler({Exception.class})
    @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = Response.class)))
    public ResponseEntity<Response<Object>> handleException(Exception ex) {
        return getResponse(ex.getClass().getSimpleName(), ErrorCodes.UNDEFINED, HttpStatus.INTERNAL_SERVER_ERROR, "{0}: {1}", ex.getClass().getSimpleName(), ex.getMessage());
    }


    private ResponseEntity<Response<Object>> handleValidationException(BindingResult bindingResult, String simpleName) {
        List<ErrorDetail> errorDetails = bindingResult.getAllErrors().stream().map(error -> {
            String fieldName = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
            return ErrorDetail.builder()
                    .code(ErrorCodes.REQUEST_VALIDATION.getCode())
                    .title(fieldName + ": " + error.getDefaultMessage())
                    .type(simpleName)
                    .build();
        }).toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(errorDetails).build();
        return getResponse(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Response<Object>> getResponse(String type, ErrorCodes errorCodes, HttpStatus statusCode, String message, Object... parameters) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(errorCodes.getCode())
                .errors(Collections.singletonList(ErrorDetail.builder()
                        .code(errorCodes.getCode())
                        .title(MessageFormat.format(message, parameters))
                        .type(type)
                        .build()))
                .build();
        return getResponse(errorResponse, statusCode);
    }

    private ResponseEntity<Response<Object>> getResponse(ErrorResponse errorResponse, HttpStatus statusCode) {
        Response<Object> response = new Response<>();
        response.setError(errorResponse);
        response.getError().setStatus(statusCode.value());
        log.error("GlobalExceptionHandler errorResponse: {}, statusCode: {}", errorResponse, statusCode);
        return new ResponseEntity<>(response, statusCode);
    }
}
