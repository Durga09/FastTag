    package com.agent.fasttag.view.model

    data class VehicleRegReqJsonData(

        val idExpiry:String="",
        val pincode:String="",
        val country:String="",
        val state:String="",
        val city:String="",
        val address2:String="",
        val address:String="",
        val emailAddress:String="",
        val contactNo:String="",
        val lastName:String="",
        val firstName:String="",
        val specialDate:String="",
        val dependant:Boolean=true,
        val kycDocuments:List<KycDocumentsVehicle>,
        val gender:String="",
        val programType:String="",
        val kycStatus:String="",
        val business:String="",
        val parentEntityId:String="",
        val fleetAddField:FleetAddFieldVehicle,
        val businessType:String="",
        val businessId:String="",
        val documents:List<Documents>,
        val countryofIssue:String="",
        val idType:String="",
        val entityType:String="",
        val color:String="",
        val kitNo:String="",
        val profileId:String="",
        val tagId:String="",
        val entityId:String="",

        )
    data class KycDocumentsVehicle(
        val documentType:String="",
        val documentFileName:String="",
    )
    data class FleetAddFieldVehicle(
        val entityId:String="",
        val thresholdLimit:String="",
        val totalcost:String="",
        val customerType:String="",
        val applicationNumber:String="",
        val customerAcknowledgement2:Boolean=true,
        val customerAcknowledgement1:Boolean=true,
        val pymntType:String="",
        val loadAmount:String="",
        val conveFee:String="",
        val registrationDate:String="",
        val isCommercial:Boolean=false,
    )
    data class Documents(
        val docExpDate:String="",
        val docType:String="",
    )