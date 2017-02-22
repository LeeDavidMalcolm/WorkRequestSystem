package com.lee.workrequest.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Factory that produces {@link WorkRequest} concrete types
 */
public class WorkRequestFactory {

    private static final Logger LOG = LoggerFactory.getLogger(WorkRequestFactory.class);

    /**
     * Create a concrete type of {@link WorkRequest} based on id using the current time
     * @param id Requester id
     * @return
     */
    public WorkRequest getWorkRequest(long id) {
        return getWorkRequest(id, LocalDateTime.now());
    }

    /**
     * Create a concrete type of {@link WorkRequest} based on id using the supplied time
     * @param id Requester id
     * @param enqueuedTime time {@link WorkRequest} was enqueued
     * @return
     */
    public WorkRequest getWorkRequest(long id, LocalDateTime enqueuedTime) {
        final boolean idDividesByThree = id % 3 == 0;
        final boolean idDividesByFive = id % 5 == 0;

        if (idDividesByThree && idDividesByFive) {
            LOG.trace("Created Management Override type");
            return new WorkRequestManagementOverride(id, enqueuedTime);
        } else if (idDividesByFive) {
            LOG.trace("Created VIP type");
            return new WorkRequestVIP(id, enqueuedTime);
        } else if (idDividesByThree) {
            LOG.trace("Created Priority type");
            return new WorkRequestPriority(id, enqueuedTime);
        } else {
            LOG.trace("Created Normal type");
            return new WorkRequestNormal(id, enqueuedTime);
        }
    }
}
