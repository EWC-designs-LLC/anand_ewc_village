package com.golfcart.application

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.golfcart.R
import com.golfcart.di.*
import com.mapbox.mapboxsdk.Mapbox
import org.koin.android.ext.koin.*
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module

class VillageApplication  : MultiDexApplication()  {

    // Declaring the context variable
    companion object {
        lateinit var context: Context
    }

    // initialsing the views and varible inside the oncreate method
    override fun onCreate() {
        super.onCreate()
        // initialise the context with application context
        context=this@VillageApplication
        // declaring the night mode force stop
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // staring the koin with configure the modules and launching the android context
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@VillageApplication)
            modules(getModules())
        }
        // initialising the mapbox in the application class
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
    }

    // fetching all the koin modules and passing inside the mutable list
    fun getModules(): MutableList<Module> {
        return mutableListOf<Module>(appModule, repoModule, viewModelModule)
    }
}