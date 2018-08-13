package ru.altabaev.iliya.shoplist.interfaces

import io.realm.RealmObject

interface OnRecyclerViewItemClickListener {

    fun <T : RealmObject> onClick(obj: T?)

    fun onClick()
}