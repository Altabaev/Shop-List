package ru.altabaev.iliya.shoplist

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmObject
import kotlinx.android.synthetic.main.activity_main.*
import ru.altabaev.iliya.shoplist.adapters.ListsRecyclerAdapter
import ru.altabaev.iliya.shoplist.db.models.CList
import ru.altabaev.iliya.shoplist.dialogs.CreateList
import ru.altabaev.iliya.shoplist.interfaces.OnRecyclerViewItemClickListener

class MainActivity : BaseActivity() {

    private val createList = CreateList()

    private var adapter: ListsRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Realm.init(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        lists.layoutManager = LinearLayoutManager(this)
        adapter = ListsRecyclerAdapter(this)
        adapter?.onItemClickListener = object : OnRecyclerViewItemClickListener {

            override fun onClick() {
                if (!createList.isResumed)
                    createList.show(supportFragmentManager, "create new list")
            }

            override fun <T : RealmObject> onClick(obj: T?) {
                if (obj is CList) {
                    val intent = Intent(this@MainActivity, ShowList::class.java)
                    intent.putExtra("list_id", obj.id)
                    intent.putExtra("list_title", obj.title)
                    startActivity(intent)
                }
            }

        }
        lists.adapter = adapter

        getDbWorker().getAll(CList::class.java)
                .subscribe({
                    adapter?.setData(it)
                }, {
                    it.printStackTrace()
                    Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_SHORT).show()
                })

    }
}
