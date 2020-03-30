package com.cvte.maxhub.mvvmsample.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import kotlinx.coroutines.launch


class SetPeopleNumViewModel(private val votingRepository: VotingRepository) : ViewModel() {
    val num = MutableLiveData<Int>()
    val votingLive: LiveData<Voting> = votingRepository.load()

    fun initFunctionBean() {
        votingLive.value?.let {
            num.postValue(it.peopleNum)
        }
    }

    fun saveData() {
        num.value?.let {
            votingLive.value?.let { voting ->
                voting.peopleNum = it
                viewModelScope.launch {
                    votingRepository.saveToDataBase(voting)
                }
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            votingRepository.saveToDataBase(Voting(id = 1L, votingContents = mutableListOf()))
        }
    }

}