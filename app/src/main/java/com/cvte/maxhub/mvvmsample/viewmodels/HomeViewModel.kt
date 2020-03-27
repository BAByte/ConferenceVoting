package com.cvte.maxhub.mvvmsample.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvte.maxhub.mvvmsample.R
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.cvte.maxhub.mvvmsample.module.VotingRepository
import com.cvte.maxhub.mvvmsample.module.FunctionType
import com.orhanobut.logger.Logger
import kotlinx.coroutines.launch


class HomeViewModel(private val votingRepository: VotingRepository) : ViewModel() {
    var realtime: MutableLiveData<FunctionBean> = MutableLiveData()
    var anonymous: MutableLiveData<FunctionBean> = MutableLiveData()
    var location: MutableLiveData<FunctionBean> = MutableLiveData()
    private lateinit var voting: Voting

    fun initFunctionBean(context: Context) {
        voting = votingRepository.load()
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

        Logger.d("voting = $voting")

    }

    private fun isChoice(type: Int): Boolean {
        Logger.d("isChoice voting = $voting")
        if ((voting.type and type) != 0)
            return true

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


        voting.type = newType
        Logger.d("voting newtype = $newType")
        viewModelScope.launch {
            votingRepository.saveToDataBase(voting)
        }
    }

     fun delete() {
         viewModelScope.launch {
             votingRepository.saveToDataBase(Voting(id = 1L))
         }
    }


    data class FunctionBean(val type: Int, val imageId: Int, val text: String, var choice: Boolean)

}