package com.sector.mapchecker

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("0c509a9b-e962-4ee4-b603-220928d7c3da")
    }
}