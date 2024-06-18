package com.agent.fasttag.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.agent.fasttag.CreateAgentActivity
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityAgentHomeBinding
import com.agent.fasttag.encript.TestEncryptionNew
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.model.CsvJsonObj
import com.agent.fasttag.view.model.GetTagListReqJson
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.FasTagSharedPreference
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_USERNAME
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_agentID
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_roleName
import com.agent.fasttag.view.util.FasTagSharedPreference.clear
import com.agent.fasttag.view.util.Status
import com.agent.fasttag.view.viewmodel.FasTagRepository
import com.agent.fasttag.view.viewmodel.FasTagViewModelFactory
import com.agent.fasttag.view.viewmodel.FastTagViewModel
import com.google.gson.Gson
import org.json.JSONArray
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*


class AgentHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgentHomeBinding
     var loginFrom =""
    val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
     val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
     val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="
    val encriptSt="durga"
    private val PICKFILE_REQUEST_CODE = 100
    lateinit var viewModel: FastTagViewModel
    var retrofitService: RetrofitService? =null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setupViewModel()
        setUpObserver()
        setDrawerMenuClickListener()

    }
    private fun setupViewModel() {
        retrofitService = RetrofitService.getInstance(AppConstants.SSLTestBaseUrl)
        var repository = FasTagRepository(retrofitService!!)
        viewModel = ViewModelProvider(
            this,
            FasTagViewModelFactory(repository)
        )[FastTagViewModel::class.java]
    }


    private fun setUpObserver(){
        viewModel.uploadTagsequestData().observe(this){
            AppConstants.cancelSunsetDialog()

            println("uploadTagsequestData:: $it")
            when(it.status){
                Status.SUCCESS ->{

                if(it.data?.message=="Success" || it.data?.message=="Updated"){
                    AppConstants.showMessageAlert(this,"Tags uploaded successfully")
                }else{
                    AppConstants.showMessageAlert(this,"Something went wrong. Please try again..")

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
    fun getRawUri(filename: String): Uri? {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator + packageName + "/raw/" + filename)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView(){
//        binding.headerLayout.tvToolbarHederTitle.visibility=View.VISIBLE
//        binding.headerLayout.tvToolbarTitle.visibility=View.GONE
//        binding.headerLayout.ivToolBarBack.visibility=View.VISIBLE
//        binding.headerLayout.tvToolbarHederTitle.text=getString(R.string.vehicle_details)
//        binding.drawerLayout.closeDrawers()
        var body= "aeOa/hedibdCnUQpyopkTzHnCu2uDclmgZV2NVu4eEbgyZT6feF+IaD2mHqEm3OMuCt2H8v0DWs8GEpQI5sMLdc+H4GQa9GuUjF0HlR+q2HOM1Cyh23y7ytFSA539r9LDL0FjmjyyY77dirGu32Mhw3VlcGohZXXYVlqhtWzKQhXmVu0DWWoJuHES5t7KSTCqwQVw/hsTOrEbM5Nlj9MHcRO3uCCKuv4CVgrARpZRkfjbgf/oKXz80xlB8i2kiI2sNsQqMjXiCHkc9ki9Vq3UGKioDQNti9tZUWfYUsJB0cSjNdJylXEviCe3oMvUeSsW59K0GALj9/hpkp4pCD5JA7QaIdP3Ub8wTpKUA+LocXlXcMTO/6yjmKqOnluFORrnSMCFdi62LnTGsyGGfW/AP+mZlkVniWM90PN9OwoAH4oPNK5Mjc3xk7LWCreN0wrMsFCMHcimycsysA95+g8YehkMXb1OlaqsbJfi23Ls8+sguV9crjk9m+UgjbuxLco"
        var key ="AFikoNDmt3HP0taGDb18DKI++LPugciAMHkIMmLH0cMz1ON2tzM2u8LAxkC1Su+fmC0ZTqSGxXVfO74GhbIc0uzUuDoeINlB8NljJLDRZjTMFo4os1PLI6JOYnSvvudeXWut6250ZpxzUH6OQZeCgbG5U8GR9hRcWADR4uVsJjs="
        var refNo="4499290701430784"
        var hash= "DX/a4TAvlQUCqmC40LqgXKe8adaWUeDOrD587Z3Wo3Miamnk1Ky3cMlLBtYjWU4XKsEKsyumPAXWgnoU9nr1yzE006g+xP1XHIZGTu3nv7k1Ko2qujoKZCGewZ9Kfs5Q8JwgAmAoZTBS5lka0VyDE5O15pVFKPyhuZ6R7yHhKGHdITAVWeMqDw2QhFflDoWIPHnDW9TXL2QCOm9pjPXoqgV/F7vcUJsMdRoRTfG4oboZDRYaYc137c4X1OqmRA8EDjTK0xvlsxtKA1XzncYXGLSqhM9g9al3Li8F7EgmPN4kZvL9iHX+eh4EHxzQ/xexBGgrSFL7+zTzfZqWMB19IA=="
        var responseData=  TestEncryptionNew(this).decryptMessage(body,key,hash,refNo)
        println("responseData:: $responseData")

/*
       var privateKey = TestEncryptionNew(this).readPrivateKeyFromFile("")

        println("privateKey:: "+privateKey)
        var unlockData = GetTagListReqJson("TN19JY5063")
        val jsonData = Gson().toJson(unlockData)
//                    val enc = Encryption(this)
//                    val requestData = "{\"entityId\": \"s3QhsIHWErcmnzkruALBtqeAjAu1\"}"

            val enc = TestEncryptionNew(this)
            val random = Random()
            val n = (1000000000000000L + random.nextFloat() * 9000000000000000L).toLong()
            println("Random:: "+n)
            var encriptData=enc.encodeRequest(jsonData, ""+n, "LQFLEET101")
//                        var splitArr=encriptData.split(":")
            var replacedVal= encriptData.replace("'", "\"")
            println("FileInputStream encriptData:: "+replacedVal)
//                        var generateRequestData=GenerateRequestData(splitArr[0],splitArr[1],splitArr[2],splitArr[3],splitArr[4])
//                        val jsonData = Gson().toJson(generateRequestData)
            println("FileInputStream encriptData:: "+jsonData)*/
      /*  val requestData = "{\"entityId\": \"s3QhsIHWErcmnzkruALBtqeAjAu1\"}"
        try {

//            Uri uri = Uri.parse("android.resource://"+mContext.getPackageName()+"/"+ R.raw.m2psolutions_pub);
            *//*val uri =
                Uri.parse("android.resource://" + getPackageName() + "/raw/" + "m2psolutions_pub")

            val filePublicKey: File = File(uri.toString())*//*
            val url =getRawUri("m2psolutions_pub.cer")
//                Uri.parse("android.resource://" + packageName + "/" + com.agent.fasttag.R.raw.m2psolutions_pub)
            val file = File(url.toString())
            val f: File = File(this.cacheDir + "/filename")

            println("getAbsoluteFile:: " + file+ "  filePublicKey::" + url)
           var reader = BufferedReader(FileReader(file))
            println("reader:: $reader")
//            val enc = TestEncryption(this)

//            var encriptData=enc.encodeRequest(requestData, "1234123412341238", "FINOWERIZE")

//            println("encriptData:: "+encriptData)

        } catch (e: Exception) {
            e.printStackTrace()
        }*/
      /*  val enc = Encryption(this)
        try {

           var keyPair:KeyPair = enc.generateKeyPair()
            println("encriptedPubilcKey  keyPair " +
                    ":: "+keyPair)

            var encriptedPubilcKey=enc.encrypt("DurgaPrasad",keyPair.public)

            println("encriptedPubilcKey:: "+encriptedPubilcKey)

        }catch (e:Exception){
            e.printStackTrace()
        }*/

      /*  var encriptString=encrypt("Mamoluga undadu Mari..")
        println("encriptString::: "+encriptString)
        var decryptString=decript(encriptString!!)
        println("decriptString::: "+decryptString)
        var bundle :Bundle ?=intent.extras*/
        loginFrom = AppConstants.loginFrom
        println("LOGIN FROM $loginFrom")
        if(loginFrom==getString(R.string.agent)){
            binding.homeLayout.llFatRecharge.gravity=Gravity.CENTER
            binding.homeLayout.llCreateAgents.visibility=View.GONE
            binding.leftDrawerMenu.clAgents.visibility=View.GONE
            binding.leftDrawerMenu.jobNumberLine1.visibility=View.GONE
            binding.leftDrawerMenu.clCsv.visibility=View.VISIBLE
            binding.leftDrawerMenu.uploadCsvLine.visibility=View.VISIBLE

        }else if(loginFrom==getString(R.string.team_lead)){
            binding.homeLayout.llFatRecharge.gravity=Gravity.CENTER
            binding.homeLayout.llCreateAgents.visibility=View.GONE
            binding.homeLayout.createAgentByLead.visibility=View.VISIBLE
            binding.leftDrawerMenu.clCsv.visibility=View.VISIBLE
            binding.leftDrawerMenu.uploadCsvLine.visibility=View.VISIBLE
        }else{
            binding.homeLayout.llCreateAgents.visibility=View.VISIBLE
            binding.leftDrawerMenu.clCsv.visibility=View.VISIBLE
            binding.leftDrawerMenu.uploadCsvLine.visibility=View.VISIBLE

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
        binding.leftDrawerMenu.ivNavClose.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)

        }
        binding.leftDrawerMenu.clCsv.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)

//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "*/*"
//            startActivityForResult(intent, PICKFILE_REQUEST_CODE)

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*";
            startActivityForResult(Intent.createChooser(intent, "Open CSV"), PICKFILE_REQUEST_CODE)

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
    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.getContentResolver().query(contentUri!!, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
            cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }
    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val list = ArrayList<CsvJsonObj?>()

        if(data!=null) {
            AppConstants.launchSunsetDialog(this)

            var imageUri: Uri = data!!.data!!
            var filePath1: File = File(imageUri.path)
            var filePath2 = AppConstants.createTmpFileFromUri(this, imageUri, filePath1.name)
            var fullPath=filePath2!!.path
            println("filePath2:: $fullPath")
            processLineByLine(File(fullPath)) { row ->
//                println(row)
                println("kitno: ${row["KIT_NUMBER"]} ${row["SERIAL_NUMBER"]} ${row["TAG_ID"]} ${row["PROFILE_ID"]}")
                val csvObject=CsvJsonObj(row["KIT_NUMBER"],row["SERIAL_NUMBER"],row["TAG_ID"],row["PROFILE_ID"])
                list.add(csvObject)
                println("list of:: "+list)

                /* println("c: ${row["B"]}")
                     println("serial: ${row["C"]}")
                     println("serial: ${row["D"]}")*/


            }
//            var csvjsonArry = JSONArray(list)
//            val jsonElements = (JsonArray) new Gson().toJsonTree(list)
            val serialized = Gson().toJson(list)
            val jsonArray = JSONArray(serialized)
            viewModel.uploadTagsReq(jsonArray)
            println("csvjsonArry123:: "+jsonArray)

        }
        else{
            Toast.makeText(this,"Please try again..",Toast.LENGTH_SHORT).show()
        }

//        val FilePath = data.data!!.path




    }

    fun processLineByLine(csv: File, processor: (Map<String, String>) -> Unit)  {
        val BOM = "\uFEFF"
        val header = csv.useLines { it.firstOrNull()?.replace(BOM, "")?.split(",") }
            ?: throw Exception("This file does not contain a valid header")

        csv.useLines { linesSequence ->
            linesSequence
                .drop(1)
                .map {
//                    println("MAP::"+it)
                    it.split(",") }
                .map { header.zip(it).toMap() }
                .forEach(processor)
        }
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
        var intent=Intent(this, CreateAgentActivity::class.java)
//         var intent=Intent(this, PhonePayPaymentGatewayActivity::class.java)
//         intent.putExtra(getString(R.string.to_payment_gateway),getString(R.string.documents_details))
         intent.putExtra(getString(R.string.role_from),getString(R.string.agent))
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

