package com.agent.fasttag.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityResetPasswordBinding
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.model.ResetPasswordRequestJson
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.FasTagSharedPreference
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_phoneNumber
import com.agent.fasttag.view.util.Status
import com.agent.fasttag.view.viewmodel.FasTagRepository
import com.agent.fasttag.view.viewmodel.FasTagViewModelFactory
import com.agent.fasttag.view.viewmodel.FastTagViewModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding:ActivityResetPasswordBinding
    private var oldPasswordVal:String=""
    private var newPasswordVal:String=""
    private var confirmPasswordVal:String=""
    var retrofitService: RetrofitService? =null
    lateinit var viewModel: FastTagViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setupViewModel()
        setUpObserver()
    }
    @SuppressLint("SuspiciousIndentation")
    private fun initView() {
        binding.headerLayout.ivToolBarBack.visibility= View.VISIBLE
        binding.headerLayout.ivToolBarBack.setOnClickListener {
            finish()
            AppConstants.slideToLeftAnim(this)
        }
        binding.submit.setOnClickListener {
        oldPasswordVal = binding.editOldPasswordInput.text.toString().trim()
        newPasswordVal = binding.editNewPasswodInput.text.toString().trim()
        confirmPasswordVal = binding.editConfirmPasswordInput.text.toString().trim()

            if(oldPasswordVal == ""){

                Toast.makeText(this,"Please enter old password",Toast.LENGTH_SHORT).show()
            }
            else if(newPasswordVal == ""){

                Toast.makeText(this,"Please enter new password",Toast.LENGTH_SHORT).show()
            }
            else if(confirmPasswordVal == ""){

                Toast.makeText(this,"Please enter confirm password",Toast.LENGTH_SHORT).show()
            }
            else if(newPasswordVal != confirmPasswordVal){

                Toast.makeText(this,"new password and confirm password should be same",Toast.LENGTH_SHORT).show()
            }else{
                val fasTagPref = FasTagSharedPreference.customPreference(this, FasTagSharedPreference.CUSTOM_PREF_NAME)

                AppConstants.launchSunsetDialog(this)
                var requestData= ResetPasswordRequestJson(fasTagPref.USER_phoneNumber!!,oldPasswordVal,newPasswordVal,confirmPasswordVal)
                val loginRequestJsonData = Gson().toJson(requestData)
                val jsonObject = JSONObject(loginRequestJsonData)
                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
                println("loginRequestJsonData:: "+jsonObject)
                viewModel.agentResetPassword(request)
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
        viewModel.resetPassword().observe(this){
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
//                        val i = Intent(this, AgentHomeActivity::class.java)
//                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(i)
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
}