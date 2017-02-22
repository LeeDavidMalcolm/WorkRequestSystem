package com.lee.workrequest.exception;

/**
 * Indicates the work request queue is empty so action requested cannot be performed
 */
public class WorkRequestQueueEmptyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public WorkRequestQueueEmptyException() {
        super("WorkRequest queue is empty");
    }
}
