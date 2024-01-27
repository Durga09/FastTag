package com.agent.fasttag.view

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agent.fasttag.databinding.ActivityAgentSignupBinding
import com.agent.fasttag.view.util.AppConstants

class AgentSignupActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAgentSignupBinding
    private var firstNameVal:String=""
    private var lastNameVal:String=""
    private var phoneNumberVal:String=""
    private var passwordVal:String=""
    private var confirmPasswordVal:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_agent_signup)
        binding = ActivityAgentSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }
    fun initView(){

        binding.signup.setOnClickListener {

            firstNameVal = binding.editFirstNameInput.text.toString().trim()
            lastNameVal  = binding.editLastNameInput.text.toString().trim()
            phoneNumberVal = binding.editPhoneNumberInput.text.toString().trim()
            passwordVal = binding.editPasswordInput.text.toString().trim()
            confirmPasswordVal = binding.editConfirmPasswordInput.text.toString().trim()

            if(firstNameVal == ""){

                Toast.makeText(this,"Please enter first name",Toast.LENGTH_SHORT).show()
            }
            else if(lastNameVal == ""){

                Toast.makeText(this,"Please enter last name",Toast.LENGTH_SHORT).show()
            }
            else if(phoneNumberVal == ""){

                Toast.makeText(this,"Please enter phone number",Toast.LENGTH_SHORT).show()
            }
            else if(!AppConstants.isValidPhoneNumber(phoneNumberVal)){
                Toast.makeText(this,"Please enter valid phone number",Toast.LENGTH_SHORT).show()
            }
            else if(passwordVal == ""){

                Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show()
            }
            else if(confirmPasswordVal == ""){

                Toast.makeText(this,"Please enter confirm password",Toast.LENGTH_SHORT).show()
            }
            else if(passwordVal != confirmPasswordVal){

                Toast.makeText(this,"Password and Confirm password should same",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Done!",Toast.LENGTH_SHORT).show()

            }
        }

    }

}