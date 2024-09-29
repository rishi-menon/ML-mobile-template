package com.imashnake.aiels.di

import com.imashnake.aiels.AielsInterpreter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val utilsModule = module {
    single { AielsInterpreter(fileName = "model", context = androidContext()) }
}
