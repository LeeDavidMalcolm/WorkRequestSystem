package com.lee.workrequest.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lee.workrequest.exception.WorkRequestDoesNotExistException;
import com.lee.workrequest.exception.WorkRequestExistsException;
import com.lee.workrequest.exception.WorkRequestQueueEmptyException;
import com.lee.workrequest.model.WorkRequest;
import com.lee.workrequest.model.WorkRequestFactory;
import com.lee.workrequest.queue.WorkRequestQueue;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contains all business logic needed to service requests in the {@link WorkRequestController}
 *
 */
public class WorkRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkRequestService.class);

    private final WorkRequestFactory workRequestFactory;
    private final WorkRequestQueue workRequestQueue;

    public WorkRequestService(final WorkRequestFactory workRequestFactory, final WorkRequestQueue workRequestQueue){
        this.workRequestFactory = workRequestFactory;
        this.workRequestQueue = workRequestQueue;
    }

    /**
     * Add a {@link WorkRequest} to the {@link WorkRequestQueue}
     * @param id of requester
     * @param time enqueued, this is optional. Will use current time if null
     * @throws WorkRequestExistsException if entry with id already exists
     */
    public void enqueue(final long id, final LocalDateTime time) {
        WorkRequest workRequest;
        if(time == null) {
            LOG.debug("No time specified, using current time");
            workRequest = workRequestFactory.getWorkRequest(id);
        } else {
            workRequest = workRequestFactory.getWorkRequest(id, time);
        }

        if(workRequestQueue.enqueue(workRequest) != null) {
            LOG.debug("Work request with id: {} already exists", id);
            throw new WorkRequestExistsException(id);
        }
    }

    /**
     * Get the {@link WorkRequestQueue} sorted by priority
     * @return list of {@link WorkRequest}s
     */
    public List<WorkRequest> getSortedWorkRequests() {
        return workRequestQueue.getSortedWorkRequests();
    }

    /**
     * Get the average wait time for {@link WorkRequest}s in the {@link WorkRequestQueue}
     * @param time to take the duration from. If null will use local time
     * @return time in seconds representing the average wait time
     */
    public long getAverageWaitTime(final LocalDateTime time) {

        if(time ==null) {
            LOG.debug("No time specified, using current time");
            return workRequestQueue.getAverageWaitTime();
        } else {
            return workRequestQueue.getAverageWaitTime(time);
        }
    }

    /**
     * Get the list of ids sorted by priority in the {@link WorkRequest} queue
     * @return list of sorted ids
     */
    public List<Long> getSortedIds() {
        return workRequestQueue.getSortedIds();
    }

    /**
     * Get the {@link WorkRequest} from the {@link WorkRequestQueue}
     * @param id of {@link WorkRequest} to get
     * @return {@link WorkRequest}
     * @throws WorkRequestDoesNotExistException if no {@link WorkRequest} exists with that id
     */
    public WorkRequest getWorkRequest(final long id) {
        final WorkRequest workRequest = workRequestQueue.get(id);

        if(workRequest == null) {
            LOG.debug("Work request with id: {} does not exist", id);
            throw new WorkRequestDoesNotExistException(id);
        }

        return workRequest;
    }

    /**
     * Get the position of the {@link WorkRequest} in the {@link WorkRequestQueue} based on priority
     * @param id of {@link WorkRequest}
     * @return position based on priority
     * @throws WorkRequestDoesNotExistException if no {@link WorkRequest} exists with that id
     */
    public int getPosition(final long id) {
        final int position = workRequestQueue.getPosition(id);

        if(position == -1) {
            LOG.debug("Work request with id: {} does not exist", id);
            throw new WorkRequestDoesNotExistException(id);
        }

        return position;
    }

    /**
     * Remove {@link WorkRequest} from the {@link WorkRequestQueue}
     * @param id of {@link WorkRequest} to remove
     * @throws WorkRequestDoesNotExistException if no {@link WorkRequest} exists with that id
     */
    public void dequeue(final long id) {
        if(workRequestQueue.dequeue(id) == null) {
            LOG.debug("Work request with id: {} does not exist", id);
            throw new WorkRequestDoesNotExistException(id);
        }
    }

    /**
     * Remove highest priority {@link WorkRequest} from the {@link WorkRequestQueue}
     * @throws WorkRequestQueueEmptyException if {@link WorkRequestQueue} is empty
     */
    public long dequeueTop() {
        final WorkRequest workRequest = workRequestQueue.dequeueTop();
        if(workRequest == null) {
            LOG.debug("Work request queue is empty");
            throw new WorkRequestQueueEmptyException();
        } else {
            return workRequest.getId();
        }
    }
}
