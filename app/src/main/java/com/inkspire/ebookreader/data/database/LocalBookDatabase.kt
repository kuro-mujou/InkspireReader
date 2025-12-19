package com.inkspire.ebookreader.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.inkspire.ebookreader.common.StringListTypeConverter
import com.inkspire.ebookreader.data.dao.BookDao
import com.inkspire.ebookreader.data.dao.CategoryDao
import com.inkspire.ebookreader.data.dao.ChapterDao
import com.inkspire.ebookreader.data.dao.ImagePathDao
import com.inkspire.ebookreader.data.dao.LibraryDao
import com.inkspire.ebookreader.data.dao.MusicPathDao
import com.inkspire.ebookreader.data.dao.NoteDao
import com.inkspire.ebookreader.data.dao.RecentBookDao
import com.inkspire.ebookreader.data.dao.TableOfContentDao
import com.inkspire.ebookreader.data.model.BookCategoryCrossRef
import com.inkspire.ebookreader.data.model.BookEntity
import com.inkspire.ebookreader.data.model.CategoryEntity
import com.inkspire.ebookreader.data.model.ChapterContentEntity
import com.inkspire.ebookreader.data.model.ImagePathEntity
import com.inkspire.ebookreader.data.model.MusicPathEntity
import com.inkspire.ebookreader.data.model.NoteEntity
import com.inkspire.ebookreader.data.model.TableOfContentEntity

@Database(
    entities = [
        BookEntity::class,
        TableOfContentEntity::class,
        ChapterContentEntity::class,
        ImagePathEntity::class,
        MusicPathEntity::class,
        NoteEntity::class,
        CategoryEntity::class,
        BookCategoryCrossRef::class
    ],
    exportSchema = false,
    version = 1
)
@TypeConverters(StringListTypeConverter::class)
abstract class LocalBookDatabase : RoomDatabase() {
    abstract val bookDao: BookDao
    abstract val recentBookDao: RecentBookDao
    abstract val libraryDao: LibraryDao
    abstract val categoryDao: CategoryDao
    abstract val chapterDao: ChapterDao
    abstract val tableOfContentDao: TableOfContentDao
    abstract val imagePathDao: ImagePathDao
    abstract val musicPathDao: MusicPathDao
    abstract val noteDao: NoteDao

    companion object {
        const val DATABASE_NAME = "local_book_database"
    }
}
