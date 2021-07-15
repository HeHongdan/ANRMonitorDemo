package com.lanshifu.demo.anrmonitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.hehongdan.anr.Monitor1Activity
import kotlin.concurrent.thread

class MainActivity_ : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_)


        DeadLockUtil.createDeadLock()

        findViewById<Button>(R.id.startMonitor).setOnClickListener(View.OnClickListener {
            Monitor1Activity.start(this@MainActivity_)
        })

        findViewById<Button>(R.id.startAnr).setOnClickListener(View.OnClickListener {
        })
    }



}