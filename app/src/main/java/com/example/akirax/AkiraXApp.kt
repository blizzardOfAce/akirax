package com.example.akirax

import android.app.Application
import com.example.akirax.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AkiraXApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AkiraXApp)
            modules(appModule)
        }
    }
}
