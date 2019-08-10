package com.amaiyorov.multysample.dagger

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(val ctx: Context) {
    private val context: Context = ctx

    @Provides
    fun context(): Context = context
}