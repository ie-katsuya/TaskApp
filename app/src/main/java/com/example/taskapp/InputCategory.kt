package com.example.taskapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_category_id.*

class InputCategory : AppCompatActivity(), View.OnClickListener {

    private var mCategory: Category? = null
    private var categoryname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_id)

        // UI部品の設定
        refine_button.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (category_edit.text.toString().equals("") == false) {
            Toast.makeText(applicationContext, "カテゴリーを追加しました", Toast.LENGTH_SHORT).show()
            categoryname = category_edit.text.toString()
            addTask()
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }
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

        mCategory!!.name = categoryname

        realm.copyToRealmOrUpdate(mCategory!!)
        realm.commitTransaction()

        realm.close()

    }

}
