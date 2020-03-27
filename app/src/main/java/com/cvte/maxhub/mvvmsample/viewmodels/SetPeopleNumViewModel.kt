package com.cvte.maxhub.mvvmsample.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import kotlinx.coroutines.launch


class SetPeopleNumViewModel(private val votingRepository: VotingRepository) : ViewModel() {
    private lateinit var voting: Voting
    val num = MutableLiveData<Int>()

    fun initFunctionBean(context: Context) {
        voting = votingRepository.load()
        num.postValue(voting.peopleNum)
    }

    fun saveData() {
        num.value?.let {
            voting.peopleNum = it
            viewModelScope.launch {
                votingRepository.saveToDataBase(voting)
            }
        }

    }

}