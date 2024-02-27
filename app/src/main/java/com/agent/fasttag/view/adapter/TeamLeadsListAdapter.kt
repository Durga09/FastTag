package com.agent.fasttag.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.agent.fasttag.R
import com.agent.fasttag.databinding.LayoutTlBottomAdapeterBinding
import com.agent.fasttag.view.model.ReponseData


class TeamLeadsListAdapter(private val dataSet: ArrayList<ReponseData>,val callBack : (ReponseData) -> Unit) :
    RecyclerView.Adapter<TeamLeadsListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutTlBottomAdapeterBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dataSet: ReponseData) {
            val context = binding.root.context
            binding.name.text = "Name : " + dataSet.userName
            binding.phoneNumber.text = "Contact : " + dataSet.phoneNumber
            binding.email.text = "Email : " + dataSet.email
            binding.role.text = "Role : " + dataSet.roleName
            binding.root.setOnClickListener {
                callBack.invoke(dataSet)
            }
        }
    }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val binding = LayoutTlBottomAdapeterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(dataSet[position])
        }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}


