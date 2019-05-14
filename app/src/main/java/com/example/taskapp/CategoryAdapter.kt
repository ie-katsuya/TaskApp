package com.example.taskapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import android.app.PendingIntent
import android.content.Intent

class CategoryAdapter(context: Context): BaseAdapter()  {
    private val mLayoutInflater: LayoutInflater
    var spinnerlist = mutableListOf<Category>()

    init {
        this.mLayoutInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return spinnerlist.size
    }

    override fun getItem(position: Int): Any {
        return spinnerlist[position]
    }

    override fun getItemId(position: Int): Long {
        return spinnerlist[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        // EXTRA_TASK から Task の id を取得して、 id から Task のインスタンスを取得する
        val intent = intent
        val list = intent.getIntExtra(EXTRA_CATEGORY, -1)

        val view: View = convertView ?: mLayoutInflater.inflate(android.R.layout.simple_list_item_1, null)

        val textView1 = view.findViewById<TextView>(android.R.id.text1)

        textView1.text = "カテゴリー： " + spinnerlist[position].category


        return view
    }
}