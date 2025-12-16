package com.exam.me.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exam.me.data.local.SessionManager
import com.exam.me.model.User
import com.exam.me.network.RetrofitInstance
import com.exam.me.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class AdminState {
    object Loading : AdminState()
    data class Success(val users: List<User>) : AdminState()
    data class Error(val message: String) : AdminState()
}

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(RetrofitInstance.api, SessionManager(getApplication()))
    private val sessionManager = SessionManager(getApplication())

    private val _adminState = MutableStateFlow<AdminState>(AdminState.Loading)
    val adminState: StateFlow<AdminState> = _adminState

    val userRole: StateFlow<String?> = sessionManager.userRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        fetchAllUsers()
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            _adminState.value = AdminState.Loading
            try {
                val users = userRepository.getAllUsers()
                _adminState.value = AdminState.Success(users)
            } catch (e: Exception) {
                _adminState.value = AdminState.Error(e.message ?: "Failed to fetch users")
            }
        }
    }
}