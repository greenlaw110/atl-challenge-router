# Router

## Requirement

Design a http request router that can dispatch request to request handler
- dispatch static route: /a/b/c
- dispatch dynamic route: /a/{v}/c where `v` is variable could be anything
- dispatch dynamic route with regex match: /a/{v:(a[0-9]+)/, where `v` is variable but needs to be `a` followed by number

## Models and concepts

- Router
  - register request handler to route
  - dispatch request to handler
- RequestHandler
  - A dumb interface in this challenge
- HttpMethod
  - Enum for GET/POST
- RouteNode
  - Encapsulate a node in a route path
- Request
  - A request to be routed
  - route: String
  - method: HttpMethod
  - argument: Map
