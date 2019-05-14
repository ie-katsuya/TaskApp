package com.example.taskapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_category_id.*
import android.util.Log
import android.widget.SimpleAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_input.*
import io.realm.Realm
import io.realm.RealmChangeListener

const val EXTRA_CATEGORY = "com.example.taskapp.TASK"

class Category_id : AppCompatActivity(), View.OnClickListener{
    private lateinit var mRealm: Realm

    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
        }
    }

    // Map<String, String> 型の ArrayList を作成します
    val list = ArrayList<String>()
    var count = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_id)


        refine_button.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        // エディットテキストのテキストを取得
        if (category_edit.text != null) {
            val intent = Intent(this, InputCategory::class.java)
            intent.putExtra(EXTRA_CATEGORY, category_edit.text)
            startActivity(intent)
        }
    }
}
