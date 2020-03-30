package com.cvte.maxhub.mvvmsample.models.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cvte.maxhub.mvvmsample.models.data.Voting

@Dao
interface VotingDao {

    @Query("SELECT * FROM Voting")
    fun findVoting(): LiveData<Voting>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(voting: Voting)

    @Delete
    suspend fun delete(voting: Voting)
}
