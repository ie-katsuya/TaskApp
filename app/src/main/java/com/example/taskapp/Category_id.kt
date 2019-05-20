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
    private var mCategory: Category? = null
    private lateinit var mCategoryAdapter: CategoryAdapter

    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_id)

        refine_button.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        // エディットテキストのテキストを取得
        if (category_edit.text.toString().equals("") == false) {
            val intent = Intent(this, CategoryAdapter::class.java)
            intent.putExtra(EXTRA_CATEGORY, category_edit.text)
            startActivity(intent)
        }

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

        realm.copyToRealmOrUpdate(mCategory!!)
        realm.commitTransaction()

        realm.close()

    }
}
