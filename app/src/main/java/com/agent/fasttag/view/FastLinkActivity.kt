package com.agent.fasttag.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityFastLinkBinding
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException

class FastLinkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFastLinkBinding
    private var qrScanIntegrator: IntentIntegrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFastLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }
    private fun initView(){
        binding.headerLayout.tvToolbarHederTitle.visibility= View.GONE
        binding.headerLayout.tvToolbarTitle.visibility= View.VISIBLE
        binding.headerLayout.ivToolBarBack.visibility= View.VISIBLE
        binding.headerLayout.tvToolbarHederTitle.text=getString(R.string.personal_details)
        setupScanner()
    }
    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(this)
        qrScanIntegrator?.setOrientationLocked(true)

    }
    public fun submit(view:View){
        startActivity(Intent(this, SuccessActivity::class.java))

        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        );
    }

    public fun scanBarCode(view:View){
        println("scanBarCode:: ")
         performAction()
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
    private fun performAction() {
        // Code to perform action when button is clicked.
        qrScanIntegrator?.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            // If QRCode has no data.
            if (result.contents == null) {
                Toast.makeText(this, getString(R.string.result_not_found), Toast.LENGTH_LONG).show()
            } else {
                // If QRCode contains data.
                try {
                    // Converting the data to json format
                    println("Scan data:${result.contents}")

                    Toast.makeText(this,result.contents,Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    e.printStackTrace()

                    // Data not in the expected format. So, whole object as toast message.
                    Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}