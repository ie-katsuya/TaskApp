package com.example.taskapp

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView


class CustumAdapter: BaseAdapter(){
    var dataList: List<String> = emptyList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: View.inflate(parent.context, android.R.layout.simple_spinner_dropdown_item, null)

        (view as CheckedTextView).text = dataList[position]

        return view
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return  position.toLong()
    }

    override fun getCount(): Int {
        return dataList.size
    }
}