package com.abg.pyi

import android.app.Application
import com.abg.pyi.data.ActivityRepository
import com.abg.pyi.data.AppDatabase
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MyApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { ActivityRepository(database.userActivityDao()) }

    override fun onCreate() {
        super.onCreate()
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
    }
}