package com.copperleaf.ballast.debugger.idea.settings

import com.copperleaf.ballast.debugger.idea.ui.debugger.DebuggerUiSettings
import com.copperleaf.ballast.debugger.server.BallastDebuggerServerSettings

/**
 * All settings for the plugin, wrapped into a single interface.
 *
 * See [IntellijPluginMutableSettings] for a mutable variant of settings.
 */
interface IntellijPluginSettings : DebuggerUiSettings, BallastDebuggerServerSettings
