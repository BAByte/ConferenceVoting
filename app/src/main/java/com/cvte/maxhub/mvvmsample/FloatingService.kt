package com.cvte.maxhub.mvvmsample

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.view.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.observe
import com.orhanobut.logger.Logger


class FloatingService : LifecycleService() {
    private lateinit var wManager: WindowManager// 窗口管理者
    private lateinit var mParams: WindowManager.LayoutParams// 窗口的属性
    private lateinit var floatView: View


    @SuppressLint("InflateParams")
    override fun onCreate() {
        super.onCreate()
        initParams()
        floatView = initFloatView()
        subscribeUI()
    }

    private fun subscribeUI() {
        App.isShow.observe(LifecycleOwner { lifecycle }) { result ->
            if (result) {
                wManager.removeView(floatView)
            }else {
                wManager.addView(floatView, mParams)
            }
        }
    }


    private fun initFloatView(): View {
        val layoutInflater = LayoutInflater.from(applicationContext)
        return layoutInflater.inflate(R.layout.float_image_view, null, false).also { floatView ->
            floatView.setOnClickListener {
                Logger.d("floatView on click")
                startActivity(Intent(
                    this,
                    MainActivity::class.java
                ).also { it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
            }
            floatView.setOnTouchListener(FloatingOnTouchListener())
        }
    }

    private fun initParams() {
        wManager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT
        )
        mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY// 系统提示window
        mParams.format = PixelFormat.TRANSLUCENT// 支持透明
        // mParams.format = PixelFormat.RGBA_8888;
        mParams.flags =
            mParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 焦点
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT// 窗口的宽和高
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        mParams.gravity = Gravity.START or Gravity.TOP
        mParams.y = 100
        mParams.x = 100
        mParams.windowAnimations = android.R.style.Animation_Toast
    }


    private inner class FloatingOnTouchListener : View.OnTouchListener {
        private var x: Int = 0
        private var y: Int = 0

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.rawX.toInt()
                    y = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val nowX = event.rawX.toInt()
                    val nowY = event.rawY.toInt()
                    val movedX = nowX - x
                    val movedY = nowY - y
                    x = nowX
                    y = nowY
                    mParams.x = mParams.x + movedX
                    mParams.y = mParams.y + movedY

                    // 更新悬浮窗控件布局
                    wManager.updateViewLayout(v, mParams)
                }
                else -> {
                }
            }
            return false
        }
    }
}
