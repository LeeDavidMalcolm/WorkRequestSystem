package com.lee.workrequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lee.workrequest.model.WorkRequestFactory;
import com.lee.workrequest.queue.WorkRequestQueue;
import com.lee.workrequest.rest.WorkRequestService;
import com.lee.workrequest.rest.WorkRequestValidation;

@Configuration
public class AppConfig {

    @Bean
    public WorkRequestValidation workRequestValidation() {
        return new WorkRequestValidation();
    }

    @Bean
    public WorkRequestService workRequestService() {
        return new WorkRequestService(workRequestFactory(), workRequestQueue());
    }

    @Bean
    public WorkRequestFactory workRequestFactory() {
        return new WorkRequestFactory();
    }

    @Bean
    public WorkRequestQueue workRequestQueue() {
        return new WorkRequestQueue();
    }
}
