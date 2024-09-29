package com.imashnake.aiels.di

import com.imashnake.aiels.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}
