package com.lee.workrequest.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lee.workrequest.exception.WorkRequestInvalidIdException;

/**
 * Validates parameters provided to {@link WorkRequestController} with application specific restrictions
 */
public class WorkRequestValidation {

    private static final Logger LOG = LoggerFactory.getLogger(WorkRequestValidation.class);

    /**
     * Id must be greater than 0 and within long type maximum
     * @param id of work request to validate
     */
    public void idInRange(final long id){
        if(id < 1){
            LOG.debug("Out of range id: {}", id);
            throw new WorkRequestInvalidIdException(id);
        }
    }
}
