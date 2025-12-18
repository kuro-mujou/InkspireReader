package com.inkspire.ebookreader.di

import androidx.media3.common.util.UnstableApi
import androidx.room.Room
import com.inkspire.ebookreader.data.database.LocalBookDatabase
import com.inkspire.ebookreader.data.network.HttpClientFactory
import com.inkspire.ebookreader.data.preference.AppPreferences
import com.inkspire.ebookreader.data.repository.AppPreferencesRepositoryImpl
import com.inkspire.ebookreader.data.repository.BookRepositoryImpl
import com.inkspire.ebookreader.data.repository.ChapterRepositoryImpl
import com.inkspire.ebookreader.data.repository.ImagePathRepositoryImpl
import com.inkspire.ebookreader.data.repository.MusicPathRepositoryImpl
import com.inkspire.ebookreader.data.repository.NoteRepositoryImpl
import com.inkspire.ebookreader.data.repository.TableOfContentRepositoryImpl
import com.inkspire.ebookreader.domain.repository.AppPreferencesRepository
import com.inkspire.ebookreader.domain.repository.BookRepository
import com.inkspire.ebookreader.domain.repository.ChapterRepository
import com.inkspire.ebookreader.domain.repository.ImagePathRepository
import com.inkspire.ebookreader.domain.repository.MusicPathRepository
import com.inkspire.ebookreader.domain.repository.NoteRepository
import com.inkspire.ebookreader.domain.repository.TableOfContentRepository
import com.inkspire.ebookreader.service.TTSManager
import com.inkspire.ebookreader.service.TTSServiceHandler
import com.inkspire.ebookreader.ui.bookcontent.BookContentViewModel
import com.inkspire.ebookreader.ui.bookdetail.BookDetailViewModel
import com.inkspire.ebookreader.ui.home.libary.LibraryViewModel
import com.inkspire.ebookreader.ui.home.recentbook.RecentBookViewModel
import com.inkspire.ebookreader.ui.music.MusicViewModel
import com.inkspire.ebookreader.ui.setting.SettingViewModel
import com.inkspire.ebookreader.ui.sharedviewmodel.AsyncImportBookViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

object KoinModule {
    val networkModule = module {
        single<HttpClientEngine> { Android.create {} }
        single<HttpClient> { HttpClientFactory.create(get()) }
    }

    val databaseModule = module {
        single {
            Room
                .databaseBuilder(
                    context = get(),
                    klass = LocalBookDatabase::class.java,
                    name = LocalBookDatabase.DATABASE_NAME
                )
                .fallbackToDestructiveMigration(false)
                .build()
        }
        single { get<LocalBookDatabase>().bookDao }
        single { get<LocalBookDatabase>().chapterDao }
        single { get<LocalBookDatabase>().tableOfContentDao }
        single { get<LocalBookDatabase>().imagePathDao }
        single { get<LocalBookDatabase>().musicPathDao }
        single { get<LocalBookDatabase>().noteDao }
    }

    val repositoryModule = module {
        singleOf(::BookRepositoryImpl).bind<BookRepository>()
        singleOf(::ChapterRepositoryImpl).bind<ChapterRepository>()
        singleOf(::TableOfContentRepositoryImpl).bind<TableOfContentRepository>()
        singleOf(::ImagePathRepositoryImpl).bind<ImagePathRepository>()
        singleOf(::MusicPathRepositoryImpl).bind<MusicPathRepository>()
        singleOf(::NoteRepositoryImpl).bind<NoteRepository>()
        singleOf(::AppPreferencesRepositoryImpl).bind<AppPreferencesRepository>()
    }

    val viewModelModule = module {
        viewModelOf(::LibraryViewModel)
        viewModelOf(::AsyncImportBookViewModel)
        viewModelOf(::SettingViewModel)
//        viewModelOf(::BottomBarViewModel)
//        viewModelOf(::AutoScrollViewModel)
//        viewModelOf(::BookContentViewModel)
//        viewModelOf(::DrawerContainerViewModel)
//        viewModelOf(::TopBarViewModel)
//        viewModelOf(::ColorPaletteViewModel)
        viewModelOf(::MusicViewModel)
//        viewModelOf(::BookWriterViewModel)
        viewModelOf(::RecentBookViewModel)
        viewModel {
            BookContentViewModel(
                bookId = it.get(),
                bookRepository = get(),
                chapterRepository = get()
            )
        }
        viewModel {
            BookDetailViewModel(
                bookId = it.get(),
                bookRepository = get(),
                tableOfContentRepository = get()
            )
        }
    }

    val dataStoreModule = module {
        single { AppPreferences(androidContext()) }
    }

    @UnstableApi
    val ttsModule = module {
        single { TTSServiceHandler(context = androidContext()) }
        single<TTSManager>(createdAtStart = true) { TTSManager(context = androidContext()) }
    }
}