package com.example.myfragments.ui.activity

import android.os.Bundle
import com.example.myfragments.core.base.BaseActivity
import com.example.myfragments.databinding.ActivityMain2Binding

class Main2Activity : BaseActivity() {
    lateinit var viewBinding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        supportActionBar?.hide()

    }
}