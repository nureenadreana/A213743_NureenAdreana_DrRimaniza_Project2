package com.example.a213743_nureenadreana_drrimaniza_lab1.repository

import com.example.a213743_nureenadreana_drrimaniza_lab1.data.BookmarkDao
import com.example.a213743_nureenadreana_drrimaniza_lab1.data.BookmarkEntity
import kotlinx.coroutines.flow.Flow

class BookmarkRepository(private val bookmarkDao: BookmarkDao) {
    fun getBookmarksByUser(userName: String): Flow<List<BookmarkEntity>> =
        bookmarkDao.getBookmarksByUser(userName)

    fun isBookmarked(foodId: String, userName: String): Flow<Boolean> =
        bookmarkDao.isBookmarked(foodId, userName)

    suspend fun insert(bookmark: BookmarkEntity) {
        bookmarkDao.insertBookmark(bookmark)
    }

    suspend fun delete(bookmark: BookmarkEntity) {
        bookmarkDao.deleteBookmark(bookmark)
    }
}
