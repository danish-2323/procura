package com.procura.shared.exception;

import com.procura.shared.response.ErrorResponse;
import com.procura.shared.response.FieldErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(BusinessRuleException ex, HttpServletRequest request) {
        log.warn("Business rule violation [{}]: {}", ex.getRuleId(), ex.getMessage());
        String traceId = getOrCreateTraceId(request);
        ErrorResponse body = ErrorResponse.withRule(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            ex.getErrorCode() != null ? ex.getErrorCode() : ApiErrorCode.BUSINESS_RULE_VIOLATION,
            ex.getMessage(),
            ex.getRuleId(),
            request.getRequestURI(),
            traceId
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorResponse body = ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            ApiErrorCode.NOT_FOUND,
            ex.getMessage(),
            request.getRequestURI(),
            getOrCreateTraceId(request)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(StateTransitionException.class)
    public ResponseEntity<ErrorResponse> handleStateTransition(StateTransitionException ex, HttpServletRequest request) {
        log.warn("Illegal state transition: {}", ex.getMessage());
        ErrorResponse body = ErrorResponse.of(
            HttpStatus.CONFLICT.value(),
            ApiErrorCode.ILLEGAL_TRANSITION,
            ex.getMessage(),
            request.getRequestURI(),
            getOrCreateTraceId(request)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler({ConcurrencyConflictException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> handleConcurrency(Exception ex, HttpServletRequest request) {
        log.warn("Concurrency conflict: {}", ex.getMessage());
        ErrorResponse body = ErrorResponse.of(
            HttpStatus.CONFLICT.value(),
            ApiErrorCode.VERSION_CONFLICT,
            "Resource has been modified concurrently by another transaction",
            request.getRequestURI(),
            getOrCreateTraceId(request)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(DepartmentScopeException.class)
    public ResponseEntity<ErrorResponse> handleDepartmentScope(DepartmentScopeException ex, HttpServletRequest request) {
        log.warn("Department scope violation: {}", ex.getMessage());
        ErrorResponse body = ErrorResponse.of(
            HttpStatus.FORBIDDEN.value(),
            ApiErrorCode.DEPARTMENT_SCOPE_VIOLATION,
            ex.getMessage(),
            request.getRequestURI(),
            getOrCreateTraceId(request)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponse body = ErrorResponse.of(
            HttpStatus.FORBIDDEN.value(),
            ApiErrorCode.FORBIDDEN,
            "Access denied: insufficient privileges for this operation",
            request.getRequestURI(),
            getOrCreateTraceId(request)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler({BadCredentialsException.class, UnauthorizedException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorized(Exception ex, HttpServletRequest request) {
        log.warn("Authentication failure: {}", ex.getMessage());
        ErrorResponse body = ErrorResponse.of(
            HttpStatus.UNAUTHORIZED.value(),
            ApiErrorCode.UNAUTHENTICATED,
            ex.getMessage(),
            request.getRequestURI(),
            getOrCreateTraceId(request)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldErrorDto> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> new FieldErrorDto(
                err.getField(),
                err.getRejectedValue() != null ? String.valueOf(err.getRejectedValue()) : null,
                err.getDefaultMessage()
            ))
            .toList();

        ErrorResponse body = ErrorResponse.validation(
            HttpStatus.BAD_REQUEST.value(),
            ApiErrorCode.VALIDATION_FAILED,
            "Validation failed for one or more fields",
            request.getRequestURI(),
            getOrCreateTraceId(request),
            fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        String traceId = getOrCreateTraceId(request);
        log.error("Unhandled exception [traceId={}]: ", traceId, ex);
        ErrorResponse body = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ApiErrorCode.INTERNAL_SERVER_ERROR,
            "An unexpected internal error occurred",
            request.getRequestURI(),
            traceId
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String getOrCreateTraceId(HttpServletRequest request) {
        String traceId = (String) request.getAttribute("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = request.getHeader("X-Trace-Id");
        }
        return (traceId != null && !traceId.isBlank()) ? traceId : UUID.randomUUID().toString();
    }
}
