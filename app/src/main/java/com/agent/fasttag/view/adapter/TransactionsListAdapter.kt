package com.agent.fasttag.view.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agent.fasttag.R
import com.agent.fasttag.databinding.LayoutTrasactionsViewAdapterBinding
import com.agent.fasttag.view.model.GetTransactionByIdRes
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.AppConstants.convertDateFormat

class TransactionsListAdapter(private val dataSet: List<GetTransactionByIdRes?>, val callBack: (GetTransactionByIdRes) -> Unit) :
    RecyclerView.Adapter<TransactionsListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutTrasactionsViewAdapterBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dataSet: GetTransactionByIdRes) {
            val context = binding.root.context
            binding.tvTransactionId.text = "Transaction Id : " + dataSet.transactionId
            binding.tvVehicleNumber.text = "Vehicle Number : " + dataSet.vehicleNumber
            binding.tvMobileNumber.text = "Phone Number : " + dataSet.userMobile
            binding.tvAmount.text = "Amount : " + dataSet.amount
            binding.tvStatus.text = dataSet.status
            binding.tvDate.text =
                dataSet.createdOn.convertDateFormat("dd MMM, yyyy, hh:mma")
            if (dataSet.status == "COMPLETED") {
                binding.tvRetry.visibility = View.GONE
                binding.tvStatus.setTextColor(Color.parseColor("#FF009688"))
            } else if(dataSet.status.equals("initiated",ignoreCase = true)){
                binding.tvStatus.setTextColor(Color.parseColor("#FFFF5722"))
                binding.tvRetry.visibility = View.VISIBLE
                binding.tvRetry.text = "Retry"
            }
            else {
                binding.tvStatus.setTextColor(Color.parseColor("#FFFF5722"))
                binding.tvRetry.visibility = View.VISIBLE

            }
            binding.tvRetry.setOnClickListener {
                callBack.invoke(dataSet)
            }
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = LayoutTrasactionsViewAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position]!!)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}