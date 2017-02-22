package com.lee.workrequest.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.lee.workrequest.exception.WorkRequestDoesNotExistException;
import com.lee.workrequest.exception.WorkRequestExistsException;
import com.lee.workrequest.exception.WorkRequestQueueEmptyException;
import com.lee.workrequest.model.WorkRequest;
import com.lee.workrequest.model.WorkRequestFactory;
import com.lee.workrequest.queue.WorkRequestQueue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WorkRequestServiceTest {

    @InjectMocks
    private WorkRequestService workRequestService;

    @Mock
    private WorkRequestFactory mockWorkRequestFactory;

    @Mock
    private WorkRequestQueue mockWorkRequestQueue;

    @Mock
    private WorkRequest mockWorkRequest;

    private static final long ID = 123;
    private static final LocalDateTime TIME = LocalDateTime.now();

    @Test
    public void enqueue_ValidParams_CreatesWorkRequestAndQueues(){

        when(mockWorkRequestFactory.getWorkRequest(ID, TIME)).thenReturn(mockWorkRequest);

        workRequestService.enqueue(ID, TIME);

        verify(mockWorkRequestQueue).enqueue(mockWorkRequest);
    }

    @Test
    public void enqueue_NoTime_CreatesWorkRequestAndQueues(){

        when(mockWorkRequestFactory.getWorkRequest(ID)).thenReturn(mockWorkRequest);

        workRequestService.enqueue(ID, null);

        verify(mockWorkRequestQueue).enqueue(mockWorkRequest);
    }

    @Test(expected = WorkRequestExistsException.class)
    public void enqueue_IdAlreadyExists_ThrowsException(){

        when(mockWorkRequestFactory.getWorkRequest(ID)).thenReturn(mockWorkRequest);
        when(mockWorkRequestQueue.enqueue(mockWorkRequest)).thenReturn(mockWorkRequest);

        workRequestService.enqueue(ID, null);
    }

    @Test
    public void getSortedWorkRequests_ReturnsSortedListFromQueue(){

        final List<WorkRequest> sortedWorkRequests = new ArrayList<>();
        sortedWorkRequests.add(mockWorkRequest);
        when(mockWorkRequestQueue.getSortedWorkRequests()).thenReturn(sortedWorkRequests);

        final List<WorkRequest> returnedWorkRequests= workRequestService.getSortedWorkRequests();

        assertEquals(sortedWorkRequests, returnedWorkRequests);
    }

    @Test
    public void getAverageWaitTime_NoTime_ReturnsAverageWaitTimeFromQueue(){

        final long averageWaitTime = 500;
        when(mockWorkRequestQueue.getAverageWaitTime()).thenReturn(averageWaitTime);

        final long returnedAverageTime= workRequestService.getAverageWaitTime(null);

        assertEquals(averageWaitTime, returnedAverageTime);
    }

    @Test
    public void getAverageWaitTime_WithTime_ReturnsAverageWaitTimeFromQueue(){

        final long averageWaitTime = 500;
        when(mockWorkRequestQueue.getAverageWaitTime(TIME)).thenReturn(averageWaitTime);

        final long returnedAverageTime= workRequestService.getAverageWaitTime(TIME);

        assertEquals(averageWaitTime, returnedAverageTime);
    }

    @Test
    public void getSortedIds_ReturnsSortedListFromQueue(){

        final List<Long> sortedIds = new ArrayList<>();
        sortedIds.add(123L);
        when(mockWorkRequestQueue.getSortedIds()).thenReturn(sortedIds);

        final List<Long> returnedSortedIds= workRequestService.getSortedIds();

        assertEquals(sortedIds, returnedSortedIds);
    }

    @Test
    public void getWorkRequest_ExistsForId_ReturnsWorkRequest(){

        when(mockWorkRequestQueue.get(ID)).thenReturn(mockWorkRequest);

        final WorkRequest returnedWorkRequest= workRequestService.getWorkRequest(ID);

        assertEquals(mockWorkRequest, returnedWorkRequest);
    }

    @Test(expected = WorkRequestDoesNotExistException.class)
    public void getWorkRequest_DoesntExistForId_ThrowsException(){

        when(mockWorkRequestQueue.get(ID)).thenReturn(null);

        workRequestService.getWorkRequest(ID);
    }

    @Test
    public void getPosition_ExistsForId_ReturnsPosition(){

        final int position = 1;
        when(mockWorkRequestQueue.getPosition(ID)).thenReturn(position);

        final int returnedPosition= workRequestService.getPosition(ID);

        assertEquals(position, returnedPosition);
    }

    @Test(expected = WorkRequestDoesNotExistException.class)
    public void getPosition_DoesntExistForId_ThrowsException(){

        when(mockWorkRequestQueue.getPosition(ID)).thenReturn(-1);

        workRequestService.getPosition(ID);
    }

    @Test
    public void dequeue_ExistsForId_Dequeues(){

        when(mockWorkRequestQueue.dequeue(ID)).thenReturn(mockWorkRequest);

        workRequestService.dequeue(ID);

        verify(mockWorkRequestQueue).dequeue(ID);
    }

    @Test(expected = WorkRequestDoesNotExistException.class)
    public void dequeue_DoesntExistForId_ThrowsException(){

        when(mockWorkRequestQueue.dequeue(ID)).thenReturn(null);

        workRequestService.dequeue(ID);
    }

    @Test
    public void dequeueTop_QueueHasElements_ReturnsId(){

        when(mockWorkRequestQueue.dequeueTop()).thenReturn(mockWorkRequest);
        when(mockWorkRequest.getId()).thenReturn(ID);

        final long removedId = workRequestService.dequeueTop();

        assertEquals(ID, removedId);
    }

    @Test(expected = WorkRequestQueueEmptyException.class)
    public void dequeueTop_QueueEmpty_ThrowsException(){

        when(mockWorkRequestQueue.dequeueTop()).thenReturn(null);

        workRequestService.dequeueTop();
    }
}
