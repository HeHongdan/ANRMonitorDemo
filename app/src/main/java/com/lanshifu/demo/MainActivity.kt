package com.lanshifu.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.hehongdan.anr.plan1.Monitor1Activity
import com.hehongdan.anr.plan2.Monitor2Activity
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

        findViewById<Button>(R.id.tv_anr2).setOnClickListener(View.OnClickListener {
            Monitor2Activity.start(this@MainActivity)
        })
    }



}