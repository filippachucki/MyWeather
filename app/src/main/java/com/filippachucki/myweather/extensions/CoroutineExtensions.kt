package com.filippachucki.myweather.extensions

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun CoroutineScope.launch(
    onError: (Throwable) -> Unit = { throw IllegalStateException("Error block not implemented", it) },
    onSuccess: suspend CoroutineScope.() -> Unit
): Job {
    return launch(CoroutineExceptionHandler { _, exception -> onError(exception) }) {
        onSuccess()
    }
}