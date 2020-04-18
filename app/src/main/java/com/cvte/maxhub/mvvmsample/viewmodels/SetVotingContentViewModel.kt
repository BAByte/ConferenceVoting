package com.cvte.maxhub.mvvmsample.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.cvte.maxhub.mvvmsample.models.data.VotingContent
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import com.orhanobut.logger.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SetVotingContentViewModel(private val votingRepository: VotingRepository) : ViewModel() {
    val votingLive: LiveData<Voting> = votingRepository.load()
    val votingContents = MutableLiveData<MutableList<VotingContent>>()

    fun initFunctionBean(voting: Voting) {
        Logger.d("init : $voting")
        when {
            voting.votingContents.isEmpty() -> {
                for (index in 1..voting.votingNum) {
                    voting.votingContents.add(VotingContent(id = index))
                }
            }
            voting.votingContents.size < voting.votingNum -> {
                for (index in 1..(voting.votingNum - voting.votingContents.size)) {
                    Logger.d("<$index")
                    voting.votingContents.add(VotingContent())
                }
            }
            voting.votingContents.size > voting.votingNum -> {
                val num = voting.votingContents.size - 1
                for (index in num downTo voting.votingNum) {
                    Logger.d("减去$index")
                    voting.votingContents.removeAt(index)
                }
            }
        }

        votingContents.postValue(voting.votingContents)
    }

    fun saveData() {
        votingContents.value?.let { votingContentsIt ->
            com.orhanobut.logger.Logger.d("save$votingContentsIt")
            votingLive.value?.let { voting ->
                voting.votingContents = votingContentsIt
                GlobalScope.launch {
                    com.orhanobut.logger.Logger.d("saving")
                    votingRepository.saveToDataBase(voting)
                    com.orhanobut.logger.Logger.d("saved ${votingRepository.load()}")
                }
            }


        }
    }

    fun delete() {
        com.orhanobut.logger.Logger.d("delete")
        viewModelScope.launch {
            votingRepository.saveToDataBase(Voting(id = 1L, votingContents = mutableListOf()))
        }
    }
}