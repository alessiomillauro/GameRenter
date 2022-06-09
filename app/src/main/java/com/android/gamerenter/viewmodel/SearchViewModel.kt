package com.android.gamerenter.viewmodel

import androidx.lifecycle.*
import com.android.gamerenter.model.VideogameModel
import com.android.gamerenter.repository.PlatformRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val platformRepository: PlatformRepository
) : ViewModel() {

    var searchResult = MutableLiveData<MutableList<VideogameModel>>()

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = platformRepository.searchVideogame(query)
            searchResult.postValue(result)
        }
    }

    val recentSearchVideogamesLiveData = liveData(Dispatchers.IO) {
        emit(platformRepository.getRecentSearchVideogameList())
    }

}