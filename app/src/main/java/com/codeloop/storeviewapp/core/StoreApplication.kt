package com.codeloop.storeviewapp.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StoreApplication (

) : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}