package com.inkspire.ebookreader

import android.app.Application
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
                KoinModule.networkModule,
                KoinModule.databaseModule,
                KoinModule.repositoryModule,
                KoinModule.viewModelModule,
                KoinModule.dataStoreModule,
                KoinModule.ttsModule
            )
        }
        PDFBoxResourceLoader.init(this@App)
    }
}