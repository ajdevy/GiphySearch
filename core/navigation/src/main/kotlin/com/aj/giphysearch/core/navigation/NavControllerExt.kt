package com.aj.giphysearch.core.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

internal fun canNavigateForState(state: Lifecycle.State?): Boolean =
    state?.isAtLeast(Lifecycle.State.RESUMED) == true

fun NavController.navigateIfResumed(
    route: Any,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    if (canNavigateForState(currentBackStackEntry?.lifecycle?.currentState)) {
        navigate(route, builder)
    }
}
