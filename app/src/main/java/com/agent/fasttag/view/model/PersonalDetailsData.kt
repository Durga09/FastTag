package com.agent.fasttag.view.model

data class PersonalDetailsData(
    val isCustomerAlreadyRegister: Boolean =false,
    val pincode: String="",
    val country: String="INDIA",
    val state: String="",
    val city: String="",
    val address: String="",
    val address2: String="",
    val address1: String="",
    val emailAddress: String="",
    val contactNo: String="",
    val lastName: String="",
    val firstName: String="",
    val gender: String="",
    val vehicleClass: String="",
    val dob: String="",
    val documentType: String="",
    val documentNumber: String="",
    val customerId: String="",
    val vehicleData: ArrayList<VehicleData>?=null,

    )
