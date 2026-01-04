package com.inkspire.ebookreader

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.inkspire.ebookreader.di.KoinModule
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import io.kotzilla.sdk.analytics.koin.analytics
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            analytics()
            modules(
                KoinModule.ttsModule,
                KoinModule.networkModule,
                KoinModule.databaseModule,
                KoinModule.repositoryModule,
                KoinModule.useCaseModule,
                KoinModule.viewModelModule,
            )
        }
        PDFBoxResourceLoader.init(this@App)
        initRemoteConfig()
    }

    fun initRemoteConfig() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }
}