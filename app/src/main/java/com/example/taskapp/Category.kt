package com.example.taskapp

import java.io.Serializable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Category : RealmObject(), Serializable {
    var name: String = ""   //カテゴリー

    // id をプライマリーキーとして設定
    @PrimaryKey
    var id: Int = 0
}