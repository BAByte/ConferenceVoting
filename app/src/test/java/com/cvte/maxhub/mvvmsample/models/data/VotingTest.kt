package com.cvte.maxhub.mvvmsample.models.data

import com.google.gson.Gson
import com.orhanobut.logger.Logger
import org.junit.Test


internal class VotingTest {

    @Test
    fun test() {
        val viotings = mutableListOf<VotingContent>()
        val peoples = mutableListOf<People>()
        for (i in 1..5) {
            peoples.add(People("手机唯一标识符", "张$i", 1, 23f, 123f))
        }
        for (i in 1..5) {
            viotings.add(VotingContent(id = i, content = "选项：$i", peoples = peoples))
        }

        val voting = Voting(
            1L,
            FunctionType.default.type,
            10,
            10,
            0,
            12f,
            123f,
            "http://xxxxxxx",
            "http//xxxx",
            viotings
        )

        val gson = Gson()
        print(gson.toJson(voting))
    }
}