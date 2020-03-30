package com.cvte.maxhub.mvvmsample.models.database

import androidx.room.TypeConverter
import com.cvte.maxhub.mvvmsample.models.data.VotingContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class VotingContentConverters {

    @TypeConverter
    fun stringToObject(value: String): MutableList<VotingContent> {
        val listType = object : TypeToken<MutableList<VotingContent>>() {

        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun objectToString(list: MutableList<VotingContent>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}