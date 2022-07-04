package com.ttstranslate.di

import com.ttstranslate.feature.AppViewModel
import com.ttstranslate.feature.favorite.FavoritesViewModel
import com.ttstranslate.feature.main.BottomNavigationViewModel
import com.ttstranslate.feature.recognition.VoiceRecognitionViewModel
import com.ttstranslate.feature.translate.TranslateViewModel
import com.ttstranslate.global.viewmodel.NavigationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val viewModelModule = module {
    viewModel { NavigationViewModel(get()) }
    viewModel { AppViewModel() }
    viewModel { BottomNavigationViewModel(get()) }
    viewModel { TranslateViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { VoiceRecognitionViewModel(get()) }
    viewModel { FavoritesViewModel(get(), get(), get(), get(), get()) }
}