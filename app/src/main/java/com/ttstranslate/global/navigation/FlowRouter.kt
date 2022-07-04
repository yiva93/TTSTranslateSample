package com.ttstranslate.global.navigation

import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen

class FlowRouter(private val appRouter: AppRouter) : Router() {
    fun startFlow(screen: Screen) = appRouter.navigateTo(screen)

    fun finishFlow() = appRouter.exit()

    fun newRootFlow(flow: Screen) = appRouter.newRootScreen(flow)

    fun replaceFlow(flow: Screen) = appRouter.replaceScreen(flow)

    fun forwardTo(flow: Screen) = appRouter.forwardTo(flow)

    fun toTop(flow: Screen) = appRouter.toTop(flow)
}
