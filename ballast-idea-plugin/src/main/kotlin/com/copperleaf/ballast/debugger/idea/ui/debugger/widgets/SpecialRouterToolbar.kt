@file:Suppress("UNUSED_PARAMETER")
package com.copperleaf.ballast.debugger.idea.ui.debugger.widgets

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import com.copperleaf.ballast.debugger.idea.ui.debugger.DebuggerUiContract

@Composable
fun ColumnScope.SpecialRouterToolbar(
    url: String?,
    postInput: (DebuggerUiContract.Inputs) -> Unit,
) {
    if (url != null) {
//        ToolbarRow {
//            TextField(
//                value = url,
//                onValueChange = { }, // ignore
//                modifier = Modifier.fillMaxWidth(),
//            )
//        }
    }
}
