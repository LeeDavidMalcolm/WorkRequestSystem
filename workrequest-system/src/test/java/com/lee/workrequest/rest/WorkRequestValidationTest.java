package com.lee.workrequest.rest;

import org.junit.Before;
import org.junit.Test;

import com.lee.workrequest.exception.WorkRequestInvalidIdException;

public class WorkRequestValidationTest {

    private WorkRequestValidation workRequestValidation;

    @Before
    public void setup(){
        workRequestValidation = new WorkRequestValidation();
    }

    @Test
    public void idInRange_IdIsOne_NoException(){

        workRequestValidation.idInRange(1);
    }

    @Test
    public void idInRange_IdMaxLongValue_NoException(){

        workRequestValidation.idInRange(9223372036854775807L);
    }

    @Test(expected = WorkRequestInvalidIdException.class)
    public void idInRange_IdLessThan1_ThrowsException(){

        workRequestValidation.idInRange(0);
    }
}
