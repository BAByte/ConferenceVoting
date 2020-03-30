package com.cvte.maxhub.mvvmsample.models.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.cvte.maxhub.mvvmsample.models.database.VotingContentConverters

/**
 * 水果类
 */


@Entity(tableName = "Voting")
@TypeConverters(VotingContentConverters::class)
data class Voting(
    @PrimaryKey val id: Long,
    var type: Int = FunctionType.default.type, // 十六进制存放的能组合
    var peopleNum: Int = 1, //投票人数
    var votingNum: Int = 2, //投票选项数
    var votingContents: MutableList<VotingContent> //选项内容
)