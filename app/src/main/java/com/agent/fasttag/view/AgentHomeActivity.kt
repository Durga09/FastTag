package com.agent.fasttag.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.agent.fasttag.CreateAgentActivity
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityAgentHomeBinding
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.FasTagSharedPreference
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_USERNAME
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_agentID
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_roleName
import com.agent.fasttag.view.util.FasTagSharedPreference.clear
import java.util.*


class AgentHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgentHomeBinding
     var loginFrom =""
    val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
     val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
     val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="
    val encriptSt="durga"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setDrawerMenuClickListener()

    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView(){
//        binding.headerLayout.tvToolbarHederTitle.visibility=View.VISIBLE
//        binding.headerLayout.tvToolbarTitle.visibility=View.GONE
//        binding.headerLayout.ivToolBarBack.visibility=View.VISIBLE
//        binding.headerLayout.tvToolbarHederTitle.text=getString(R.string.vehicle_details)
//        binding.drawerLayout.closeDrawers()
        var encriptString=encrypt("Mamoluga undadu Mari..")
        println("encriptString::: "+encriptString)
        var decryptString=decript(encriptString!!)
        println("decriptString::: "+decryptString)
        var bundle :Bundle ?=intent.extras
        loginFrom = AppConstants.loginFrom
        println("LOGIN FROM $loginFrom")
        if(loginFrom==getString(R.string.agent)){
            binding.homeLayout.llFatRecharge.gravity=Gravity.CENTER
            binding.homeLayout.llCreateAgents.visibility=View.GONE
            binding.leftDrawerMenu.clAgents.visibility=View.GONE
            binding.leftDrawerMenu.jobNumberLine1.visibility=View.GONE

        }else if(loginFrom==getString(R.string.team_lead)){
            binding.homeLayout.llFatRecharge.gravity=Gravity.CENTER
            binding.homeLayout.llCreateAgents.visibility=View.GONE
            binding.homeLayout.createAgentByLead.visibility=View.VISIBLE
        }else{
            binding.homeLayout.llCreateAgents.visibility=View.VISIBLE
        }

//        AppConstants.launchSunsetDialog(this)
        binding.homeLayout.headerLayout1.ivNavMenu.visibility= View.VISIBLE
        binding.homeLayout.headerLayout1.ivNavMenu.setOnClickListener {
            toggleLeftDrawer()
        }
        binding.leftDrawerMenu.clChangePassword.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)

            startActivity(Intent(this, ResetPasswordActivity::class.java))
            AppConstants.slideToRightAnim(this)

        }
        binding.leftDrawerMenu.clAgents.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            loginFrom = AppConstants.loginFrom
            if(loginFrom==getString(R.string.super_agent)) {
                startActivity(Intent(this, TeamLeadsListActivity::class.java))
                AppConstants.slideToRightAnim(this)
            }else if(loginFrom==getString(R.string.team_lead)){
                val fasTagPref = FasTagSharedPreference.customPreference(this, FasTagSharedPreference.CUSTOM_PREF_NAME)
                var parentId=fasTagPref.USER_agentID!!
                AppConstants.parentId = parentId

                startActivity(Intent(this, AgentsListActivity::class.java))
                AppConstants.slideToRightAnim(this)
            }

        }
        val fasTagPref = FasTagSharedPreference.customPreference(this, FasTagSharedPreference.CUSTOM_PREF_NAME)

        binding.leftDrawerMenu.tvUsername.text = fasTagPref.USER_USERNAME
        binding.leftDrawerMenu.tvRoleName.text = fasTagPref.USER_roleName

    }
    private fun setDrawerMenuClickListener() {


        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {
                setNotificationCountToDrawerLayout()
            }
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}

        })
    }
    private fun setNotificationCountToDrawerLayout(){
//        binding.leftDrawerMenu.tvNotificationsCount.text = PrefsHelper.notificationCount
        binding.leftDrawerMenu.tvNotificationsCount.text = "54"
//        binding.leftDrawerMenu.tvNotificationsCount.visibility = if (PrefsHelper.notificationCount == "0") View.GONE else View.VISIBLE
//        binding.leftDrawerMenu.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        binding.leftDrawerMenu.clLogOut.setOnClickListener {
//            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
           logoutAlert()


        }
    }
    private fun toggleLeftDrawer() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {

            binding.drawerLayout.closeDrawer(GravityCompat.START)

        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }
      fun buyFastTag(view:View){
        startActivity(Intent(this, VehicleRegistration::class.java))
        AppConstants.slideToRightAnim(this)


    }
     fun createTeamLead(view:View){
        var intent=Intent(this, CreateAgentActivity::class.java)
        intent.putExtra(getString(R.string.role_from),getString(R.string.team_lead))
        startActivity(intent)
        AppConstants.slideToRightAnim(this)


    }
    fun getTransactions(view:View){
        var intent=Intent(this, TransactionsListActivity::class.java)
        intent.putExtra(getString(R.string.role_from),getString(R.string.team_lead))
        startActivity(intent)
        AppConstants.slideToRightAnim(this)


    }
     fun createAgent(view:View){
//        var intent=Intent(this, CreateAgentActivity::class.java)
         var intent=Intent(this, PhonePayPaymentGatewayActivity::class.java)
         intent.putExtra(getString(R.string.to_payment_gateway),getString(R.string.documents_details))
        startActivity(intent)
        AppConstants.slideToRightAnim(this)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(strToEncrypt: String) :  String?
    {
        try
        {
          /*  val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec)
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)*/
            val encoder: Base64.Encoder = Base64.getEncoder()
            val encoded: String = encoder.encodeToString(strToEncrypt.toByteArray())

            return encoded
        }
        catch (e: Exception)
        {
            println("Error while encrypting: $e")
        }
        return null
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun decript(encoded:String):String{
        val decoder: Base64.Decoder = Base64.getDecoder()
        val decoded = String(decoder.decode(encoded))

        return decoded
    }
    private fun logoutAlert()
    {

        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.do_you_want_to_logout))
            builder.setTitle(getString(R.string.app_name))
            builder.setCancelable(false)
            builder.setPositiveButton(
                Html.fromHtml("<font color=" + this.resources.getColor(R.color.black) + ">Yes</font>"),
                DialogInterface.OnClickListener {
                        dialog: DialogInterface, _: Int -> dialog.dismiss()
                    dialog.dismiss()
                    binding.drawerLayout.closeDrawers()
                    var  fasTagPref = FasTagSharedPreference.customPreference(this, FasTagSharedPreference.CUSTOM_PREF_NAME)
                    fasTagPref.clear {
                        it.clear().commit()
                    }
                    val intent = Intent(this, AgentLoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    AppConstants.slideToLeftAnim(this)

                })
            builder.setNegativeButton(
                Html.fromHtml("<font color=" + this.resources.getColor(R.color.black) + ">No</font>"),
                DialogInterface.OnClickListener {
                        dialog: DialogInterface, _: Int -> dialog.dismiss()
                    dialog.dismiss()
                })
            val alertDialog = builder.create()
            // Show the Alert Dialog box
            alertDialog.show()
        }
    }
    /*fun decrypt(strToDecrypt : String) : String? {
        try
        {

            val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return  String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
        }
        catch (e : Exception) {
            println("Error while decrypting: $e");
        }
        return null
    }*/

}

