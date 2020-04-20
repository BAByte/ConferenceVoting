package com.cvte.maxhub.mvvmsample.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.*
import com.cvte.maxhub.mvvmsample.R
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import com.cvte.maxhub.mvvmsample.utils.SimpleUtils
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*


class ResultViewModel(private val votingRepository: VotingRepository) : ViewModel() {
    val votingLive: LiveData<Voting> = votingRepository.load()
    var isStart = false
    private val success = 1
    val fail = 2
    var isOpenVoteSuccess: MutableLiveData<Int> = MutableLiveData(0)


    fun delete() {
        viewModelScope.launch {
            votingRepository.saveToDataBase(Voting(id = 1L, votingContents = mutableListOf()))
        }
    }

    //发起投票
    fun InitiateVote(context: Context, voting: Voting) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("ResultViewModel InitiateVote")
            if (votingRepository.InitiateVote(voting)) {
                isStart = true
                isOpenVoteSuccess.postValue(success)
                repeat(10 * 10000000) {
                    Logger.d("ResultViewModel repeat")
                    if (!votingRepository.getVoteDataFromRemote()) {
                        viewModelScope.launch {
                            Toast.makeText(context, "获取投票数据失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                    delay(3000L)
                }
            } else {
                isStart = false
                isOpenVoteSuccess.postValue(fail)
                Logger.d("发起投票失败")
            }
        }
    }

    fun deleteVoteFromRemote(context: Context) {
        delete()
        GlobalScope.launch(Dispatchers.IO) {
            if (!votingRepository.deleteVoteFromRemote()){
                viewModelScope.launch {
                    Toast.makeText(context, context.getString(R.string.cancel_vote_fail), Toast.LENGTH_SHORT).show()
                }
            }
            isStart = false
        }
    }

    fun endVoteFromRemote(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            if (votingRepository.endVoteFromRemote()) {
                votingRepository.getVoteDataFromRemote()
                delay(5000)
                viewModelScope.cancel()
            } else {
                viewModelScope.launch {
                    Toast.makeText(context, context.getString(R.string.time_out_fail), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun saveResultToLocal(context: Context, bitmap: Bitmap) {
        SimpleUtils.saveBitmapToSdCard(context, bitmap)
    }

    fun upLoadResultBitmap(context: Context) {
        votingRepository.upLoadResultBitmap(context)
    }
}