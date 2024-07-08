package com.example.ancandroid

import android.app.Application
import com.example.ancandroid.di.appModules
import org.koin.core.context.startKoin

class ChurchApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModules)
        }
    }
}
