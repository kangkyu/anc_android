package com.example.ancandroid.di

import com.example.ancandroid.viewmodel.JuboViewModel
import com.example.ancandroid.viewmodel.VideosViewModel
import org.koin.dsl.module

val appModules = module {
    single { VideosViewModel() }
    single { JuboViewModel() }
}
