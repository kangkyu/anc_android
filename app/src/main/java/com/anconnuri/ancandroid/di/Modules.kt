package com.anconnuri.ancandroid.di

import com.anconnuri.ancandroid.viewmodel.JuboViewModel
import com.anconnuri.ancandroid.viewmodel.VideosViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel { VideosViewModel() }
    viewModel { JuboViewModel() }
}
