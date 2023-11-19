package com.damon.demo.common.http;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpResponse {
    private int code;

    private String response;

    public Boolean is2xx() {
        return code >= 200 || code <= 206;
    }

    public Boolean is5xx() {
        return code >= 500 || code <= 505;
    }

    public Boolean is4xx() {
        return code >= 400 || code <= 416;
    }

}
