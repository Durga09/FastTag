package com.agent.fasttag

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.agent.fasttag.databinding.ActivityForgotPasswordBinding
import com.agent.fasttag.databinding.ActivityResetPasswordBinding
import com.agent.fasttag.databinding.LayoutDailogListViewBinding
import com.agent.fasttag.view.adapter.CommanAdapter
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.model.GenerateOtpRequestJson
import com.agent.fasttag.view.model.LoginRequestJson
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.Status
import com.agent.fasttag.view.viewmodel.FasTagRepository
import com.agent.fasttag.view.viewmodel.FasTagViewModelFactory
import com.agent.fasttag.view.viewmodel.FastTagViewModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.ArrayList

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding:ActivityForgotPasswordBinding
    private var phoneNumberVal:String=""
    private var newPasswordVal:String=""
    private var confirmPasswordVal:String=""
    private var otpInputValVal:String=""
    var categoryTypeArr = ArrayList<String>()
    private var categoryTypeVal:String=""

    var retrofitService: RetrofitService? =null
    lateinit var viewModel: FastTagViewModel
    private var responseOTP=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setupViewModel()
        setUpObserver()
    }
    private fun sendOtpRequest(){
        phoneNumberVal = binding.editPhoneNumberInput.text.toString().trim()
        categoryTypeVal = binding.selectCategory.text.toString().trim()

        if(phoneNumberVal == ""){
            AppConstants.showMessageAlert(this,getString(R.string.please_enter_phone_number))
        }
        else if(categoryTypeVal == ""){
            AppConstants.showMessageAlert(this,getString(R.string.please_select_category))
        }
        else{
            AppConstants.launchSunsetDialog(this)
            var requestData= GenerateOtpRequestJson(phoneNumberVal,categoryTypeVal)
            val loginRequestJsonData = Gson().toJson(requestData)
            val jsonObject = JSONObject(loginRequestJsonData)
            val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
            println("GenerateOtpRequestJsonData:: "+request)
            viewModel.getOTP(request)
        }
    }
    private fun initView(){
        categoryTypeArr.add("Customer")
        categoryTypeArr.add("Agent")
        binding.headerLayout.ivToolBarBack.setOnClickListener {
            finish()
            AppConstants.slideToLeftAnim(this)
        }
        binding.selectCategory.setOnClickListener {
            openDialog(getString(R.string.select_category),categoryTypeArr)
        }
        binding.submitGenerateOtp.setOnClickListener {
            sendOtpRequest()

        }
        binding.resendOtp.setOnClickListener {
            sendOtpRequest()
        }
        binding.submit.setOnClickListener {

            phoneNumberVal = binding.editPhoneNumberInput.text.toString().trim()
            newPasswordVal = binding.editNewPasswodInput.text.toString().trim()
            confirmPasswordVal = binding.editConfirmPasswordInput.text.toString().trim()
            otpInputValVal = binding.editOtpInput.text.toString().trim()


            if(phoneNumberVal == ""){
                AppConstants.showMessageAlert(this,getString(R.string.please_enter_phone_number))
            }
            if(otpInputValVal==""){
                AppConstants.showMessageAlert(this,getString(R.string.please_enter_otp))

            }else if(otpInputValVal!=responseOTP){
                AppConstants.showMessageAlert(this,getString(R.string.invalid_otp))

            }
            else if(newPasswordVal == ""){
                AppConstants.showMessageAlert(this,getString(R.string.please_enter_new_password))
            }else if(confirmPasswordVal == ""){
                AppConstants.showMessageAlert(this,getString(R.string.please_enter_confirm_password))
            }else if(newPasswordVal != confirmPasswordVal){
                AppConstants.showMessageAlert(this,getString(R.string.confirm_password_validation))
            }else{
                AppConstants.launchSunsetDialog(this)
                var requestData= LoginRequestJson(phoneNumberVal,confirmPasswordVal)
                val loginRequestJsonData = Gson().toJson(requestData)
                val jsonObject = JSONObject(loginRequestJsonData)
                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
                println("loginRequestJsonData:: "+request)
                viewModel.agentForgotPassword(request)
            }
        }
    }
    private fun setupViewModel() {
        retrofitService = RetrofitService.getInstance(AppConstants.baseURL)
        var repository = FasTagRepository(retrofitService!!)
        viewModel = ViewModelProvider(
            this,
            FasTagViewModelFactory(repository)
        )[FastTagViewModel::class.java]
    }
    private fun setUpObserver(){
        viewModel.forgotPasswordRequest().observe(this){
            AppConstants.cancelSunsetDialog()

            println("loginRequestData:: $it")
            when(it.status){
                Status.SUCCESS ->{

                    showResponseMessageAlert(this,it.data!!.message)
                }
                Status.LOADING -> {
                    AppConstants.launchSunsetDialog(this)

                }
                Status.ERROR -> {
                    //Handle Error
                    AppConstants.cancelSunsetDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.getOtpRequestData().observe(this){
            AppConstants.cancelSunsetDialog()

            println("loginRequestData:: $it")
            when(it.status){
                Status.SUCCESS ->{

                    if(it.data?.message!!.contains("Success")){
                        responseOTP = it.data.reponseData?.otp!!
                        showResponseMessageAlert(this,"Otp sent success")
                        binding.llChangePwd.visibility=View.VISIBLE
                        binding.submitGenerateOtp.visibility = View.GONE
                        countDownTimer()

                    }else{
                        showResponseMessageAlert(this,it.data!!.message)
                    }
                }
                Status.LOADING -> {
                    AppConstants.launchSunsetDialog(this)

                }
                Status.ERROR -> {
                    //Handle Error
                    AppConstants.cancelSunsetDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
    fun showResponseMessageAlert(mContext: Activity, message: String?) {
        mContext.runOnUiThread {
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(message)
            builder.setTitle(mContext.getString(R.string.app_name))
            builder.setCancelable(false)
            builder.setPositiveButton(
                Html.fromHtml("<font color=" + mContext.resources.getColor(R.color.colorAccent) + ">OK</font>"),
                DialogInterface.OnClickListener {
                        dialog: DialogInterface, which: Int -> dialog.dismiss()
                     if(message == "Password updated successfully"){
                         finish()
                         AppConstants.slideToLeftAnim(this)
                     }
                })
            /* builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });*/
            val alertDialog = builder.create()
            // Show the Alert Dialog box
            alertDialog.show()
        }
    }
    fun openDialog(dropFrom:String,listArr: ArrayList<String>) {

        var layoutDailogListViewBinding =  LayoutDailogListViewBinding.inflate(layoutInflater)
        val shareAlertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        shareAlertBuilder.setView(layoutDailogListViewBinding.root)
        layoutDailogListViewBinding.tvHeader.text=dropFrom

        val alertDialog: AlertDialog = shareAlertBuilder.create()
        layoutDailogListViewBinding.listView.adapter= CommanAdapter(this,listArr)
        layoutDailogListViewBinding.listView.setOnItemClickListener { parent, view, position, id ->


            binding.selectCategory.setText(listArr[position])
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
   fun countDownTimer(){
        binding.resendOtp.alpha=0.5f
        binding.resendOtp.visibility=View.GONE

        binding.resendOtp.isEnabled=false
        binding.tvTimer.visibility=View.VISIBLE
        val timer = object: CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text="Resend in  "+millisUntilFinished/1000

            }

            override fun onFinish() {
                binding.resendOtp.alpha=1f
                binding.resendOtp.isEnabled=true
                binding.tvTimer.visibility=View.GONE
                binding.resendOtp.visibility=View.VISIBLE


            }
        }
        timer.start()
    }
}