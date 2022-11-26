package com.atl;

import org.osgl.http.H;

public class Router {

    private final RouteNode GET_ROOT = new RouteNode("");
    private final RouteNode POST_ROOT = new RouteNode("");

    public void register(HttpMethod method, String route, Handler handler) {
        RouteNode root = root(method);
        root.register(route, handler);
    }

    public Handler dispatch(Request request) {
        RouteNode root = root(request.getMethod());
        return root.dispatch(request);
    }

    private RouteNode root(HttpMethod method) {
        switch (method) {
            case GET: return GET_ROOT;
            case POST: return POST_ROOT;
            default:
                throw new UnsupportedOperationException("http method not supported: " + method);
        }
    }
}
