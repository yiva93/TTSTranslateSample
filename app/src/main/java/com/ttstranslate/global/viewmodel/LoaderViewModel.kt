package com.ttstranslate.global.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler

@Suppress("CanSealedSubClassBeObject")
sealed class LoadState {
    class Loading : LoadState()
    class Loaded(val data: Any? = null) : LoadState()
    class Error(val t: Throwable?, val message: String?) : LoadState()
}

@Suppress("MemberVisibilityCanBePrivate")
abstract class LoaderViewModel : ViewModel() {
    private val _loadLiveData: MutableLiveData<LoadState> = MutableLiveData()
    val loadLiveData: LiveData<LoadState> = _loadLiveData

    protected fun errorCatcher(callback: ((Throwable) -> Unit)? = null) =
        CoroutineExceptionHandler { _, throwable ->
            callback?.invoke(throwable) ?: onError(throwable)
        }

    protected open fun showProgress() {
        _loadLiveData.value = LoadState.Loading()
    }

    protected open fun hideProgress() {
        _loadLiveData.value = LoadState.Loaded()
    }

    protected open fun <T> showData(data: T) {
        _loadLiveData.value = LoadState.Loaded(data = data)
    }

    protected open fun onError(error: Throwable? = null, message: String? = error?.message) {
        _loadLiveData.value = LoadState.Error(error, message)
    }
}

