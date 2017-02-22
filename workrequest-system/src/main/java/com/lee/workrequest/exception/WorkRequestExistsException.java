package com.lee.workrequest.exception;

/**
 * Indicates that the work request id already exists in the queue
 */
public class WorkRequestExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WorkRequestExistsException(long id) {
        super("WorkRequest already exists with id: " + id);
    }
}
