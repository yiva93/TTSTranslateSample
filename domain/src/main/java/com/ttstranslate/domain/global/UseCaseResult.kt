package com.ttstranslate.domain.global

class UseCaseResult<out R>(
    val data: R? = null,
    val error: Exception? = null
) {
    override fun toString(): String {
        return error?.let {
            "Error[exception=$error]"
        } ?: run {
            "Success[data=$data]"
        }
    }

    val isSuccessful : Boolean
        get() = (error == null) && (data != null)

    suspend fun process(
        onSuccessCallback: suspend (R) -> Unit,
        onErrorCallback: (suspend (Exception) -> Unit)? = null
    ): UseCaseResult<R> {
        error?.let {
            onErrorCallback?.invoke(it)
        } ?: run {
            data?.let {
                onSuccessCallback(it)
            } ?: run {
                onErrorCallback?.invoke(
                    Exception("Unexpected exception in UseCaseResult: data=null and error=null")
                )
            }
        }

        return this
    }

    suspend fun <T> then(
        nextUseCase: UseCase<T>,
        ignoreCurrentError: Boolean = true
    ): UseCaseResult<T>? =
        if (ignoreCurrentError || isSuccessful) nextUseCase() else null

    suspend fun <P, T> then(
        nextUseCase: UseCaseWithParams<P, T>,
        params: P,
        ignoreCurrentError: Boolean = true
    ): UseCaseResult<T>? =
        if (ignoreCurrentError || isSuccessful) nextUseCase(params) else null
}