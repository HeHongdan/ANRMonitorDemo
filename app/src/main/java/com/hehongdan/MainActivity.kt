package com.hehongdan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.hehongdan.anr.plan1.Monitor1Activity
import com.hehongdan.anr.plan3.Monitor3Activity
import com.hehongdan.anr.plan4.Monitor4Activity
import com.lanshifu.demo.anrmonitor.R

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.tv_anr1).setOnClickListener(View.OnClickListener {
            Monitor1Activity.start(this@MainActivity)
        })

        findViewById<Button>(R.id.tv_anr3).setOnClickListener(View.OnClickListener {
            Monitor3Activity.start(this@MainActivity)
        })

        findViewById<Button>(R.id.tv_anr).setOnClickListener(View.OnClickListener {
            Monitor4Activity.start(this@MainActivity)
        })
    }



}