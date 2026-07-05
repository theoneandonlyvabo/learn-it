package com.learnit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnit.app.data.repository.AuthRepository
import com.learnit.app.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    // null = no login/register attempt yet (distinct from ApiState.Loading, which would
    // otherwise show a spinner as soon as the screen opens).
    private val _authState = MutableStateFlow<ApiState<Unit>?>(null)
    val authState: StateFlow<ApiState<Unit>?> = _authState.asStateFlow()

    val currentEmail: String?
        get() = repository.getCurrentUser()?.email

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = ApiState.Error("Email and password cannot be empty")
            return
        }
        viewModelScope.launch {
            _authState.value = ApiState.Loading
            repository.login(email, password).fold(
                onSuccess = { _authState.value = ApiState.Success(Unit) },
                onFailure = { e -> _authState.value = ApiState.Error(e.message ?: "Login failed") }
            )
        }
    }

    fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = ApiState.Error("Email and password cannot be empty")
            return
        }
        viewModelScope.launch {
            _authState.value = ApiState.Loading
            repository.register(email, password).fold(
                onSuccess = { _authState.value = ApiState.Success(Unit) },
                onFailure = { e -> _authState.value = ApiState.Error(e.message ?: "Registration failed") }
            )
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = null
    }
}
