package ru.altabaev.iliya.shoplist

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_show_list.*
import ru.altabaev.iliya.shoplist.adapters.ItemsRecyclerAdapter
import ru.altabaev.iliya.shoplist.adapters.SimpleItemTouchHelperCallback
import ru.altabaev.iliya.shoplist.db.models.CList
import ru.altabaev.iliya.shoplist.db.models.Item
import ru.altabaev.iliya.shoplist.dialogs.CreateList


class ShowList : BaseActivity() {

    private var adapter: ItemsRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        getDbWorker().getAll(CList::class.java, intent.getIntExtra("list_id", 0))
                .filter {
                    it.size > 0
                }
                .subscribe({
                    supportActionBar?.title = it[0]?.title
                })

        RxUI.getEditTextObservable(product)
                .subscribe({
                    addBtn.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
                })

        product.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                product.text.clear()
            }
            true
        }

        addBtn.setOnClickListener {
            submit()
            product.text.clear()
        }


        items.layoutManager = LinearLayoutManager(this)
        adapter = ItemsRecyclerAdapter(this)
        items.recycledViewPool.setMaxRecycledViews(0, 0);
        items.adapter = adapter

        val callback = SimpleItemTouchHelperCallback(adapter!!)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(items)

        getDbWorker().getAll(CList::class.java, intent.getIntExtra("list_id", 0))
                .filter {
                    it.size > 0
                }
                .filter {
                    it[0]?.items != null
                }
                .subscribe({
                    adapter?.setData(it[0]?.items)
                    var totalVal:Float = 0.toFloat()
                    var balanceVal:Float = 0.toFloat()
                    for (item in it[0]?.items!!) {
                        totalVal += item.count!! * item.price!!
                        if (item.complited!!.not()) {
                            balanceVal += item.count!! * item.price!!
                        }
                    }
                    total.text = totalVal.toString()
                    balance.text = balanceVal.toString()
                }, {
                    it.printStackTrace()
                })

    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.show_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_share -> {
                val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val builder = StringBuilder()
                builder.append("${intent.getStringExtra("list_title")} \n\n")
                val list = getDbWorker().getElementById(CList::class.java, intent.getIntExtra("list_id", 0))
                for ((i, item) in list?.items!!.withIndex()) {
                    builder.append("${i + 1}) ${item.title} \n")
                }
                builder.append("\n\nОтправлено из приложения \"Список покупок\".")
                val shareBodyText = builder.toString()
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Cписок покупок")
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText)
                startActivity(Intent.createChooser(sharingIntent, "Отправить список"))
            }
            R.id.action_edit -> {
                CreateList.newInstance(intent.getIntExtra("list_id", 0)).show(supportFragmentManager, "update list")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun submit() {
        if (product.text.isEmpty()) {
            Toast.makeText(this, "Заполните поле", Toast.LENGTH_SHORT).show()
            return
        }
        val item = Item()
        item.id = getDbWorker().getNextId(Item::class.java)
        item.title = product.text.toString()
        item.complited = false
        val realm =  getDbWorker().getRealm()
        realm.executeTransaction {
            val list = it.where(CList::class.java).equalTo("id", intent.getIntExtra("list_id", 0)).findFirst()
            list?.items?.add(item)
        }
    }
}
