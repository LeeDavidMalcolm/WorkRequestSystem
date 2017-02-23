package com.lee.workrequest.rest;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthNone;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.annotation.ApiVersion;
import org.jsondoc.core.pojo.ApiStage;
import org.jsondoc.core.pojo.ApiVisibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lee.workrequest.model.WorkRequest;

import java.time.LocalDateTime;
import java.util.List;

@Api(name = "Work Request System", description = "Functionality related to submitting and managing work requests", visibility = ApiVisibility.PUBLIC, stage = ApiStage.GA)
@ApiVersion(since = "0.0.1")
@ApiAuthNone
@RestController
@RequestMapping("/workrequests")
public class WorkRequestController {

    private static final Logger LOG = LoggerFactory.getLogger(WorkRequestController.class);

    private final WorkRequestService workRequestService;
    private final WorkRequestValidation workRequestValidation;

    @ApiObject(name = "GetOperation - AVERAGE_WAIT_TIME, IDS")
    public enum GetOperation {
        AVERAGE_WAIT_TIME, IDS;
    };

    @ApiObject(name = "GetIdOperation - POSTION")
    public enum GetIdOperation {
        POSITION;
    };

    @ApiObject(name = "DeleteOperation - TOP")
    public enum DeleteOperation {
        TOP;
    };

    public WorkRequestController(WorkRequestService workRequestService, WorkRequestValidation workRequestValidation) {
        this.workRequestService = workRequestService;
        this.workRequestValidation = workRequestValidation;
    }

    @ApiMethod(description = "Submit work request to the queue. Optionally pass enqueued time")
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void enqueue(@ApiPathParam(name = "id", description = "Requester id. Must not have submitted a work request already to be added")
    @PathVariable final long id,
    @ApiQueryParam(name = "time", description = "Time request will be enqueued at. ISO Date Time format")
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime time) {
        LOG.info("Enqueue request with id: {} and time: {}", id, time);

        workRequestValidation.idInRange(id);
        workRequestService.enqueue(id, time);
    }

    @ApiMethod(description = "Get work request queue or user operation for ids only or average wait time")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getWorkRequests(@ApiQueryParam(name = "operation", description = "Operation to perform on this resource")
    @RequestParam(required = false) final GetOperation operation,
    @ApiQueryParam(name = "time", description = "Used with AVERAGE_WAIT_TIME operation to define time to take duration to")
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime time) {
        LOG.info("Get work requests request with operation: {} and time: {}", operation, time);

        final HttpStatus responseStatus = HttpStatus.OK;
        ResponseEntity<?> responseEntity;

        if (operation == GetOperation.AVERAGE_WAIT_TIME){
            responseEntity = new ResponseEntity<>(workRequestService.getAverageWaitTime(time), responseStatus);
        } else if (operation == GetOperation.IDS){
            responseEntity = handleGetIds(responseStatus);
        } else {
            responseEntity = handleGetWorkRequests(responseStatus);
        }

        return responseEntity;
    }

    private ResponseEntity<List<Long>> handleGetIds(final HttpStatus responseStatus){
        final List<Long> sortedIds = workRequestService.getSortedIds();

        if(sortedIds.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(sortedIds, responseStatus);
        }
    }

    private ResponseEntity<List<WorkRequest>> handleGetWorkRequests(final HttpStatus responseStatus){
        final List<WorkRequest> sortedWorkRequests = workRequestService.getSortedWorkRequests();

        if(sortedWorkRequests.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(sortedWorkRequests, responseStatus);
        }
    }

    @ApiMethod(description = "Get specific work request or use operation to get position")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getForId(@ApiPathParam(name = "id", description = "Requester id")
    @PathVariable final long id,
    @ApiQueryParam(name = "operation", description = "Operation to perform on this resource")
    @RequestParam(required = false) final GetIdOperation operation) {
        LOG.info("Get work request with id: {} and operation: {}", id, operation);

        final HttpStatus responseStatus = HttpStatus.OK;
        ResponseEntity<?> responseEntity;

        if(operation == GetIdOperation.POSITION) {
            responseEntity = new ResponseEntity<>(workRequestService.getPosition(id), responseStatus);
        } else {
            responseEntity = new ResponseEntity<>(workRequestService.getWorkRequest(id), responseStatus);
        }

        return responseEntity;
    }

    @ApiMethod(description = "Remove specific work request")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void dequeue(@ApiPathParam(name = "id", description = "Requester id")
    @PathVariable final long id) {
        LOG.info("Dequeue work request with id: {}", id);

        workRequestValidation.idInRange(id);
        workRequestService.dequeue(id);
    }

    @ApiMethod(description = "Remove highest priority work request, requires operation")
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public long dequeue(@ApiQueryParam(name = "operation", description = "Operation to perform on this resource")
    @RequestParam(required = true) final DeleteOperation operation) {
        LOG.info("Dequeue work requests request with operation: {}", operation);

        // Only one operation type defined so proceed for TOP
        return workRequestService.dequeueTop();
    }


}
