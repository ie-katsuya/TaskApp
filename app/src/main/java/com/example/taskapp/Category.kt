package com.example.taskapp

import java.io.Serializable
import java.util.Date
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

class Category {
    var category: String = ""   //カテゴリー

    // id をプライマリーキーとして設定
    @PrimaryKey
    var id: Int = 0
}