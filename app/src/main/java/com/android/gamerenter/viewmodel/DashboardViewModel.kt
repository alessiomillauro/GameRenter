package com.android.gamerenter.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.android.gamerenter.ADMIN_CODE
import com.android.gamerenter.fragment.DashboardFragment
import com.android.gamerenter.model.VideogameModel
import com.android.gamerenter.repository.PlatformRepository
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val platformRepository: PlatformRepository
) : ViewModel() {

    var recentSearchVideogamesLiveData = MutableLiveData<MutableList<VideogameModel>>()

    val platformLiveData = liveData(Dispatchers.IO) {
        emit(platformRepository.getPlatformsList())
    }

    val rentedVideogamesLiveData = liveData(Dispatchers.IO) {
        emit(platformRepository.getRentedVideogameList())
    }

    fun getRecentSearchVideogameList() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = platformRepository.getRecentSearchVideogameList()
            recentSearchVideogamesLiveData.postValue(result)
        }
    }

    val upcomingVideogamesLiveData = liveData(Dispatchers.IO) {
        emit(platformRepository.getUpcomingVideogameList())
    }

    fun removeRecentSearchItem(
        id: Int,
        onRemoveItem: DashboardFragment.OnRemoveItem
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            platformRepository.removeRecentSearchItem(
                id, object : DashboardFragment.OnRemoveItem {
                    override fun onSuccessRemove() {
                        onRemoveItem.onSuccessRemove()
                    }

                    override fun onFailureRemove() {
                        onRemoveItem.onFailureRemove()
                    }
                }
            )
        }
    }

    fun addItemInRecentSearch(item: VideogameModel) {
        viewModelScope.launch(Dispatchers.IO) {
            platformRepository.insertItemInRecentSearch(item)
        }
    }

    fun checkAdminCode(value: String): Boolean {
        return value == ADMIN_CODE
    }

}