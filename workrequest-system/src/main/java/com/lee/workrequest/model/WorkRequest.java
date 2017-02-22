package com.lee.workrequest.model;

import java.time.LocalDateTime;

public interface WorkRequest extends Comparable<WorkRequest> {

    long getId();

    LocalDateTime getEnqueuedTime();

    double calculateRank();
}
