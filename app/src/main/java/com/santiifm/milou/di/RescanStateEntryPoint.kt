package com.santiifm.milou.di

import com.santiifm.milou.data.state.RescanStateHolder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface RescanStateEntryPoint {
    fun rescanStateHolder(): RescanStateHolder
}
