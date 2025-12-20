package com.inkspire.ebookreader.data.mapper

import com.inkspire.ebookreader.data.database.model.ChapterContentEntity
import com.inkspire.ebookreader.domain.model.Chapter

fun ChapterContentEntity.toDataClass(): Chapter {
    return Chapter(
        chapterContentId = chapterContentId,
        tocId = tocId,
        bookId = bookId,
        chapterTitle = chapterTitle,
        content = content,
    )
}

fun Chapter.toEntity(): ChapterContentEntity {
    return ChapterContentEntity(
        chapterContentId = chapterContentId,
        tocId = tocId,
        bookId = bookId,
        chapterTitle = chapterTitle,
        content = content,
    )
}