package com.lee.workrequest.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class WorkRequestFactoryTest {

    private WorkRequestFactory workRequestFactory;

    @Before
    public void setup(){
        workRequestFactory = new WorkRequestFactory();
    }

    @Test
    public void getWorkRequest_IdOnly_SetsIdAndEnqueuedTime(){

        final long id = 123;

        final WorkRequest workRequest = workRequestFactory.getWorkRequest(id);

        assertEquals(id, workRequest.getId());
        assertNotNull(workRequest.getEnqueuedTime());
    }

    @Test
    public void getWorkRequest_IdAndTime_SetsIdAndEnqueuedTime(){

        final long id = 123;
        final LocalDateTime time = LocalDateTime.now().plusHours(1);

        final WorkRequest workRequest = workRequestFactory.getWorkRequest(id, time);

        assertEquals(id, workRequest.getId());
        assertEquals(time, workRequest.getEnqueuedTime());
    }

    @Test
    public void getWorkRequest_IdDividesByThreeAndFive_ReturnsWorkRequestManagementOverride(){

        final WorkRequest workRequest = workRequestFactory.getWorkRequest(15);

        assertThat(workRequest, instanceOf(WorkRequestManagementOverride.class));
    }

    @Test
    public void getWorkRequest_IdDividesByFive_ReturnsWorkRequestVIP(){

        final WorkRequest workRequest = workRequestFactory.getWorkRequest(5);

        assertThat(workRequest, instanceOf(WorkRequestVIP.class));
    }

    @Test
    public void getWorkRequest_IdDividesByThree_ReturnsWorkRequestPriority(){

        final WorkRequest workRequest = workRequestFactory.getWorkRequest(3);

        assertThat(workRequest, instanceOf(WorkRequestPriority.class));
    }

    @Test
    public void getWorkRequest_IdDoesntDivideByThreeOrFive_ReturnsWorkRequestNormal(){

        final WorkRequest workRequest = workRequestFactory.getWorkRequest(2);

        assertThat(workRequest, instanceOf(WorkRequestNormal.class));
    }
}
