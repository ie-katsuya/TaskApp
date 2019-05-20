package com.example.taskapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import io.realm.RealmChangeListener
import io.realm.Sort
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.app.AlarmManager
import android.app.PendingIntent
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.content_input.*
import kotlinx.android.synthetic.main.content_input.view.*
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.ArrayAdapter

const val EXTRA_TASK = "com.example.taskapp.TASK"

class MainActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var mRealm: Realm

    private var spinnerItems: MutableList<String> = mutableListOf()

    private var spinnerAdapter = CustumAdapter()

    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            reloadListView()
        }
    }

    private lateinit var mTaskAdapter: TaskAdapter
    private lateinit var mCategoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }

        // Realmの設定
        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(mRealmListener)

        // ListViewの設定
        mCategoryAdapter = CategoryAdapter(this@MainActivity)

        // spinner に adapter をセット
        // Kotlin Android Extensions
        category_spinner.adapter = spinnerAdapter
        spinnerAdapter.dataList = spinnerItems

        //spinnerにカテゴリーをセット
        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        val categoryRefineResults = mRealm.where(Category::class.java).findAll()

        // 上記の結果を、TaskList としてセットする
        mCategoryAdapter.spinnerlist = mRealm.copyFromRealm(categoryRefineResults)

        category_spinner.adapter = mCategoryAdapter

        mCategoryAdapter.notifyDataSetChanged()


        category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?, position: Int, id: Long
            ) {
                val spinnerParent = parent as Spinner
                //mCategoryId = spinnerParent.selectedItem as Int
                //textView.text = item
                reloadListView()
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {
                sort()
            }
        }


        // ListViewの設定
        mTaskAdapter = TaskAdapter(this@MainActivity)

        // ListViewをタップしたときの処理
        listView1.setOnItemClickListener { parent, _, position, _ ->
            // 入力・編集する画面に遷移させる
            val task = parent.adapter.getItem(position) as Task
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            intent.putExtra(EXTRA_TASK, task.id)
            startActivity(intent)
        }

        // ListViewを長押ししたときの処理
        listView1.setOnItemLongClickListener { parent, _, position, _ ->
            // タスクを削除する
            val task = parent.adapter.getItem(position) as Task

            // ダイアログを表示する
            val builder = AlertDialog.Builder(this@MainActivity)

            builder.setTitle("削除")
            builder.setMessage(task.title + "を削除しますか")

            builder.setPositiveButton("OK") { _, _ ->
                val results = mRealm.where(Task::class.java).equalTo("id", task.id).findAll()

                mRealm.beginTransaction()
                results.deleteAllFromRealm()
                mRealm.commitTransaction()

                val resultIntent = Intent(applicationContext, TaskAlarmReceiver::class.java)
                val resultPendingIntent = PendingIntent.getBroadcast(
                    this@MainActivity,
                    task.id,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(resultPendingIntent)

                reloadListView()
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }


        category_button.setOnClickListener(this)
    }


    private fun reloadListView() {

        sort()

            val taskRefineResults =
                mRealm.where(Task::class.java).findAll()

            // 上記の結果を、TaskList としてセットする  mutableListOf()
            mTaskAdapter.taskList = mRealm.copyFromRealm(taskRefineResults)

            // TaskのListView用のアダプタに渡す
            listView1.adapter = mTaskAdapter

            // 表示を更新するために、アダプターにデータが変更されたことを知らせる
            mTaskAdapter.notifyDataSetChanged()

    }

    private fun sort() {

        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        val tasksortResults = mRealm.where(Task::class.java).findAll().sort("date", Sort.DESCENDING)

        // 上記の結果を、TaskList としてセットする
        mTaskAdapter.taskList = mRealm.copyFromRealm(tasksortResults)

        // TaskのListView用のアダプタに渡す
        listView1.adapter = mTaskAdapter

        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mTaskAdapter.notifyDataSetChanged()
    }


    override fun onClick(v: View) {
        val intent = Intent(this, InputCategory::class.java)
        startActivity(intent)

    }

    override fun onResume() {
        super.onResume()

        spinnerItems.clear()

        //spinnerにカテゴリーをセット
        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        val categoryRefineResults = mRealm.where(Category::class.java).findAll()

        // 上記の結果を、TaskList としてセットする
        mCategoryAdapter.spinnerlist = mRealm.copyFromRealm(categoryRefineResults)

        category_spinner.adapter = mCategoryAdapter

        mCategoryAdapter.notifyDataSetChanged()

        reloadListView()
    }

    override fun onDestroy() {
        super.onDestroy()

        //mRealm.close()
    }

}
