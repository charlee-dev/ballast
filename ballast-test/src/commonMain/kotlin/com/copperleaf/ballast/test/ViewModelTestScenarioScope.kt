package com.copperleaf.ballast.test

import com.copperleaf.ballast.BallastInterceptor
import com.copperleaf.ballast.core.LoggingInterceptor
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
public interface ViewModelTestScenarioScope<Inputs : Any, Events : Any, State : Any> {
    public val name: String

    /**
     * A callback function for viewing logs emitted during this test scenario. This includes logs from a
     * [LoggingInterceptor], and additional logs from this test runner.
     */
    public fun logger(block: (String) -> Unit)

    /**
     * Set the timeout for waiting for test side-effects to complete for this test scenario.
     */
    public fun timeout(timeout: Duration)

    /**
     * Provide an alternative starting state for this scenario. Overrides the default starting state provided to the
     * entire suite.
     */
    public fun given(block: () -> State)

    /**
     * The input sequence that this scenario will execute. Inputs are processed in order, and the block will suspend for
     * one input to actually be accepted and be processed by the ViewModel before starting to process the next.
     *
     * This entire script will run to completion and all sent inputs will be handled before final test results are
     * collected and sent to [ViewModelTestScenarioScope.resultsIn] for verification.
     */
    public fun running(block: suspend ViewModelTestScenarioInputSequenceScope<Inputs, Events, State>.() -> Unit)

    /**
     * Once the scneario test script in [ViewModelTestScenarioScope.running] has completed, inspect and make assertions
     * on what actually happened during the test, and what it produced as a result. The properties in [TestResults]
     * correspond directly to the callbacks of [BallastInterceptor], and the relative ordering of properties in each
     * list is maintained with respect to the order the inputs were delivered to the test.
     */
    public fun resultsIn(block: TestResults<Inputs, Events, State>.() -> Unit)
}
