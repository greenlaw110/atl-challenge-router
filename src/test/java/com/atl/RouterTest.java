package com.atl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import osgl.ut.TestBase;

public class RouterTest extends TestBase {
    private Router router;

    @Before
    public void setup() {
        router = new Router();
    }

    @Test
    public void itShallDispatchStaticRouteOnDifferentMethod() {
        // Arrange
        Handler getHandler = Mockito.mock(Handler.class);
        Handler postHandler = Mockito.mock(Handler.class);
        router.register(HttpMethod.GET, "/a/b/c", getHandler);
        router.register(HttpMethod.POST, "/a/b/c", postHandler);
        Request getReq = new Request(HttpMethod.GET, "/a/b/c");
        Request postReq = new Request(HttpMethod.POST, "/a/b/c");

        // Act
        Handler h1 = router.dispatch(getReq);
        Handler h2 = router.dispatch(postReq);

        // Assert
        same(getHandler, h1);
        same(postHandler, h2);
    }

    @Test
    public void itShallReturnNotFoundIfRouteNotRegistered() {
        // Arrange
        Request req = new Request(HttpMethod.GET, "/a/b/c");

        // Act
        Handler h = router.dispatch(req);

        // Assert
        same(Handler.NOT_FOUND, h);
    }

    @Test
    public void itShallDispatchDynamicRoute() {
        // Arrange
        Handler prodNameHandler = Mockito.mock(Handler.class);
        router.register(HttpMethod.GET, "/products/{id}/name", prodNameHandler);
        Request req = new Request(HttpMethod.GET, "/products/1/name");

        // Act
        Handler h = router.dispatch(req);

        // Assert
        same(prodNameHandler, h);
        eq("1", req.getArguments().get("id"));
    }

    @Test
    public void itShallDispatchDynamicRouteAccordingToRegEx() {
        // Arrange
        Handler prodNameHandler = Mockito.mock(Handler.class);
        router.register(HttpMethod.GET, "/products/{id:[0-9]+}/name", prodNameHandler);
        Request validReq = new Request(HttpMethod.GET, "/products/1/name");
        Request invalidReq = new Request(HttpMethod.GET, "/products/a/name");

        // Act & Assert
        Handler h = router.dispatch(validReq);
        same(prodNameHandler, h);
        eq("1", validReq.getArguments().get("id"));
        h = router.dispatch(invalidReq);
        same(Handler.NOT_FOUND, h);
    }
}
