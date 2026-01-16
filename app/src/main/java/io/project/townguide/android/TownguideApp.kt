package io.project.townguide.android

import android.app.Application

class TownguideApp : Application() {
    companion object {
        lateinit var appContext: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}