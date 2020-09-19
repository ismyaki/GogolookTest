package com.wang.gogolook.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList


public abstract class BaseRecyclerViewDataBindingAdapter<T>(private val context: Context, var layoutID:List<Int>) :
        RecyclerView.Adapter<BaseRecyclerViewDataBindingAdapter<T>.ViewHolder>(), View.OnClickListener, View.OnLongClickListener, Filterable {
    private var list = ArrayList<T>()

    private val myInflater: LayoutInflater? = null
    private var sortKey: String? = null

    private val mFilter = ItemFilter()
    /**
     * 搜尋過的list
     */
    private var filteredList: ArrayList<T>? = ArrayList<T>()

//    private var mOnItemClickListener: RecyclerViewTool.OnRecyclerViewItemClickListener<T>? = null
//    private var mOnItemLongClickListener: RecyclerViewTool.OnRecyclerViewItemLongClickListener<T>? = null

    /**
     * 初始化 ViewHolder
     * */
    abstract fun initViewHolder(viewHolder: ViewHolder)

    /**
     * 每次 ViewHolder 變化
     * */
    abstract fun onBindViewHolder(viewHolder: ViewHolder, position: Int , data:T)

    /**
     * 點擊事件
     * @return true 為以處理
     * */
    abstract fun onItemClick(view: View, position: Int, data: T) : Boolean

    /**
     * 常案事件
     * @return true 為以處理
     * */
    abstract fun onItemLongClick(view: View, position: Int, data: T) : Boolean

    /**
     * 搜尋
     * 不需要則 return list 即可
     * */
    abstract fun search(constraint: CharSequence , list : ArrayList<T>) : ArrayList<T>

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    inner class ViewHolder(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener(this@BaseRecyclerViewDataBindingAdapter)
            binding.root.setOnLongClickListener(this@BaseRecyclerViewDataBindingAdapter)
            initViewHolder(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(layoutID, parent, false)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), layoutID[0], parent, false)
        val viewHolder = ViewHolder(binding)
        if (viewType == 0) {
            //根據不同的 viewType 改變 tools.view 風格
        }
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.binding.root.tag = position
        val viewType = viewHolder.itemViewType

        val data = filteredList!![position]

        onBindViewHolder(viewHolder , position , data)
    }

    override fun getItemCount(): Int {
        return filteredList!!.size
    }

    fun getItem(position: Int): T {
        return filteredList!![position]
    }

    fun addItem(list: List<T>) {
        this.list.addAll(list)
        filter.filter("")
    }

    fun setItem(list: List<T>) {
        this.list = list as ArrayList<T>
        filter.filter("")
    }

    fun addItem(list: ArrayList<T>) {
        this.list = list
        filter.filter("")
    }

    fun addItem(data: T) {
        list.add(data)
        filter.filter("")
    }

    fun remove(data: T) {
        list.remove(data)
        filter.filter("")
    }

    fun clear() {
        list.clear()
        filter.filter("")
    }

//    fun setOnLongClickListener(listener: RecyclerViewTool.OnRecyclerViewItemLongClickListener<T>) {
//        mOnItemLongClickListener = listener
//    }
//
//    fun setOnItemClickListener(listener: RecyclerViewTool.OnRecyclerViewItemClickListener<T>) {
//        this.mOnItemClickListener = listener
//    }


    override fun onClick(v: View) {
        onItemClick(v, v.tag as Int, getItem(v.tag as Int))
    }

    override fun onLongClick(v: View): Boolean {
        return onItemLongClick(v, v.tag as Int, getItem(v.tag as Int))
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    private inner class ItemFilter : Filter() {

        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
//            var constraint = constraint
//
//            constraint = constraint.toString().toLowerCase(Locale.getDefault())
//
//            val results = Filter.FilterResults()
//            val count = list.size
//            val nlist = ArrayList<T>()
//            val imgDataList = arrayOf<String>(Product.PRODUCT_NAME, Product.PRODUCT_SHORT_NAME, Product.BARCODE)
//            for (i in 0 until count) {
//                val data = list[i]
//
//                var isAdd = false
//                if (constraint.toString() == "") {
//                    isAdd = true
//                }
//                var j = 0
//                while (isAdd == false && j < imgDataList.size) {
//                    val key = imgDataList[j]
//                    val `val` = data.getString(key, "").toLowerCase(Locale.getDefault())
//                    if (`val`.contains(constraint) && `val`.indexOf(constraint.toString()) == 0) {
//                        isAdd = true
//                        break
//                    }
//                    j++
//                }
//                if (isAdd == true) {
//                    nlist.add(data)
//                }
//            }

            val results = Filter.FilterResults()
            val nlist = search(constraint , list)
            results.values = nlist
            results.count = nlist.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            val arrayList = results.values as ArrayList<T>
            filteredList = arrayList
            if (arrayList == null) {
                filteredList = ArrayList<T>()
            }

            //TODO 暫時拿掉排序功能
//            if (sortKey != null && sortKey.equals("") == false) {
//                sort(sortKey);
//            }
            notifyDataSetChanged()
        }
    }
}