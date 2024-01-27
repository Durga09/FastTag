package com.agent.fasttag.view.adapter

import android.content.Context
import android.icu.text.CaseMap.Title
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.agent.fasttag.R

class CommanAdapter (private val context: Context, private val arrayList: ArrayList<String>) : BaseAdapter() {
    private lateinit var title:  TextView
    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.list_tieam, parent, false)
        title = convertView.findViewById(R.id.title)

        title.text = arrayList[position]
        return convertView
    }
}