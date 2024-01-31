package com.golfcart.di

import com.golfcart.model.repo.AppRepository
import com.golfcart.model.repo.MyRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

var repoModule = module {

    /**
     * Provide MyRepository class  Singleton object
     * you can use it any KoinComponent class  below is declaration
     * private val appRepository: AppRepository by inject()
     **/
    single { MyRepository(get(),get(),androidContext()) }
    single { AppRepository(get(),androidContext()) }
}