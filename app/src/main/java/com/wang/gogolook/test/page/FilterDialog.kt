package com.wang.gogolook.test.page

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.wang.gogolook.base.BaseRecyclerViewDataBindingAdapter
import com.wang.gogolook.test.R
import com.wang.gogolook.test.databinding.AdapterDialogListBinding
import com.wang.gogolook.test.databinding.DialogFilterBinding
import com.wang.gogolook.test.databinding.DialogListBinding
import com.wang.gogolook.tools.*

class FilterDialog(val context: Context) {
    val TAG = "FilterDialog"

    val dialog by lazy { MaterialDialog(context) }
    val binding by lazy { DataBindingUtil.inflate<DialogFilterBinding>(LayoutInflater.from(context), R.layout.dialog_filter, null, false) }
    fun show(){
        if (dialog.isShowing){
            return
        }
        //
        binding.apply {
            val backgroundCorner = 25
            root.background = BackgroundDrawable.getRectangleBg(context, backgroundCorner, backgroundCorner, backgroundCorner, backgroundCorner, R.color.dialog_bg, 0, 0)
            //
            svMain.setViewSize(ViewGroup.LayoutParams.MATCH_PARENT, (UserInterfaceTool.getScreenHeightPixels(context) * 0.5).toInt())
            initChipGroup(chipGroupImage, "image_type", listOf("all", "photo", "illustration", "vector"))
            initChipGroup(chipGroupOrientation, "orientation", listOf("all", "horizontal", "vertical"))
            initChipGroup(chipGroupCategory, "category", listOf("all", "backgrounds", "fashion", "nature", "science", "education", "feelings", "health", "people", "religion", "places", "animals", "industry", "computer", "food", "sports", "transportation", "travel", "buildings", "business", "music"))
            initChipGroup(chipGroupColors, "colors", listOf("all", "grayscale", "transparent", "red", "orange", "yellow", "green", "turquoise", "blue", "lilac", "pink", "white", "gray", "black", "brown"))
            initChipGroup(chipGroupEditorsChoice, "editors_choice", listOf("false", "true"))
            initChipGroup(chipGroupSafesearch, "safesearch", listOf("false", "true"))
            initChipGroup(chipGroupOrder, "order", listOf("popular", "latest"))
            seekBarMinWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    tvMinWidth.text = "min_width(${progress * 100})"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
            seekBarMinHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    tvMinHeight.text = "min_height(${progress * 100})"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
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

    fun getFilterTag(): List<String>{
        val results = mutableListOf<String>()
        (binding.root.findViewById(binding.chipGroupImage.checkedChipId) as? Chip)?.apply {
            val tag = tag as? String
            if (tag != null) {
                results.add(tag)
            }
        }
        (binding.root.findViewById(binding.chipGroupOrientation.checkedChipId) as? Chip)?.apply {
            val tag = tag as? String
            if (tag != null) {
                results.add(tag)
            }
        }
        (binding.root.findViewById(binding.chipGroupCategory.checkedChipId) as? Chip)?.apply {
            val tag = tag as? String
            if (tag != null) {
                results.add(tag)
            }
        }
        (binding.root.findViewById(binding.chipGroupEditorsChoice.checkedChipId) as? Chip)?.apply {
            val tag = tag as? String
            if (tag != null) {
                results.add(tag)
            }
        }
        (binding.root.findViewById(binding.chipGroupSafesearch.checkedChipId) as? Chip)?.apply {
            val tag = tag as? String
            if (tag != null) {
                results.add(tag)
            }
        }
        (binding.root.findViewById(binding.chipGroupCategory.checkedChipId) as? Chip)?.apply {
            val tag = tag as? String
            if (tag != null) {
                results.add(tag)
            }
        }
        (binding.root.findViewById(binding.chipGroupOrder.checkedChipId) as? Chip)?.apply {
            val tag = tag as? String
            if (tag != null) {
                results.add(tag)
            }
        }
        for (id in binding.chipGroupColors.checkedChipIds) {
            (binding.root.findViewById(id) as? Chip)?.apply {
                val tag = tag as? String
                if (tag != null) {
                    results.add(tag)
                }
            }
        }
        results.add("min_width:${binding.seekBarMinWidth.progress * 100}")
        results.add("min_height:${binding.seekBarMinHeight.progress * 100}")
        return results
    }

    private fun initChipGroup(chipGroup: ChipGroup, key: String, valueList: List<String>){
        for (tag in valueList) {
            val chip = LayoutInflater.from(context).inflate(R.layout.chip,null) as? Chip
            chip?.isCheckable = true
            chip?.text = tag
            chip?.tag = "$key:$tag"
            chipGroup.addView(chip)
        }
        (chipGroup.children.firstOrNull() as? Chip)?.isChecked = true
    }
}