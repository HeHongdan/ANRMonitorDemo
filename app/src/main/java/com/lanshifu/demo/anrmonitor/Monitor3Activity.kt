package com.lanshifu.demo.anrmonitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class Monitor3Activity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    val anrMonitor = AnrMonitor(this.lifecycle)

    val deadLockMonitor = DeadLockMonitor()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor3)


        DeadLockUtil.createDeadLock()

        findViewById<Button>(R.id.startMonitor).setOnClickListener(View.OnClickListener {
            deadLockMonitor.startMonitor()
        })

        findViewById<Button>(R.id.startAnr).setOnClickListener(View.OnClickListener {
            testAnr()
        })
    }

    private fun testAnr(){
        //死锁导致ANR
        DeadLockUtil.createDeadLockAnr()
    }


}