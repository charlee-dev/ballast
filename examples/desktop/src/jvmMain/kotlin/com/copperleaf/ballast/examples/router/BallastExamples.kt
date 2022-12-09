package com.copperleaf.ballast.examples.router

import com.copperleaf.ballast.navigation.routing.Route
import com.copperleaf.ballast.navigation.routing.RouteAnnotation
import com.copperleaf.ballast.navigation.routing.RouteMatcher

enum class BallastExamples(
    routeFormat: String,
    override val annotations: Set<RouteAnnotation> = emptySet(),
) : Route {
    Counter("/examples/counter"),
    Scorekeeper("/examples/scorekeeper"),
    Sync("/examples/sync"),
    Undo("/examples/undo"),
    ApiCall("/examples/api-call"),
    KitchenSink("/examples/kitchen-sink?inputStrategy={?}"),
    ;

    override val matcher: RouteMatcher = RouteMatcher.create(routeFormat)
}
