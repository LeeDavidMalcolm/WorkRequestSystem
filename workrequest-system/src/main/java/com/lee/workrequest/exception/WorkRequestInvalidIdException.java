package com.lee.workrequest.exception;

/**
 * Indicates that the work request id is out of range
 */
public class WorkRequestInvalidIdException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WorkRequestInvalidIdException(long id) {
        super("WorkRequest id must be 1 or greater, supplied id: " + id);
    }
}
