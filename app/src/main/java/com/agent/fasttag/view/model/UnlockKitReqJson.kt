package com.agent.fasttag.view.model

data class UnlockKitReqJson (val kitNo:String="",val excCode:String="",val tagOperation:String="")
data class GetTagListReqJson (val entityId:String="")
data class TagClosureReqJson (val kitNo:String="",val excCode:String="",val tagOperation:String="")
data class ReplaceTagReqJson (val entityId:String="",val oldKitNo:String="",val newKitNo:String="",val profileId:String="")