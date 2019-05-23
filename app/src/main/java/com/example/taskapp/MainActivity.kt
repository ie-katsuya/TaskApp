package com.example.taskapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*

const val EXTRA_TASK = "com.example.taskapp.TASK"

class MainActivity : AppCompatActivity(){
    private lateinit var mRealm: Realm

    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            reloadListView(mCategoryId)
        }
    }

    private lateinit var mTaskAdapter: TaskAdapter
    private lateinit var mCategoryAdapter: CategoryAdapter
    private var mCategoryId = 0
    private var spinnerAdapter = CustumAdapter()
    private var spinnerItems: MutableList<String> = mutableListOf()
    private lateinit var taskRefineResults: RealmResults<Task>
    private var mCategory = Category()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }

        //Realmを設定
        setRealm()

        //カテゴリーがnullならALLを追加
        first()

        //スピナーを選択する処理
        category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?, position: Int, id: Long
            ) {
                var spinnerParent = parent as Spinner
                //item = spinnerParent.selectedItem as Category
                mCategoryId = id.toInt()
                reloadListView(mCategoryId)
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {
                mCategoryId = 0
            }
        }

        // ListViewをタップしたときの処理
        listView1.setOnItemClickListener { parent, _, position, _ ->
            // 入力・編集する画面に遷移させる
            val task = parent
                .adapter
                .getItem(position) as Task
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            intent.putExtra(EXTRA_TASK, task.id)
            startActivity(intent)
        }

        // ListViewを長押ししたときの処理
        listView1.setOnItemLongClickListener { parent, _, position, _ ->
            // タスクを削除する
            val task = parent
                .adapter
                .getItem(position) as Task

            // ダイアログを表示する
            val builder = AlertDialog.Builder(this@MainActivity)

            builder.setTitle("削除")
            builder.setMessage(task.title + "を削除しますか")

            builder.setPositiveButton("OK") { _, _ ->
                deleteTask(task.id)
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }
    }

    private fun reloadListView(select: Int) {

        if(select == 0){
            taskRefineResults =
                mRealm
                    .where(Task::class.java)
                    .findAll().sort("date", Sort.DESCENDING)
        }else {
            taskRefineResults =
                mRealm
                    .where(Task::class.java)
                    .equalTo("category.id", select)
                    .findAll().sort("date", Sort.DESCENDING)
       }

        // 上記の結果を、TaskList としてセットする  mutableListOf()
        mTaskAdapter.taskList = mRealm.copyFromRealm(taskRefineResults)

        // TaskのListView用のアダプタに渡す
        listView1.adapter = mTaskAdapter

        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mTaskAdapter.notifyDataSetChanged()

    }

    override fun onResume() {
        super.onResume()

        //spinnerにカテゴリーをセット
        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        val categoryRefineResults = mRealm.where(Category::class.java).findAll().sort("id")

        // 上記の結果を、spinnerList としてセットする
        mCategoryAdapter.spinnerlist = mRealm.copyFromRealm(categoryRefineResults)

        category_spinner.adapter = mCategoryAdapter

        mCategoryAdapter.notifyDataSetChanged()

        reloadListView(mCategoryId)
    }

    override fun onDestroy() {
        super.onDestroy()

        //mRealm.close()
    }

    private fun deleteTask(taskId: Int){
        val results = mRealm
            .where(Task::class.java)
            .equalTo("id", taskId)
            .findAll()

        mRealm.beginTransaction()
        results.deleteAllFromRealm()
        mRealm.commitTransaction()

        val resultIntent = Intent(applicationContext, TaskAlarmReceiver::class.java)
        val resultPendingIntent = PendingIntent.getBroadcast(
            this,
            taskId,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(resultPendingIntent)

        reloadListView(mCategoryId)
    }

    private fun setRealm(){
        // Realmの設定
        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(mRealmListener)

        // ListViewの設定
        mCategoryAdapter = CategoryAdapter(this@MainActivity)

        // ListViewの設定
        mTaskAdapter = TaskAdapter(this@MainActivity)

        // spinner に adapter をセット
        category_spinner.adapter = spinnerAdapter
        spinnerAdapter.dataList = spinnerItems

        //spinnerにカテゴリーをセット
        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        val categoryRefineResults = mRealm.where(Category::class.java).findAll().sort("id")

        // 上記の結果を、spinnerList としてセットする
        mCategoryAdapter.spinnerlist = mRealm.copyFromRealm(categoryRefineResults)

        category_spinner.adapter = mCategoryAdapter

        mCategoryAdapter.notifyDataSetChanged()
    }

    private fun first(){
        val realm = Realm.getDefaultInstance()
        val CategoryRealmResults = realm.where(Category::class.java).findAll()

        if (CategoryRealmResults.max("id") == null) {
            realm.beginTransaction()
            mCategory!!.id = 0
            mCategory!!.name = "ALL"
            realm.copyToRealmOrUpdate(mCategory!!)
            realm.commitTransaction()
        }
    }
}
