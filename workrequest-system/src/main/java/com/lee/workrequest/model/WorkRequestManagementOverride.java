package com.lee.workrequest.model;

import java.time.LocalDateTime;

/**
 * Represents a {@link WorkRequest} with Management Override priority
 */
public class WorkRequestManagementOverride extends WorkRequestNormal {

    public WorkRequestManagementOverride(final long id, final LocalDateTime enqueuedTime){
        super(id, enqueuedTime);
    }

    @Override
    public int compareTo(WorkRequest wr) {
        if(wr instanceof WorkRequestManagementOverride){
            return (int) (wr.calculateRank() - calculateRank());
        }

        return -1;
    }

    @Override
    public String toString() {
        return "WorkRequestManagementOverride [id=" + id + ", enqueuedTime=" + enqueuedTime + "]";
    }
}
