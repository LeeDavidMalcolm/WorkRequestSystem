package com.lee.workrequest.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lee.workrequest.model.WorkRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

/**
 * Queue containing {@link WorkRequest}s and functionality to submit, remove and get information from queue
 */
public class WorkRequestQueue {

    private static final Logger LOG = LoggerFactory.getLogger(WorkRequestQueue.class);

    final Map<Long, WorkRequest> workRequests = new ConcurrentSkipListMap<>();

    /**
     * Add {@link WorkRequest} to queue
     * @param workRequest {@link WorkRequest} to add
     * @return Null if no entry exists for id or the previous value if it does
     */
    public WorkRequest enqueue(WorkRequest workRequest) {
        return workRequests.putIfAbsent(workRequest.getId(), workRequest);
    }

    /**
     * Get a list of WorkRequest sorted by priority
     * @return list of {@link WorkRequest}s
     */
    public List<WorkRequest> getSortedWorkRequests(){
        final List<WorkRequest> workRequestEntries = new ArrayList<>(workRequests.values());
        LOG.trace("Presorted list: {}", workRequestEntries);
        Collections.sort(workRequestEntries);
        LOG.trace("Sorted list: {}", workRequestEntries);
        return workRequestEntries;
    }

    /**
     * Get all {@link WorkRequest} ids sorted by priority
     * @return
     */
    public List<Long> getSortedIds(){

        final List<WorkRequest> sortedWorkRequests = getSortedWorkRequests();
        if(sortedWorkRequests.isEmpty()){
            LOG.trace("Empty workRequests list");
            return new ArrayList<>();
        }

        LOG.trace("Sorted work request list: {}", sortedWorkRequests);
        final List<Long> sortedIds = sortedWorkRequests.stream().map( workRequestEntry -> workRequestEntry.getId() ).collect( Collectors.toList() );
        LOG.trace("Sorted id list: {}", sortedIds);
        return sortedIds;
    }

    /**
     * Get a specified {@link WorkRequest}
     * @param id of {@link WorkRequest}
     * @return {@link WorkRequest} with id if it exists or null if it does not
     */
    public WorkRequest get(final long id) {
        return workRequests.get(id);
    }

    /**
     * Get the position of the {@link WorkRequest} with the specified id
     * @param id of {@link WorkRequest}
     * @return position in relation to priority or -1 if it does not exist in list
     */
    public int getPosition(final long id) {
        return getSortedIds().indexOf(id);
    }

    /**
     * Get the average wait time based on the current time
     * @return average wait time in seconds
     */
    public long getAverageWaitTime(){
        return getAverageWaitTime(LocalDateTime.now());
    }

    /**
     * Get the average wait time with duration based on time supplied
     * @param time to take the duration from
     * @return average wait time in seconds
     */
    public long getAverageWaitTime(LocalDateTime time){

        long totalDuration = 0;
        for (final Map.Entry<Long, WorkRequest> entry : workRequests.entrySet()) {
            totalDuration += Duration.between(entry.getValue().getEnqueuedTime(), time).toMillis();
        }

        LOG.trace("Total duration: {}, list size: {}", totalDuration, workRequests.size());

        if(totalDuration <= 0){
            LOG.debug("Total duration is {} which is less than 0, setting as 0", totalDuration);
            return 0;
        }

        // Get average time in seconds, if less than 0 then return 0
        final long averageWaitTime = totalDuration / workRequests.size() / 1000;
        LOG.trace("Average wait time: {}", averageWaitTime);

        if(averageWaitTime < 0){
            LOG.debug("Average wait time is {} which is less than 0, setting as 0", averageWaitTime);
            return 0;
        }

        return averageWaitTime;
    }

    /**
     * Remove {@link WorkRequest} from queue
     * @param id of {@link WorkRequest} to remove
     * @return Null if no entry exists for id or the previous value if it does and was removed
     */
    public WorkRequest dequeue(final long id) {
        return workRequests.remove(id);
    }

    /**
     * Remove highest priority {@link WorkRequest} from queue
     * @return value removed or null if queue is empty
     */
    public WorkRequest dequeueTop() {
        final List<Long> sortedIds = getSortedIds();
        if(sortedIds.isEmpty()){
            LOG.debug("Queue is empty, returning null");
            return null;
        }

        return workRequests.remove(sortedIds.get(0));
    }
}
