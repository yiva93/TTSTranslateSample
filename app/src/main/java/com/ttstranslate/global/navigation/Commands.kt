package com.ttstranslate.global.navigation

import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Screen

/**
 * Opens new screen if screen not exists on stack top
 */
class ForwardTo(val screen: Screen) : Command

/*
 * Opens new flow on top of current with transparent background
 */
class ToTop(val screen: Screen) : Command