package com.imashnake.aiels.di

import com.imashnake.aiels.AielsInterpreter
import com.imashnake.aiels.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AielsInterpreter(fileName = "model", context = androidContext()) }
    viewModel { MainViewModel(get()) }
}
