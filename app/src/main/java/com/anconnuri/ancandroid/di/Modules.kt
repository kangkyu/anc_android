package com.anconnuri.ancandroid.di

import com.anconnuri.ancandroid.utils.TokenManager
import com.anconnuri.ancandroid.viewmodel.JuboViewModel
import com.anconnuri.ancandroid.viewmodel.PhoneAuthViewModel
import com.anconnuri.ancandroid.viewmodel.PrayerViewModel
import com.anconnuri.ancandroid.viewmodel.VideosViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModules = module {
    viewModel { VideosViewModel() }
    viewModel { JuboViewModel() }
    scope(named("main_graph_scope")) {
        viewModel { PhoneAuthViewModel() }
    }
    viewModel { PrayerViewModel() }
    single { TokenManager(get()) }
}
