package project.main.base

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import androidx.appcompat.app.AppCompatActivity
import android.view.Window

abstract class BaseActivity : AppCompatActivity() {
    open val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        actionBar?.hide()
    }

    val otherHandlerThread = HandlerThread(javaClass.simpleName + "_otherHandlerThread")
    val otherHandler by lazy {
        otherHandlerThread.start()
        Handler(otherHandlerThread.looper)
    }
}