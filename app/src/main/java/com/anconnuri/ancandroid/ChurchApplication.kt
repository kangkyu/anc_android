package com.anconnuri.ancandroid

import android.app.Application
import com.anconnuri.ancandroid.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ChurchApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ChurchApplication)
            modules(appModules)
        }
    }
}
