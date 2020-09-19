package project.main.base

import android.content.Context
import android.os.Handler
import android.os.HandlerThread

abstract class BaseFragment : androidx.fragment.app.Fragment() {
    open val TAG = javaClass.simpleName

    val mContext: Context
        get() = this@BaseFragment.context!!

    val otherHandlerThread = HandlerThread(javaClass.simpleName + "_otherHandlerThread")
    val otherHandler by lazy {
        otherHandlerThread.start()
        Handler(otherHandlerThread.looper)
    }
}