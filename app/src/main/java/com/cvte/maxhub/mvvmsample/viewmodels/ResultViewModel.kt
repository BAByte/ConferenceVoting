package com.cvte.maxhub.mvvmsample.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import com.cvte.maxhub.mvvmsample.utils.SimpleUtils
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody


class ResultViewModel(private val votingRepository: VotingRepository) : ViewModel() {
    val votingLive: LiveData<Voting> = votingRepository.load()
    var isStart = false


    fun delete() {
        viewModelScope.launch {
            votingRepository.saveToDataBase(Voting(id = 1L, votingContents = mutableListOf()))
        }
    }

    //发起投票
    fun InitiateVote(voting: Voting) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("ResultViewModel InitiateVote")
            if (votingRepository.InitiateVote(voting)) {
                isStart = true
                repeat(10 * 10000000) {
                    Logger.d("ResultViewModel repeat")
                    votingRepository.getVoteDataFromRemote()
                    delay(3000L)
                }
            }
        }
    }

    fun deleteVoteFromRemote() {
        viewModelScope.launch(Dispatchers.IO) {
            votingRepository.deleteVoteFromRemote()
            delete()
            isStart = false
        }
    }

    fun endVoteFromRemote() {
        viewModelScope.launch(Dispatchers.IO) {
            if (votingRepository.endVoteFromRemote()) {
                votingRepository.getVoteDataFromRemote()
                delay(5000)
                viewModelScope.cancel()
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