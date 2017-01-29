package com.welt.model;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.annotation.JsonRawValue;

public class GenericResponse {

    private final String content;

    public GenericResponse(String content) {
        this.content = content;
    }

    @JsonRawValue
    public String getContent() {
        if(nonNull(content) && content.trim().isEmpty())
            return null;

        return content;
    }

}
