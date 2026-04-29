package com.ff.app

import android.app.Application
import com.ff.app.core.CoreManager

class FFApplication : Application() {
    lateinit var coreManager: CoreManager
        private set

    override fun onCreate() {
        super.onCreate()
        coreManager = CoreManager(this)
    }
}
