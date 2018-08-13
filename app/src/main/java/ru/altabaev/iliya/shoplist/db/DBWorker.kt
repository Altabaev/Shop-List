package ru.altabaev.iliya.shoplist.db

import io.reactivex.Flowable
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmObject
import io.realm.RealmResults
import ru.altabaev.iliya.shoplist.db.migrations.Migration

class DBWorker {

    val config = RealmConfiguration.Builder()
            .schemaVersion(1)
            .migration(Migration())
            .build()

    companion object {
        @JvmStatic
        private val inst = DBWorker()

        @JvmStatic
        fun getInstance() = inst
    }

    fun <T : RealmObject> save(obj: T) {
        val realm = getRealm()
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(obj)
        realm.commitTransaction()
        realm.close()
    }

    fun <T : RealmObject> save(list: List<T>) {
        val realm = getRealm()
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(list)
        realm.commitTransaction()
        realm.close()
    }

    fun <T : RealmObject> getAll(clazz: Class<T>): Flowable<RealmResults<T>> {
        val realm = getRealm()
        return realm.where(clazz).findAll().asFlowable()
    }

    fun <T : RealmObject> getAll(clazz: Class<T>, id: Int): Flowable<RealmResults<T>> {
        val realm = getRealm()
        return realm.where(clazz).equalTo("id", id).findAll().asFlowable()
    }

    fun <T : RealmObject> getElementById(clazz: Class<T>, id: Int): T? {
        val realm = getRealm()
        return realm.where(clazz).equalTo("id", id).findFirst()
    }

    fun <T : RealmObject> deleteElementById(clazz: Class<T>, id: Int) {
        val realm = getRealm()
        realm.beginTransaction()
        realm.where(clazz).equalTo("id", id).findFirst()?.deleteFromRealm()
        realm.commitTransaction()
        realm.close()
    }

    fun <T : RealmObject> getNextId(clazz: Class<T>): Int? {
        val realm = getRealm()
        return realm.where(clazz).max("id")?.toInt()?.plus(1) ?: 0
    }

    fun getRealm() = Realm.getInstance(config)
}