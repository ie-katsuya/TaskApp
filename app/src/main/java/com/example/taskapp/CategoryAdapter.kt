package com.example.taskapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class CategoryAdapter(context: Context): BaseAdapter()  {
    private val mLayoutInflater: LayoutInflater
    var spinnerlist = mutableListOf<Category>()
    lateinit var mCategory: Category

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

        val view: View = convertView ?: mLayoutInflater.inflate(android.R.layout.simple_list_item_1, null)

        val textView1 = view.findViewById<TextView>(android.R.id.text1)

        textView1.text = spinnerlist[position].name

        return view
    }
}