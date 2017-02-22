package com.lee.workrequest.queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.lee.workrequest.model.WorkRequest;
import com.lee.workrequest.model.WorkRequestManagementOverride;
import com.lee.workrequest.model.WorkRequestNormal;
import com.lee.workrequest.model.WorkRequestPriority;
import com.lee.workrequest.model.WorkRequestVIP;

import java.time.LocalDateTime;
import java.util.List;

public class WorkRequestQueueTest {

    private WorkRequestQueue workRequestQueue;

    private WorkRequest workRequest;

    private WorkRequest workRequestNormal;
    private WorkRequest workRequestVIP;
    private WorkRequest workRequestPriority;
    private WorkRequest workRequestMgmtOver;

    private static final long ID = 123;
    private static final LocalDateTime TIME = LocalDateTime.now();

    @Before
    public void setup(){
        workRequestQueue = new WorkRequestQueue();
        workRequest = new WorkRequestNormal(ID, TIME);

        workRequestNormal = new WorkRequestNormal(1, TIME);
        workRequestVIP = new WorkRequestVIP(2, TIME);
        workRequestPriority = new WorkRequestPriority(3, TIME);
        workRequestMgmtOver = new WorkRequestManagementOverride(4, TIME);

    }

    @Test
    public void enqueue_IdDoesNotExist_AddsToQueueReturnsNull(){

        final WorkRequest returnedWorkRequest = workRequestQueue.enqueue(workRequest);

        assertNull(returnedWorkRequest);
        assertEquals(workRequest, workRequestQueue.get(ID));
    }

    @Test
    public void enqueue_IdExists_DoesNotAddToQueueReturnsPrevious(){

        final WorkRequest anotherWorkRequest = new WorkRequestNormal(ID, TIME.plusDays(1));

        workRequestQueue.enqueue(workRequest);
        final WorkRequest returnedWorkRequest = workRequestQueue.enqueue(anotherWorkRequest);

        assertEquals(workRequest, returnedWorkRequest);
        assertEquals(workRequest, workRequestQueue.get(ID));
    }

    @Test
    public void getSortedWorkRequests_QueuePopulated_ReturnsSortedWorkRequest(){
        populateQueue();

        final List<WorkRequest> sortedWorkrequests = workRequestQueue.getSortedWorkRequests();

        assertEquals(workRequestMgmtOver, sortedWorkrequests.get(0));
        assertEquals(workRequestVIP, sortedWorkrequests.get(1));
        assertEquals(workRequestPriority, sortedWorkrequests.get(2));
        assertEquals(workRequestNormal, sortedWorkrequests.get(3));
    }

    @Test
    public void getSortedWorkRequests_QueuePopulatedComplex_ReturnsSortedWorkRequest(){
        populateQueue();
        final WorkRequest workRequestNormal1 = new WorkRequestNormal(10, TIME.plusHours(1));
        workRequestQueue.enqueue(workRequestNormal1);
        final WorkRequest workRequestNormal2 = new WorkRequestNormal(11, TIME.minusHours(3));
        workRequestQueue.enqueue(workRequestNormal2);
        final WorkRequest workRequestVIP1 = new WorkRequestVIP(12, TIME.minusMinutes(1));
        workRequestQueue.enqueue(workRequestVIP1);
        final WorkRequest workRequestPriority1 = new WorkRequestPriority(13, TIME.plusMinutes(4));
        workRequestQueue.enqueue(workRequestPriority1);
        final WorkRequest workRequestPriority2 = new WorkRequestPriority(14, TIME.plusMinutes(5));
        workRequestQueue.enqueue(workRequestPriority2);
        final WorkRequest workRequestMgmtOver1 = new WorkRequestManagementOverride(15, TIME.minusHours(10));
        workRequestQueue.enqueue(workRequestMgmtOver1);


        final List<WorkRequest> sortedWorkrequests = workRequestQueue.getSortedWorkRequests();

        assertEquals(workRequestMgmtOver1, sortedWorkrequests.get(0));
        assertEquals(workRequestMgmtOver, sortedWorkrequests.get(1));
        assertEquals(workRequestNormal2, sortedWorkrequests.get(2));
        assertEquals(workRequestVIP1, sortedWorkrequests.get(3));
        assertEquals(workRequestVIP, sortedWorkrequests.get(4));
        assertEquals(workRequestPriority, sortedWorkrequests.get(5));
        assertEquals(workRequestPriority1, sortedWorkrequests.get(6));
        assertEquals(workRequestPriority2, sortedWorkrequests.get(7));
        assertEquals(workRequestNormal, sortedWorkrequests.get(8));
        assertEquals(workRequestNormal1, sortedWorkrequests.get(9));
    }

