package com.agent.fasttag.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivitySuccessBinding

class SuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }
    private fun initView(){
        binding.headerLayout.tvToolbarHederTitle.visibility= View.GONE
        binding.headerLayout.tvToolbarTitle.visibility= View.VISIBLE
        binding.headerLayout.ivToolBarBack.visibility= View.VISIBLE
        binding.headerLayout.tvToolbarHederTitle.text=getString(R.string.personal_details)
    }
}