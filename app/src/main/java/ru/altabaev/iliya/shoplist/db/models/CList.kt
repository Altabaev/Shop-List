package ru.altabaev.iliya.shoplist.db.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CList : RealmObject() {

    @PrimaryKey
    var id: Int? = null

    var title: String? = null

    var items: RealmList<Item>? = null
}