package com.cvte.maxhub.mvvmsample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                !Settings.canDrawOverlays(this)
            } else {
                false
            }
        ) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${packageName}")
            )
            startActivityForResult(intent, REQUEST_OVERLAY)
        } else {
            requestPermissions()
            startService(Intent(applicationContext, FloatingService::class.java))
            App.mLocationClient.start();
            finish()
        }
    }

    private fun requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                val permission: Int = ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.LOCATION_HARDWARE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_SETTINGS,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_CONTACTS
                        ), 0x0010
                    )
                }
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ), 0x0010
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
