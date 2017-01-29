package com.welt.controller;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.google.common.base.Stopwatch;
import com.welt.model.GenericResponseWrapper;
import com.welt.service.GenericMergerService;

@RestController
public class GenericMergerController {

    private static final Logger LOG = LoggerFactory.getLogger(GenericMergerController.class);

    private final GenericMergerService genericMergerService;

    @Autowired
    public GenericMergerController(GenericMergerService genericMergerService) {
        this.genericMergerService = genericMergerService;
    }

    @RequestMapping(value = "/api/generic-merger/{userId}", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<GenericResponseWrapper>> genericAsyncGet(@PathVariable("userId") Integer userId) {
        return genericAsyncPost(Arrays.asList(
                "http://jsonplaceholder.typicode.com/users/" + userId,
                "http://jsonplaceholder.typicode.com/posts?userId=" + userId,
                "http://jsonplaceholder.typicode.com/posts/" + userId + "/comments"));
    }

    @RequestMapping(value = "/api/generic-merger", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity<GenericResponseWrapper>> genericAsyncPost(@RequestBody List<String> urls) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        DeferredResult<ResponseEntity<GenericResponseWrapper>> result = genericMergerService.getDeferredResult(urls);

        LOG.info("Generic Async method exited in: {} ms", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        return result;
    }

}
