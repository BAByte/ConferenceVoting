package com.cvte.maxhub.mvvmsample.models.database

import androidx.room.TypeConverter
import com.cvte.maxhub.mvvmsample.models.data.People
import com.cvte.maxhub.mvvmsample.models.data.VotingContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PeopleConverters {

    @TypeConverter
    fun stringToObject(value: String): MutableList<People> {
        val listType = object : TypeToken<MutableList<People>>() {

        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun objectToString(list: MutableList<People>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}