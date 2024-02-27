package com.agent.fasttag.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityAgentLoginBinding
import com.agent.fasttag.databinding.ActivitySplashScreenBinding
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.FasTagSharedPreference
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_roleId

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var fasTagPref: SharedPreferences
    var USER_roleId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fasTagPref = FasTagSharedPreference.customPreference(this, FasTagSharedPreference.CUSTOM_PREF_NAME)
        USER_roleId=fasTagPref.USER_roleId!!
        Handler(Looper.getMainLooper()).postDelayed({
            if(USER_roleId!=""){
                loginToUser(USER_roleId)
            }else {
                val intent = Intent(this, AgentLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                AppConstants.slideToRightAnim(this)

            }

        }, 1000)
    }
    fun loginToUser(roleId:String){
        var intent = Intent(this, AgentHomeActivity::class.java)
        var agentType = getString(R.string.agent)
        if (roleId == "1") {
            agentType = getString(R.string.super_agent)
        } else if (roleId == "2") {
            agentType = getString(R.string.team_lead)

        } else {
            agentType = getString(R.string.agent)

        }
        intent.putExtra(getString(R.string.login_from), agentType)
        AppConstants.loginFrom=agentType
        startActivity(intent)
        finish()
        AppConstants.slideToRightAnim(this)
    }

}