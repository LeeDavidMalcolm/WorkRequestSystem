package com.lee.workrequest.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Handle all work request related exceptions and return informative response code and error
 */
@ControllerAdvice
public class WorkRequestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WorkRequestExceptionHandler.class);

    @ExceptionHandler({WorkRequestExistsException.class, WorkRequestQueueEmptyException.class, WorkRequestInvalidIdException.class})
    void handleBadRequest(HttpServletResponse response, Exception e) throws IOException {
        LOG.error("Mapping to BAD_REQUEST", e);
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({WorkRequestDoesNotExistException.class})
    void handleNotFound(HttpServletResponse response, Exception e) throws IOException {
        LOG.error("Mapping to NOT_FOUND", e);
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}
