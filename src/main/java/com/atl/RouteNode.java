package com.atl;

import org.osgl.util.S;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

class RouteNode {

    private String name;
    private Handler handler;
    private Map<String, RouteNode> subStaticRoutes;

    private RouteNode dynamicNode;
    private String variableName;

    private Pattern pattern;

    public RouteNode(String name) {
        this.name = name;
        this.subStaticRoutes = new HashMap<>();
    }

    void register(String route, Handler handler) {
        Deque<String> tokens = split(route);
        register(tokens, Objects.requireNonNull(handler));
    }

    Handler dispatch(Request request) {
        String route = request.getRoute();
        Deque<String> tokens = split(route);
        return dispatch(tokens, request);
    }

    private void register(Deque<String> tokens, Handler handler) {
        if (tokens.isEmpty()) {
            this.handler = handler;
            return;
        }
        String token = tokens.pollFirst();
        if (isVariable(token)) {
            String s = S.strip(token).of(S.CURLY_BRACES);
            String variableName = parseRegex(s);
            if (this.variableName != null && !variableName.equals(this.variableName)) {
                throw new RuntimeException("variable name conflict");
            }
            this.variableName = variableName;
            if (dynamicNode == null) {
                dynamicNode = new RouteNode(token);
            }
            dynamicNode.register(tokens, handler);
        } else {
            RouteNode staticChild = staticChild(token);
            staticChild.register(tokens, handler);
        }
    }

    private Handler dispatch(Deque<String> tokens, Request request) {
        if (tokens.isEmpty()) {
            return handler();
        }
        String token = tokens.pollFirst();
        RouteNode staticChild = subStaticRoutes.get(token);
        if (null != staticChild) {
            return staticChild.dispatch(tokens, request);
        }
        if (null != dynamicNode) {
            if (null != pattern && !pattern.matcher(token).matches()) {
                return Handler.NOT_FOUND;
            }
            request.getArguments().put(variableName, token);
            return dynamicNode.dispatch(tokens, request);
        }
        return Handler.NOT_FOUND;
    }

    private RouteNode staticChild(String name) {
        return subStaticRoutes.computeIfAbsent(name, (k) -> new RouteNode(name));
    }

    private Deque<String> split(String route) {
        List<String> list = S.fastSplit(route, "/");
        return new LinkedList<>(list);
    }

    private Handler handler() {
        return null == handler ? Handler.NOT_FOUND : handler;
    }

    private boolean isVariable(String token) {
        return S.is(token).wrappedWith(S.CURLY_BRACES);
    }

    private String parseRegex(String token) {
        if (token.contains(":")) {
            S.Pair pair = S.binarySplit(token, ':');
            pattern = Pattern.compile(pair.second());
            return pair.first();
        }
        return token;
    }
}
