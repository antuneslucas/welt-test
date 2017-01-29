package com.welt.model;

import java.util.List;

public class GenericResponseWrapper {

    private final List<GenericResponse> responses;

    public GenericResponseWrapper(List<GenericResponse> responses) {
        this.responses = responses;
    }

    public List<GenericResponse> getResponses() {
        return responses;
    }

}
