package com.cvte.maxhub.mvvmsample.utils

object RequestInf {
    private val base = "https://linyudi.easy.echosite.cn"
    val SUCCESS = 200
    val FAIL = 20007
    val INITIATE_VOTE = "$base/vote"
    val UPLOAD_RESULT_IMG = "$base/vote/upload-result"
    val END_VOTING = "$base/vote/stop"
}