package com.lanshifu.demo.anrmonitor

import android.os.Debug
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.lanshifu.demo.anrmonitor.LogUtil.logd
import com.lanshifu.demo.anrmonitor.LogUtil.loge
import com.lanshifu.demo.anrmonitor.LogUtil.logi
import com.lanshifu.demo.anrmonitor.LogUtil.logw

/**
 * 类描述：方案4。
 * [com.github.anrwatchdog.ANRWatchDog]的加强版，误差在1S内。
 *
 * @author HeHongdan
 * @date 7/16/21
 * @since v7/16/21
 *
 * @author lanxiaobin
 * @date 5/15/21
 */
class AnrMonitor(lifecycle: Lifecycle) : LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        logd("onResume")
        start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        logd("onPause")
        pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        logd("onDestroy")
        stop()
    }


    /** 主线程的 Handler。 */
    private val mMainHandler by lazy { Handler(Looper.getMainLooper()) }
    /** 主线程卡顿5s就算ANR。 */
    private val ARN_TIMEOUT_SECOND = 5
    /** 主线程任务(修改不阻塞标记为)。 */
    private val mMainRunnable = Runnable {
        mainHandlerRunEnd = true //主线程只是单纯修改这个标志位(不阻塞)
    }
    /** (子线程)间隔1s执行一次任务。 */
    private var mAnrMonitorThread: HandlerThread? = null
    /** 子线程间隔1s执行一次任务(助手)。 */
    private var mAnrMonitorHandler: Handler? = null
    /** 子线程间隔(1s)执行一次任务。 */
    private val THREAD_CHCEK_INTERVAL = 1000L
    /** 子线程间隔1s执行一次(任务)。 */
    private val mThreadRunnable = Runnable {
        //每隔1s检测一下
        blockTime++
        if (!mainHandlerRunEnd && !isDebugger()) {
            logw(TAG, "mThreadRunnable: main thread may be block at least $blockTime s")
        }

        //主线程的标志位5s还没更新，说明主线程卡顿了
        if (blockTime >= ARN_TIMEOUT_SECOND) {
            if (!mainHandlerRunEnd && !isDebugger() && !mHadReport) {
                mHadReport = true
                //5s了，主线程还没更新这个标志，ANR
                loge(TAG, "ANR->main thread may be block at least $blockTime s ")
                loge(TAG, getMainThreadStack())
                //todo 回调出去，这里可以按需把其它线程的堆栈也输出
                //todo debug环境可以开一个新进程，弹出堆栈信息
            }
        }

        if (isPause) {
            logi("isPause return")
            return@Runnable
        }

        //如果上一秒没有耗时，重置状态(并重新发送一个任务)
        if (mainHandlerRunEnd) {
            resetFlagAndSendMainMessage()
        }

        sendDelayThreadMessage()
    }

    /** 卡顿(阻塞)的时间(S)。 */
    @Volatile
    var blockTime = 0
    /** (是否为)重复上报。 */
    @Volatile
    var mHadReport = false
    /** 主线程是否已经执行了任务(更新标记位)。 */
    @Volatile
    var mainHandlerRunEnd = true
    /** 是否暂停监控。 */
    @Volatile
    var isPause = false




    /**
     * 子线程间隔1s执行一次任务(1s误差)。
     */
    private fun sendDelayThreadMessage() {
        mAnrMonitorHandler?.removeCallbacks(mThreadRunnable)//清空之前的任务
        mAnrMonitorHandler?.postDelayed(// 延迟1s执行子线程任务
            mThreadRunnable, THREAD_CHCEK_INTERVAL
        )
    }

    /**
     * 更新(主线程)标记位。
     */
    private fun resetFlagAndSendMainMessage() {
        blockTime = 0
        mainHandlerRunEnd = false
        mHadReport = false

        //往主线程post一下消息，然后子线程会1s检测一次，看什么时候这个target 被赋值
        mMainHandler.post {
            mainHandlerRunEnd = true  //主线程执行了 Runnable 修改 mainHandlerRunEnd
        }
    }

    /**
     * 开始监控(页面 onResume)，Lifecycle 自动控制。
     */
    fun start() {
        isPause = false
        if (mAnrMonitorThread == null) {
            mAnrMonitorThread = object : HandlerThread("AnrMonitor") {
                override fun onLooperPrepared() {
                    mAnrMonitorHandler = Handler(mAnrMonitorThread!!.looper)
                    resetFlagAndSendMainMessage()
                    sendDelayThreadMessage()
                }
            }
            mAnrMonitorThread?.start()
        } else {
            resetFlagAndSendMainMessage()
            sendDelayThreadMessage()
        }

    }

    /**
     * 暂停监控(页面 onPause)，Lifecycle 自动控制。
     */
    fun pause() {
        isPause = true
        mMainHandler.removeCallbacks(mMainRunnable)
        mAnrMonitorHandler?.removeCallbacks(mThreadRunnable)
    }

    /**
     * 停止监控(页面 onDestroy)，Lifecycle 自动控制。
     */
    fun stop() {
        mAnrMonitorHandler?.removeCallbacks(mThreadRunnable)//移除未完成任务
        mAnrMonitorThread?.interrupt()//停止子线程
        mAnrMonitorThread = null
    }

    /**
     * 获取主线程的堆栈信息。
     *
     * @return 堆栈信息。
     */
    private fun getMainThreadStack(): String {
        val mainThread = Looper.getMainLooper().thread
        val mainStackTrace = mainThread.stackTrace
        val sb = StringBuilder()
        for (element in mainStackTrace) {
            sb.append(element.toString())
            sb.append("\r\n")
        }
        return sb.toString()
    }

    /**
     * 是否调试模式。
     *
     * @return 是否调试模式。
     */
    private fun isDebugger(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }



    companion object {
        const val TAG = "AnrMonitor"
    }
}