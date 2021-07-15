package com.hehongdan.anr.plan1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.util.Printer
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lanshifu.demo.anrmonitor.R
import com.lanshifu.demo.anrmonitor.databinding.ActivityMonitor1Binding

/**
 * 类描述：。
 *
 * @author HeHongdan
 * @date 7/15/21
 * @since v7/15/21
 */
class Monitor1Activity : AppCompatActivity() {

    /** 认定卡顿的时间。  */
    private val THREAD_HOLD = 16
    /** 耗时操作时间。  */
    private val SLEEP_TIME = 1000 // >16毫秒
    private val TAG = "【HHD】卡顿性能检测"
    private val mCheckTask = CheckTask()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        check()
        setContentView(R.layout.activity_monitor1)
        // https://blog.csdn.net/qq_20330595/article/details/79358570
        val binding: ActivityMonitor1Binding = DataBindingUtil.setContentView(this@Monitor1Activity, R.layout.activity_monitor1);
        binding.btn1.setOnClickListener({
            Log.w(TAG, "点击了")
            uiLongTimeWork() })
    }




    /**
     * 设置(日志)打印器，并。
     */
    private fun check() {
        Looper.getMainLooper().setMessageLogging(object : Printer {
            private val START = ">>>>> Dispatching to"
            private val END = "<<<<< Finished to"
            override fun println(s: String) {
                if (s.startsWith(START)) {
                    mCheckTask.start()
                } else if (s.startsWith(END)) {
                    mCheckTask.end()
                }
            }
        })
    }



    /**
     * 耗时(1000毫秒)操作。
     */
    private fun uiLongTimeWork() {
        try {
            Thread.sleep(SLEEP_TIME.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 输出当前异常或及错误堆栈信息。
     */
    private fun log() {
        val sb = StringBuilder()
        val stackTrace =
            Looper.getMainLooper().thread.stackTrace
        for (s in stackTrace) {
            sb.append(
                """
                    $s
                    
                    """.trimIndent()
            )
        }
        Log.e(TAG, sb.toString())
    }


    private inner class CheckTask {
        private val mHandlerThread = HandlerThread("【HHD】卡顿检测")
        private val mHandler: Handler

        /** 打印日志的任务。  */
        private val mRunnable = Runnable { log() }

        /**
         * 延迟(1000毫秒)执行。
         */
        fun start() {
            mHandler.postDelayed(mRunnable, THREAD_HOLD.toLong())
        }

        fun end() {
            mHandler.removeCallbacks(mRunnable)
        }

        init {
            mHandlerThread.start()
            mHandler = Handler(mHandlerThread.looper)
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, Monitor1Activity::class.java)
            context.startActivity(intent)
        }
    }

}