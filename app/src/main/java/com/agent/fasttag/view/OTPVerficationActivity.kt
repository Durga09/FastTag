package com.agent.fasttag.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityOtpverficationBinding
import com.agent.fasttag.databinding.ActivityVehicleRegistrationBinding

class OTPVerficationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpverficationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpverficationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        countDownTimer()
    }
    public fun next(view: View){
        startActivity(Intent(this, PersonalDetailsActivity::class.java))
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        );

    }
    private fun initView(){
        binding.headerLayout.tvToolbarHederTitle.visibility=View.GONE
        binding.headerLayout.tvToolbarTitle.visibility=View.VISIBLE
        binding.headerLayout.ivToolBarBack.visibility=View.VISIBLE
        binding.headerLayout.tvToolbarHederTitle.text=getString(R.string.vehicle_details)
        binding.resend.setOnClickListener {
            countDownTimer()
        }
    }
    public fun back(view:View){
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        );
    }
    fun countDownTimer(){
        binding.resend.alpha=0.5f
        binding.resend.visibility=View.GONE

        binding.resend.isEnabled=false
        binding.tvTimer.visibility=View.VISIBLE
        val timer = object: CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
              binding.tvTimer.text="Resend in  "+millisUntilFinished/1000

            }

            override fun onFinish() {
                binding.resend.alpha=1f
                binding.resend.isEnabled=true
                binding.tvTimer.visibility=View.GONE
                binding.resend.visibility=View.VISIBLE


            }
        }
        timer.start()
    }
}