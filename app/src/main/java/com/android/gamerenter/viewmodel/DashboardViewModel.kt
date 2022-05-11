package com.android.gamerenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.android.gamerenter.repository.PlatformRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val platformRepository: PlatformRepository
) : ViewModel() {

    val platformLiveData = liveData(Dispatchers.IO) {
        emit(platformRepository.getPlatformsList())
    }

    val upcomingVideogamesLiveData = liveData(Dispatchers.IO) {
        emit(platformRepository.getUpcomingVideogameList())
    }
}