package com.imashnake.aiels

import android.app.Application
import com.imashnake.aiels.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AielsApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AielsApplication)
            modules(appModule)
        }
    }
}
