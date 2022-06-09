package com.android.gamerenter.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.android.gamerenter.model.PlatformModel
import com.android.gamerenter.model.VideogameModel
import com.android.gamerenter.repository.PlatformRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.security.auth.callback.Callback

@HiltViewModel
class AddVideogameViewModel @Inject constructor(
    private val platformRepository: PlatformRepository
) : ViewModel() {

    var genreVideogamesLiveData = MutableLiveData<MutableList<Map<String, String>>>()
    var platformVideogamesLiveData = MutableLiveData<MutableList<PlatformModel>>()
    var publishersVideogamesLiveData = MutableLiveData<MutableList<Map<String, String>>>()

    var feedbackInsert = MutableLiveData<Boolean>()


    fun getGenreVideogameList() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = platformRepository.getGenresList()
            genreVideogamesLiveData.postValue(result)
        }
    }

    fun getPlatformVideogameList() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = platformRepository.getPlatformsList()
            platformVideogamesLiveData.postValue(result)
        }
    }

    fun getPublishersVideogameList() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = platformRepository.getPublishersList()
            publishersVideogamesLiveData.postValue(result)
        }
    }

    fun addVideogame(bitmap: Bitmap, item: VideogameModel, document: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val feedback = platformRepository.insertVideogame(bitmap, item, document)
            feedbackInsert.postValue(feedback)
        }
    }
}