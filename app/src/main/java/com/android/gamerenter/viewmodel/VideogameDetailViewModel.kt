package com.android.gamerenter.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.android.gamerenter.repository.PlatformRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideogameDetailViewModel @Inject constructor(
    private val platformRepository: PlatformRepository
) : ViewModel() {

    var updateRentResutl: MutableLiveData<Boolean> = MutableLiveData()

    fun rent(id: Int, periodRent: Int, timestamp: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = platformRepository.updateRentOperation(id, periodRent, timestamp)
            updateRentResutl.postValue(result)
        }
    }

    val platformListLiveData = liveData(Dispatchers.IO) {
        emit(platformRepository.getPlatformsList())
    }
}