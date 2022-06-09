package com.android.gamerenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.android.gamerenter.repository.PlatformRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class RentedViewModel @Inject constructor(
    private val platformRepository: PlatformRepository
) : ViewModel() {

    val rentedVideogamesLiveData = liveData(Dispatchers.IO) {
        emit(platformRepository.getRentedVideogameList())
    }
}