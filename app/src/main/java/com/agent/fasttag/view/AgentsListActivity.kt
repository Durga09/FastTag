package com.agent.fasttag.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityDocumentsDetailsBinding
import com.agent.fasttag.databinding.ActivityTeamLeadesListBinding
import com.agent.fasttag.view.adapter.TeamLeadsListAdapter
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.model.CreateAgentRequestJson
import com.agent.fasttag.view.model.GetAgentsByIdRequestJson
import com.agent.fasttag.view.model.ReponseData
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.FasTagSharedPreference
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_agentID
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_parentId
import com.agent.fasttag.view.util.Status
import com.agent.fasttag.view.viewmodel.FasTagRepository
import com.agent.fasttag.view.viewmodel.FasTagViewModelFactory
import com.agent.fasttag.view.viewmodel.FastTagViewModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class AgentsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeamLeadesListBinding
    lateinit var viewModel: FastTagViewModel
    var retrofitService: RetrofitService? =null
    lateinit var teamLeadsListDataList:ArrayList<ReponseData>
    var teamLeadsListAdapter: TeamLeadsListAdapter? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamLeadesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setupViewModel()
        setUpObserver()
    }
    private fun initView(){
        binding.headerLayout.tvToolbarHederTitle.visibility= View.VISIBLE
        binding.headerLayout.tvToolbarTitle.visibility= View.GONE
        binding.headerLayout.tvToolbarSubHeaderTitle.visibility= View.GONE

        binding.headerLayout.ivToolBarBack.visibility= View.VISIBLE
        binding.headerLayout.tvToolbarHederTitle.text=getString(R.string.agents)
        binding.headerLayout.ivToolBarBack.setOnClickListener {
            finish()
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
//        val fasTagPref = FasTagSharedPreference.customPreference(this, FasTagSharedPreference.CUSTOM_PREF_NAME)

        var requestData= GetAgentsByIdRequestJson("00000000-0000-0000-0000-000000000000",AppConstants.parentId)
        val agentRequestJsonData = Gson().toJson(requestData)
        val jsonObject = JSONObject(agentRequestJsonData)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
        println("GetAgentsByIdRequestJson:: "+jsonObject)
        viewModel.getAgentsByRequest(request)
    }
    private fun setUpObserver() {
        viewModel.getAllTeamLeadsRequest().observe(this) {
            AppConstants.cancelSunsetDialog()
            when (it.status) {
                Status.SUCCESS -> {
                    teamLeadsListDataList = it.data!!.reponseData
                    if(it.data!!.reponseData.size>0) {
                        println("teamLeadsDataList:: $teamLeadsListDataList")
                        teamLeadsListAdapter = TeamLeadsListAdapter(teamLeadsListDataList) {
                            println("TeamLeadsListAdapter:: Click$it")
                        }
                        binding.agentsTlList.layoutManager = LinearLayoutManager(this)

                        binding.agentsTlList.adapter = teamLeadsListAdapter
//                    showResponseMessageAlert(this,it.data!!.message)
                        binding.noDataAvailable.visibility=View.GONE
                        binding.agentsTlList.visibility=View.VISIBLE
                    }else{
                        binding.noDataAvailable.visibility=View.VISIBLE
                        binding.agentsTlList.visibility=View.GONE
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
}