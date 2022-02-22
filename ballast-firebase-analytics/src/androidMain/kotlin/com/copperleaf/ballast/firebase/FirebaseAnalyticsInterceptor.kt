package com.copperleaf.ballast.firebase

import com.copperleaf.ballast.BallastInterceptor
import com.copperleaf.ballast.BallastNotification
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import kotlin.reflect.KClass

class FirebaseAnalyticsInterceptor<Inputs : Any, Events : Any, State : Any>(
    private val analytics: FirebaseAnalytics,
) : BallastInterceptor<Inputs, Events, State> {

    private object Keys {
        const val ViewModelName = "ViewModelName"
        const val InputType = "InputType"
        const val InputValue = "InputValue"
    }

    /**
     * Mark an Input as one that should be tracked automatically in Firebase Analytics. Inputs
     * without this annotation are ignored.
     */
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    annotation class TrackInput

    override suspend fun onNotify(notification: BallastNotification<Inputs, Events, State>) {
        if (notification is BallastNotification.InputAccepted) {
            if (notification.input.isAnnotatedWith(TrackInput::class)) {
                analytics.logEvent("action") {
                    param(Keys.ViewModelName, notification.vm.name)
                    param(Keys.InputType, "${notification.vm.name}.${notification.input::class.java.simpleName}")
                    param(Keys.InputValue, "${notification.vm.name}.${notification.input}")
                }
            }
        }
    }

    private fun <T : Any, Ann : Annotation> T.isAnnotatedWith(annotationClass: KClass<Ann>): Boolean {
        return this::class.java.isAnnotationPresent(annotationClass.java)
    }
}
