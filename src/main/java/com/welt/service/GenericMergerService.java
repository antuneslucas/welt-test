package com.welt.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import com.welt.model.GenericResponseWrapper;

public interface GenericMergerService {

    DeferredResult<ResponseEntity<GenericResponseWrapper>> getDeferredResult(List<String> urls);

}
