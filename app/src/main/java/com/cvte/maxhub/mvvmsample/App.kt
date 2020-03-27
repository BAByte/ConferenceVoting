package com.cvte.maxhub.mvvmsample

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy


class App : Application() {
    companion object {
        val TAG = "MVVMSample"

        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context
        var isShow: MutableLiveData<Boolean> = MutableLiveData(false)
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this
        initLogger()  //打印日志框架初始化
    }

    private fun initLogger() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)  // Whether to show thread info or not. Default true
            .methodCount(0)         // How many method line to show. Default 2
            .methodOffset(7)        // Hides internal method calls up to offset. Default 5
            .tag(TAG)   //Global tag for every log.
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }


}

