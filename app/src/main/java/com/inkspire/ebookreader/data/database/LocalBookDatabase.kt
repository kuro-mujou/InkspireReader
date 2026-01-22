package com.inkspire.ebookreader.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.inkspire.ebookreader.data.database.converter.StringListTypeConverter
import com.inkspire.ebookreader.data.database.dao.BookDao
import com.inkspire.ebookreader.data.database.dao.ChapterDao
import com.inkspire.ebookreader.data.database.dao.HiddenTextDao
import com.inkspire.ebookreader.data.database.dao.HighlightDao
import com.inkspire.ebookreader.data.database.dao.ImagePathDao
import com.inkspire.ebookreader.data.database.dao.LibraryDao
import com.inkspire.ebookreader.data.database.dao.MusicPathDao
import com.inkspire.ebookreader.data.database.dao.NoteDao
import com.inkspire.ebookreader.data.database.dao.TableOfContentDao
import com.inkspire.ebookreader.data.database.model.BookCategoryCrossRef
import com.inkspire.ebookreader.data.database.model.BookEntity
import com.inkspire.ebookreader.data.database.model.CategoryEntity
import com.inkspire.ebookreader.data.database.model.ChapterContentEntity
import com.inkspire.ebookreader.data.database.model.HiddenTextEntity
import com.inkspire.ebookreader.data.database.model.HighlightEntity
import com.inkspire.ebookreader.data.database.model.ImagePathEntity
import com.inkspire.ebookreader.data.database.model.MusicPathEntity
import com.inkspire.ebookreader.data.database.model.NoteEntity
import com.inkspire.ebookreader.data.database.model.TableOfContentEntity

@Database(
    entities = [
        BookEntity::class,
        TableOfContentEntity::class,
        ChapterContentEntity::class,
        ImagePathEntity::class,
        MusicPathEntity::class,
        NoteEntity::class,
        CategoryEntity::class,
        BookCategoryCrossRef::class,
        HighlightEntity::class,
        HiddenTextEntity::class
    ],
    exportSchema = false,
    version = 1
)
@TypeConverters(StringListTypeConverter::class)
abstract class LocalBookDatabase : RoomDatabase() {
    abstract val bookDao: BookDao
    abstract val libraryDao: LibraryDao
    abstract val chapterDao: ChapterDao
    abstract val tableOfContentDao: TableOfContentDao
    abstract val imagePathDao: ImagePathDao
    abstract val musicPathDao: MusicPathDao
    abstract val noteDao: NoteDao
    abstract val highlightDao: HighlightDao
    abstract val hiddenTextDao: HiddenTextDao

    companion object {
        const val DATABASE_NAME = "local_book_database"
    }
}
