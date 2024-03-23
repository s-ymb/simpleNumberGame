package io.github.s_ymb.simplenumbergame

import android.app.Application
import io.github.s_ymb.simplenumbergame.data.AppContainer
import io.github.s_ymb.simplenumbergame.data.AppDataContainer

class NumbergameApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }

}