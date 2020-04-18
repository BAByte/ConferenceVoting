package com.cvte.maxhub.mvvmsample.module

import android.content.Context
import androidx.lifecycle.LiveData
import com.cvte.maxhub.mvvmsample.App
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.cvte.maxhub.mvvmsample.models.database.VotingDao
import com.cvte.maxhub.mvvmsample.utils.RequestInf
import com.cvte.maxhub.mvvmsample.utils.SimpleUtils
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.orhanobut.logger.Logger
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

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

    //发起投票
    suspend fun InitiateVote(voting: Voting): Boolean {
        val client = OkHttpClient()
        val gson = Gson()
        voting.latitude = App.latitude
        voting.longitude = App.longitude
        val request: Request = Request.Builder()
            .url(RequestInf.INITIATE_VOTE)
            .post(gson.toJson(voting).toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
        val call: Call = client.newCall(request)
        try {
            val response: Response = call.execute()
            val code = JsonParser().parse(response.body?.string()).asJsonObject.get("code").asInt
            return code == RequestInf.SUCCESS
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }


    fun endVoteFromRemote(): Boolean  {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(RequestInf.END_VOTING)
            .put("".toRequestBody())
            .build()
        val call: Call = client.newCall(request)
        try {
            val response: Response = call.execute()
            val code = JsonParser().parse(response.body?.string()).asJsonObject.get("code").asInt
            return code == RequestInf.SUCCESS
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    suspend fun getVoteDataFromRemote() {
        val client = OkHttpClient()
        val gson = Gson()
        val request: Request = Request.Builder()
            .url(RequestInf.INITIATE_VOTE)
            .get()
            .build()
        val call: Call = client.newCall(request)
        try {
            val response: Response = call.execute()
            val resultJson = JsonParser().parse(response.body?.string()).asJsonObject
            val code = resultJson.get("code").asInt
            if (code == RequestInf.SUCCESS) {
                val results = resultJson.get("results").asJsonObject
                val voting = gson.fromJson<Voting>(results, Voting::class.java)
                Logger.d("getVoteData = $voting")
                voting.id = 1
                saveToDataBase(voting)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun deleteVoteFromRemote() {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(RequestInf.INITIATE_VOTE)
            .delete()
            .build()
        val call: Call = client.newCall(request)
        try {
            val response: Response = call.execute()
            Logger.d("response = ${response.body?.string()}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun upLoadResultBitmap(context: Context) {
        val client = OkHttpClient()
        //2.创建RequestBody
        val fileBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(), File(
                context.getExternalFilesDir(
                    null
                ).toString() + SimpleUtils.pathEnd
            )
        )

        //3.构建MultipartBody
        val requestBody = MultipartBody.Builder()
            .addFormDataPart("file", "testImage.png", fileBody)
            .build();

        //4.构建请求
        val request = Request.Builder()
            .url(RequestInf.UPLOAD_RESULT_IMG)
            .post(requestBody)
            .build();

        //5.发送请求
        val response = client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call, response: Response) {
                Logger.d("response = $response")
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })



    }

}