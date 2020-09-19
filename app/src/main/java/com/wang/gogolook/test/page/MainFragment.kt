package com.wang.gogolook.test.page

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.wang.gogolook.base.BaseRecyclerViewDataBindingAdapter
import com.wang.gogolook.dialog.ListDialog
import com.wang.gogolook.dialog.TextDialog
import com.wang.gogolook.test.R
import com.wang.gogolook.test.api.SearchModel
import com.wang.gogolook.test.databinding.AdapterMainRvGridBinding
import com.wang.gogolook.test.databinding.AdapterMainRvListBinding
import com.wang.gogolook.test.databinding.FragmentMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import project.main.api.ApiConnect
import project.main.base.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : BaseFragment() {

    private lateinit var mBinding: FragmentMainBinding

    private var job: Job? = null
    private var page = 1
    private val historyList = mutableListOf<String>()
    private var filterTagList = mutableListOf<String>()
    private var searchKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        syncApi()
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }

    private fun initView(){
        mBinding.ivHistory.setOnClickListener {
            if (historyList.size == 0) {
                TextDialog(context!!).apply {
                    title = "History is Empty!!"
                    dialogBinding.btnLift.visibility = View.GONE
                    dialogBinding.btnRight.text = context.getString(R.string.ok)
                    dialogBinding.btnRight.setOnClickListener {
                        dialog.dismiss()
                    }
                    show()
                }
                return@setOnClickListener
            }
            ListDialog(context!!).apply {
                list.addAll(historyList)
                binding.btn.setOnClickListener { dialog.dismiss() }
                listener = object : ListDialog.Listener {
                    override fun onItemClick(position: Int, data: String): Boolean {
                        searchKey = data
                        syncApi(searchKey)
                        mBinding.etSearch.setText(data)
                        dialog.dismiss()
                        return true
                    }
                }
                show()
            }
        }
        mBinding.ivFilter.setOnClickListener {
            FilterDialog(context!!).apply {
                binding.btn.setOnClickListener {
                    filterTagList = getFilterTag().toMutableList()
                    mBinding.chipGroupFilter.removeAllViews()
                    for (tag in filterTagList) {
                        val chip = Chip(context!!)
                        chip.text = tag
                        chip.tag = tag
                        mBinding.chipGroupFilter.addView(chip)
                    }
                    syncApi(filterList = filterTagList)
                    dialog.dismiss()
                }
                show()
            }
        }
        mBinding.ivSort.setOnClickListener {
            val layoutManager = mBinding.rv.layoutManager as? StaggeredGridLayoutManager
            if (layoutManager?.spanCount == 1) {
                changeSort(Sort.GRID)
            } else if (layoutManager?.spanCount == 2) {
                changeSort(Sort.LIST)
            }
        }
        mBinding.etSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = v.text.toString()
                if (text.length > 0 && historyList.contains(text) == false) {
                    historyList.add(0, text)
                }
                searchKey = text
                syncApi(searchKey)
            }
            false
        })
        //
        mBinding.rv.adapter = adapter
        changeSort(Sort.GRID)

    }

    private enum class Sort(val spanCount: Int){
        LIST(1),
        GRID(2)
    }
    private fun changeSort(sort: Sort){
        val layoutManager = mBinding.rv.layoutManager as? StaggeredGridLayoutManager
        layoutManager?.spanCount = sort.spanCount
        if (layoutManager?.spanCount == 1) {
            mBinding.ivSort.setImageResource(R.drawable.ic_sharp_grid_on_24)
        } else if (layoutManager?.spanCount == 2) {
            mBinding.ivSort.setImageResource(R.drawable.ic_baseline_view_list_24)
        }
    }

    private fun syncApi(search: String = searchKey, page: Int = 1, filterList: List<String> = filterTagList){
        if (job?.isActive == true) {
            return
        }
        if (page == 1){
            adapter.clear()
        }
        mBinding.progressBar.visibility = View.VISIBLE
        this.page = page
        job = GlobalScope.launch {
            val map = mutableMapOf<String, String>()
            map["key"] = "18359801-5f61a6b39789bfe670e92216e"
            map["q"] = search
            map["page"] = page.toString()
            for (tag in filterList.filter { it.contains("all") == false }) {
                val arr = tag.split(":")
                if (arr.size < 2) {
                    continue
                }
                map[arr[0]] = arr[1]
            }
            val cell = ApiConnect.getService(context!!).search(map)
            val response = cell.execute()
            if (response.isSuccessful) {
                Log.e(TAG, "${response.body()?.hits?.size}")
            }
            GlobalScope.launch(Dispatchers.Main) {
                adapter.addItem(response.body()?.hits ?: mutableListOf())
                mBinding.progressBar.visibility = View.GONE
            }
        }
    }

    private val adapter by lazy { Adapter(context!!) }
    inner class Adapter(val context: Context): BaseRecyclerViewDataBindingAdapter<SearchModel.Hit>(
        context, listOf(
            R.layout.adapter_main_rv_grid,
            R.layout.adapter_main_rv_list
        )
    ) {

        override fun getItemViewType(position: Int): Int {
            val spanCount = (mBinding.rv.layoutManager as? StaggeredGridLayoutManager)?.spanCount ?: 1
            return if (spanCount == 2)
                0
            else
                1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return if (viewType == 0) {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    LayoutInflater.from(parent.context),
                    layoutID[0],
                    parent,
                    false
                )
                ViewHolder(binding)
            } else {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    LayoutInflater.from(parent.context),
                    layoutID[1],
                    parent,
                    false
                )
                ViewHolder(binding)
            }
        }

        override fun initViewHolder(viewHolder: ViewHolder) {

        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, data: SearchModel.Hit) {
            if (viewHolder.itemViewType == 0) {
                //spanCount == 2
                val binding = viewHolder.binding as AdapterMainRvGridBinding
                Glide.with(context)
                    .load(data.previewURL)
                    .into(binding.iv)
            } else {
                //spanCount == 1
                val binding = viewHolder.binding as AdapterMainRvListBinding
                Glide.with(context)
                    .load(data.previewURL)
                    .into(binding.iv)
                binding.tvUser.text = data.user
            }
            if (position == itemCount - 1) {
                syncApi(page = page + 1)
            }
        }

        override fun onItemClick(view: View, position: Int, data: SearchModel.Hit): Boolean {
            return false
        }

        override fun onItemLongClick(view: View, position: Int, data: SearchModel.Hit): Boolean {
            return false
        }

        override fun search(
            constraint: CharSequence,
            list: ArrayList<SearchModel.Hit>
        ): ArrayList<SearchModel.Hit> {
            return list
        }
    }
}









