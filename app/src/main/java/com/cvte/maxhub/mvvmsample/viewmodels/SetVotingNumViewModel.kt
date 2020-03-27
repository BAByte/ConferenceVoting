package com.cvte.maxhub.mvvmsample.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import kotlinx.coroutines.launch


class SetVotingNumViewModel(private val votingRepository: VotingRepository) : ViewModel() {
    private lateinit var voting: Voting
    val num = MutableLiveData<Int>()

    fun initFunctionBean(context: Context) {
        voting = votingRepository.load()
        num.postValue(voting.votingNum)
    }

    fun saveData() {
        num.value?.let {
            voting.votingNum = it
            viewModelScope.launch {
                votingRepository.saveToDataBase(voting)
            }
        }

    }

}