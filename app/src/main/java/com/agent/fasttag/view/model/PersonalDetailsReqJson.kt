    package com.agent.fasttag.view.model

    data class PersonalDetailsReqJson(
        val dateInfo: List<DateInfo>,
        val communicationInfo: List<CommunicationInfo>,
        val addressInfo: List<AddressInfo>,
        val kycInfo: List<KycInfo>,
        val kitInfo: List<KitInfo>,
        val kycDocuments: List<KycDocuments>,
        val customerStatus:String="Individual",
        val countryCode:String="91",
        val channelName:String="MIN_KYC",
        val kycStatus:String="MIN_KYC",
        val fatcaDecl:String="12",
        val consent:String="Y",
        val politicallyExposed:String="N",
        val entityType:String="CUSTOMER",
        val businessType:String="LQFLEET101",
        val business:String="LQFLEET101",
        val businessId:String="LQAPPL21",
        val specialDate:String="",
        val branch:String="LQFLEET101",
        val corporate:String="LQFLEET101",
        val entityId:String="LQAPPL21",
        val countryofIssue:String="IND",
        val dependent:Boolean=false,
        val fleetAddField:FleetAddField,
        val otp:String="",
        val contactNo:String="",
        val city:String="",
        val state:String="",
        val pincode:String="",
        val address2:String="",
        val address:String="",
        val dap:String="",
        val idNumber:String="",
        val proofType:String="",
        val dob:String="",
        val gender:String="",
        val lastName:String="",
        val firstName:String="",
        val emailAddress:String="",
        val country:String="India",
        val reqBranch:String="",
        val preference:PreferenceData,
        val title:String="M/s",
    )
    data class DateInfo(
        val date: String,
        val dateType: String="DOB",
    )
    data class CommunicationInfo(
        val emailId: String="",
        val notification: Boolean=true,
        val contactNo: String,
    )
    data class AddressInfo(
        val pincode: String="",
        val country: String="INDIA",
        val state: String="",
        val city: String="",
        val address3: String="",
        val address2: String="",
        val address1: String="",
        val addressCategory: String="PERMANENT",
    )
    data class KycInfo(
        val documentNo: String="",
        val documentType: String="",
        val kycRefNo: String="",
    )
    data class KitInfo(
        val cardType: String="VIRTUAL",
        val cardCategory: String="PREPAID",
        val cardRegStatus: String="ACTIVE",
        val aliasName: String="",
    )
    data class KycDocuments(
        val documentType: String="",
        val documentFileName: String="",
    )
    data class FleetAddField(
        val applicationDate: String="",
        val applicationNumber: String="",
    )
    data class PreferenceData(
        val address: List<Address>
    )
    data class Address(
        val country: String="India",
        val city: String="",
        val address2: String="",
        val address1: String="",
    )