package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.BookRepository

class RecentBookUseCase (
    private val bookRepository: BookRepository
) {
    fun getRecentBookList() = bookRepository.getBookListForMainScreen()
}