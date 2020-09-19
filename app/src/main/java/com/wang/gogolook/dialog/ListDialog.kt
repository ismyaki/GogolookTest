package com.wang.gogolook.dialog

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.wang.gogolook.base.BaseRecyclerViewDataBindingAdapter
import com.wang.gogolook.test.R
import com.wang.gogolook.test.databinding.AdapterDialogListBinding
import com.wang.gogolook.test.databinding.DialogListBinding
import com.wang.gogolook.tools.*

class ListDialog(val context: Context) {
    var title = ""
    var list = mutableListOf<String>()

    val dialog by lazy { MaterialDialog(context) }
    val binding by lazy { DataBindingUtil.inflate<DialogListBinding>(LayoutInflater.from(context), R.layout.dialog_list, null, false) }
    fun show(){
        if (dialog.isShowing){
            return
        }
        //
        binding.apply {
            val backgroundCorner = 25
            root.background = BackgroundDrawable.getRectangleBg(context, backgroundCorner, backgroundCorner, backgroundCorner, backgroundCorner, R.color.dialog_bg, 0, 0)
            tvTitle.text = title
            tvTitle.setTextSize(16)
            if (title == ""){
                tvTitle.visibility = View.GONE
            }
            //
            val layoutManager = object : LinearLayoutManager(context!! , LinearLayoutManager.VERTICAL , false) {
                override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) = try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
            recyclerView.layoutManager = layoutManager

            if (list.size > 10){
                val layoutParams = recyclerView.layoutParams ?: RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0)
                layoutParams.height = (UserInterfaceTool.getScreenHeightPixels(context) * 0.5).toInt()
                recyclerView.layoutParams = layoutParams
            }

            val divider = DividerItemDecoration(context , layoutManager.orientation)
            divider.setDrawable(ColorDrawable(ContextCompat.getColor(context , R.color.dialog_divider)))
            recyclerView.addItemDecoration(divider)
            adapter.addItem(list)
            recyclerView.adapter = adapter
            //
            //按鈕設定參數：
            val corner = 100 //圓角弧度
            val btnTextSize = context.resources.getDimensionPixelSize(R.dimen.btn_text_size) //按鍵文字大小
            val strokeWidth = context.resources.getDimensionPixelSize(R.dimen.btn_stroke_width) //按鈕邊界寬度

            btn.setTextSize(btnTextSize)
//            btnLift.background = null
            btn.setPressedBackground(
                BackgroundDrawable.getRectangleBg(context, corner, corner, corner, corner, R.color.white, R.color.gray, strokeWidth),
                BackgroundDrawable.getRectangleBg(context, corner, corner, corner, corner, R.color.gray, R.color.gray, strokeWidth)
            )
        }
        //
        dialog.apply {
            setContentView(binding.root)
            setCancelable(false)
            window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }

    var listener: Listener? = null
    interface Listener{
        fun onItemClick(position: Int, data: String): Boolean
    }

    private val adapter by lazy { ListAdapter(context) }
    private inner class ListAdapter(val context: Context): BaseRecyclerViewDataBindingAdapter<String>(context, listOf(R.layout.adapter_dialog_list)){
        override fun initViewHolder(viewHolder: ViewHolder) {
            val binding = viewHolder.binding as AdapterDialogListBinding
            binding.tvName.setTextSize(14)
            binding.tvName.setPressedTextColor(R.color.dialog_txt_message , R.color.txt_gray)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, data: String) {
            val binding = viewHolder.binding as AdapterDialogListBinding
            binding.tvName.text = data
        }

        override fun onItemClick(view: View, position: Int, data: String): Boolean {
            return listener?.onItemClick(position, data) ?: false
        }

        override fun onItemLongClick(view: View, position: Int, data: String): Boolean {
            return false
        }

        override fun search(constraint: CharSequence, list: ArrayList<String>): ArrayList<String> {
            return list
        }
    }
}