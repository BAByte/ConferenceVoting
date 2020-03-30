package com.cvte.maxhub.mvvmsample

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.cvte.maxhub.mvvmsample.models.data.Voting
import com.cvte.maxhub.mvvmsample.models.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StartActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_OVERLAY = 4444
    }

    override fun onResume() {
        super.onResume()
        requestOverlayPermission()
        GlobalScope.launch {
            AppDatabase.getInstance(this@StartActivity).votingDao().insert(Voting(id = 1L,votingContents = mutableListOf()))
        }

    }

    @SuppressLint("ObsoleteSdkInt")
    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${packageName}")
            )
            startActivityForResult(intent, REQUEST_OVERLAY)
        } else {
            startService(Intent(applicationContext, FloatingService::class.java))
            finish()
        }
    }

}
