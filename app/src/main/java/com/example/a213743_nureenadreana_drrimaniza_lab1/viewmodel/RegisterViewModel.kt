package com.example.a213743_nureenadreana_drrimaniza_lab1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.a213743_nureenadreana_drrimaniza_lab1.repository.AuthRepository

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var error by mutableStateOf("")
    // Di dalam RegisterViewModel
    fun register(name: String, username: String, onSuccess: () -> Unit) {
        repository.register(email, password, name, username, {
            onSuccess()
        }, {
            error = it
        })

    }

}