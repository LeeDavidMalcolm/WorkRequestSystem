package com.lee.workrequest.model;

import java.time.LocalDateTime;

/**
 * Represents a {@link WorkRequest} with VIP priority
 */
public class WorkRequestVIP extends WorkRequestNormal {

    public WorkRequestVIP(final long id, final LocalDateTime enqueuedTime){
        super(id, enqueuedTime);
    }

    @Override
    public double calculateRank() {
        final double durationInQueue = super.calculateRank();
        return durationInQueue > 0 ? Math.max(4, 2 * durationInQueue * Math.log(durationInQueue)) : 4;
    }

    @Override
    public String toString() {
        return "WorkRequestVIP [id=" + id + ", enqueuedTime=" + enqueuedTime + "]";
    }
}
