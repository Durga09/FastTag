package com.agent.fasttag.view

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.agent.fasttag.ForgotPasswordActivity
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityAgentLoginBinding
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.model.LoginRequestJson
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.FasTagSharedPreference
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_USERNAME
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_agentID
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_email
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_firstName
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_lastName
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_parentId
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_phoneNumber
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_roleId
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_roleName
import com.agent.fasttag.view.util.Status
import com.agent.fasttag.view.viewmodel.FasTagRepository
import com.agent.fasttag.view.viewmodel.FasTagViewModelFactory
import com.agent.fasttag.view.viewmodel.FastTagViewModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.security.GeneralSecurityException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AgentLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgentLoginBinding
    private val characterEncoding = "UTF-8"
    private val cipherTransformation = "AES/CBC/PKCS5Padding"
    private val aesEncryptionAlgorithm = "AES"
    private val actualVal="Mamoluga undadu Mari By Durga..."
    var retrofitService: RetrofitService? =null
    private var userNameVal:String=""
    private var passwordVal:String=""
    lateinit var viewModel: FastTagViewModel
    val CUSTOM_PREF_NAME = "User_data"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

     /*  var encriptVal= encrypt(actualVal,"ASDFGHJKLASDFGHJ")
        println("ENCRIPTVAL EEEE::$encriptVal")
        var decriptVal= decrypt("3kumWrwb5At/tBOvQZcyC9HsDWg6UPZsJGHw9rFa2oQ=","ASDFGHJKLASDFGHJ")
        println("DECRIPTVAL EEEE::$decriptVal")*/
        initView()
        setupViewModel()
        setUpObserver()
    }

    fun initView(){

        binding.editUsernameInput.setText("1122334455")
        binding.editPasswordInput.setText("Jai@123#")
        binding.btnSignup.setOnClickListener {
            var intent=  Intent(this, AgentSignupActivity::class.java)
            intent.putExtra(getString(R.string.login_from),getString(R.string.agent))
            startActivity(intent)
           AppConstants.slideToRightAnim(this)
        }
        binding.buttonLogin.setOnClickListener {

            userNameVal = binding.editUsernameInput.text.toString().trim()
            passwordVal = binding.editPasswordInput.text.toString().trim()
            if(userNameVal == ""){
                AppConstants.showMessageAlert(this,getString(R.string.please_enter_user_name))
            }
            else if( passwordVal == ""){
                AppConstants.showMessageAlert(this,getString(R.string.please_enter_password))
            }else{
                if(AppConstants.isNetworkAvailable(this)) {
                    login()
                }else{
                    AppConstants.showNoInternetConnectionMessageAlert(this)
                }

            }
        }
        binding.buttonForgotPwd.setOnClickListener {
            var intent=  Intent(this, PhonePayPaymentGatewayActivity::class.java)

//            var intent=  Intent(this, ForgotPasswordActivity::class.java)
            intent.putExtra(getString(R.string.login_from),getString(R.string.agent))
            startActivity(intent)
            AppConstants.slideToRightAnim(this)
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
        viewModel.loginRequest().observe(this){
            AppConstants.cancelSunsetDialog()

            println("loginRequestData:: $it")
            when(it.status){
                Status.SUCCESS ->{

                    if(it.data!!.code==0) {
                        val prefs = FasTagSharedPreference.customPreference(this, CUSTOM_PREF_NAME)
                        println("loginRequestData parentId:: " + it.data?.reponseData?.parentId)

                        prefs.USER_USERNAME = it.data?.reponseData?.userName
                        prefs.USER_firstName = it.data?.reponseData?.firstName
                        prefs.USER_phoneNumber = it.data?.reponseData?.phoneNumber
                        prefs.USER_parentId = it.data?.reponseData?.parentId
                        prefs.USER_email = it.data?.reponseData?.email
                        prefs.USER_lastName = it.data?.reponseData?.lastName
                        prefs.USER_roleId = it.data?.reponseData?.roleId
                        prefs.USER_roleName = it.data?.reponseData?.roleName
                        prefs.USER_agentID = it.data?.reponseData?.agentId

                        if(it.data!!.message == "User logged in successfully.") {
                            var intent = Intent(this, AgentHomeActivity::class.java)
                            var agentType = getString(R.string.agent)
                            if (it.data?.reponseData?.roleId!! == "1") {
                                agentType = getString(R.string.super_agent)
                            } else if (it.data?.reponseData?.roleId!! == "2") {
                                agentType = getString(R.string.team_lead)

                            } else {
                                agentType = getString(R.string.agent)

                            }
                            intent.putExtra(getString(R.string.login_from), agentType)
                            AppConstants.loginFrom=agentType
                            startActivity(intent)

                            AppConstants.slideToRightAnim(this)
                        }
                    }else if(it.data!!.code==1){
                        showResponseMessageAlert(
                            this,
                            it.data!!.message!!
                        )
                    }else{
                        showResponseMessageAlert(
                            this,
                            "Invalid user name Or password."
                        )
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

      fun login(){
         AppConstants.launchSunsetDialog(this)
         var requestData=LoginRequestJson(userNameVal,passwordVal)
         val loginRequestJsonData = Gson().toJson(requestData)
         val jsonObject = JSONObject(loginRequestJsonData)
         val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
         viewModel.agentLogin(request)
        /*var intent=  Intent(this, AgentHomeActivity::class.java)
        intent.putExtra(getString(R.string.login_from),getString(R.string.agent))
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        );*/
    }
    fun showResponseMessageAlert(mContext: Activity, message: String) {
        mContext.runOnUiThread {
//            val defaultPrefs = FasTagSharedPreference.customPreference(this, CUSTOM_PREF_NAME)
//            println("USER NAME:: "+defaultPrefs.USER_USERNAME+" USER_parentId::"+defaultPrefs.USER_parentId+" USER_agentID:: "+defaultPrefs.USER_agentID)
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(message)
            builder.setTitle(mContext.getString(R.string.app_name))
            builder.setCancelable(false)
            builder.setPositiveButton(
                Html.fromHtml("<font color=" + mContext.resources.getColor(R.color.colorAccent) + ">OK</font>"),
                DialogInterface.OnClickListener {
                        dialog: DialogInterface, which: Int -> dialog.dismiss()

                    }
            )
            /* builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });*/
            val alertDialog = builder.create()
            // Show the Alert Dialog box
            alertDialog.show()
        }
    }
    public fun superAgent(view:View){
        var intent=  Intent(this, AgentHomeActivity::class.java)
        intent.putExtra(getString(R.string.login_from),getString(R.string.super_agent))
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        );
    }
  /*  public fun teamLead(view:View){
      var intent=  Intent(this, AgentHomeActivity::class.java)
        intent.putExtra(getString(R.string.login_from),getString(R.string.team_lead))
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        );
    }
*/
    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun decrypt(cipherText: ByteArray?, key: ByteArray?, initialVector: ByteArray?): ByteArray? {
        var cipherText = cipherText
        val cipher: Cipher = Cipher.getInstance(cipherTransformation)
        val secretKeySpecy = SecretKeySpec(key, aesEncryptionAlgorithm)
        val ivParameterSpec = IvParameterSpec(initialVector)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec)
        cipherText = cipher.doFinal(cipherText)
        return cipherText
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encrypt(plainText: ByteArray?, key: ByteArray?, initialVector: ByteArray?): ByteArray? {
        var plainText = plainText
        val cipher: Cipher = Cipher.getInstance(cipherTransformation)
        val secretKeySpec = SecretKeySpec(key, aesEncryptionAlgorithm)
        val ivParameterSpec = IvParameterSpec(initialVector)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        plainText = cipher.doFinal(plainText)
        return plainText
    }
    @Throws(UnsupportedEncodingException::class)
    private fun getKeyBytes(key: String): ByteArray {
        val keyBytes = ByteArray(16)
        val parameterKeyBytes = key.toByteArray(charset(characterEncoding))
        System.arraycopy(
            parameterKeyBytes,
            0,
            keyBytes,
            0,
            Math.min(parameterKeyBytes.size, keyBytes.size)
        )
        return keyBytes
    }

    /// <summary>
    /// Encrypts plaintext using AES 128bit key and a Chain Block Cipher and returns a base64 encoded string
    /// </summary>
    /// <param name="plainText">Plain text to encrypt</param>
    /// <param name="key">Secret key</param>
    /// <returns>Base64 encoded string</returns>
    @Throws(
        UnsupportedEncodingException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encrypt(plainText: String, key: String): String? {
        val plainTextbytes = plainText.toByteArray(charset(characterEncoding))
        val keyBytes = getKeyBytes(key)
        return Base64.encodeToString(encrypt(plainTextbytes, keyBytes, keyBytes), Base64.DEFAULT)
    }

    @Throws(
        KeyException::class,
        GeneralSecurityException::class,
        GeneralSecurityException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        IOException::class
    )
    fun decrypt(encryptedText: String?, key: String?): String? {
        val cipheredBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val keyBytes = getKeyBytes(key!!)
        return String(decrypt(cipheredBytes, keyBytes, keyBytes)!!, Charset.forName(characterEncoding)
        )
    }
    private fun getInstalledUPIApps(): ArrayList<String>? {
        val upiList: ArrayList<String> = ArrayList()
        val uri: Uri = Uri.parse(String.format("%s://%s", "upi", "pay"))
        val upiUriIntent = Intent()
        upiUriIntent.data = uri
        val packageManager = application.packageManager
        val resolveInfoList =
            packageManager.queryIntentActivities(upiUriIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resolveInfoList) {
            upiList.add(resolveInfo.activityInfo.packageName)
        }
        return upiList
    }
}