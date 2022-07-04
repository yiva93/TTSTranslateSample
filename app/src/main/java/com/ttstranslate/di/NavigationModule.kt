package com.ttstranslate.di

import com.github.terrakok.cicerone.Cicerone
import com.ttstranslate.global.navigation.AppRouter
import org.koin.dsl.module

internal val navigationModule = module {
    val cicerone: Cicerone<AppRouter> = Cicerone.create(AppRouter())
    single { cicerone.router }
    single { cicerone.getNavigatorHolder() }
}