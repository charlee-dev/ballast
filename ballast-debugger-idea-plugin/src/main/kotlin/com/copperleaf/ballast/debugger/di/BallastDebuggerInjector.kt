package com.copperleaf.ballast.debugger.di

import androidx.compose.runtime.compositionLocalOf
import com.copperleaf.ballast.BallastLogger
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.InputStrategy
import com.copperleaf.ballast.debugger.BallastDebuggerClientConnection
import com.copperleaf.ballast.debugger.idea.BallastIdeaPlugin
import com.copperleaf.ballast.debugger.idea.settings.IdeaPluginPrefs
import com.copperleaf.ballast.debugger.idea.settings.IdeaPluginPrefsImpl
import com.copperleaf.ballast.debugger.ui.debugger.DebuggerEventHandler
import com.copperleaf.ballast.debugger.ui.debugger.DebuggerInputHandler
import com.copperleaf.ballast.debugger.ui.debugger.DebuggerViewModel
import com.copperleaf.ballast.debugger.ui.sample.SampleEventHandler
import com.copperleaf.ballast.debugger.ui.sample.SampleInputHandler
import com.copperleaf.ballast.debugger.ui.sample.SampleViewModel
import com.copperleaf.ballast.debugger.ui.samplecontroller.SampleControllerEventHandler
import com.copperleaf.ballast.debugger.ui.samplecontroller.SampleControllerInputHandler
import com.copperleaf.ballast.debugger.ui.samplecontroller.SampleControllerViewModel
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import io.github.copper_leaf.ballast_debugger_idea_plugin.BALLAST_VERSION
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val LocalInjector = compositionLocalOf<BallastDebuggerInjector> { error("LocalInjector not provided") }

interface BallastDebuggerInjector {
    val repoBaseUrl: String
    val sampleSourcesPathInRepo: String

    fun debuggerViewModel(
        coroutineScope: CoroutineScope,
    ): DebuggerViewModel

    fun sampleControllerViewModel(
        coroutineScope: CoroutineScope,
    ): SampleControllerViewModel

    fun sampleViewModel(
        coroutineScope: CoroutineScope,
        inputStrategy: InputStrategy,
    ): SampleViewModel

    companion object {
        private val injectors = mutableMapOf<Project, BallastDebuggerInjector>()

        fun getInstance(project: Project): BallastDebuggerInjector {
            return injectors.getOrPut(project) { BallastDebuggerInjectorImpl(project) }
        }
    }
}

class BallastDebuggerInjectorImpl(
    private val project: Project,
) : BallastDebuggerInjector {
    override val repoBaseUrl: String =
        "https://github.com/copper-leaf/ballast/tree/$BALLAST_VERSION"
    override val sampleSourcesPathInRepo: String =
        "ballast-debugger-idea-plugin/src/main/kotlin/com/copperleaf/ballast/debugger/ui/sample"

    private val ideaPluginLogger: Logger = Logger.getInstance(BallastIdeaPlugin::class.java)
    private val prefs: IdeaPluginPrefs = IdeaPluginPrefsImpl(project)
    private val toolWindowManager: ToolWindowManager get() = ToolWindowManager.getInstance(project)
    private val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val debuggerConnection by lazy {
        BallastDebuggerClientConnection(CIO, applicationScope).also { it.connect() }
    }

    private fun commonBuilder(): BallastViewModelConfiguration.Builder {
        return BallastViewModelConfiguration.Builder()
            .apply {
                logger = object : BallastLogger {
                    override fun debug(message: String) {
                        ideaPluginLogger.debug(message)
                    }

                    override fun info(message: String) {
                        ideaPluginLogger.info(message)
                    }

                    override fun error(throwable: Throwable) {
                        ideaPluginLogger.error(throwable)
                    }
                }
            }
    }

    override fun debuggerViewModel(coroutineScope: CoroutineScope): DebuggerViewModel {
        return DebuggerViewModel(
            coroutineScope = coroutineScope,
            configurationBuilder = commonBuilder(),
            inputHandler = DebuggerInputHandler(prefs),
            eventHandler = DebuggerEventHandler(),
        )
    }

    override fun sampleControllerViewModel(coroutineScope: CoroutineScope): SampleControllerViewModel {
        return SampleControllerViewModel(
            coroutineScope = coroutineScope,
            configurationBuilder = commonBuilder(),
            inputHandler = SampleControllerInputHandler(this, prefs),
            eventHandler = SampleControllerEventHandler(),
        )
    }

    override fun sampleViewModel(
        coroutineScope: CoroutineScope,
        inputStrategy: InputStrategy,
    ): SampleViewModel {
        return SampleViewModel(
            viewModelCoroutineScope = coroutineScope,
            configurationBuilder = commonBuilder(),
            debuggerConnection = debuggerConnection,
            inputStrategy = inputStrategy,
            inputHandler = SampleInputHandler(),
            eventHandler = SampleEventHandler(onWindowClosed = {
                toolWindowManager.getToolWindow("Ballast Sample")?.hide()
            }),
        )
    }
}

