package com.wang.gogolook.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.wang.gogolook.test.R
import com.wang.gogolook.test.databinding.DialogTextBinding
import com.wang.gogolook.tools.*

fun showMessageDialogOnlyOKButton(context: Context, message: String ,lambda: () -> Unit = {}) {
    TextDialog(context).apply {
        title = message
        dialogBinding.btnLift.visibility = View.GONE
        dialogBinding.btnRight.text = context.getString(R.string.ok)
        dialogBinding.btnRight.setOnClickListener {
            lambda()
            dialog.dismiss()
        }
        show()
    }
}

fun showMessageDialogOnlyOKButton(context: Context, title: String, message: String,lambda: () -> Unit = {}) {
    TextDialog(context).apply {
        this.title = title
        this.message = message
        dialogBinding.btnLift.visibility = View.GONE
        dialogBinding.btnRight.text = context.getString(R.string.ok)
        dialogBinding.btnRight.setOnClickListener {
            lambda()
            dialog.dismiss()
        }
        show()
    }
}


class TextDialog(val context: Context) {
    var title = ""
    var message = ""

    val dialog by lazy { MaterialDialog(context) }
    val dialogBinding by lazy { DataBindingUtil.inflate<DialogTextBinding>(LayoutInflater.from(context), R.layout.dialog_text, null, false) }
    fun show() {
        if (dialog.isShowing) {
            return
        }
        //
        dialogBinding.apply {
            val backgroundCorner = 25
            root.background = BackgroundDrawable.getRectangleBg(context, backgroundCorner, backgroundCorner, backgroundCorner, backgroundCorner, R.color.dialog_bg, 0, 0)
            tvTitle.text = title
            tvTitle.setTextSize(20)
            if (title == "") {
                tvTitle.visibility = View.GONE
            }
            tvMessage.text = message
            tvMessage.setTextSize(16)
//            tvMessage.movementMethod = ScrollingMovementMethod.getInstance()
            if (message.length > 600) {
                val layoutParams = scrollView.layoutParams ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
                layoutParams.height = (UserInterfaceTool.getScreenHeightPixels(context) * 0.4).toInt()
                scrollView.layoutParams = layoutParams
            }
            if (message == "") {
                tvMessage.visibility = View.GONE
            }
            //按鈕設定參數：
            val corner = 100 //圓角弧度
            val btnTextSize = context.resources.getDimensionPixelSize(R.dimen.btn_text_size) //按鍵文字大小
            val strokeWidth = context.resources.getDimensionPixelSize(R.dimen.btn_stroke_width) //按鈕邊界寬度

            btnRight.setTextSize(btnTextSize)
//            btnRight.background = null
            btnRight.setDefaultBackgroundAndTouchListener(R.color.white, R.color.green, R.color.white, R.color.green)

//            if (btnLift.visibility == View.VISIBLE) {
                btnLift.setTextSize(btnTextSize)
//            btnLift.background = null
                btnLift.setPressedBackground(
                    BackgroundDrawable.getRectangleBg(context, corner, corner, corner, corner, R.color.white, R.color.gray, strokeWidth),
                    BackgroundDrawable.getRectangleBg(context, corner, corner, corner, corner, R.color.gray, R.color.gray, strokeWidth)
                )
//            }
        }
        //
        dialog.apply {

            setContentView(dialogBinding.root)
            setCancelable(false)
            window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }
}