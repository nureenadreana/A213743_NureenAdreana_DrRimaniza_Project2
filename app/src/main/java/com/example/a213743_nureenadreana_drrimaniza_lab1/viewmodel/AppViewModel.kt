package com.example.a213743_nureenadreana_drrimaniza_lab1.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.a213743_nureenadreana_drrimaniza_lab1.repository.AuthRepository
import com.example.a213743_nureenadreana_drrimaniza_lab1.data.BookingEntity
import com.example.a213743_nureenadreana_drrimaniza_lab1.repository.BookingRepository
import com.example.a213743_nureenadreana_drrimaniza_lab1.data.BookmarkEntity
import com.example.a213743_nureenadreana_drrimaniza_lab1.repository.BookmarkRepository
import com.example.a213743_nureenadreana_drrimaniza_lab1.repository.FirebaseRepository
import com.example.a213743_nureenadreana_drrimaniza_lab1.data.FoodDatabase
import com.example.a213743_nureenadreana_drrimaniza_lab1.data.FoodEntity
import com.example.a213743_nureenadreana_drrimaniza_lab1.data.FoodItemData
import com.example.a213743_nureenadreana_drrimaniza_lab1.repository.FoodRepository
import com.example.a213743_nureenadreana_drrimaniza_lab1.data.UserProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flatMapLatest
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.flow.combine

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: FoodRepository
    private val bookingRepository: BookingRepository
    private val bookmarkRepository: BookmarkRepository
    private val authRepository = AuthRepository()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile = _userProfile.asStateFlow()

    init {
        val db = FoodDatabase.Companion.getDatabase(application)
        repository = FoodRepository(db.foodDao())
        bookingRepository = BookingRepository(db.bookingDao())
        bookmarkRepository = BookmarkRepository(db.bookmarkDao())
        
        syncProfile()
    }

    fun syncProfile() {
        val currentUser = authRepository.currentUser()
        if (currentUser == null) {
            clearProfile()
            return
        }

        authRepository.getUserProfile(currentUser.uid) { data ->
            if (data != null) {
                val name = data["name"] as? String ?: currentUser.displayName ?: "User"
                val rawUsername = data["username"] as? String ?: name.lowercase().replace(" ", "")
                val formattedUsername = if (rawUsername.startsWith("@")) rawUsername else "@$rawUsername"
                
                _userProfile.update {
                    it.copy(
                        name = name,
                        username = formattedUsername,
                        profileImageUri = data["profileImageUri"] as? String
                    )
                }
            } else {
                // Fallback jika Firestore kosong
                val name = currentUser.displayName ?: "User"
                val username = "@${name.lowercase().replace(" ", "")}"
                _userProfile.update {
                    it.copy(
                        name = name,
                        username = username
                    )
                }
            }
        }
    }

    fun clearProfile() {
        _userProfile.value = UserProfile()
    }

    // --- Data Flows ---
    val foodItems: StateFlow<List<FoodItemData>> = repository.allItems.map { list ->
        list.map {
            FoodItemData(
                it.id,
                it.title,
                it.distance,
                it.imageUri,
                it.userName,
                it.isFree,
                it.description,
                it.price
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myItems: StateFlow<List<FoodItemData>> = userProfile.flatMapLatest { profile ->
        repository.getItemsByUser(profile.name).map { list ->
            list.map {
                FoodItemData(
                    it.id,
                    it.title,
                    it.distance,
                    it.imageUri,
                    it.userName,
                    it.isFree,
                    it.description,
                    it.price
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookings: StateFlow<List<BookingEntity>> = userProfile.flatMapLatest { profile ->
        bookingRepository.getBookingsByUser(profile.name)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedItemIds: StateFlow<Set<Int>> = userProfile.flatMapLatest { profile ->
        bookmarkRepository.getBookmarksByUser(profile.name).map { it.map { bookmark -> bookmark.foodId }.toSet() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val savedItems: StateFlow<List<FoodItemData>> = combine(foodItems, bookmarkedItemIds) { items, bookmarkedIds ->
        items.filter { it.id in bookmarkedIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Bookmark Methods ---
    fun toggleBookmark(foodId: Int) {
        viewModelScope.launch {
            val userName = _userProfile.value.name
            val isCurrentlyBookmarked = bookmarkedItemIds.value.contains(foodId)
            if (isCurrentlyBookmarked) {
                bookmarkRepository.delete(BookmarkEntity(foodId, userName))
            } else {
                bookmarkRepository.insert(BookmarkEntity(foodId, userName))
            }
        }
    }

    // --- Form States & Methods ---
    private val _newItemName = MutableStateFlow("")
    val newItemName = _newItemName.asStateFlow()
    private val _newItemLocation = MutableStateFlow("")
    val newItemLocation = _newItemLocation.asStateFlow()
    private val _newItemDescription = MutableStateFlow("")
    val newItemDescription = _newItemDescription.asStateFlow()
    private val _newItemIsFree = MutableStateFlow(true)
    val newItemIsFree = _newItemIsFree.asStateFlow()
    private val _newItemPrice = MutableStateFlow("")
    val newItemPrice = _newItemPrice.asStateFlow()
    private val _newItemImageUri = MutableStateFlow<String?>(null)
    val newItemImageUri = _newItemImageUri.asStateFlow()
    private val _isEditing = MutableStateFlow(false)
    val isEditing = _isEditing.asStateFlow()

    fun updateNewItemName(name: String) { _newItemName.value = name }
    fun updateNewItemLocation(location: String) { _newItemLocation.value = location }
    fun updateNewItemDescription(description: String) { _newItemDescription.value = description }
    fun updateNewItemIsFree(isFree: Boolean) {
        _newItemIsFree.value = isFree
        if (isFree) _newItemPrice.value = ""
    }
    fun updateNewItemPrice(price: String) { _newItemPrice.value = price }
    fun updateNewItemImageUri(uriString: String?) {
        if (uriString == null) { _newItemImageUri.value = null; return }
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val uri = Uri.parse(uriString)
                if (uri.scheme == "file") { _newItemImageUri.value = uriString; return@launch }
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = "food_${System.currentTimeMillis()}.jpg"
                val file = File(context.filesDir, fileName)
                inputStream?.use { input -> FileOutputStream(file).use { output -> input.copyTo(output) } }
                _newItemImageUri.value = Uri.fromFile(file).toString()
            } catch (e: Exception) { _newItemImageUri.value = uriString }
        }
    }

    fun addItem() {
        viewModelScope.launch {
            val item = FoodEntity(
                title = _newItemName.value,
                distance = _newItemLocation.value,
                imageUri = _newItemImageUri.value,
                userName = _userProfile.value.name,
                isFree = _newItemIsFree.value,
                description = _newItemDescription.value,
                price = if (_newItemIsFree.value) "" else _newItemPrice.value
            )
            repository.insert(item)
            clearForm()
        }
    }

    fun addItemToFirebase() {
        FirebaseRepository.saveFoodListing(title = _newItemName.value, location = _newItemLocation.value, userName = _userProfile.value.name, isFree = _newItemIsFree.value, description = _newItemDescription.value, price = _newItemPrice.value, imageUri = _newItemImageUri.value, onComplete = {})
    }

    fun confirmBooking(itemName: String, location: String, price: String, isFree: Boolean) {
        viewModelScope.launch {
            val booking = BookingEntity(
                itemName = itemName,
                userName = _userProfile.value.name,
                location = location,
                price = price,
                isFree = isFree
            )
            bookingRepository.insert(booking)
        }
    }

    fun deleteItem(itemData: FoodItemData) {
        viewModelScope.launch {
            val entity = FoodEntity(
                id = itemData.id,
                title = itemData.title,
                distance = itemData.distance,
                imageUri = itemData.imageUri,
                userName = itemData.userName,
                isFree = itemData.isFree,
                description = itemData.description,
                price = itemData.price
            )
            repository.delete(entity)
        }
    }

    private var editingItemId: Int? = null
    fun prepareEdit(item: FoodItemData) {
        editingItemId = item.id; _isEditing.value = true; _newItemName.value = item.title; _newItemLocation.value = item.distance; _newItemDescription.value = item.description; _newItemIsFree.value = item.isFree; _newItemPrice.value = item.price; _newItemImageUri.value = item.imageUri
    }

    fun clearForm() {
        editingItemId = null; _isEditing.value = false; _newItemName.value = ""; _newItemLocation.value = ""; _newItemDescription.value = ""; _newItemIsFree.value = true; _newItemPrice.value = ""; _newItemImageUri.value = null
    }

    fun saveItem() {
        viewModelScope.launch {
            val entity = FoodEntity(
                id = editingItemId ?: 0,
                title = _newItemName.value,
                distance = _newItemLocation.value,
                imageUri = _newItemImageUri.value,
                userName = _userProfile.value.name,
                isFree = _newItemIsFree.value,
                description = _newItemDescription.value,
                price = if (_newItemIsFree.value) "" else _newItemPrice.value
            )
            if (editingItemId == null) repository.insert(entity) else repository.update(entity)
            clearForm()
        }
    }

    fun updateUserProfile(newName: String, newUsername: String, newImageUri: String?) {
        val formattedUsername = if (newUsername.startsWith("@")) newUsername else "@$newUsername"
        _userProfile.update { it.copy(name = newName, username = formattedUsername, profileImageUri = newImageUri) }
        val uid = authRepository.currentUser()?.uid
        if (uid != null) { authRepository.saveUserProfile(uid, newName, formattedUsername, newImageUri) }
    }
}
