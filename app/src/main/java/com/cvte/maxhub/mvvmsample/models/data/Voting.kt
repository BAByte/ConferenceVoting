package com.cvte.maxhub.mvvmsample.models.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.cvte.maxhub.mvvmsample.models.database.PeopleConverters
import com.cvte.maxhub.mvvmsample.models.database.VotingContentConverters

/**
 * 水果类
 */


@Entity(tableName = "Voting")
@TypeConverters(VotingContentConverters::class, PeopleConverters::class)
data class Voting(
    @PrimaryKey var id: Long,
    var type: Int = FunctionType.default.type, // 十六进制存放的能组合
    var peopleNum: Int = 1, //参加投票的人数
    var votingNum: Int = 2, //投票选项数
    var voted: Int = 0, //已经投票的人数
    var status: Boolean = true,// true 投票正在进行，false 投票取消但是未删除数据
    var longitude: Double = 0.0, //经度
    var latitude: Double = 0.0,//维度
    var joinQRCode: String = "" ,//扫码投票的二维码下载地址
    var downloadQRCode: String = "", //扫码带走的二维码下载地址
    var votingContents: MutableList<VotingContent> //选项内容
)