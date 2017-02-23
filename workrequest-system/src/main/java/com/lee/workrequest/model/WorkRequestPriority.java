package com.lee.workrequest.model;

import java.time.LocalDateTime;

/**
 * Represents a {@link WorkRequest} with priority level of priority
 */
public class WorkRequestPriority extends WorkRequestNormal {

    public WorkRequestPriority(final long id, final LocalDateTime enqueuedTime){
        super(id, enqueuedTime);
    }

    @Override
    public double calculateRank() {
        final double durationInQueue = super.calculateRank();
        return durationInQueue > 0 ? Math.max(3, durationInQueue * Math.log(durationInQueue)) : 3;
    }

    @Override
    public String toString() {
        return "WorkRequestPriority [id=" + id + ", enqueuedTime=" + enqueuedTime + "]";
    }
}
