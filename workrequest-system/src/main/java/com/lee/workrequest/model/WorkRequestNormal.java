package com.lee.workrequest.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents a {@link WorkRequest} with normal priority
 */
public class WorkRequestNormal implements WorkRequest {

    protected final long id;
    protected LocalDateTime enqueuedTime;

    public WorkRequestNormal(long id, LocalDateTime enqueuedTime){
        this.id = id;
        this.enqueuedTime = enqueuedTime;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public LocalDateTime getEnqueuedTime() {
        return enqueuedTime;
    }

    @Override
    public double calculateRank() {
        return Duration.between(enqueuedTime, LocalDateTime.now()).toMillis() / 1000;
    }

    @Override
    public int compareTo(WorkRequest wr) {
        if(wr instanceof WorkRequestManagementOverride){
            return 1;
        }
        return (int) (wr.calculateRank() - calculateRank());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof WorkRequest)) {
            return false;
        }

        final WorkRequest wr = (WorkRequest) o;

        return wr.getId() == id;
    }

    @Override
    public String toString() {
        return "WorkRequestNormal [id=" + id + ", enqueuedTime=" + enqueuedTime + "]";
    }
}
