package com.riteshbkadam.beatsfitapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riteshbkadam.beatsfitapp.room.data.User
import com.riteshbkadam.beatsfitapp.room.data.UserRepository
import kotlinx.coroutines.launch


class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    init {
        fetchUser()
    }

    private fun fetchUser() = viewModelScope.launch {
        _user.postValue(repository.getUser())
    }

    fun clearDatabase() = viewModelScope.launch {
        repository.clearDatabase()
    }


    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
        fetchUser() // Refresh user data after insert
    }
}
