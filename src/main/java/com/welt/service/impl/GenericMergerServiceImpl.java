package com.welt.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.welt.model.GenericResponse;
import com.welt.model.GenericResponseWrapper;
import com.welt.service.GenericMergerService;

@Service
public class GenericMergerServiceImpl implements GenericMergerService {

    private static final Logger LOG = LoggerFactory.getLogger(GenericMergerServiceImpl.class);

    private final ListeningExecutorService executorService;
    private final RestTemplate restTemplate;

    @Autowired
    public GenericMergerServiceImpl(ListeningExecutorService executorService, RestTemplate restTemplate) {
        this.executorService = executorService;
        this.restTemplate = restTemplate;
    }

    @Override
    public DeferredResult<ResponseEntity<GenericResponseWrapper>> getDeferredResult(List<String> urls) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        DeferredResult<ResponseEntity<GenericResponseWrapper>> deferredResult = new DeferredResult<>();
        LOG.debug("Triggering {} requests for the received urls", urls.size());

        Futures.addCallback(Futures.allAsList(submitRequests(urls)), new FutureCallback<List<ResponseEntity<String>>>() {
            @Override
            public void onSuccess(List<ResponseEntity<String>> result) {
                deferredResult.setResult(buildWrapperResponse(result));

                LOG.info("Getting ACTUAL results in: {} ms", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
            }

            @Override
            public void onFailure(Throwable t) {
                deferredResult.setResult(ResponseEntity.badRequest().build());
                LOG.error("Something went wrong during the request calls", t);
            }
        });

        LOG.debug("Method getDeferredResult exited");
        return deferredResult;
    }

    private List<ListenableFuture<ResponseEntity<String>>> submitRequests(List<String> urls) {
        return urls.stream()
                .map(url -> executorService.submit(() -> restTemplate.getForEntity(url, String.class)))
                .collect(Collectors.toList());
    }

    private ResponseEntity<GenericResponseWrapper> buildWrapperResponse(List<ResponseEntity<String>> result) {
        return ResponseEntity.ok(
                new GenericResponseWrapper(
                    result.stream()
                        .map(ResponseEntity::getBody)
                        .map(GenericResponse::new)
                        .collect(Collectors.toList())
                )
            );
    }

}
