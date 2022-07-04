package com.ttstranslate.global.navigation

import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen

class AppRouter : Router() {
    fun forwardTo(flow: Screen) = executeCommands(ForwardTo(flow))

    fun toTop(flow: Screen) = executeCommands(ToTop(flow))
}
