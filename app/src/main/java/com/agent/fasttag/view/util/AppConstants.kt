package com.agent.fasttag.view.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.text.Html
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.util.Patterns
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.agent.fasttag.R
import com.agent.fasttag.view.model.PersonalDetailsData
import com.marwaeltayeb.progressdialog.ProgressDialog
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*


object AppConstants {

    val uploadKyc="uploadKyc"
//    var vehicleRegistration="v3/register"
    var vehicleRegistration="registration-manager/v3/register"
    const val DATE_FORMAT = "dd/MM/yyyy"
    const val SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

    var UnregisteredNegativeList="fleet-manager/UnregisteredNegativeList"
    var getTagList= "business-entity-manager/getTagList"
    var tagClosure= "fleet-manager/negativeList"
    var replaceTag= "business-entity-manager/replaceTag"
    val agentRollId="3"
    val teamLeadRollId="2"
    val superAgentRollId="1"
    var parentId=""
    var vehicleNumberVal=""
    var baseURL="https://kycuat.yappay.in/kyc/"
    var vehicleRegistrationBaseUrl="https://uat-fleetdrive.m2pfintech.com/core/Yappay/registration-manager/"
    var KitBaseUrl= "https://sit-secure.yappay.in/Yappay/"
    var LoginBaseUrl= "https://loginmanagement2023.azurewebsites.net/api/"
    var AgentLogin="User/agent/login"
    var AgentforgotPassword="User/agent/forgotPassword"
    var AgentResetPassword="User/agent/resetPassword"
    var paymentGatewayBaseUrl= "https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/pay"
    var paymentLoadWalletBaseUrl= "https://uat-fleetdrive.m2pfintech.com/core/Yappay/txn-manager/create"

    var paymentGatewayCheckStatusBaseUrl= "https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/status"
    var GetTransactionId="transaction/saveTransaction"
    var GetTransactionStatus="transaction/getTransactionStatus"
    var GetTransactionByAgent="transaction/getTransactionByAgent"

    var SaveOrEditAgent="agent/saveOrEditAgent"
    var GetAllRolls="role/getAllRoles"
    val GetAllTeamLeads="agent/getAllTeamLeades"
    val saveCustomeDetails="customer/saveOrEditCustomer"
    val getCustomerByMb="customer/getCustomerByMb"
    val getAgentsByID="agent/getAgentById"
    val deleteCustomeDetails="customer/deleteCustomer"
    val getOtp="SMSApi/SendOtp"

    var LoginAuthorization="Basic QWJjZGVmZ2hpams6eHl6QDEyMyM="
    var loginFrom=""
    val customerStatus:String="Individual"
    val countryCode:String="91"
    val channelName:String="MIN_KYC"
    val fatcaDecl:String="12"
    val consent:String="Y"
    val politicallyExposed:String="N"
    var entityType:String="CUSTOMER"
    val businessType:String="LQFLEET101"
    val businessType_vehicle:String="LQFLEET101_VC"
    val tenant_M2P = "M2P"
    val color:String="#609"
    val kycStatus:String= "MIN_KYC"
    var entityId:String="LQAPPL21"
    val RC_NUMBER:String="RC_NUMBER"
    val specialDate:String=""
    val countryofIssue:String="IND"
    val dependent:Boolean=false
    val country:String="India"
    val title:String="M/s"
    val dob: String="DOB"
    val addressCategory: String="PERMANENT"
    val cardType: String="VIRTUAL"
    val cardCategory: String="PREPAID"
    val cardRegStatus: String="ACTIVE"
    var progressDialog:ProgressDialog?=null
    var phoneNumber: String=""
    val tenant:String="LQFLEET"
    val YESFLEET:String="YESFLEET"
    var retrofit: Retrofit? =null
    val authorization:String="Basic TFFGTEVFVA=="

    val partnerId:String="LQFLEET"
    val partnerToken:String="Basic TFFGTEVFVA"
    val vehicleToken:String="Basic YWRtaW46YWRtaW4="
    var registerFromCode:Int=0

