package com.lee.workrequest.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lee.workrequest.model.WorkRequest;
import com.lee.workrequest.rest.WorkRequestController.DeleteOperation;
import com.lee.workrequest.rest.WorkRequestController.GetIdOperation;
import com.lee.workrequest.rest.WorkRequestController.GetOperation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WorkRequestControllerTest {

    @InjectMocks
    private WorkRequestController workRequestController;

    @Mock
    private WorkRequestService mockWorkRequestService;

    @Mock
    private WorkRequestValidation mockWorkRequestValidation;

    private static final long ID = 123;
    private static final LocalDateTime TIME = LocalDateTime.now();

    @Test
    public void enqueue_ValidParams_ValidatesAndServices(){

        workRequestController.enqueue(ID, TIME);

        verify(mockWorkRequestValidation).idInRange(ID);
        verify(mockWorkRequestService).enqueue(ID, TIME);
    }

    @Test
    public void getWorkRequests_OperationAverageWaitTime_ReturnsOKAndAverageWaitTime(){

        final long averageWaitTime = 500;
        when(mockWorkRequestService.getAverageWaitTime(TIME)).thenReturn(averageWaitTime);

        final ResponseEntity<?> response = workRequestController.getWorkRequests(GetOperation.AVERAGE_WAIT_TIME, TIME);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(averageWaitTime, response.getBody());
    }

    @Test
    public void getWorkRequests_OperationIds_ReturnsOKAndListOfIds(){

        final List<Long> sortedIds = new ArrayList<>();
        sortedIds.add(123L);
        sortedIds.add(456L);
        when(mockWorkRequestService.getSortedIds()).thenReturn(sortedIds);

        final ResponseEntity<?> response = workRequestController.getWorkRequests(GetOperation.IDS, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sortedIds, response.getBody());
    }

    @Test
    public void getWorkRequests_OperationIdsListEmpty_ReturnsNoContent(){

        when(mockWorkRequestService.getSortedIds()).thenReturn(new ArrayList<>());

        final ResponseEntity<?> response = workRequestController.getWorkRequests(GetOperation.IDS, null);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void getWorkRequests_NoOperation_ReturnsOKAndListOfWorkRequests(){

        final List<WorkRequest> sortedWorkRequests = new ArrayList<>();
        sortedWorkRequests.add(mock(WorkRequest.class));
        sortedWorkRequests.add(mock(WorkRequest.class));
        when(mockWorkRequestService.getSortedWorkRequests()).thenReturn(sortedWorkRequests);

        final ResponseEntity<?> response = workRequestController.getWorkRequests(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sortedWorkRequests, response.getBody());
    }

    @Test
    public void getWorkRequests_NoOperationListEmpty_ReturnsNoContent(){

        when(mockWorkRequestService.getSortedWorkRequests()).thenReturn(new ArrayList<>());

        final ResponseEntity<?> response = workRequestController.getWorkRequests(null, null);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void getForId_OperationPosition_ReturnsOKAndPosition(){

        final int position = 1;
        when(mockWorkRequestService.getPosition(ID)).thenReturn(position);

        final ResponseEntity<?> response = workRequestController.getForId(ID, GetIdOperation.POSITION);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(position, response.getBody());
    }

    @Test
    public void getForId_NoOperation_ReturnsOKAndWorkRequest(){

        final WorkRequest mockWorkRequest = mock(WorkRequest.class);
        when(mockWorkRequestService.getWorkRequest(ID)).thenReturn(mockWorkRequest);

        final ResponseEntity<?> response = workRequestController.getForId(ID, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockWorkRequest, response.getBody());
    }

    @Test
    public void dequeue_ValidParams_ValidatesAndServices(){

        workRequestController.dequeue(ID);

        verify(mockWorkRequestValidation).idInRange(ID);
        verify(mockWorkRequestService).dequeue(ID);
    }

    @Test
    public void dequeue_OperationTop_ReturnsOKAndId(){

        when(mockWorkRequestService.dequeueTop()).thenReturn(ID);

        final long returnedID = workRequestController.dequeue(DeleteOperation.TOP);

        assertEquals(ID, returnedID);
    }
}
