package com.lee.workrequest.exception;

/**
 * Indicates that the work request id does not exist in the queue
 */
public class WorkRequestDoesNotExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WorkRequestDoesNotExistException(long id) {
        super("WorkRequest does not exist with id: " + id);
    }
}
