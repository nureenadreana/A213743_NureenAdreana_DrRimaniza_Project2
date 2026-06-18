package com.example.a213743_nureenadreana_drrimaniza_lab1.repository

import com.example.a213743_nureenadreana_drrimaniza_lab1.data.BookingDao
import com.example.a213743_nureenadreana_drrimaniza_lab1.data.BookingEntity
import kotlinx.coroutines.flow.Flow

class BookingRepository(private val bookingDao: BookingDao) {

    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()

    fun getBookingsByUser(userName: String): Flow<List<BookingEntity>> =
        bookingDao.getBookingsByUser(userName)

    suspend fun insert(booking: BookingEntity) {
        bookingDao.insertBooking(booking)
    }
}
