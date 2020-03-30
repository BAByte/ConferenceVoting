package com.cvte.maxhub.mvvmsample.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvte.maxhub.mvvmsample.R
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import com.cvte.maxhub.mvvmsample.models.data.FunctionType
import com.orhanobut.logger.Logger
import kotlinx.coroutines.launch


class HomeViewModel(private val votingRepository: VotingRepository) : ViewModel() {
    var realtime: MutableLiveData<FunctionBean> = MutableLiveData()
    var anonymous: MutableLiveData<FunctionBean> = MutableLiveData()
    var location: MutableLiveData<FunctionBean> = MutableLiveData()
    val votingLive: LiveData<Voting> = votingRepository.load()

    fun initFunctionBean(context: Context) {
        realtime.postValue(
            FunctionBean(
                FunctionType.realtime.type,
                R.drawable.home_function_realtime,
                context.resources.getString(R.string.real_time),
                isChoice(FunctionType.realtime.type)
            )
        )

        anonymous.postValue(
            FunctionBean(
                FunctionType.anonymous.type,
                R.drawable.home_function_banlance,
                context.resources.getString(R.string.anonymous),
                isChoice(FunctionType.anonymous.type)
            )
        )

        location.postValue(
            FunctionBean(
                FunctionType.location.type,
                R.drawable.home_function_location,
                context.resources.getString(R.string.location),
                isChoice(FunctionType.location.type)
            )
        )

    }

    private fun isChoice(type: Int): Boolean {
        votingLive.value?.let {
            if ((it.type and type) != 0)
                return true
        }


        return false
    }

    fun saveData() {
        var newType = FunctionType.default.type
        realtime.value?.let {
            if (it.choice)
                newType = newType or FunctionType.realtime.type
        }
        anonymous.value?.let {
            if (it.choice)
                newType = newType or FunctionType.anonymous.type
        }
        location.value?.let {
            if (it.choice)
                newType = newType or FunctionType.location.type
        }

        votingLive.value?.let {
            it.type = newType
            Logger.d("voting newtype = $newType")
            viewModelScope.launch {
                votingRepository.saveToDataBase(it)
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            votingRepository.saveToDataBase(Voting(id = 1L, votingContents = mutableListOf()))
        }
    }


    data class FunctionBean(val type: Int, val imageId: Int, val text: String, var choice: Boolean)

}