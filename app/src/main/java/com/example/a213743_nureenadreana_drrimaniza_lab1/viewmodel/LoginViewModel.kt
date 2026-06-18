package com.example.a213743_nureenadreana_drrimaniza_lab1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.a213743_nureenadreana_drrimaniza_lab1.repository.AuthRepository

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var error by mutableStateOf("")
    var loading by mutableStateOf(false)

    fun login(
        onSuccess: () -> Unit
    ) {

        loading = true

        repository.login(
            email,
            password,
            {

                loading = false
                onSuccess()

            },
            {

                loading = false
                error = it

            }
        )

    }

}
