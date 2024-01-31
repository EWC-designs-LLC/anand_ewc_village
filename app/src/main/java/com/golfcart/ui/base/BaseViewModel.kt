package com.golfcart.ui.base

import androidx.lifecycle.ViewModel
import com.golfcart.model.remote.ReflectionUtil
import com.golfcart.model.repo.AppRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel : ViewModel() , KoinComponent {
    private val appRepo: AppRepository by inject()
    private val reflectionUtil : ReflectionUtil by inject()

    fun getAppRepository(): AppRepository {
        return appRepo
    }

    fun getReflectionUtils(): ReflectionUtil {
        return reflectionUtil
    }
}