    val customerType:String="Individual Customer"
     var personalDetail:PersonalDetailsData?=null
    fun launchSunsetDialog(context:Context) {
        val textView = TextView(context)
//        textView.text = resources.getString(R.string.app_name)
//        textView.setPadding(20, 30, 20, 30)
//        textView.textSize = 20f
//        textView.setBackgroundColor(resources.getColor(R.color.colorAccent))
        textView.setTextColor(Color.WHITE)
         progressDialog = ProgressDialog(context)
            .setDialogPadding(20)
            .setCustomTitle(textView)
            .setTextSize(18f)
            .setProgressBarPadding(20)
             .setCancelable(false)
//            .setProgressBarShape(ContextCompat.getDrawable(context, R.drawable.animated_images))
            .setText("Please wait...")
        if(progressDialog?.isShowing == false) {
            progressDialog?.show()
        }
    }
    fun cancelSunsetDialog(){
        progressDialog?.dismiss()
    }


    fun showMessageAlert(mContext: Activity, message: String?) {
        mContext.runOnUiThread {
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(message)
            builder.setTitle(mContext.getString(R.string.app_name))
            builder.setCancelable(false)
            builder.setPositiveButton(Html.fromHtml("<font color=" + mContext.resources.getColor(R.color.colorAccent) + ">OK</font>"),
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.dismiss() })
            /* builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });*/
            val alertDialog = builder.create()
            // Show the Alert Dialog box
            alertDialog.show()
        }
    }
    fun showNoInternetConnectionMessageAlert(mContext: Activity) {
        mContext.runOnUiThread {
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(mContext.getString(R.string.no_internet_connection))
            builder.setTitle(mContext.getString(R.string.app_name))
            builder.setCancelable(false)
            builder.setPositiveButton(Html.fromHtml("<font color=" + mContext.resources.getColor(R.color.colorAccent) + ">OK</font>"),
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.dismiss() })
            /* builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });*/
            val alertDialog = builder.create()
            // Show the Alert Dialog box
            alertDialog.show()
        }
    }
    fun showMessageAlert(mContext: Activity) {
        mContext.runOnUiThread {
            val builder = AlertDialog.Builder(mContext)
            val title = SpannableString(mContext.getString(R.string.app_name))
            title.setSpan(
                AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0,
                title.length,
                0
            )
            val message = SpannableString("Do you want to change phase?")
            message.setSpan(
                AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0,
                title.length,
                0
            )
            builder.setMessage(message)
            builder.setTitle(title)
            builder.setCancelable(false)
            builder.setPositiveButton(Html.fromHtml("<font color=" + mContext.resources.getColor(R.color.colorAccent) + ">Yes</font>"),
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->

                })
            builder.setNegativeButton("No",
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.cancel() })
            val alertDialog = builder.create()
            // Show the Alert Dialog box
            alertDialog.show()
            val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 10f
            btnPositive.layoutParams = layoutParams
            btnNegative.layoutParams = layoutParams
        }
    }
    fun isValidPhoneNumber(target: CharSequence): Boolean {
        return if (target.length != 10) {
            false
        } else {
            Patterns.PHONE.matcher(target).matches()
        }
    }
    fun slideToRightAnim(context: Activity){
        context.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }
    fun slideToLeftAnim(context: Activity){
        context.overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        );
    }
     fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
    fun getShortDate(ts:String?):String{
        if(ts == null) return ""
        //Get instance of calendar
        val calendar = Calendar.getInstance(Locale.getDefault())
        //get current date from ts
        calendar.timeInMillis = ts.toLong()
        //return formatted date
        return android.text.format.DateFormat.format("E, dd MMM yyyy", calendar).toString()
    }
     @RequiresApi(Build.VERSION_CODES.O)
     fun getDateTime(s: String): String? {
//            val sdf = SimpleDateFormat("MM/dd/yyyy")
//            val netDate = Date(s.toLong() * 1000)
//            return sdf.format(netDate)
         val formatter: DateTimeFormatter = DateTimeFormatterBuilder()
             .optionalStart()
             .append(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
             .optionalEnd()
             .optionalStart()
             .append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
             .optionalEnd().toFormatter()
         val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSX")
         val result: LocalDateTime = LocalDateTime.parse(s, formatter)


         return result.toString()

     }
    fun String.convertDateFormat(dateFormat : String = DATE_FORMAT, actualFormat : String = SERVER_DATE_FORMAT): String{
        val sdf = SimpleDateFormat(actualFormat, Locale.getDefault())
        val convertSdf = SimpleDateFormat(dateFormat, Locale.getDefault())
//    sdf.timeZone = TimeZone.getTimeZone("UTC")
        val dateTime = sdf.parse(this)
        val cal  = Calendar.getInstance()
        cal.time = dateTime
        /* if (dateFormat == DOCUMENTS_FORMAT)
             cal.add(Calendar.HOUR, -11)*/
        return convertSdf.format(cal.time)
    }
}