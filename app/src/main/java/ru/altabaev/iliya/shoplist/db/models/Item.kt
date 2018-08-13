package ru.altabaev.iliya.shoplist.db.models

import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Item : RealmObject(), RealmModel {

    @PrimaryKey
    var id: Int? = null

    var title: String? = null

    var complited: Boolean? = false

    var count: Int? = 1

    var price: Float? = 0.toFloat()
}