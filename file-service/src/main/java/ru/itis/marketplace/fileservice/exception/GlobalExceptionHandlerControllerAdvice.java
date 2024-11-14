package ru.itis.marketplace.fileservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {

    private static final String PROBLEM_DETAIL_TITLE = "/swagger-ui/index.html";

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequestException(HttpServletRequest request, BadRequestException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setTitle("Bad Request");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setType(URI.create(PROBLEM_DETAIL_TITLE));
        return problemDetail;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ProblemDetail handleIllegalsException(HttpServletRequest request, Exception exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        problemDetail.setTitle("Error on the server");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setType(URI.create(PROBLEM_DETAIL_TITLE));
        return problemDetail;
    }
}