    @Test
    public void getSortedWorkRequests_QueueEmpty_ReturnsEmptyList(){

        final List<WorkRequest> sortedWorkrequests = workRequestQueue.getSortedWorkRequests();

        assertTrue(sortedWorkrequests.isEmpty());
    }

    @Test
    public void getSortedIds_QueuePopulated_ReturnsSortedIds(){
        populateQueue();

        final List<Long> sortedIds = workRequestQueue.getSortedIds();

        assertEquals(workRequestMgmtOver.getId(), sortedIds.get(0).longValue());
        assertEquals(workRequestVIP.getId(), sortedIds.get(1).longValue());
        assertEquals(workRequestPriority.getId(), sortedIds.get(2).longValue());
        assertEquals(workRequestNormal.getId(), sortedIds.get(3).longValue());
    }

    @Test
    public void getSortedIds_QueueEmpty_ReturnsEmptyList(){

        final List<Long> sortedIds = workRequestQueue.getSortedIds();

        assertTrue(sortedIds.isEmpty());
    }

    @Test
    public void getPosition_ValidIdQueuePopulated_ReturnsPosition(){
        populateQueue();

        final long position = workRequestQueue.getPosition(workRequestPriority.getId());

        assertEquals(2, position);
    }

    @Test
    public void getPosition_InvalidIdQueuePopulated_ReturnsMinusOne(){
        populateQueue();

        final long position = workRequestQueue.getPosition(1000);

        assertEquals(-1, position);
    }

    @Test
    public void getPosition_QueueEmpty_ReturnsMinusOne(){

        final long position = workRequestQueue.getPosition(0);

        assertEquals(-1, position);
    }

    @Test
    public void getAverageWaitTime_DurationOneHour_ReturnsAverageWaitTime(){
        populateQueue();

        final long averageWaitTime = workRequestQueue.getAverageWaitTime(TIME.plusHours(1));

        assertEquals(3600, averageWaitTime);
    }

    @Test
    public void getAverageWaitTime_TimeInPast_ReturnsZero(){
        populateQueue();

        final long averageWaitTime = workRequestQueue.getAverageWaitTime(TIME.minusHours(1));

        assertEquals(0, averageWaitTime);
    }

    @Test
    public void getAverageWaitTime_EmptyQueue_ReturnsZero(){

        final long averageWaitTime = workRequestQueue.getAverageWaitTime(TIME.plusHours(1));

        assertEquals(0, averageWaitTime);
    }

    @Test
    public void dequeue_IdExists_RemovesFromQueueReturnsValue(){
        populateQueue();

        final WorkRequest returnedWorkRequest = workRequestQueue.dequeue(workRequestNormal.getId());

        assertEquals(workRequestNormal, returnedWorkRequest);
    }

    @Test
    public void dequeue_IdDoesNotExist_ReturnsNull(){
        populateQueue();

        final WorkRequest returnedWorkRequest = workRequestQueue.dequeue(12345);

        assertNull(returnedWorkRequest);
    }

    @Test
    public void dequeue_QueueEmpty_ReturnsNull(){

        final WorkRequest returnedWorkRequest = workRequestQueue.dequeue(12345);

        assertNull(returnedWorkRequest);
    }

    @Test
    public void dequeueTop_QueuePopulated_RemovesFromTopOfQueueReturnsValue(){
        populateQueue();

        final WorkRequest returnedWorkRequest = workRequestQueue.dequeueTop();

        assertEquals(workRequestMgmtOver, returnedWorkRequest);
    }

    @Test
    public void dequeueTop_QueueEmpty_ReturnsNull(){

        final WorkRequest returnedWorkRequest = workRequestQueue.dequeueTop();

        assertNull(returnedWorkRequest);
    }

    private void populateQueue(){
        workRequestQueue.enqueue(workRequestNormal);
        workRequestQueue.enqueue(workRequestVIP);
        workRequestQueue.enqueue(workRequestPriority);
        workRequestQueue.enqueue(workRequestMgmtOver);
    }
}
