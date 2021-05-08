package com.kiran.myclothes.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


private var viewModelJob = Job()

val coroutineScope = CoroutineScope(
    viewModelJob + Dispatchers.Default
)

val coroutineMainScope = CoroutineScope(
    viewModelJob + Dispatchers.Main
)