package com.hehongdan.anr.plan4

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.lanshifu.demo.anrmonitor.AnrMonitor
import com.lanshifu.demo.anrmonitor.DeadLockMonitor
import com.lanshifu.demo.anrmonitor.R

/**
 * 类描述：方案4。
 *
 * @author HeHongdan
 * @date 7/16/21
 * @since v7/16/21
 */
class Monitor4Activity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"

        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, Monitor4Activity::class.java))
        }
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