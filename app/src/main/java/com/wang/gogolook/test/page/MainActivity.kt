package com.wang.gogolook.test.page

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.wang.gogolook.test.R
import com.wang.gogolook.test.databinding.ActivityMainBinding
import project.main.base.BaseActivity

class MainActivity : BaseActivity() {
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        FragmentModular.changeFragment(
                supportFragmentManager,
                R.id.fragment,
                null,
                MainFragment(),
                null,
                false
        )
    }
}