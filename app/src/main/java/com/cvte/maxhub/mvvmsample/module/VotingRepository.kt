package com.cvte.maxhub.mvvmsample.module

import androidx.lifecycle.LiveData
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.cvte.maxhub.mvvmsample.models.database.VotingDao

class VotingRepository private constructor(private val votingDao: VotingDao) {

    fun load(): LiveData<Voting> = votingDao.findVoting()

    companion object {
        @Volatile
        private var instance: VotingRepository? = null

        fun getInstance(votingDao: VotingDao) = instance
            ?: synchronized(this) {
                instance
                    ?: VotingRepository(votingDao)
                        .also { instance = it }
            }
    }

    suspend fun saveToDataBase(voting: Voting) {
        votingDao.insert(voting)
    }

    suspend fun delete(voting: Voting) {
        votingDao.delete(voting)
    }

}