package com.ttstranslate.global.viewmodel

import com.github.terrakok.cicerone.Screen
import com.ttstranslate.global.navigation.AppRouter
import com.ttstranslate.global.navigation.FlowNavigationViewModel

class NavigationViewModel(appRouter: AppRouter) : FlowNavigationViewModel(appRouter) {
    fun startFlow(screen: Screen) = router.startFlow(screen)

    fun replaceFlow(screen: Screen) = router.replaceFlow(screen)

    fun newRootFlow(screen: Screen) = router.newRootFlow(screen)

    fun finishFlow() = router.finishFlow()

    fun navigateTo(screen: Screen) = router.navigateTo(screen)

    fun exit() = router.exit()
}
