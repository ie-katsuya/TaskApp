package com.example.taskapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import io.realm.Realm
import kotlinx.android.synthetic.main.content_input.*
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import io.realm.RealmChangeListener
import kotlinx.android.synthetic.main.activity_category_id.*

abstract class InputCategory : AppCompatActivity(), View.OnClickListener {

    private var category:String = ""
    private var mCategory: Category? = null

    private lateinit var mRealm: Realm

    /*
    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_id)

        // Realmの設定
        mRealm = Realm.getDefaultInstance()
        //mRealm.addChangeListener(mRealmListener)

        // ActionBarを設定する
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        // UI部品の設定
        refine_button.setOnClickListener(this)


        // EXTRA_TASK から Task の id を取得して、 id から Task のインスタンスを取得する
        val intent = intent
        val category_text = intent.getStringExtra(EXTRA_CATEGORY)
        val realm = Realm.getDefaultInstance()
        mCategory = realm.where(Category::class.java).equalTo("category", category_text).findFirst()
        realm.close()

        addTask()
    }

    private fun addTask() {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()

        if (mCategory == null) {
            // 新規作成の場合
            mCategory = Category()

            val CategoryRealmResults = realm.where(Category::class.java).findAll()

            val identifier: Int =
                if (CategoryRealmResults.max("id") != null) {
                    CategoryRealmResults.max("id")!!.toInt() + 1
                } else {
                    0
                }
            mCategory!!.id = identifier
        }

        val cate = category_edit_text.toString()

        mCategory!!.category = cate

        realm.copyToRealmOrUpdate(mCategory!!)
        realm.commitTransaction()
    }
}
