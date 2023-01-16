package com.copperleaf.ballast.core

import com.copperleaf.ballast.BallastLogger

@Deprecated(
    "Use JsConsoleLogger instead. Deprecated since v3, to be removed in v4.",
    replaceWith = ReplaceWith("JsConsoleLogger", "com.copperleaf.ballast.core.JsConsoleLogger")
)
/**
 * An implementation of a [BallastLogger] which writes log messages to the JavaScript `console`.
 *
 * This class has been deprecated, and you should use JsConsoleLogger instead to keep more consistent naming of Loggers.
 * Deprecated since v3, to be removed in v4.
 */
public class JsConsoleBallastLogger(private val tag: String? = null) : BallastLogger {
    override fun debug(message: String) {
        console.log(formatMessage(tag, message))
    }

    override fun info(message: String) {
        console.info(formatMessage(tag, message))
    }

    override fun error(throwable: Throwable) {
        console.error(throwable)
    }
}
