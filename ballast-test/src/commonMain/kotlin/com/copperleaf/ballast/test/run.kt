package com.copperleaf.ballast.test

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.InputFilter
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.test.internal.ViewModelTestSuiteScopeImpl
import com.copperleaf.ballast.test.internal.runTestSuite
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
public suspend fun <Inputs : Any, Events : Any, State : Any> viewModelTest(
    inputHandler: InputHandler<Inputs, Events, State>,
    eventHandler: EventHandler<Inputs, Events, State>,
    filter: InputFilter<Inputs, Events, State>? = null,
    block: ViewModelTestSuiteScope<Inputs, Events, State>.() -> Unit
) {
    val testSuite = ViewModelTestSuiteScopeImpl(
        inputHandler = inputHandler,
        eventHandler = eventHandler,
        filter = filter,
    ).apply(block)

    runTestSuite(testSuite)
}
