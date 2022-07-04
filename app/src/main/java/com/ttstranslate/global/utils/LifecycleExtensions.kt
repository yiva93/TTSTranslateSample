package com.ttstranslate.global.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Observe LiveData with the caller lifecycle (onCreate() / onDestroy())
 */
fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observer: Observer<T>) =
    liveData.observe(this, observer)

/**
 * Observe LiveData with the fragment's view lifecycle
 */
fun <T> Fragment.viewObserve(liveData: LiveData<T>, observer: Observer<T>) =
    liveData.observe(viewLifecycleOwner, observer)

/**
 * Remove LiveData observers from the fragment's view lifecycle
 */
fun <T> Fragment.viewUnobserve(liveData: LiveData<T>) =
    liveData.removeObservers(viewLifecycleOwner)

val ViewModel.context
    get() = viewModelScope.coroutineContext

fun <T> LiveData<T>.debounce(duration: Long = 1000L, coroutineScope: CoroutineScope) =
    MediatorLiveData<T>().also { mld ->
        val source = this
        var job: Job? = null

        mld.addSource(source) {
            job?.cancel()
            job = coroutineScope.launch {
                delay(duration)
                mld.value = source.value
            }
        }
    }

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this
