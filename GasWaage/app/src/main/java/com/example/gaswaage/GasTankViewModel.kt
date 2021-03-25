package com.example.gaswaage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GasTankViewModel : ViewModel() {
    val progress = MutableLiveData<Int>(0)
}