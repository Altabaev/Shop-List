package ru.altabaev.iliya.shoplist

import android.support.v7.app.AppCompatActivity
import ru.altabaev.iliya.shoplist.db.DBWorker

abstract class BaseActivity : AppCompatActivity() {

    fun getDbWorker() = DBWorker.getInstance()

}