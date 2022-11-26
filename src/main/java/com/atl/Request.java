package com.atl;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private HttpMethod method;
    private String route;
    private Map<String, String> arguments;

    public Request(HttpMethod method, String route) {
        this.method = method;
        this.route = route;
        this.arguments = new HashMap<>();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRoute() {
        return route;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }
}
